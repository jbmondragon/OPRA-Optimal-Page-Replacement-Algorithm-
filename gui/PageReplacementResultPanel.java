package gui;

import algorithms.SimulationResult;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PageReplacementResultPanel extends JPanel {
    private final SimulationResult result;
    private final int frameCount;
    private final int[] referenceString;
    private final String algorithmName;

    public PageReplacementResultPanel(SimulationResult result) {
        this.result = result;
        this.frameCount = result.getSteps().get(0).getFrameState().length;
        this.referenceString = result.getSteps().stream().mapToInt(SimulationResult.Step::getPageNumber).toArray();
        this.algorithmName = result.getAlgorithmName();
        setPreferredSize(new Dimension(80 * referenceString.length + 40, 200 + 40 * frameCount));
        setBackground(new Color(225, 230, 250));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int colWidth = 80;
        int rowHeight = 40;
        int topMargin = 40;
        int leftMargin = 20;
        // Draw reference string numbers
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        for (int i = 0; i < referenceString.length; i++) {
            String num = String.valueOf(referenceString[i]);
            int x = leftMargin + i * colWidth + colWidth / 2 - g2.getFontMetrics().stringWidth(num) / 2;
            g2.setColor(new Color(30, 120, 60));
            g2.drawString(num, x, topMargin - 10);
        }
        // Draw frame grid
        List<SimulationResult.Step> steps = result.getSteps();
        for (int i = 0; i < referenceString.length; i++) {
            int[] frameState = steps.get(i).getFrameState();
            for (int f = 0; f < frameCount; f++) {
                int x = leftMargin + i * colWidth;
                int y = topMargin + f * rowHeight;
                g2.setColor(new Color(180, 210, 240));
                g2.fillRect(x, y, colWidth, rowHeight);
                g2.setColor(Color.GRAY);
                g2.drawRect(x, y, colWidth, rowHeight);
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
        // Draw fault/hit icons
        for (int i = 0; i < referenceString.length; i++) {
            boolean isFault = steps.get(i).isFault();
            int x = leftMargin + i * colWidth + colWidth / 2 - 12;
            int y = topMargin + frameCount * rowHeight + 8;
            if (isFault) {
                // Red X
                g2.setColor(new Color(180, 40, 40));
                g2.setStroke(new BasicStroke(3));
                g2.drawRect(x - 2, y - 2, 28, 28);
                g2.drawLine(x, y, x + 24, y + 24);
                g2.drawLine(x + 24, y, x, y + 24);
            } else {
                // Green check
                g2.setColor(new Color(40, 160, 80));
                g2.setStroke(new BasicStroke(4));
                g2.drawRect(x - 2, y - 2, 28, 28);
                g2.drawLine(x + 4, y + 16, x + 12, y + 24);
                g2.drawLine(x + 12, y + 24, x + 24, y + 4);
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
