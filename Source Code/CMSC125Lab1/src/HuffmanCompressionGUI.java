package imagecompression1;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;

import java.util.Scanner;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;


public class HuffmanCompressionGUI {
    private JFrame frame;
    private JLabel imageLabel;
    private File selectedFile;
	private Node root;
	private HashMap<Integer, String> huffmanCodes;
	private int pixelData[];
	private int width, height;

    public HuffmanCompressionGUI() {
        frame = new JFrame("Huffman Compression Program");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(850, 525);
        frame.setLayout(null);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        imageLabel = new JLabel("Image Preview", SwingConstants.CENTER);
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        imageLabel.setBounds(10, 10, 500, 468);
        frame.add(imageLabel);

        JButton openButton = new JButton("Open (New)");
        openButton.setBounds(520, 28, 300, 100);
        openButton.setFocusPainted(false);
        frame.add(openButton);

        JButton trainButton = new JButton("Train");
        trainButton.setBounds(520, 138, 300, 100);
        frame.add(trainButton);

        JButton compressButton = new JButton("Compress");
        compressButton.setBounds(520, 248, 300, 100);
        frame.add(compressButton);

        JButton openCompressedButton = new JButton("Open (Compressed)");
        openCompressedButton.setBounds(520, 358, 300, 100);
        frame.add(openCompressedButton);

        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                FileNameExtensionFilter imageFilter = new FileNameExtensionFilter(
                        "Image Files (PNG, JPG, JPEG)", "png", "jpg", "jpeg");
                fileChooser.setFileFilter(imageFilter);

                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    selectedFile = fileChooser.getSelectedFile();

                    ImageIcon originalIcon = new ImageIcon(selectedFile.getPath());
                    Image resizedImage = originalIcon.getImage().getScaledInstance(
                            imageLabel.getWidth(),
                            imageLabel.getHeight(),
                            Image.SCALE_SMOOTH
                    );
                    ImageIcon resizedIcon = new ImageIcon(resizedImage);

                    imageLabel.setIcon(resizedIcon);
                    imageLabel.setText(null); 
                }
            }
        });

        trainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedFile != null) {
					// get RGB values using BufferedImage
					BufferedImage image = null;
					try {
						image = ImageIO.read(new File(selectedFile.getPath()));
					}catch(IOException ex){
						ex.printStackTrace();
					}
					
					// Extracting pixel data
					width = image.getWidth();
					height = image.getHeight();
					
					pixelData = new int[width * height];
					image.getRGB(0, 0, width, height, pixelData, 0, width);
					
					// Get frequency of pixel data
					HashMap<Integer, Integer> frequencyMap = new HashMap<>();
					for(int pixel : pixelData){
						frequencyMap.put(pixel, frequencyMap.getOrDefault(pixel, 0) + 1);
					}
					
					//Build huffman tree from data
					PriorityQueue<Node>pq = new PriorityQueue<>();
					for(Map.Entry<Integer, Integer> entry : frequencyMap.entrySet()){
						pq.offer(new Node(entry.getValue(), entry.getKey()));
					}
					
					root = buildHuffmanTree(pq);
					
					// Generate huffman codes
					huffmanCodes = new HashMap<>();
					generateHuffmanCodes(root, "", huffmanCodes);
					
                    // DITO HUFFMAN TRAINING LOGIC NA10
                    JOptionPane.showMessageDialog(frame, "Huffman tree trained using: " + selectedFile.getName());
                } else {
                    JOptionPane.showMessageDialog(frame, "No image selected to train.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        compressButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedFile != null) {
					// Compress image
					StringBuilder compressedImage = new StringBuilder();
					for(int pixel : pixelData) {
						compressedImage.append(huffmanCodes.get(pixel));
					}
					
					// Save huffman tree and compressed data to a file
					saveHuffmanTree(root,  "huffman_tree.huff");
					
					try(BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile.getName() + "_compressed.huff"))){
						writer.write(compressedImage.toString());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
                    // // DITO COMPRESSION LOGIC NA10
                    JOptionPane.showMessageDialog(frame, "Image compressed: " + selectedFile.getName());
                } else {
                    JOptionPane.showMessageDialog(frame, "No image selected to compress.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        openCompressedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File compressedFile = fileChooser.getSelectedFile();
                    
                    Node huffmanTreeRoot = loadHuffmanTree("huffman_tree.huff");
                    StringBuilder compressedImage = new StringBuilder();
                    // Read the compressed binary data (similar to how we saved it)
                    try (BufferedReader reader = new BufferedReader(new FileReader(compressedFile.getName()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            compressedImage.append(line);
                        }
                    } catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

                    // Decode the binary string back to pixel data
                    ArrayList<Integer> decompressedPixels = new ArrayList<>();
                    Node currentNode = huffmanTreeRoot;
                    for (char bit : compressedImage.toString().toCharArray()) {
                        if (bit == '0') {
                            currentNode = currentNode.left;
                        } else {
                            currentNode = currentNode.right;
                        }

                        if (currentNode.left == null && currentNode.right == null) {
                            decompressedPixels.add(currentNode.value);  // Add decoded pixel value
                            currentNode = huffmanTreeRoot;  // Go back to root for next pixel
                        }
                    }

                    // Rebuild the image from the decompressed pixels
                    int[] decompressedPixelData = new int[decompressedPixels.size()];
                    for (int i = 0; i < decompressedPixels.size(); i++) {
                        decompressedPixelData[i] = decompressedPixels.get(i);
                    }

                    BufferedImage decompressedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                    decompressedImage.setRGB(0, 0, width, height, decompressedPixelData, 0, width);

                    Image scaledImage = decompressedImage.getScaledInstance(
                            imageLabel.getWidth(),
                            imageLabel.getHeight(),
                            Image.SCALE_SMOOTH
                    );
                    ImageIcon loadedIcon = new ImageIcon(scaledImage);

                    imageLabel.setIcon(loadedIcon);
                    imageLabel.setText(null);
                    
                    // // DITO OPENING COMPRESSED IMG LOGIC
                    JOptionPane.showMessageDialog(frame, "Opened compressed image: " + compressedFile.getName());
                }
            }
        });

        frame.setVisible(true);
    }
	
	// Builds the huffman tree from the data
	private Node buildHuffmanTree(PriorityQueue<Node> pq) {
		do {
			Node node1 = pq.poll(); // gets left node
			Node node2 = pq.poll(); // gets right node
			
			// Sum of node1 and node2
			int sum = node1.frequency + node2.frequency;
			
			// Setting up parent node (sum value)
			Node parent = new Node(sum, 0);
			parent.left = node1;
			parent.right = node2;
			
			// Add parent to PQ
			pq.offer(parent);
		}
		while(pq.size() > 1);
		return pq.poll();
	}


	// Generates Huffman Code
	private void generateHuffmanCodes(Node node, String code, HashMap<Integer, String> hc) {
		if(node == null) return;
		
		// If this is a leaf node, add the character and its code
        if (node.left == null && node.right == null) {
            hc.put(node.value, code);
        }

        // Traverse left and right
        generateHuffmanCodes(node.left, code + "0", hc);
        generateHuffmanCodes(node.right, code + "1", hc);
	}

    private void saveHuffmanTree(Node root, String filename) {
		try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))){
			oos.writeObject(root); 
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    
    // Load the Huffman Tree from a file
    private Node loadHuffmanTree(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (Node) ois.readObject();  // Deserialize the Huffman Tree
        } catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        new HuffmanCompressionGUI();
    }
	
}

class Node implements Serializable, Comparable<Node>{
	int frequency;
	int value;
	Node left, right;
	
	public Node(int frequency, int value){
		this.frequency = frequency;
		this.value = value;
	}
	
	@Override
	public int compareTo(Node other){
		return Integer.compare(this.frequency, other.frequency);
	}
	
}
