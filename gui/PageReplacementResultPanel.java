
package gui;

import algorithms.SimulationResult;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.imageio.ImageIO;
// PDFBox imports for PDF export
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;

public class PageReplacementResultPanel extends JPanel {
    private final SimulationResult result;
    private final int frameCount;
    private final int[] referenceString;
    private final String algorithmName;
    private int currentStep = 0;
    private Timer timer;
    private long startTime = 0;
    private long elapsed = 0;
    private JLabel timerLabel;
    private JButton playBtn, pauseBtn, resetBtn;
    private int animationSpeed = 500;
    private JPanel controlsBar;

    public PageReplacementResultPanel(SimulationResult result) {
        this.result = result;
        this.frameCount = result.getSteps().get(0).getFrameState().length;
        this.referenceString = result.getSteps().stream().mapToInt(SimulationResult.Step::getPageNumber).toArray();
        this.algorithmName = result.getAlgorithmName();
        setPreferredSize(new Dimension(80 * referenceString.length + 40, 220 + 40 * frameCount));
        setBackground(new Color(245, 248, 252));
        setLayout(null);
        addSimulationControls();
        setupTimer();
    }

    private void addSimulationControls() {
        controlsBar = new JPanel();
        controlsBar.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 6));
        controlsBar.setBackground(new Color(245, 248, 252));
        controlsBar.setBounds(0, 0, Math.max(400, 80 * referenceString.length), 38);

        timerLabel = new JLabel("Time: 0.0s");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        timerLabel.setForeground(new Color(40, 80, 120));
        controlsBar.add(timerLabel);

        playBtn = new JButton("Play");
        playBtn.setToolTipText("Play");
        playBtn.setPreferredSize(new Dimension(100, 25));
        playBtn.setFocusPainted(false);
        playBtn.setFont(new Font("Arial", Font.BOLD, 15));
        playBtn.setForeground(new Color(40, 80, 120));
        playBtn.setBackground(new Color(220, 230, 245));
        playBtn.addActionListener(e -> startAnimation());
        controlsBar.add(playBtn);

        pauseBtn = new JButton("Pause");
        pauseBtn.setToolTipText("Pause");
        pauseBtn.setPreferredSize(new Dimension(100, 25));
        pauseBtn.setFocusPainted(false);
        pauseBtn.setFont(new Font("Arial", Font.BOLD, 15));
        pauseBtn.setForeground(new Color(40, 80, 120));
        pauseBtn.setBackground(new Color(220, 230, 245));
        pauseBtn.addActionListener(e -> pauseAnimation());
        pauseBtn.setEnabled(false);
        controlsBar.add(pauseBtn);

        resetBtn = new JButton("Reset");
        resetBtn.setToolTipText("Reset");
        resetBtn.setPreferredSize(new Dimension(100, 25));
        resetBtn.setFocusPainted(false);
        resetBtn.setFont(new Font("Arial", Font.BOLD, 15));
        resetBtn.setForeground(new Color(40, 80, 120));
        resetBtn.setBackground(new Color(220, 230, 245));
        resetBtn.addActionListener(e -> resetAnimation());
        controlsBar.add(resetBtn);

        add(controlsBar);
    }

    private void setupTimer() {
        timer = new Timer(animationSpeed, e -> {
            if (currentStep < referenceString.length - 1) {
                currentStep++;
                elapsed = System.currentTimeMillis() - startTime;
                timerLabel.setText(String.format("Time: %.1fs", elapsed / 1000.0));
                repaint();
            } else {
                timer.stop();
                pauseBtn.setEnabled(false);
                playBtn.setEnabled(false);
            }
        });
        timer.setInitialDelay(0);
    }

    public void setAnimationSpeed(int ms) {
        this.animationSpeed = ms;
        if (timer != null) {
            timer.setDelay(ms);
        }
    }

    public void exportAsPng() {
        // Export the visible simulation panel as PNG
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export as PNG");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PNG Image (*.png)", "png"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                Dimension originalSize = getSize();
                Dimension exportSize = getPreferredSize();
                setSize(exportSize);
                BufferedImage img = new BufferedImage(exportSize.width, exportSize.height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = img.createGraphics();
                paint(g2);
                g2.dispose();
                setSize(originalSize);
                java.io.File file = chooser.getSelectedFile();
                String path = file.getAbsolutePath();
                if (!path.toLowerCase().endsWith(".png")) {
                    file = new java.io.File(path + ".png");
                }
                javax.imageio.ImageIO.write(img, "png", file);
                JOptionPane.showMessageDialog(this, "Exported as PNG!", "Export", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Export Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void exportAsPdf() {
        // Export the visible simulation panel as PDF (simple, using Java 2D PDF lib if
        // available)
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export as PDF");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF Document (*.pdf)", "pdf"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                // Use PDFBox if available, else show error
                try {
                    Dimension originalSize = getSize();
                    Dimension exportSize = getPreferredSize();
                    setSize(exportSize);
                    org.apache.pdfbox.pdmodel.PDDocument doc = new org.apache.pdfbox.pdmodel.PDDocument();
                    org.apache.pdfbox.pdmodel.PDPage page = new org.apache.pdfbox.pdmodel.PDPage(
                            new org.apache.pdfbox.pdmodel.common.PDRectangle(exportSize.width, exportSize.height));
                    doc.addPage(page);
                    org.apache.pdfbox.pdmodel.PDPageContentStream contentStream = new org.apache.pdfbox.pdmodel.PDPageContentStream(
                            doc, page);
                    BufferedImage img = new BufferedImage(exportSize.width, exportSize.height,
                            BufferedImage.TYPE_INT_RGB);
                    Graphics2D g2 = img.createGraphics();
                    paint(g2);
                    g2.dispose();
                    setSize(originalSize);
                    org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject pdImage = org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory
                            .createFromImage(doc, img);
                    contentStream.drawImage(pdImage, 0, 0, exportSize.width, exportSize.height);
                    contentStream.close();
                    java.io.File file = chooser.getSelectedFile();
                    String path = file.getAbsolutePath();
                    if (!path.toLowerCase().endsWith(".pdf")) {
                        file = new java.io.File(path + ".pdf");
                    }
                    doc.save(file);
                    doc.close();
                    JOptionPane.showMessageDialog(this, "Exported as PDF!", "Export", JOptionPane.INFORMATION_MESSAGE);
                } catch (NoClassDefFoundError | ClassNotFoundException pdfLibMissing) {
                    JOptionPane.showMessageDialog(this, "PDF export requires Apache PDFBox library.", "Export Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Export Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void startAnimation() {
        if (currentStep >= referenceString.length - 1)
            return;
        playBtn.setEnabled(false);
        pauseBtn.setEnabled(true);
        resetBtn.setEnabled(true);
        if (currentStep == 0) {
            startTime = System.currentTimeMillis();
        } else {
            startTime = System.currentTimeMillis() - elapsed;
        }
        timer.start();
    }

    private void pauseAnimation() {
        timer.stop();
        playBtn.setEnabled(true);
        pauseBtn.setEnabled(false);
    }

    private void resetAnimation() {
        timer.stop();
        currentStep = 0;
        elapsed = 0;
        timerLabel.setText("Time: 0.0s");
        playBtn.setEnabled(true);
        pauseBtn.setEnabled(false);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int colWidth = 70;
        int colGap = 20; // Gap between columns
        int rowHeight = 40;
        int controlsBarHeight = 38; // Should match controlsBar height
        int refYOffset = controlsBarHeight + 18; // Fixed offset below controls bar
        int topMargin = refYOffset + 10;
        int leftMargin = 20;
        // Draw reference string numbers (page references)
        g2.setFont(new Font("Arial", Font.BOLD, 22));
        for (int i = 0; i < referenceString.length; i++) {
            String num = String.valueOf(referenceString[i]);
            int colX = leftMargin + i * (colWidth + colGap);
            int centerX = colX + colWidth / 2;
            g2.setColor(new Color(30, 120, 60));
            int strW = g2.getFontMetrics().stringWidth(num);
            g2.drawString(num, centerX - strW / 2, refYOffset);
        }
        int topMarginAdjusted = topMargin;
        // Draw frame columns (up to currentStep) with gaps
        java.util.List<SimulationResult.Step> steps = result.getSteps();
        for (int i = 0; i <= currentStep && i < referenceString.length; i++) {
            int[] frameState = steps.get(i).getFrameState();
            int colX = leftMargin + i * (colWidth + colGap);
            boolean isCurrent = (i == currentStep);
            for (int f = 0; f < frameCount; f++) {
                int x = colX;
                int y = topMargin + f * rowHeight;
                g2.setColor(new Color(180, 210, 240));
                g2.fillRoundRect(x, y, colWidth, rowHeight, 16, 16);
                // Highlight current column with blue border
                if (isCurrent) {
                    g2.setColor(new Color(30, 120, 240));
                    g2.setStroke(new BasicStroke(2.5f));
                } else {
                    g2.setColor(Color.GRAY);
                    g2.setStroke(new BasicStroke(1f));
                }
                g2.drawRoundRect(x, y, colWidth, rowHeight, 16, 16);
                // Draw page number in cell
                if (frameState[f] != -1) {
                    String page = String.valueOf(frameState[f]);
                    g2.setColor(new Color(40, 80, 120));
                    g2.setFont(new Font("Arial", Font.PLAIN, 16));
                    int tx = x + colWidth / 2 - g2.getFontMetrics().stringWidth(page) / 2;
                    int ty = y + rowHeight / 2 + g2.getFontMetrics().getAscent() / 2 - 4;
                    g2.drawString(page, tx, ty);
                }
            }
        }
        // Draw fault/hit labels (up to currentStep)
        for (int i = 0; i <= currentStep && i < referenceString.length; i++) {
            boolean isFault = steps.get(i).isFault();
            int x = leftMargin + i * (colWidth + colGap) + colWidth / 2;
            int y = topMargin + frameCount * rowHeight + 28;
            g2.setFont(new Font("Arial", Font.BOLD, 15));
            if (isFault) {
                g2.setColor(new Color(180, 40, 40));
                String miss = "miss";
                int strW = g2.getFontMetrics().stringWidth(miss);
                g2.drawString(miss, x - strW / 2, y);
            } else {
                g2.setColor(new Color(40, 160, 80));
                String hit = "hit";
                int strW = g2.getFontMetrics().stringWidth(hit);
                g2.drawString(hit, x - strW / 2, y);
            }
        }
        // Draw algorithm name and total faults
        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        g2.setColor(new Color(40, 80, 120));
        g2.drawString("Algorithm Name: " + algorithmName, leftMargin, topMargin + frameCount * rowHeight + 50);
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.setColor(new Color(0, 150, 60));
        g2.drawString("Total Page Fault = " + result.getPageFaults(), leftMargin + 220,
                topMargin + frameCount * rowHeight + 50);
    }
}
