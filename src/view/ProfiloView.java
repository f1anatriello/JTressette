package view;

import controller.GameEngine;
import data.UtentePojo;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.BorderFactory;

import java.awt.Image;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;

import ui.UIAssets;
import ui.UIConstants;
import ui.UISettings;

/**
 * Pannello Swing che visualizza le informazioni del profilo di un utente.
 * <p>
 * Questo componente mostra dettagli come il nickname dell'utente, il suo avatar
 * e le statistiche di gioco (partite giocate, vinte, perse e livello).
 * Dispone gli elementi verticalmente e include un pulsante per tornare al menu principale.
 */
public class ProfiloView extends JPanel {
    private GameEngine controller;

    private JLabel lblNickname;
    private JLabel lblAvatar;
    private JLabel lblStatistiche;
    private JButton backButton;
    private JButton modificaProfiloButton;
    private JButton cambiaProfiloButton;

    /**
     * Costruisce il pannello del profilo.
     * <p>
     * Inizializza i componenti grafici (etichette per i dati e pulsante di ritorno)
     * e li organizza utilizzando un {@link javax.swing.BoxLayout}.
     *
     * @param controller Il {@link GameController} a cui notificare l'azione del pulsante di ritorno.
     */
    public ProfiloView(GameEngine controller) {
        this.controller = controller; 
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(UIConstants.BACKGROUND_PANEL);
        setBorder(UIConstants.BORDER_TITLE_PANEL);

        setAlignmentX(Component.CENTER_ALIGNMENT);
        setAlignmentY(Component.CENTER_ALIGNMENT);

        // Etichetta per il nickname
        lblNickname = new JLabel("Nickname: ");
        lblNickname.setFont(UIConstants.FONT_SUB_TITLE);
        lblNickname.setForeground(UIConstants.ACCENT_COLOR);
        lblNickname.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Etichetta per visualizzare l'immagine dell'avatar
        lblAvatar = new JLabel();
        lblAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Etichetta per le statistiche di gioco
        lblStatistiche = new JLabel("Partite Giocate: 0 | Vinte: 0 | Perse: 0 | Livello: 0");
        lblStatistiche.setFont(UIConstants.FONT_REGULAR);
        lblStatistiche.setForeground(UIConstants.TEXT_COLOR);
        lblStatistiche.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Aggiunge i componenti al pannello con spaziature verticali
        add(Box.createVerticalGlue());
        add(lblNickname);
        add(Box.createVerticalStrut(20));
        add(lblAvatar);
        add(Box.createVerticalStrut(20));
        add(lblStatistiche);
        add(Box.createVerticalGlue());

        // Pulsanti per modifica e cambio profilo
        modificaProfiloButton = new JButton("Modifica Profilo");
        cambiaProfiloButton = new JButton("Cambia Profilo");
        UISettings.applyButtonStyle(modificaProfiloButton);
        UISettings.applyButtonStyle(cambiaProfiloButton);

        JPanel actionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        actionButtonsPanel.setOpaque(false);
        actionButtonsPanel.add(modificaProfiloButton);
        actionButtonsPanel.add(cambiaProfiloButton);
        actionButtonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        add(actionButtonsPanel);
        add(Box.createVerticalStrut(15));
        
        // Listener per i nuovi pulsanti
        modificaProfiloButton.addActionListener(e -> controller.avviaModificaProfilo());
        cambiaProfiloButton.addActionListener(e -> controller.avviaCambioProfilo());

        // Pulsante per tornare al menu
        backButton = new JButton("Torna al Menu");
        UISettings.applyButtonStyle(backButton);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(backButton);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(buttonPanel);
        add(Box.createVerticalStrut(20));
        
        // Associa l'azione di ritorno al menu al click del pulsante
        backButton.addActionListener(e -> controller.visualizzaMenu());
    }

    /**
     * Aggiorna i componenti del pannello con i dati di un profilo utente specifico.
     *
     * @param profilo L'oggetto {@link model.ProfiloUtente} da cui leggere i dati.
     *                Se nullo, il pannello mostrerà uno stato di "nessun profilo".
     */
    public void mostraProfile(UtentePojo profilo) {
        if (profilo != null) {
            lblNickname.setText("Nickname: " + profilo.getUsername());
            
            // Carica e imposta l'immagine dell'avatar
            if (profilo.getAvatarPath() != null && !profilo.getAvatarPath().isEmpty()) {
                Image avatarImg = UIAssets.getInstance().getImmagineAvatar(profilo);
                if (avatarImg != null) {
                    // Ridimensiona l'immagine per adattarla alla UI
                    lblAvatar.setIcon(new ImageIcon(avatarImg.getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
                    lblAvatar.setText("");
                } else {
                    lblAvatar.setIcon(null);
                    lblAvatar.setText("[Avatar non trovato]");
                }
            } else {
                lblAvatar.setIcon(null);
                lblAvatar.setText("[Nessun Avatar]");
            }

            // Formatta e imposta la stringa delle statistiche
            lblStatistiche.setText(String.format("Partite Giocate: %d | Vinte: %d | Perse: %d",
                    profilo.getPartiteGiocate(), profilo.getPartiteVinte(), profilo.getPartitePerse()));
        } else {
            // Stato di fallback se non viene fornito un profilo
            lblNickname.setText("Nessun profilo caricato.");
            lblAvatar.setIcon(null);
            lblStatistiche.setText("");
        }
        revalidate();
        repaint();
    }


}
