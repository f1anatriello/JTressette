package view;

import controller.GameEngine;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import model.Carta;
import ui.UIAssets;

public class GameView extends JFrame{

    private static GameView instance = null;
    private JPanel tablePanel = new JPanel();
    private String player1Name;
    private ImageIcon player1Avatar;
    private String player2Name;
    private ImageIcon player2Avatar;
    private List<Carta> player1Hand;
    private List<Carta> player2Hand;
    private GameEngine gameEngine;
    public static final Color BACKGROUND_GREEN = new java.awt.Color(0, 128, 0);

    public static GameView getInstance(GameEngine gameEngine, String player1Name, ImageIcon player1Avatar, String player2Name, ImageIcon player2Avatar, List<Carta> player1Hand, List<Carta> player2Hand) {
        if (instance == null) {
        instance = new GameView(gameEngine, player1Name, player1Avatar, player2Name, player2Avatar, player1Hand, player2Hand);
        }
        return instance;
    }

    private GameView(GameEngine gameEngine, String player1Name, ImageIcon player1Avatar, String player2Name, ImageIcon player2Avatar, List<Carta> player1Hand, List<Carta> player2Hand) {
        this.gameEngine = gameEngine;
        this.player1Name = player1Name;
        this.player1Avatar = player1Avatar;
        this.player2Name = player2Name;
        this.player2Avatar = player2Avatar;
        this.player1Hand = player1Hand;
        this.player2Hand = player2Hand;

        tablePanel = new JPanel();
        tablePanel.setLayout(null);
        tablePanel.setBackground(BACKGROUND_GREEN);

        // Player 1 (bottom)
        JLabel avatar1 = new JLabel(player1Avatar);
        avatar1.setBounds(80, 530, 70, 70);
        tablePanel.add(avatar1);

        JLabel name1 = new JLabel(player1Name, SwingConstants.CENTER);
        name1.setBounds(60, 600, 100, 30);
        tablePanel.add(name1);

        JPanel handPanel1 = new JPanel();
        handPanel1.setOpaque(false);
        handPanel1.setBounds(170, 600, 600, 120);
        handPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 10));
        UIAssets uiAssets = UIAssets.getInstance();
        for (Carta card : player1Hand) {
            // non ci entro
            Image cardIcon = uiAssets.getImmagineCarta(card);
            JLabel cardLabel = new JLabel(new ImageIcon(cardIcon));
            handPanel1.add(cardLabel);
        }
        tablePanel.add(handPanel1);

        // Player 2 (top)
        JLabel avatar2 = new JLabel(player2Avatar);
        avatar2.setBounds(80, 30, 70, 70);
        tablePanel.add(avatar2);

        JLabel name2 = new JLabel(player2Name, SwingConstants.CENTER);
        name2.setBounds(60, 100, 100, 30);
        tablePanel.add(name2);

        JPanel handPanel2 = new JPanel();
        handPanel2.setOpaque(false);
        handPanel2.setBounds(170, 30, 600, 120);
        handPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 10));
        for (Carta card : player2Hand) {
            Image cardIcon = uiAssets.getImmagineCarta(card);
            JLabel cardLabel = new JLabel(new ImageIcon(cardIcon));
            handPanel2.add(cardLabel);
        }
        tablePanel.add(handPanel2);

        setContentPane(tablePanel);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
            dispose();
            MenuPrincipaleView menuView = MenuPrincipaleView.getInstance(gameEngine);
            gameEngine.visualizzaMenu();
            }
        });
        setVisible(true);
    }
}