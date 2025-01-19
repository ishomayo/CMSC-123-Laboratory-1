import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class HuffmanCompressionGUI {
    private JFrame frame;
    private JLabel imageLabel;
    private File selectedFile;

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
                    // // DITO OPENING COMPRESSED IMG LOGIC
                    JOptionPane.showMessageDialog(frame, "Opened compressed image: " + compressedFile.getName());
                }
            }
        });

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new HuffmanCompressionGUI();
    }
}
