package view;

import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.*;
import model.Carta;
import ui.UIAssets;

public class GameView extends JFrame{

    private static GameView instance = null;
    private final JPanel tablePanel;

    public static GameView getInstance(String player1Name, ImageIcon player1Avatar, String player2Name, ImageIcon player2Avatar, List<Carta> player1Hand, List<Carta> player2Hand) {
        if (instance == null) {
            instance = new GameView(player1Name, player1Avatar, player2Name, player2Avatar, player1Hand, player2Hand);
        }
        return instance;
    }
    
    private GameView(String player1Name, ImageIcon player1Avatar, String player2Name, ImageIcon player2Avatar, List<Carta> player1Hand, List<Carta> player2Hand) {
        setTitle("JTressette");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int scl = JOptionPane.showConfirmDialog(
                        GameView.this,
                        "Sei sicuro di voler uscire?",
                        "Già te ne vai? :(",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (scl == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
        tablePanel = new JPanel() {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);
                ImageIcon bg = new ImageIcon("images/backgound/tavolo.jpg");
                g.drawImage(bg.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        tablePanel.setLayout(null);

        // Player 1 (bottom)
        JLabel avatar1 = new JLabel(player1Avatar);
        avatar1.setBounds(50, getHeight() - 180, 100, 100);
        tablePanel.add(avatar1);

        JLabel name1 = new JLabel(player1Name, SwingConstants.CENTER);
        name1.setBounds(50, getHeight() - 80, 100, 30);
        tablePanel.add(name1);

        JPanel handPanel1 = new JPanel();
        handPanel1.setOpaque(false);
        handPanel1.setBounds(170, getHeight() - 160, 600, 120);
        handPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 10));
        UIAssets uiAssets = UIAssets.getInstance();
        for (Carta card : player1Hand) {
            Image cardIcon = uiAssets.getImmagineCarta(card);
            JLabel cardLabel = new JLabel(new ImageIcon(cardIcon));
            handPanel1.add(cardLabel);
        }
        tablePanel.add(handPanel1);

        // Player 2 (top)
        JLabel avatar2 = new JLabel(player2Avatar);
        avatar2.setBounds(50, 30, 100, 100);
        tablePanel.add(avatar2);

        JLabel name2 = new JLabel(player2Name, SwingConstants.CENTER);
        name2.setBounds(50, 130, 100, 30);
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
        setVisible(true);
    }

    public JPanel getPanel() {
        return tablePanel;
    }
}
