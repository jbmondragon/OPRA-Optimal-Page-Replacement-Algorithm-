package gui;

import algorithms.SimulationResult;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Result extends JPanel {

    // private final Mainframe mainframe;
    private JLabel algoNameLbl;
    private JPanel resultPanelContainer;
    private PageReplacementResultPanel pageResultPanel;

    public Result(Mainframe frame) {
        setLayout(new BorderLayout());
        setBackground(Mainframe.BG_DARK);
        add(createHeader(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Mainframe.BG_LIGHT_GRAY);

        JLabel mainPageLabel = new JLabel(" Simulation Output");
        headerPanel.add(mainPageLabel, BorderLayout.WEST);

        return headerPanel;
    }

    private JPanel createMainContent() {
        JPanel whiteWrapper = new JPanel(new BorderLayout());
        whiteWrapper.setBackground(Color.WHITE);
        whiteWrapper.setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel contentBody = new JPanel(new BorderLayout());
        contentBody.setBackground(Mainframe.BG_LIGHT_GRAY);
        contentBody.setBorder(new EmptyBorder(20, 20, 0, 20));

        // Algorithm name label at the top of the result area
        algoNameLbl = new JLabel("");
        algoNameLbl.setFont(new Font("Arial", Font.BOLD, 18));
        algoNameLbl.setForeground(new Color(40, 80, 120));
        algoNameLbl.setBorder(new EmptyBorder(0, 0, 10, 0));
        contentBody.add(algoNameLbl, BorderLayout.NORTH);

        resultPanelContainer = new JPanel(new BorderLayout());
        resultPanelContainer.setBackground(Color.WHITE);
        resultPanelContainer.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentBody.add(resultPanelContainer, BorderLayout.CENTER);

        whiteWrapper.add(contentBody, BorderLayout.CENTER);
        return whiteWrapper;
    }

    /**
     * Display the result of a page replacement simulation using the custom panel.
     */
    public void displayResult(String algorithmName, SimulationResult result) {
        algoNameLbl.setText(algorithmName);
        resultPanelContainer.removeAll();
        pageResultPanel = new PageReplacementResultPanel(result);
        resultPanelContainer.add(pageResultPanel, BorderLayout.CENTER);
        resultPanelContainer.revalidate();
        resultPanelContainer.repaint();
    }
}
