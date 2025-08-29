package view;

import controller.GameEngine;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import ui.AudioManager;
import ui.UIAssets;
import ui.UIConstants;
import ui.UISettings;

/**
 * Schermata principale (menu) come JPanel singleton.
 * Usare {@link #getInstance(GameEngine)} per ottenere l'istanza.
 * @author 1957447
 */

public class MenuPrincipaleView extends JPanel {
    private final JButton btnPartita1v1;
    private final JButton btnPartita2v2;
    private final JButton btnProfilo;
    private final JButton btnClassifica;
    private final JButton btnEsci;
    private static MenuPrincipaleView instance;

    /**
     * Restituisce l'istanza singleton del menu principale.
     * @param gameEngine Il controller del gioco per gestire le azioni dei pulsanti.
     * @return L'istanza singleton di MenuPrincipaleView.
     */
    public static MenuPrincipaleView getInstance(GameEngine gameEngine) {
        if (instance == null) {
            instance = new MenuPrincipaleView(gameEngine);
        }
        return instance;
    }

    /**
     * Costruttore privato per il pattern Singleton.
     * @param gameEngine Il controller del gioco per gestire le azioni dei pulsanti.
     */
    private MenuPrincipaleView(GameEngine gameEngine) {
        AudioManager.getInstance().stop();

        setLayout(new BorderLayout());
        setBackground(UIConstants.BACKGROUND_DARK);
        setPreferredSize(new Dimension(400, 600));

        // Header con logo
        BufferedImage logo = UIAssets.getInstance().getMenuLogo();
        Image scaledLogo = (logo != null) ? logo.getScaledInstance(300, 200, Image.SCALE_SMOOTH) : null;

        JLabel logoLabel = (scaledLogo != null)
                ? new JLabel(new ImageIcon(scaledLogo))
                : new JLabel("JTressette", SwingConstants.CENTER);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel header = UISettings.createPanel(null);
        header.setLayout(new BorderLayout());
        header.setOpaque(false);
        header.add(logoLabel, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // Centro con i pulsanti (stack verticale)
        JPanel center = UISettings.createPanel(null);
        center.setLayout(new GridBagLayout());
        center.setOpaque(true);

        JPanel stack = new JPanel(new GridLayout(0, 1, UIConstants.GAP, UIConstants.GAP));
        stack.setOpaque(false);

        btnPartita1v1 = UISettings.createButton("Partita 1v1", UISettings.ButtonVariant.PRIMARY);
        btnPartita2v2 = UISettings.createButton("Partita 2v2", UISettings.ButtonVariant.PRIMARY);
        btnProfilo    = UISettings.createButton("Profilo", UISettings.ButtonVariant.SECONDARY);
        btnClassifica = UISettings.createButton("Classifica", UISettings.ButtonVariant.SECONDARY);
        btnEsci       = UISettings.createButton("Esci", UISettings.ButtonVariant.TERTIARY);

        stack.add(btnPartita1v1);
        stack.add(btnPartita2v2);
        stack.add(btnProfilo);
        stack.add(btnClassifica);
        stack.add(btnEsci);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(UIConstants.GAP, UIConstants.GAP, UIConstants.GAP, UIConstants.GAP);
        center.add(stack, gbc);

        add(center, BorderLayout.CENTER);
        center.setBackground(UIConstants.BACKGROUND_PANEL);

        // Azioni
        btnPartita1v1.addActionListener(e -> {
            AudioManager.getInstance().stop();
            AudioManager.getInstance().play("audio/start.wav");
            gameEngine.avviaNuovaPartita(2);
        });
        btnPartita2v2.addActionListener(e -> {
            AudioManager.getInstance().stop();
            AudioManager.getInstance().play("audio/start.wav");
            gameEngine.avviaNuovaPartita(4);
        });
        btnProfilo.addActionListener(e -> {
            AudioManager.getInstance().play("audio/button.wav");
            gameEngine.visualizzaProfilo();
        });
        btnClassifica.addActionListener(e -> {
            AudioManager.getInstance().play("audio/button.wav");
            gameEngine.visualizzaStatistiche();
        });
        btnEsci.addActionListener(e -> {
            AudioManager.getInstance().play("audio/button.wav");
            System.exit(0);
        });
    }

    
    
}
