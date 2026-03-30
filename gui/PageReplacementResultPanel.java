package gui;

import algorithms.SimulationResult;
import java.awt.*;
import java.util.List;
import javax.swing.*;

public class PageReplacementResultPanel extends JPanel {
    private final SimulationResult result;
    private int visibleColumns = 0; 

    // Mockup Colors
    private static final Color BG_LIGHT_BLUE = new Color(225, 230, 250);
    private static final Color FRAME_FILL = new Color(197, 219, 238);
    private static final Color TEXT_GREEN = new Color(46, 139, 87);
    private static final Color FAULT_RED = new Color(180, 50, 50);

    public PageReplacementResultPanel(SimulationResult result) {
        this.result = result;
        setOpaque(false);
        int steps = result.getSteps().size();
        int frames = result.getSteps().get(0).getFrameState().length;
        // Dynamic width based on reference string length
        setPreferredSize(new Dimension(85 * steps + 40, 220 + 45 * frames));
    }

    public void setVisibleColumns(int count) {
        this.visibleColumns = count;
        repaint();
    }

    public int getVisibleColumns() {
        return visibleColumns;
    }

    public int getTotalColumns() {
        return result.getSteps().size();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        List<SimulationResult.Step> steps = result.getSteps();
        int frameCount = steps.get(0).getFrameState().length;
        int colWidth = 80;
        int rowHeight = 40;

        for (int i = 0; i < visibleColumns; i++) {
            SimulationResult.Step step = steps.get(i);
            int x = 20 + (i * colWidth);

            // 1. Reference Number
            g2.setColor(TEXT_GREEN);
            g2.setFont(new Font("Arial", Font.BOLD, 18));
            String ref = String.valueOf(step.getPageNumber());
            g2.drawString(ref, x + (colWidth/2) - (g2.getFontMetrics().stringWidth(ref)/2), 30);

            // 2. Frames
            int[] frames = step.getFrameState();
            for (int f = 0; f < frameCount; f++) {
                int y = 50 + (f * rowHeight);
                g2.setColor(FRAME_FILL);
                g2.fillRect(x + 5, y, colWidth - 10, rowHeight - 5);
                g2.setColor(Color.GRAY);
                g2.drawRect(x + 5, y, colWidth - 10, rowHeight - 5);

                if (frames[f] != -1) {
                    g2.setColor(Color.BLACK);
                    g2.setFont(new Font("Arial", Font.PLAIN, 16));
                    String val = String.valueOf(frames[f]);
                    g2.drawString(val, x + (colWidth/2) - (g2.getFontMetrics().stringWidth(val)/2), y + 25);
                }
            }

            // 3. Status Text (Hit or Miss)
            int iconY = 50 + (frameCount * rowHeight) + 20;
            drawStatusText(g2, x + (colWidth/2), iconY, step.isFault());
        }

        // 4. Footer Info
        if (visibleColumns > 0) {
            int footerY = 50 + (frameCount * rowHeight) + 75;
            
            // Draw Algorithm Name
            g2.setFont(new Font("Arial", Font.BOLD, 13));
            g2.setColor(Color.DARK_GRAY);
            String algoNameText = "Algorithm Name: " + result.getAlgorithmName();
            g2.drawString(algoNameText, 20, footerY);
            
            // Only show Total Page Faults when the simulation for this algorithm finishes
            if (visibleColumns >= getTotalColumns()) {
                int textWidth = g2.getFontMetrics().stringWidth(algoNameText);
                g2.setColor(TEXT_GREEN);
                g2.setFont(new Font("Arial", Font.BOLD, 16));
                // 40px buffer after the algorithm text to prevent overlapping
                g2.drawString("Total Page Fault = " + result.getPageFaults(), 20 + textWidth + 40, footerY);
            }
        }
    }

    private void drawStatusText(Graphics2D g2, int centerX, int y, boolean isFault) {
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        if (isFault) {
            g2.setColor(FAULT_RED);
            String text = "Miss";
            int textWidth = g2.getFontMetrics().stringWidth(text);
            g2.drawString(text, centerX - (textWidth / 2), y);
        } else {
            g2.setColor(TEXT_GREEN);
            String text = "Hit";
            int textWidth = g2.getFontMetrics().stringWidth(text);
            g2.drawString(text, centerX - (textWidth / 2), y);
        }
    }
}