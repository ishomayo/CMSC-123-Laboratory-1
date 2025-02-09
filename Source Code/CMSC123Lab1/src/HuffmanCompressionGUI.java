import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
    private String filename;
    private int pixelData[];
    private int width, height;

    public HuffmanCompressionGUI() {
        frame = new JFrame("Huffman Compression Program");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(850, 525);
        frame.setLayout(null);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        ImageIcon background = new ImageIcon("C:\\Users\\ACER\\Downloads\\CMSC 123 Lab Outputs\\Huffman GUI\\fullFrame.png");
        JLabel backgroundLabel = new JLabel(background);
        backgroundLabel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        frame.setContentPane(backgroundLabel);
        backgroundLabel.setLayout(null);

        ImageIcon imagePreview = new ImageIcon("C:\\Users\\ACER\\Downloads\\CMSC 123 Lab Outputs\\Huffman GUI\\imagePreview.png");
        imageLabel = new JLabel();
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        imageLabel.setBounds(10, 10, 500, 468);
        imageLabel.setIcon(imagePreview);
        frame.add(imageLabel);

        ImageIcon openIcon = new ImageIcon("C:\\Users\\ACER\\Downloads\\CMSC 123 Lab Outputs\\Huffman GUI\\openNew.png");
        JButton openButton = new JButton(openIcon);
        openButton.setBounds(520, 28, 300, 100);
        openButton.setFocusPainted(false);
        openButton.setBorderPainted(false);
        openButton.setContentAreaFilled(false);
        openButton.setOpaque(false);
        frame.add(openButton);

        ImageIcon trainIcon = new ImageIcon("C:\\Users\\ACER\\Downloads\\CMSC 123 Lab Outputs\\Huffman GUI\\trainButton.png");
        JButton trainButton = new JButton(trainIcon);
        trainButton.setBounds(520, 138, 300, 100);
        trainButton.setFocusPainted(false);
        trainButton.setBorderPainted(false);
        trainButton.setContentAreaFilled(false);
        trainButton.setOpaque(false);
        frame.add(trainButton);

        ImageIcon compressIcon = new ImageIcon("C:\\Users\\ACER\\Downloads\\CMSC 123 Lab Outputs\\Huffman GUI\\compressButton.png");
        JButton compressButton = new JButton(compressIcon);
        compressButton.setBounds(520, 248, 300, 100);
        compressButton.setFocusPainted(false);
        compressButton.setBorderPainted(false);
        compressButton.setContentAreaFilled(false);
        compressButton.setOpaque(false);
        frame.add(compressButton);

        ImageIcon openCompressedIcon = new ImageIcon("C:\\Users\\ACER\\Downloads\\CMSC 123 Lab Outputs\\Huffman GUI\\openCompressed.png");
        JButton openCompressedButton = new JButton(openCompressedIcon);
        openCompressedButton.setBounds(520, 358, 300, 100);
        openCompressedButton.setFocusPainted(false);
        openCompressedButton.setBorderPainted(false);
        openCompressedButton.setContentAreaFilled(false);
        openCompressedButton.setOpaque(false);
        frame.add(openCompressedButton);

        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                
                // Filters to look for a certain type of file
                FileNameExtensionFilter imageFilter = new FileNameExtensionFilter("Images", "bmp", "png", "jpg", "jpeg");
                fileChooser.setFileFilter(imageFilter);
                int result = fileChooser.showOpenDialog(frame);
                
                if (result != JFileChooser.APPROVE_OPTION) { // No image was selected
                	JOptionPane.showMessageDialog(frame, "No image selected to train.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
            	// Starts time for opening the image
            	double startTime = System.currentTimeMillis();
            	
                selectedFile = fileChooser.getSelectedFile();
				try {
					BufferedImage originalImage = ImageIO.read(selectedFile);
					Image resizedImage = originalImage.getScaledInstance(
                            imageLabel.getWidth(),
                            imageLabel.getHeight(),
                            Image.SCALE_SMOOTH
                    );
                    ImageIcon resizedIcon = new ImageIcon(resizedImage);
                    
                    // Changes icon contents
                    imageLabel.setIcon(resizedIcon);
                    imageLabel.setText(null); 
                    
                    // Gets total openTime
                    double openTime = (System.currentTimeMillis() - startTime) / 1000;
                    JOptionPane.showMessageDialog(frame, "Image selected to train.\nTime: " + openTime + " seconds");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
            }
        });

        trainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedFile == null) { // No image was selected
                    JOptionPane.showMessageDialog(frame, "No image selected to train.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Starts time for training the huffman tree
                double startTime = System.currentTimeMillis();
				
				try {
					// Read the image file into BufferedImage
					BufferedImage image = ImageIO.read(new File(selectedFile.getPath()));
					
					// Get original image width and height
					width = image.getWidth();
					height = image.getHeight();
					
					// Get pixel data from BufferedImage
					pixelData = new int[width * height];
					pixelData = image.getRGB(0, 0, width, height, pixelData, 0, width);
				}catch(IOException ex){
					ex.printStackTrace();
					JOptionPane.showMessageDialog(frame, "Image failed to load.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				// Get frequency of pixel data
				HashMap<Integer, Integer> frequencyMap = new HashMap<>();
				for(int pixel : pixelData) {
					frequencyMap.put(pixel, frequencyMap.getOrDefault(pixel, 0) + 1);
				}
				
				//Build huffman tree from data
				PriorityQueue<Node>pq = new PriorityQueue<>();
				for(Map.Entry<Integer, Integer> entry : frequencyMap.entrySet()) {
					pq.offer(new Node(entry.getValue(), entry.getKey()));
				}
				root = buildHuffmanTree(pq); // Set root the huffman tree
				
				// Generate huffman codes
				huffmanCodes = new HashMap<>();
				generateHuffmanCodes(root, "");
				
				// Stops training time
				double trainTime = (System.currentTimeMillis() - startTime) / 1000;
				// Display Huffman Tree training success!
                JOptionPane.showMessageDialog(frame, "Huffman tree trained using: " + selectedFile.getName() + "\nTime: " + trainTime + " seconds");
            }
        });

        compressButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedFile == null) { // No image was selected
                    JOptionPane.showMessageDialog(frame, "No image selected to compress.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Start time for compressing image
                double startTime = System.currentTimeMillis();
                
				// Append into compressedImage (strbldr) the generated huffman codes
				StringBuilder compressedImage = new StringBuilder();
				for(int pixel : pixelData) {
					compressedImage.append(huffmanCodes.get(pixel));
				}
				
				// Save huffman tree to a HUFF file
				filename = selectedFile.getName().substring(0, selectedFile.getName().lastIndexOf("."));
				saveHuffmanTree(root,  filename + "_huffmantree.HUFF");		
				
				// Save compressed data to a DEW file
				File compressedFile = new File(filename + "_compressed.DEW");
				try(FileOutputStream fos = new FileOutputStream(compressedFile)){
					int bitBuffer = 0, bitCount = 0;
				    for (char bit : compressedImage.toString().toCharArray()) {
				    	// Convert '0'/'1' to binary
				        bitBuffer = (bitBuffer << 1) | (bit - '0'); // left shift by 1 OR bit (char) - '0' (48)
				        bitCount++;

				        // Write each full byte
				        if (bitCount == 8) { 
				            fos.write(bitBuffer);
				            bitBuffer = 0;
				            bitCount = 0;
				        }
				    }
				    
				    if (bitCount > 0){
				        // Write any remaining bits
				        bitBuffer <<= (8 - bitCount); // left shift by (8 - no. of bits)
			        	fos.write(bitBuffer); // write it to file
				    }
				    
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(frame, "File not found.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				} catch (IOException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(frame, "File failed to load.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				// Stop time for compressing image
				double compressTime = (System.currentTimeMillis() - startTime) / 1000;
				// Display success for compression + results and time
				JOptionPane.showMessageDialog(frame, "Image compressed: " + selectedFile.getName() + "\n Original Size: " + selectedFile.length() + "\n Compressed Size: " + compressedFile.length() + "\nTime: " + compressTime + " seconds");
            }
        });

        openCompressedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	// Choose a file using JFileChooser
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int result = fileChooser.showOpenDialog(frame);
                if (result != JFileChooser.APPROVE_OPTION) {
                	JOptionPane.showMessageDialog(frame, "No file selected to decompress.", "Error", JOptionPane.ERROR_MESSAGE);
                	return;
                }
                
                // Start time for opening compressed file
                double startTime = System.currentTimeMillis();
                
                // Get the compressed file
                File compressedFile = fileChooser.getSelectedFile();
                StringBuilder compressedImage = new StringBuilder();
                try {
                	FileInputStream fis = new FileInputStream(compressedFile);
                    int b;
                    while ((b = fis.read()) != -1) {
                        // Append 8-bit binary representation of the byte to compressedImage
                        String byteString = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
                        compressedImage.append(byteString); 
                    }
                    fis.close();
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "File not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                } catch (IOException e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "File failed to load.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Load the huffman tree file
                Node huffmanTreeRoot = loadHuffmanTree(filename + "_huffmantree.HUFF");
                
                // Decode the binary string back to pixel data
                ArrayList<Integer> decompressedPixels = new ArrayList<>();
                Node currentNode = huffmanTreeRoot;
                for (char bit : compressedImage.toString().toCharArray()) {
                    if (bit == '0') currentNode = currentNode.left;
                    else currentNode = currentNode.right;
                    
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

                // Load the decompressed image into imageLabel
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
                
                // Stop time for opening compressed image
                double openCompressTime = (System.currentTimeMillis() - startTime) / 1000;
                // Display success of opening compressed image
                JOptionPane.showMessageDialog(frame, "Opened compressed image: " + compressedFile.getName() + "\nTime: " + openCompressTime + " seconds");
            }
        });

        frame.setVisible(true);
    }
	
	// Builds the Huffman Tree from the data
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

	// (Recursion) Generates Huffman Codes 
	private void generateHuffmanCodes(Node node, String code){
		if(node == null) return;
		
		// If this is a leaf node, add the character and its code
        if (node.left == null && node.right == null) {
            huffmanCodes.put(node.value, code);
            return;
        }
        // Traverse left and right
        generateHuffmanCodes(node.left, code + "0");
        generateHuffmanCodes(node.right, code + "1");
	}

	// Saves Huffman Tree
    private void saveHuffmanTree(Node root, String filename) {
    	try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))){
			oos.writeObject(root); 
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(frame, "File not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(frame, "File failed to load.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
		}
	}
    
    
    // Load the Huffman Tree from a file
    private Node loadHuffmanTree(String filename) {
    	try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
    	    return (Node) ois.readObject();
    	} catch (IOException | ClassNotFoundException e) {
    	    e.printStackTrace();
    	    JOptionPane.showMessageDialog(frame, "File failed to load.", "Error", JOptionPane.ERROR_MESSAGE);
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
