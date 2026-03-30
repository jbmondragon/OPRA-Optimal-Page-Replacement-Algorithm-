package gui;

import algorithms.SimulationResult;
import java.awt.*;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Result extends JPanel {
    private JTabbedPane tabbedPane;
    private JPanel controlsPanel;
    private JSlider speedSlider;
    private JButton exportPngBtn, exportPdfBtn, backBtn;
    private JLabel frameCountLabel;
    private Mainframe mainframe;
    private int lastFrameCount = -1;

    public Result(Mainframe frame) {
        this.mainframe = frame;
        setLayout(new BorderLayout());
        setBackground(Mainframe.BG_DARK);
        add(createHeader(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Mainframe.BG_LIGHT_GRAY);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        backBtn = new JButton("← Back");
        backBtn.setFont(new Font("Arial", Font.BOLD, 15));
        backBtn.setFocusPainted(false);
        backBtn.setBackground(new Color(220, 230, 245));
        backBtn.setForeground(new Color(40, 80, 120));
        backBtn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        backBtn.addActionListener(e -> mainframe.showCard("SCHEDULE"));
        leftPanel.add(backBtn);

        JLabel mainPageLabel = new JLabel("  Simulation Output");
        mainPageLabel.setFont(new Font("Arial", Font.BOLD, 18));
        leftPanel.add(mainPageLabel);

        frameCountLabel = new JLabel("");
        frameCountLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        frameCountLabel.setForeground(new Color(40, 80, 120));
        frameCountLabel.setBorder(new EmptyBorder(0, 30, 0, 0));
        leftPanel.add(frameCountLabel);

        headerPanel.add(leftPanel, BorderLayout.WEST);
        return headerPanel;
    }

    private JPanel createMainContent() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Controls panel (minimalist, flat)
        controlsPanel = new JPanel();
        controlsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 16, 4));
        controlsPanel.setBackground(Color.WHITE);

        JLabel speedLabel = new JLabel("Speed:");
        speedLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        speedSlider = new JSlider(100, 2000, 500);
        speedSlider.setMajorTickSpacing(500);
        speedSlider.setMinorTickSpacing(100);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(false);
        speedSlider.setPreferredSize(new Dimension(120, 32));
        speedSlider.setBackground(Color.WHITE);

        exportPngBtn = new JButton("Export PNG");
        exportPdfBtn = new JButton("Export PDF");
        exportPngBtn.setFocusPainted(false);
        exportPdfBtn.setFocusPainted(false);
        exportPngBtn.setFont(new Font("Arial", Font.BOLD, 13));
        exportPdfBtn.setFont(new Font("Arial", Font.BOLD, 13));
        exportPngBtn.setBackground(new Color(220, 230, 245));
        exportPdfBtn.setBackground(new Color(220, 230, 245));
        exportPngBtn.setForeground(new Color(40, 80, 120));
        exportPdfBtn.setForeground(new Color(40, 80, 120));

        controlsPanel.add(speedLabel);
        controlsPanel.add(speedSlider);
        controlsPanel.add(exportPngBtn);
        controlsPanel.add(exportPdfBtn);

        tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 15));
        tabbedPane.setBackground(Color.WHITE);

        wrapper.add(controlsPanel, BorderLayout.NORTH);
        wrapper.add(tabbedPane, BorderLayout.CENTER);
        return wrapper;
    }

    /**
     * Display a single algorithm result (for single algorithm mode)
     */
    public void displayResult(String algorithmName, SimulationResult result) {
        tabbedPane.removeAll();
        PageReplacementResultPanel panel = new PageReplacementResultPanel(result);
        JScrollPane scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(20);
        scrollPane.setBorder(null);
        tabbedPane.addTab(algorithmName, scrollPane);
        // Connect speed slider to panel
        speedSlider.addChangeListener(e -> panel.setAnimationSpeed(speedSlider.getValue()));
        // Export buttons
        exportPngBtn.addActionListener(ev -> panel.exportAsPng());
        exportPdfBtn.addActionListener(ev -> panel.exportAsPdf());
        // Show number of page frames
        int frameCount = result.getSteps().get(0).getFrameState().length;
        frameCountLabel.setText("Frames: " + frameCount);
        lastFrameCount = frameCount;
    }

    /**
     * Display results for multiple algorithms (for "All Algorithms" mode)
     */
    public void displayMultipleResults(Map<String, SimulationResult> results) {
        tabbedPane.removeAll();
        int frameCount = -1;
        for (Map.Entry<String, SimulationResult> entry : results.entrySet()) {
            PageReplacementResultPanel panel = new PageReplacementResultPanel(entry.getValue());
            JScrollPane scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollPane.getHorizontalScrollBar().setUnitIncrement(20);
            scrollPane.setBorder(null);
            tabbedPane.addTab(entry.getKey(), scrollPane);
            // Connect speed slider to panel
            speedSlider.addChangeListener(e -> panel.setAnimationSpeed(speedSlider.getValue()));
            // Export buttons
            exportPngBtn.addActionListener(ev -> panel.exportAsPng());
            exportPdfBtn.addActionListener(ev -> panel.exportAsPdf());
            // Use the first result's frame count for display
            if (frameCount == -1) {
                frameCount = entry.getValue().getSteps().get(0).getFrameState().length;
            }
        }
        if (frameCount != -1) {
            frameCountLabel.setText("Frames: " + frameCount);
            lastFrameCount = frameCount;
        }
    }
}
