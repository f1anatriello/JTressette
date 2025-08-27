package view;

import controller.GameEngine;
import data.UtentePojo;
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import ui.UIConstants;
import ui.UISettings;

public class ClassificaView extends JPanel {
    private final JLabel titolo;
    private final JTextArea classificaArea;
    private final JButton btnMenu;

    public ClassificaView(GameEngine gameEngine) {
        setLayout(new BorderLayout(50, 50));
        setBackground(UIConstants.BACKGROUND_PANEL);
        setBorder(BorderFactory.createEmptyBorder(70, 70, 70, 70));

        titolo = new JLabel("Classifica");
        titolo.setFont(UIConstants.FONT_MAIN_TITLE);
        titolo.setForeground(UIConstants.TITLE_COLOR);
        add(titolo, BorderLayout.NORTH);

        classificaArea = new JTextArea(20, 40);
        classificaArea.setEditable(false);
        classificaArea.setFont(UIConstants.FONT_MONOSPACE);
        classificaArea.setBackground(UIConstants.ACCENT_YELLOW);    
        classificaArea.setForeground(UIConstants.TEXT_COLOR);
        classificaArea.setBorder(BorderFactory.createLineBorder(UIConstants.BACKGROUND_PANEL, 2));

        JScrollPane scroll = new JScrollPane(classificaArea);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        add(scroll, BorderLayout.CENTER);

        btnMenu = new JButton("Torna al Menù");
        UISettings.applyButtonStyle(btnMenu, UISettings.ButtonVariant.TERTIARY);
        JPanel south = new JPanel();
        south.setOpaque(false);
        south.add(btnMenu);
        add(south, BorderLayout.SOUTH);
        btnMenu.addActionListener(e -> gameEngine.visualizzaMenu());
        
    }

    /**
     * Carica e mostra la classifica prendendo i dati dalla classe UserRepository
     */
    public void mostraClassifica(List<UtentePojo> classifica) {
    	if (classifica == null || classifica.isEmpty()) {
            classificaArea.append("Nessun dato di classifica disponibile.\n");
            return;
        }

        StringBuilder sb = new StringBuilder("Rank. |    Nickname    | P.Giocate | P.Vinte | P.Perse\n");
        sb.append("------------------------------------------------------\n");
        int rank = 0;
        for (UtentePojo p : classifica) {
        	rank++;
            sb.append(String.format("%-5d | %-14s | %-9d | %-7d | %-7"
            		+ "d\n",
                rank, p.getUsername(), p.getPartiteGiocate(), p.getPartiteVinte(), p.getPartitePerse()));
        }
        classificaArea.setText(sb.toString());
    }

}