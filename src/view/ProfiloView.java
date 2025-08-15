package view;

import controller.GameEngine;
import data.UtentePojo;
import ui.UIAssets;
import ui.UIConstants;
import ui.UISettings;

import javax.swing.*;
import java.awt.*;

/**
 * Mostra le informazioni del profilo dell'utente loggato.
 */
public class ProfiloView extends JPanel {

    private final GameEngine controller;
    private final JLabel lblNickname;
    private final JLabel lblAvatar;
    private final JLabel lblStatistiche;
    private final JButton btnModifica;

    public ProfiloView(GameEngine controller) {
        this.controller = controller;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(UIConstants.BACKGROUND_PANEL);
        setBorder(UIConstants.BORDER_TITLE_PANEL);

        // Nickname
        lblNickname = new JLabel();
        lblNickname.setFont(UIConstants.FONT_SUB_TITLE);
        lblNickname.setForeground(UIConstants.ACCENT_COLOR);
        lblNickname.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Avatar
        lblAvatar = new JLabel();
        lblAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Statistiche
        lblStatistiche = new JLabel();
        lblStatistiche.setFont(UIConstants.FONT_REGULAR);
        lblStatistiche.setForeground(UIConstants.TEXT_COLOR);
        lblStatistiche.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Pulsante modifica profilo
        btnModifica = new JButton("Modifica profilo");
        UISettings.applyButtonStyle(btnModifica);
        btnModifica.setAlignmentX(Component.CENTER_ALIGNMENT);
        // btnModifica.addActionListener(e -> {
           // new DialogModificaProfilo(SwingUtilities.getWindowAncestor(this), controller).setVisible(true);
        // });

        // Layout
        add(Box.createVerticalGlue());
        add(lblNickname);
        add(Box.createVerticalStrut(20));
        add(lblAvatar);
        add(Box.createVerticalStrut(20));
        add(lblStatistiche);
        add(Box.createVerticalStrut(20));
        add(btnModifica);
        add(Box.createVerticalGlue());
    }

    public void mostraProfilo(UtentePojo utente) {
        if (utente != null) {
            lblNickname.setText("Nickname: " + utente.getUsername());

            // Avatar
            if (utente.getAvatarPath() != null && !utente.getAvatarPath().isEmpty()) {
                Image avatarImg = UIAssets.getInstance().getImmagineAvatar(utente);
                lblAvatar.setIcon(avatarImg != null ? new ImageIcon(avatarImg.getScaledInstance(100, 100, Image.SCALE_SMOOTH)) : null);
                lblAvatar.setText(avatarImg == null ? "[Avatar non trovato]" : "");
            } else {
                lblAvatar.setIcon(null);
                lblAvatar.setText("[Nessun Avatar]");
            }

            // Statistiche
            lblStatistiche.setText(String.format("Giocate: %d | Vinte: %d | Perse: %d",
                    utente.getPartiteGiocate(),
                    utente.getPartiteVinte(),
                    utente.getPartitePerse()));
        } else {
            lblNickname.setText("Nessun profilo caricato");
            lblAvatar.setIcon(null);
            lblAvatar.setText("");
            lblStatistiche.setText("");
        }
    }
}
