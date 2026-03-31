package gui;

import algorithms.SimulationResult;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Result extends JPanel {
    private final Mainframe mainframe;
    private JPanel resultContainer;
    private JSlider speedSlider;
    private javax.swing.Timer animTimer;

    private Queue<SimulationResult> resultsQueue = new LinkedList<>();
    private PageReplacementResultPanel currentPanel;
    private JPanel currentButtonPanel; // Tracks the active algorithm's button panel
    private int currentStep = 0;
    private int currentMaxSteps = 0;

    public Result(Mainframe frame) {
        this.mainframe = frame;
        setLayout(new BorderLayout());
        setBackground(new Color(173, 196, 220));

        add(createHeader(), BorderLayout.NORTH);

        resultContainer = new JPanel();
        resultContainer.setLayout(new BoxLayout(resultContainer, BoxLayout.Y_AXIS));
        resultContainer.setOpaque(false);

        JScrollPane scroll = new JScrollPane(resultContainer);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        // Speed up the scroll wheel
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // Default timer fires every 500ms (can be adjusted by the slider)
        animTimer = new javax.swing.Timer(500, e -> updateAnimation());
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(81, 97, 113));
        header.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Center: Speed Control Slider
        JPanel speedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        speedPanel.setOpaque(false);
        JLabel speedLabel = new JLabel("Timer Speed: ");
        speedLabel.setForeground(Color.WHITE);
        speedLabel.setFont(new Font("Arial", Font.BOLD, 14));

        speedSlider = new JSlider(JSlider.HORIZONTAL, 1, 10, 2);
        speedSlider.setOpaque(false);
        speedSlider.setForeground(Color.WHITE);
        speedSlider.setMajorTickSpacing(1);
        speedSlider.setPaintTicks(true);
        speedSlider.addChangeListener(e -> {
            int speed = speedSlider.getValue();
            animTimer.setDelay(1000 / speed);
        });

        speedPanel.add(speedLabel);
        speedPanel.add(speedSlider);

        // Right side: Back Button
        JButton backBtn = new JButton("← Return");
        backBtn.setFocusPainted(false);
        backBtn.setBackground(Color.WHITE);
        backBtn.setForeground(new Color(81, 97, 113));
        backBtn.setFont(new Font("Arial", Font.BOLD, 12));
        backBtn.addActionListener(e -> {
            animTimer.stop();
            mainframe.showCard("SCHEDULE");
        });

        header.add(speedPanel, BorderLayout.WEST);
        header.add(backBtn, BorderLayout.EAST);

        return header;
    }

    public void startSimulation(Map<String, SimulationResult> results) {
        resultsQueue.clear();
        resultsQueue.addAll(results.values());
        resultContainer.removeAll();
        playNextAlgorithm();
    }

    private void playNextAlgorithm() {
        if (resultsQueue.isEmpty())
            return;

        SimulationResult next = resultsQueue.poll();
        currentStep = 0;
        currentMaxSteps = next.getSteps().size();
        currentPanel = new PageReplacementResultPanel(next);

        // Lock these variables locally so the button lambdas target the correct
        // algorithm
        PageReplacementResultPanel panelRef = currentPanel;
        String algoNameRef = next.getAlgorithmName();

        // Wrap panel in a white box like the mockup
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(new EmptyBorder(15, 15, 15, 15));

        // --- Individual PDF & Image Buttons attached below the grid ---
        currentButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        currentButtonPanel.setOpaque(false);
        currentButtonPanel.setVisible(false); // Hide the buttons initially

        JButton downloadPngBtn = new JButton("Save as Image");
        downloadPngBtn.setBackground(new Color(46, 139, 87));
        downloadPngBtn.setForeground(Color.WHITE);
        downloadPngBtn.setFocusPainted(false);
        downloadPngBtn.setFont(new Font("Arial", Font.BOLD, 14));
        downloadPngBtn.addActionListener(e -> savePanelAsImage(panelRef, algoNameRef));

        JButton downloadPdfBtn = new JButton("Save as PDF");
        downloadPdfBtn.setBackground(new Color(180, 50, 50));
        downloadPdfBtn.setForeground(Color.WHITE);
        downloadPdfBtn.setFocusPainted(false);
        downloadPdfBtn.setFont(new Font("Arial", Font.BOLD, 14));
        downloadPdfBtn.addActionListener(e -> printToPDF(panelRef, algoNameRef));

        currentButtonPanel.add(downloadPngBtn);
        currentButtonPanel.add(downloadPdfBtn);

        wrapper.add(currentButtonPanel, BorderLayout.SOUTH);

        JScrollPane innerScroll = new JScrollPane(currentPanel);
        innerScroll.setBorder(null);
        wrapper.add(innerScroll, BorderLayout.CENTER);

        resultContainer.add(wrapper);
        resultContainer.add(Box.createVerticalStrut(20));

        revalidate();

        // Set initial timer speed based on slider before starting
        animTimer.setDelay(1000 / speedSlider.getValue());
        animTimer.start();
    }

    private void updateAnimation() {
        if (currentPanel != null && currentStep < currentMaxSteps) {
            currentStep++;
            currentPanel.setVisibleColumns(currentStep);
        } else {
            // Animation finished for this algorithm: Reveal the buttons
            if (currentButtonPanel != null) {
                currentButtonPanel.setVisible(true);
            }

            animTimer.stop();
            if (!resultsQueue.isEmpty()) {
                playNextAlgorithm();
            }
        }
    }

    private void savePanelAsImage(PageReplacementResultPanel panel, String algoName) {
        // Temporarily reveal all columns so the screenshot captures the final state
        int prevCols = panel.getVisibleColumns();
        panel.setVisibleColumns(panel.getTotalColumns());

        BufferedImage img = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        panel.paint(g2);
        g2.dispose();

        // Restore animation state
        panel.setVisibleColumns(prevCols);

        JFileChooser saver = new JFileChooser();
        saver.setDialogTitle("Save " + algoName + " Result as Image");
        saver.setSelectedFile(new File(algoName.replaceAll("[^a-zA-Z0-9_-]", "_") + "_result.png"));
        int userSelection = saver.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = saver.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".png")) {
                file = new File(file.getAbsolutePath() + ".png");
            }
            try {
                ImageIO.write(img, "png", file);
                JOptionPane.showMessageDialog(this, "Image saved to " + file.getAbsolutePath(), "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving image: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void printToPDF(PageReplacementResultPanel panel, String algoName) {
        // Temporarily reveal all columns
        int prevCols = panel.getVisibleColumns();
        panel.setVisibleColumns(panel.getTotalColumns());

        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Export " + algoName + " Result");
        job.setPrintable((g, pf, page) -> {
            if (page > 0)
                return Printable.NO_SUCH_PAGE;
            Graphics2D g2 = (Graphics2D) g;
            g2.translate(pf.getImageableX(), pf.getImageableY());
            // Scale the grid to fit the width of the PDF page
            double scale = pf.getImageableWidth() / panel.getWidth();
            if (scale > 1.0)
                scale = 1.0; // Don't scale up if it's already small enough
            g2.scale(scale, scale);
            panel.paint(g2);
            return Printable.PAGE_EXISTS;
        });

        boolean doPrint = job.printDialog();
        if (doPrint) {
            try {
                job.print();
                JOptionPane.showMessageDialog(this,
                        "PDF export sent to printer. Use 'Microsoft Print to PDF' to save as PDF.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(this, "PDF Export Failed: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        // Restore animation state
        panel.setVisibleColumns(prevCols);
    }
}