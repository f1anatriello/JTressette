package view;

import controller.GameEngine;
import data.*;
import java.awt.image.BufferedImage;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.WindowConstants;

import ui.AudioManager;
import ui.UIAssets;
import ui.UIConstants;

/**
 * Dialog modale per modificare il profilo dell'utente.
 * Permette di selezionare un avatar tra quelli disponibili.
 * @author 1957447
 */
public class DialogModificaProfilo {
    private final GameEngine controller;
    private final JDialog dialog;

    /**
     * Costruttore che inizializza il dialog per modificare il profilo.
     * @param controller Il controller del gioco per accedere al giocatore corrente.
     */
    public DialogModificaProfilo(GameEngine controller) {
        this.controller = controller;

        // Mostra il dialog per modificare il profilo
        dialog = new JDialog();
        dialog.setTitle("Modifica Profilo");
        dialog.setModal(true);
        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }

    /**
     * Mostra il dialog per selezionare un avatar.
     * Quando un avatar viene selezionato, aggiorna il profilo del giocatore e chiude il dialog.
     */
    public void showDialog() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel label = new JLabel("Seleziona il tuo Avatar:", JLabel.CENTER);
        label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        panel.add(label);

        JPanel avatarsPanel = new JPanel();
        avatarsPanel.setLayout(new java.awt.GridLayout(2, 3, 10, 10)); // 2 righe, 3 colonne
        // ButtonGroup per permettere la selezione singola
        javax.swing.ButtonGroup avatarGroup = new javax.swing.ButtonGroup();

        Map<BufferedImage, String> avatarImages = UIAssets.getInstance().getAllAvatar();
        
        for (BufferedImage avatarImg : avatarImages.keySet()) {
            // Ridimensiona l'immagine a 48x48 pixel
            ImageIcon icon = new ImageIcon(
                avatarImg.getScaledInstance(48, 48, java.awt.Image.SCALE_SMOOTH)
            );
            JRadioButton avatarButton = new JRadioButton(icon);
            avatarGroup.add(avatarButton);
        }

        panel.add(avatarsPanel);
        for (Map.Entry<BufferedImage, String> entry : avatarImages.entrySet()) {
            BufferedImage avatarImg = entry.getKey();
            String avatarPath = entry.getValue();
            ImageIcon icon = new ImageIcon(
                avatarImg.getScaledInstance(48, 48, java.awt.Image.SCALE_SMOOTH)
            );
            JRadioButton avatarButton = new JRadioButton(icon);
            avatarButton.setCursor(UIConstants.CURSOR_HAND);
            avatarButton.addActionListener(e -> {
                AudioManager.getInstance().play("audio/avatar.wav");
                controller.getPlayer().setAvatarPath(avatarPath);
                dialog.dispose();
                controller.visualizzaProfilo();
                UtentePojo utente = controller.getPlayer();
                UserRepository.getInstance().aggiornaAvatar(utente.getUsername(), avatarPath);
            });
            avatarsPanel.add(avatarButton);
        }
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(null); // Centra il dialog
        dialog.setVisible(true);
    }
}
