package gui;

import algorithms.PageReplacementSimulator;
import algorithms.SimulationResult;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Page extends JPanel {

    private Mainframe mainframe;
    private JComboBox<String> algoCombo;
    private JTextField textField;
    private JTextField frameField;
    private static final int ICON_SIZE = 26;
    
    // Updated to match the instructions/mockup exactly
    private static final String[] ALGORITHMS = {
            "FIFO",
            "LRU",
            "OPT",
            "Second chance algorithm",
            "Enhanced second chance algorithm",
            "LFU",
            "MFU",
            "All Algorithms"
    };
    private final PageReplacementSimulator simulator = new PageReplacementSimulator();

    public Page(Mainframe frame) {
        this.mainframe = frame;
        setLayout(new BorderLayout());
        setBackground(Mainframe.BG_DARK);

        ImageIcon randomIcon = loadIcon("img/random.png", ICON_SIZE);
        ImageIcon importIcon = loadIcon("img/import.png", ICON_SIZE);

        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setBackground(Mainframe.BG_DARK);
        topHeader.setBorder(new EmptyBorder(8, 14, 6, 14));

        JLabel returnLbl = new JLabel("Return");
        returnLbl.setForeground(Mainframe.TEXT_LIGHT);
        returnLbl.setFont(new Font("Arial", Font.PLAIN, 13));
        returnLbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        returnLbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainframe.showCard("MENU");
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                returnLbl.setText("<html><u>Return</u></html>");
            }
            @Override
            public void mouseExited(MouseEvent e) {
                returnLbl.setText("Return");
            }
        });

        JLabel aisaLbl = new JLabel("ReBoot", SwingConstants.CENTER);
        aisaLbl.setForeground(Mainframe.TEXT_LIGHT);
        aisaLbl.setFont(new Font("Arial", Font.BOLD, 15));

        JLabel spacer = new JLabel("Return");
        spacer.setFont(returnLbl.getFont());
        spacer.setForeground(Mainframe.BG_DARK);
        spacer.setVisible(false);
        spacer.setPreferredSize(returnLbl.getPreferredSize());

        topHeader.add(returnLbl, BorderLayout.WEST);
        topHeader.add(aisaLbl, BorderLayout.CENTER);
        topHeader.add(spacer, BorderLayout.EAST);
        add(topHeader, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(12, 0));
        body.setBackground(Mainframe.BG_DARK);
        body.setBorder(new EmptyBorder(4, 8, 8, 8));

        JPanel leftCard = new JPanel(new BorderLayout());
        leftCard.setBackground(Color.WHITE);
        leftCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(185, 185, 185), 1, true),
                BorderFactory.createEmptyBorder(0, 0, 14, 0)));

        JPanel procHeader = new JPanel(new BorderLayout());
        procHeader.setBackground(new Color(81, 97, 113));
        procHeader.setBorder(new EmptyBorder(11, 16, 11, 16));

        JPanel procTitleBox = new JPanel();
        procTitleBox.setLayout(new BoxLayout(procTitleBox, BoxLayout.Y_AXIS));
        procTitleBox.setBackground(new Color(81, 97, 113));

        JLabel procTitle = new JLabel("Page Replacement Algorithm");
        procTitle.setFont(new Font("Arial", Font.BOLD, 16));
        procTitle.setForeground(Color.WHITE);

        JLabel procSub = new JLabel("Add a process for simulation");
        procSub.setFont(new Font("Arial", Font.PLAIN, 11));
        procSub.setForeground(Color.white);

        procTitleBox.add(procTitle);
        procTitleBox.add(procSub);
        procHeader.add(procTitleBox, BorderLayout.WEST);
        leftCard.add(procHeader, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(new EmptyBorder(30, 10, 10, 10));

        JLabel textLabel = new JLabel("Reference String");
        textLabel.setFont(new Font("Arial", Font.BOLD, 16));
        textLabel.setForeground(new Color(81, 97, 113));
        textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        textField = new JTextField();
        textField.setPreferredSize(new Dimension(650, 50));
        textField.setMaximumSize(new Dimension(650, 50));
        textField.setFont(new Font("Arial", Font.BOLD, 16));
        textField.setForeground(new Color(81, 97, 113));
        textField.setHorizontalAlignment(JTextField.LEFT);
        textField.setAlignmentX(Component.CENTER_ALIGNMENT);
        textField.setBackground(new Color(245, 248, 252));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(185, 185, 185), 1, true),
                BorderFactory.createEmptyBorder(0, 20, 0, 20)
        ));

        JLabel frameLabel = new JLabel("Frame Size");
        frameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        frameLabel.setForeground(new Color(81, 97, 113));
        frameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        frameField = new JTextField();
        frameField.setPreferredSize(new Dimension(120, 50));
        frameField.setMaximumSize(new Dimension(120, 50));
        frameField.setFont(new Font("Arial", Font.BOLD, 16));
        frameField.setForeground(new Color(81, 97, 113));
        frameField.setHorizontalAlignment(JTextField.CENTER);
        frameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        frameField.setBackground(new Color(245, 248, 252));
        frameField.setBorder(textField.getBorder());

        inputPanel.add(textLabel);
        inputPanel.add(Box.createVerticalStrut(15));
        inputPanel.add(textField);
        inputPanel.add(Box.createVerticalStrut(30));
        inputPanel.add(frameLabel);
        inputPanel.add(Box.createVerticalStrut(15));
        inputPanel.add(frameField);
        inputPanel.add(Box.createVerticalGlue());

        leftCard.add(inputPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Mainframe.BG_DARK);
        rightPanel.setPreferredSize(new Dimension(190, 0));

        JPanel logoBlock = new JPanel();
        logoBlock.setBackground(new Color(81, 97, 113));
        logoBlock.setMinimumSize(new Dimension(190, 68));
        logoBlock.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));
        logoBlock.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(logoBlock);
        rightPanel.add(Box.createVerticalStrut(12));

        rightPanel.add(makeActionRow(randomIcon, "Random", e -> randomFill()));
        rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(makeActionRow(importIcon, "Import", e -> importFile()));
        rightPanel.add(Box.createVerticalStrut(14));

        JPanel algoBox = new JPanel();
        algoBox.setLayout(new BoxLayout(algoBox, BoxLayout.Y_AXIS));
        algoBox.setBackground(new Color(245, 248, 252));
        algoBox.setBorder(new EmptyBorder(10, 10, 12, 10));
        algoBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        algoBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        JLabel algoLbl = new JLabel("<html>Select A Page Replacement Algorithm To Simulate:</html>");
        algoLbl.setForeground(new Color(81, 97, 113));
        algoLbl.setFont(new Font("Arial", Font.PLAIN, 11));
        algoLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        algoCombo = new JComboBox<>(ALGORITHMS);
        algoCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        algoCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        algoCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        algoBox.add(algoLbl);
        algoBox.add(Box.createVerticalStrut(7));
        algoBox.add(algoCombo);
        algoBox.add(Box.createVerticalStrut(6));

        rightPanel.add(algoBox);
        rightPanel.add(Box.createVerticalGlue());

        JButton submitBtn = new JButton("Submit");
        submitBtn.setBackground(new Color(81, 97, 113));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFont(new Font("Arial", Font.BOLD, 15));
        submitBtn.setFocusPainted(false);
        submitBtn.setOpaque(true);
        submitBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        submitBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        submitBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70), 1),
                BorderFactory.createEmptyBorder(8, 0, 8, 0)));
        submitBtn.addActionListener(e -> runSimulation());
        rightPanel.add(submitBtn);

        body.add(leftCard, BorderLayout.CENTER);
        body.add(rightPanel, BorderLayout.EAST);
        add(body, BorderLayout.CENTER);
    }

    private JPanel makeActionRow(ImageIcon icon, String label, ActionListener action) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row.setBackground(Mainframe.BG_DARK);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton iconBtn = new JButton();
        iconBtn.setPreferredSize(new Dimension(44, 44));
        iconBtn.setBackground(Color.WHITE);
        iconBtn.setFocusPainted(false);
        iconBtn.setOpaque(true);
        iconBtn.setBorder(BorderFactory.createLineBorder(new Color(81, 97, 113), 1));

        if (icon != null) {
            iconBtn.setIcon(icon);
        } else {
            iconBtn.setText(label.substring(0, 1));
            iconBtn.setFont(new Font("Arial", Font.BOLD, 16));
        }

        iconBtn.addActionListener(action);

        JLabel lbl = new JLabel(label);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));

        row.add(iconBtn);
        row.add(lbl);
        return row;
    }

    private ImageIcon loadIcon(String path, int targetSize) {
        try {
            URL url = getClass().getClassLoader().getResource(path);
            BufferedImage img;
            if (url != null) {
                img = ImageIO.read(url);
            } else {
                File f = new File(path);
                if (!f.exists())
                    return null;
                img = ImageIO.read(f);
            }
            if (img == null)
                return null;
            Image scaled = img.getScaledInstance(targetSize, targetSize, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception ex) {
            System.err.println("Could not load icon: " + path + " - " + ex.getMessage());
            return null;
        }
    }

    private void randomFill() {
        PageReplacementSimulator.SimulationInput randInput = simulator.generateRandomInput();
        StringBuilder sb = new StringBuilder();
        for (int num : randInput.getReferenceString()) {
            sb.append(num).append(" ");
        }
        textField.setText(sb.toString().trim());
        frameField.setText(String.valueOf(randInput.getFrameSize()));
    }

    private void importFile() {
        JFileChooser fc = new JFileChooser();
        File dataDir = new File("dataset");
        if (dataDir.exists() && dataDir.isDirectory()) {
            fc.setCurrentDirectory(dataDir);
        }
        fc.setDialogTitle("Import Reference String (.txt/.csv/.xlsx)");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Text/CSV/XLSX files", "txt", "csv", "xlsx"));
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
            return;
        File file = fc.getSelectedFile();
        String name = file.getName().toLowerCase();
        try {
            if (name.endsWith(".txt") || name.endsWith(".csv")) {
                java.util.List<String> lines = new java.util.ArrayList<>();
                try (java.util.Scanner sc = new java.util.Scanner(file)) {
                    while (sc.hasNextLine()) {
                        String line = sc.nextLine().trim();
                        if (!line.isEmpty())
                            lines.add(line);
                    }
                }
                if (lines.isEmpty()) {
                    error("File is empty.");
                    return;
                }
                textField.setText(lines.get(0));
                if (lines.size() > 1)
                    frameField.setText(lines.get(1));
            } else if (name.endsWith(".xlsx")) {
                java.util.List<String[]> rows = readXlsx(file);
                if (rows.isEmpty()) {
                    error("Excel file is empty.");
                    return;
                }
                String[] firstRow = rows.get(0);
                if (firstRow.length > 0)
                    textField.setText(firstRow[0]);
                if (firstRow.length > 1)
                    frameField.setText(firstRow[1]);
            } else {
                error("Unsupported file type.");
            }
        } catch (Exception ex) {
            error("Failed to import: " + ex.getMessage());
        }
    }

    private void runSimulation() {
        String refStr = textField.getText().trim();
        String[] refParts = refStr.split("\\s+");
        int[] referenceString = new int[refParts.length];
        
        for (int i = 0; i < refParts.length; i++) {
            try {
                referenceString[i] = Integer.parseInt(refParts[i]);
            } catch (NumberFormatException e) {
                error("Reference string must contain only integers.");
                return;
            }
        }

        int frameSize;
        try {
            frameSize = Integer.parseInt(frameField.getText().trim());
        } catch (NumberFormatException e) {
            error("Frame size must be an integer.");
            return;
        }

        if (!simulator.validateInput(referenceString, frameSize)) {
            error("Reference string must be 10-40 integers (0-20). Frame size must be 3-10.");
            return;
        }

        String selectedAlgo = (String) algoCombo.getSelectedItem();
        if (selectedAlgo == null) {
            error("Please select an algorithm.");
            return;
        }

        // --- SURGICALLY MAPPED NAMES FOR THE SIMULATOR ---
        String algoName = selectedAlgo;
        if (selectedAlgo.equals("OPT")) algoName = "Optimal";
        else if (selectedAlgo.equals("Second chance algorithm")) algoName = "SecondChance";
        else if (selectedAlgo.equals("Enhanced second chance algorithm")) algoName = "EnhancedSecondChance";

        if (selectedAlgo.equals("All Algorithms")) {
            Map<String, SimulationResult> results = simulator.runAllAlgorithms(referenceString, frameSize);
            mainframe.getResultPanel().startSimulation(results);
        } else {
            SimulationResult result = simulator.runAlgorithm(algoName, referenceString, frameSize);
            if (result == null) {
                error("Algorithm not found: " + algoName);
                return;
            }
            Map<String, SimulationResult> singleResultMap = new LinkedHashMap<>();
            singleResultMap.put(result.getAlgorithmName(), result);
            mainframe.getResultPanel().startSimulation(singleResultMap);
        }
        
        mainframe.showCard("RESULT");
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Invalid Input", JOptionPane.WARNING_MESSAGE);
    }

    private java.util.List<String[]> readXlsx(File file) throws Exception {
        java.util.List<String[]> rows = new java.util.ArrayList<>();
        try (ZipFile zip = new ZipFile(file)) {
            java.util.List<String> shared = new java.util.ArrayList<>();
            ZipEntry sst = zip.getEntry("xl/sharedStrings.xml");
            if (sst != null) {
                Document sstDoc = DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder().parse(zip.getInputStream(sst));
                NodeList siList = sstDoc.getElementsByTagName("si");
                for (int i = 0; i < siList.getLength(); i++) {
                    shared.add(siList.item(i).getTextContent());
                }
            }

            ZipEntry sheet = zip.getEntry("xl/worksheets/sheet1.xml");
            if (sheet != null) {
                Document sheetDoc = DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder().parse(zip.getInputStream(sheet));
                NodeList rowList = sheetDoc.getElementsByTagName("row");
                for (int i = 0; i < rowList.getLength(); i++) {
                    Element row = (Element) rowList.item(i);
                    NodeList cellList = row.getElementsByTagName("c");
                    java.util.List<String> rowVals = new java.util.ArrayList<>();
                    for (int j = 0; j < cellList.getLength(); j++) {
                        Element c = (Element) cellList.item(j);
                        String type = c.getAttribute("t");
                        String v = "";
                        NodeList vnodes = c.getElementsByTagName("v");
                        if (vnodes.getLength() > 0) {
                            v = vnodes.item(0).getTextContent();
                            if ("s".equals(type)) {
                                try {
                                    int idx = Integer.parseInt(v);
                                    if (idx < shared.size()) {
                                        v = shared.get(idx);
                                    }
                                } catch (NumberFormatException ignored) { }
                            }
                        }
                        rowVals.add(v);
                    }
                    rows.add(rowVals.toArray(new String[0]));
                }
            }
        }
        return rows;
    }
}