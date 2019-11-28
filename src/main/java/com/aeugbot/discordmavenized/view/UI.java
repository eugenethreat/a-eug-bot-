/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aeugbot.discordmavenized.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Eugene
 */
public final class UI extends JFrame {

    private final JPanel UIPanel;
    private final JPanel MiddlePanel;
    private final JPanel LeftPanel;
    private final JPanel settingsPanel;

    private final Dimension smallPanelSize;
    private final Dimension smallTextField;

    JTextArea outputTextField;
    JTextField leftTextField;
    JTextField middleTextField;

    public UI() {

        this.setTitle("Cute thing, don't be rude thing");

        setSize(700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        setLayout(gridbag);
        //defaults 

        UIPanel = new JPanel();
        MiddlePanel = new JPanel();
        LeftPanel = new JPanel();
        settingsPanel = new JPanel();

        smallPanelSize = new Dimension(100, 200);
        smallTextField = new Dimension(150, 170);
        //initializing my panels

        UIPanelConstraints(gbc);
        leftPanelConstraints(gbc);
        middlePanelConstraints(gbc);
        settingsPanelConstraints(gbc);

        arbitraryOutputForJim();

        panelBackgrounds();

        setVisible(true);
        
        
    }

    private void UIPanelConstraints(GridBagConstraints gbc) {

        gbc.anchor = GridBagConstraints.NORTH;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        //gbc.weightx = 1.0;
        gbc.weighty = 10.0;
        gbc.fill = GridBagConstraints.BOTH;

        JLabel outputLabel = new JLabel("Recent requests");
        outputLabel.setForeground(Color.white);

        outputTextField = new JTextArea("filler text for output");

        Dimension outputDimension = new Dimension(450, 350); //hardcoded since Dimensions are doo-doo ca-ca 
        outputTextField.setPreferredSize(outputDimension);

        UIPanel.add(outputLabel);
        UIPanel.add(outputTextField);

        this.add(UIPanel, gbc);

    }

    private void leftPanelConstraints(GridBagConstraints gbc) {
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.SOUTHEAST;

        gbc.gridheight = 200;
        gbc.gridwidth = 200;

        JLabel leftLabel = new JLabel("Most requested");
        leftLabel.setForeground(Color.white);

        LeftPanel.setPreferredSize(smallPanelSize);
        leftTextField = new JTextField("Left");
        leftTextField.setPreferredSize(smallTextField);

        LeftPanel.add(leftLabel);
        LeftPanel.add(leftTextField);

        this.add(LeftPanel, gbc);

    }

    private void middlePanelConstraints(GridBagConstraints gbc) {
        //uses the same gbc weight and height as the previous panel
        gbc.anchor = GridBagConstraints.PAGE_END;

        JLabel middleLabel = new JLabel("Local Requests");
        middleLabel.setForeground(Color.white);

        middleTextField = new JTextField("Middle");
        MiddlePanel.setPreferredSize(smallPanelSize);
        middleTextField.setPreferredSize(smallTextField);

        MiddlePanel.add(middleLabel);
        MiddlePanel.add(middleTextField);

        this.add(MiddlePanel, gbc);

    }

    private void settingsPanelConstraints(GridBagConstraints gbc) {
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        settingsPanel.setPreferredSize(smallPanelSize);
        /*
        try {
            File image = new File("clearSettings.png");
            //BufferedImage settingsImage = ImageIO.read(new File(getClass().getResource("clearSettings.png").toURI()));
            BufferedImage settingsImage = ImageIO.read(image);
            
            //fetches the settings.png image from the view folder
            JButton settingsButton = new JButton(new ImageIcon(settingsImage));
            settingsButton.setSize(smallPanelSize); //how can I change this to scale with the window? 
            settingsButton.setOpaque(true);
            Color otherGray = new Color(51, 51, 153);
            settingsButton.setBackground(otherGray);
            settingsPanel.add(settingsButton);

        } catch (IOException | URISyntaxException e) {
            System.out.println(e.toString());
        } 
        */
        this.add(settingsPanel, gbc);

    }

    private void panelBackgrounds() {
        Color newGray = new Color(102, 102, 153);
        Color otherGray = new Color(51, 51, 153);
        Color darkGray = new Color(61, 61, 143);

        UIPanel.setBackground(newGray);
        MiddlePanel.setBackground(darkGray);
        LeftPanel.setBackground(otherGray);
        settingsPanel.setBackground(otherGray);
    }

    public JPanel getUIPanel() {
        return UIPanel;
    }

    public JTextArea getUIText() {
        return outputTextField;
    }

    public JPanel getLeftPanel() {
        return LeftPanel;
    }

    public JTextField getLeftText() {
        return leftTextField;
    }

    public JTextField getMiddleText() {
        return middleTextField;
    }

    private void arbitraryOutputForJim() {

        JButton jimButton = new JButton("go!");
        UIPanel.add(jimButton);
        repaint();

        jimButton.addActionListener((ActionEvent evt) -> {
            outputTextField.setText("!pong");
        });

    }

}
