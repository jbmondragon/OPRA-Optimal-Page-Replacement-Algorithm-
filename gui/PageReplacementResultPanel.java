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
    private static final Color FRAME_HIGHLIGHT = new Color(255, 230, 140);

    public PageReplacementResultPanel(SimulationResult result) {
        this.result = result;
        setOpaque(false);
        int steps = result.getSteps().size();
        int frames = result.getSteps().get(0).getFrameState().length;
        // Dynamic width based on reference string length
        setPreferredSize(new Dimension(85 * steps + 40, 300 + 45 * frames));
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

            if (i == visibleColumns - 1 && visibleColumns < result.getSteps().size()) {
                g2.setColor(new Color(255, 255, 0, 80));
                g2.fillRoundRect(x + 5, 5, colWidth - 10, 35, 10, 10);
            }

            // 1. Reference Number
            g2.setColor(TEXT_GREEN);
            g2.setFont(new Font("Arial", Font.BOLD, 18));
            String ref = String.valueOf(step.getPageNumber());
            g2.drawString(ref, x + (colWidth / 2) - (g2.getFontMetrics().stringWidth(ref) / 2), 30);

            // 2. Frames
            int[] frames = step.getFrameState();
            int[] prevFrames = null;
            if (i > 0) {
                prevFrames = steps.get(i - 1).getFrameState();
            }

            for (int f = 0; f < frameCount; f++) {
                int y = 50 + (f * rowHeight);

                boolean changed;
                if (prevFrames == null) {
                    changed = (frames[f] != -1);
                } else {
                    changed = (frames[f] != prevFrames[f]);
                }

                if (changed)
                    g2.setColor(FRAME_HIGHLIGHT);
                else
                    g2.setColor(FRAME_FILL);

                g2.fillRect(x + 5, y, colWidth - 10, rowHeight - 5);
                g2.setColor(Color.GRAY);
                g2.drawRect(x + 5, y, colWidth - 10, rowHeight - 5);

                if (frames[f] != -1) {
                    g2.setColor(Color.BLACK);
                    g2.setFont(new Font("Arial", Font.PLAIN, 16));
                    String val = String.valueOf(frames[f]);
                    g2.drawString(val, x + (colWidth / 2) - (g2.getFontMetrics().stringWidth(val) / 2), y + 25);
                }
            }

            // 3. Status Text (Hit or Miss)
            int iconY = 50 + (frameCount * rowHeight) + 20;
            drawStatusText(g2, x + (colWidth / 2), iconY, step.isFault());
        }

        // 4. Footer Info
        if (visibleColumns > 0) {
            int footerY = 50 + (frameCount * rowHeight) + 75;
            // Draw Algorithm Name and Number of Frames
            g2.setFont(new Font("Arial", Font.BOLD, 13));
            g2.setColor(Color.DARK_GRAY);
            String algoNameText = "Algorithm Name: " + result.getAlgorithmName();
            String frameCountText = "Number of Frames: " + frameCount;
            g2.drawString(algoNameText, 20, footerY);
            int algoTextWidth = g2.getFontMetrics().stringWidth(algoNameText);
            g2.drawString(frameCountText, 20 + algoTextWidth + 40, footerY);
            // Only show Total Page Faults when the simulation for this algorithm finishes
            if (visibleColumns >= getTotalColumns()) {
                int frameTextWidth = g2.getFontMetrics().stringWidth(frameCountText);
                g2.setColor(TEXT_GREEN);
                g2.setFont(new Font("Arial", Font.BOLD, 16));
                // 40px buffer after the frame count text to prevent overlapping
                g2.drawString("Total Page Fault = " + result.getPageFaults(),
                        20 + algoTextWidth + 40 + frameTextWidth + 40, footerY);
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
