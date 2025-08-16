package view;

import controller.GameEngine;
import data.UtentePojo;
import ui.UIAssets;
import ui.UIConstants;
import ui.UISettings;

import javax.swing.*;
import java.awt.*;

/**
 * Mostra le informazioni del profilo dell'utente loggato (versione BorderLayout),
 * usando UISettings per lo stile e le dimensioni dei bottoni.
 */
public class ProfiloView extends JPanel {

    private final GameEngine controller;

    private final JLabel lblNickname;
    private final JLabel lblAvatar;
    private final JLabel lblStatistiche;
    private final JButton btnModifica;
    private final JButton btnMenu;

    public ProfiloView(GameEngine controller) {
        this.controller = controller;

        // Layout e look & feel base
        setLayout(new BorderLayout());
        setBackground(UIConstants.BACKGROUND_PANEL);
        setBorder(UIConstants.BORDER_TITLE_PANEL);

        // ====== HEADER (NORTH): Nickname ======
        lblNickname = new JLabel("", SwingConstants.CENTER);
        lblNickname.setFont(UIConstants.FONT_SUB_TITLE);
        lblNickname.setForeground(UIConstants.ACCENT_COLOR);
        // padding contenuto; se hai costanti in UISettings per gli spazi, puoi sostituirle qui
        lblNickname.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));
        add(lblNickname, BorderLayout.NORTH);

        // ====== CENTER: Avatar + Statistiche (colonna centrata) ======
        lblAvatar = new JLabel("", SwingConstants.CENTER);

        lblStatistiche = new JLabel("", SwingConstants.CENTER);
        lblStatistiche.setFont(UIConstants.FONT_REGULAR);
        lblStatistiche.setForeground(UIConstants.TEXT_COLOR);

        JPanel centerWrapper = new JPanel(new GridBagLayout()); // per centrare la colonna
        centerWrapper.setOpaque(false);

        JPanel centerColumn = new JPanel();
        centerColumn.setOpaque(false);
        centerColumn.setLayout(new BoxLayout(centerColumn, BoxLayout.Y_AXIS));
        centerColumn.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        lblAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblStatistiche.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerColumn.add(Box.createVerticalGlue());
        centerColumn.add(lblAvatar);
        centerColumn.add(Box.createVerticalStrut(16));
        centerColumn.add(lblStatistiche);
        centerColumn.add(Box.createVerticalGlue());

        centerWrapper.add(centerColumn, new GridBagConstraints());
        add(centerWrapper, BorderLayout.CENTER);

        // ====== FOOTER (SOUTH): Bottoni in colonna, dimensioni derivate da UISettings ======
        btnModifica = UISettings.createButton("Modifica Profilo", UISettings.ButtonVariant.PRIMARY);
        btnMenu     = UISettings.createButton("Torna al Menù", UISettings.ButtonVariant.TERTIARY);

        // Eventi
        // btnModifica.addActionListener(e ->
        //     new DialogModificaProfilo(SwingUtilities.getWindowAncestor(this), controller).setVisible(true)
        // );
        btnMenu.addActionListener(e -> controller.visualizzaMenu());

        // Wrapper centrato per la colonna di bottoni
        JPanel southWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        southWrapper.setOpaque(false);

        // Colonna verticale con gap costante
        JPanel buttonColumn = new JPanel(new GridLayout(0, 1, 0, 12));
        buttonColumn.setOpaque(false);

        // >>> Usa le preferredSize generate da UISettings e uniforma la larghezza
        uniformaLarghezzaBottoni(btnModifica, btnMenu);

        buttonColumn.add(btnModifica);
        buttonColumn.add(btnMenu);

        southWrapper.add(buttonColumn);
        add(southWrapper, BorderLayout.SOUTH);
    }

    /**
     * Rende i bottoni larghi uguali prendendo come riferimento la massima preferred width
     * calcolata dai componenti creati con UISettings.
     */
    private void uniformaLarghezzaBottoni(JButton... buttons) {
        int maxWidth = 0;
        int[] heights = new int[buttons.length];

        for (int i = 0; i < buttons.length; i++) {
            Dimension d = buttons[i].getPreferredSize(); // determinata da UISettings
            maxWidth = Math.max(maxWidth, d.width);
            heights[i] = d.height; // rispetta l'altezza suggerita da UISettings per ciascun bottone
        }

        for (int i = 0; i < buttons.length; i++) {
            JButton b = buttons[i];
            Dimension d = new Dimension(maxWidth, heights[i]);
            b.setPreferredSize(d);
            b.setMinimumSize(d);
            b.setMaximumSize(new Dimension(maxWidth, Integer.MAX_VALUE));
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
        }
    }

    /**
     * Aggiorna la vista con i dati dell'utente.
     */
    public void mostraProfilo(UtentePojo utente) {
        if (utente != null) {
            lblNickname.setText("Nickname: " + utente.getUsername());

            // Avatar
            if (utente.getAvatarPath() != null && !utente.getAvatarPath().isEmpty()) {
                Image avatarImg = UIAssets.getInstance().getImmagineAvatar(utente);
                if (avatarImg != null) {
                    Image scaled = avatarImg.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                    lblAvatar.setIcon(new ImageIcon(scaled));
                    lblAvatar.setText("");
                } else {
                    lblAvatar.setIcon(null);
                    lblAvatar.setText("[Avatar non trovato]");
                }
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
