package view;

import controller.GameEngine;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;
import model.Carta; // Ensure this is the correct package for AudioManager
import ui.AudioManager;
import ui.UIConstants;

/**
 * View per la modalità 1v1 (giocatore umano vs AI).
 * Implementa il pattern Singleton: usare {@link #getInstance(GameEngine, String, ImageIcon, String, ImageIcon, List, List)} per ottenere l'istanza.
 * Usa {@link HelperGrafico} come canvas centrale per disegnare le carte.
 * Comunica con il controller tramite {@link controller.GiocatoreUmano#notificaCartaScelta(Carta)}.
 * @author Francesco
 */
public class GameView1v1 extends JPanel {

    private static GameView1v1 instance = null;

    private String player1Name;
    private ImageIcon player1Avatar;
    private String player2Name;
    private ImageIcon player2Avatar;

    private List<Carta> player1Hand;
    private List<Carta> player2Hand;

    private GameEngine gameEngine;

    private JPanel root;        // BorderLayout
    private JPanel topHUD;      // avatar/nome avversario
    private JPanel bottomHUD;   // avatar/nome giocatore + pulsanti

    private HelperGrafico gameSupp;   // canvas centrale

    private List<Carta> terreno;    // terreno di gioco

    private JLabel score1Label;
    private JLabel score2Label;
    private JPanel leftTop;

    private JButton btnGioca;
    private boolean playLocked = false; // per evitare doppi click sul bottone Gioca

    // --- Avatar labels per bordo/glow ---
    private JLabel avatar1Label; // bottom (umano)
    private JLabel avatar2Label; // top (AI)

    /**
     * Singleton: restituisce l'istanza esistente o ne crea una nuova.
     * Se si crea una nuova istanza, è necessario passare tutti i parametri.
     * @param gameEngine
     * @param player1Name
     * @param player1Avatar
     * @param player2Name
     * @param player2Avatar
     * @param player1Hand
     * @param player2Hand
     * @return l'istanza singleton di GameView1v1
     */
    public static GameView1v1 getInstance(
            GameEngine gameEngine,
            String player1Name, ImageIcon player1Avatar,
            String player2Name, ImageIcon player2Avatar,
            List<Carta> player1Hand, List<Carta> player2Hand) {

        if (instance == null) {
            instance = new GameView1v1(
                    gameEngine,
                    player1Name, player1Avatar,
                    player2Name, player2Avatar, player1Hand, player2Hand);
        }
        return instance;
    }

    /**
     * Costruttore privato (singleton).
     */
    private GameView1v1(
            GameEngine gameEngine,
            String player1Name, ImageIcon player1Avatar,
            String player2Name, ImageIcon player2Avatar,
            List<Carta> player1Hand, List<Carta> player2Hand) {

        this.gameEngine = gameEngine;

        this.player1Name = player1Name;
        this.player1Avatar = player1Avatar;

        this.player2Name = player2Name;
        this.player2Avatar = player2Avatar;

        this.player1Hand = player1Hand;
        this.player2Hand = player2Hand;

        this.score1Label = new JLabel();
        this.score2Label = new JLabel();

        leftTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        leftTop.setOpaque(false);
        leftTop.add(score2Label);

        setLayout(new BorderLayout());
        setBackground(UIConstants.BACKGROUND_GREEN);

        buildUI();
        render(); // primo disegno
    }

    /* ================= UI ================= */

    /**
     * Costruisce l'interfaccia grafica.
     * Usa BorderLayout con:
     * - NORTH: topHUD (avatar/nome avversario + bottone menù)
     * - SOUTH: bottomHUD (avatar/nome giocatore + bottone gioca)
     * - CENTER: HelperGrafico (canvas carte)
     */
    private void buildUI() {
        // root con BorderLayout
        root = new JPanel(new BorderLayout());
        root.setBackground(UIConstants.BACKGROUND_GREEN);
        add(root, BorderLayout.CENTER);

        // --- TOP HUD: avversario (sx) + bottone Menù (dx) ---
        topHUD = new JPanel(new BorderLayout(8, 8));
        topHUD.setOpaque(false);

        // sinistra: avatar + nome avversario
        avatar2Label = new JLabel(scale(player2Avatar, 60, 60));
        JLabel name2 = new JLabel(player2Name, SwingConstants.LEFT);
        name2.setFont(name2.getFont().deriveFont(Font.BOLD, 14f));
        score2Label = new JLabel(" - Punti: " + Math.round(gameEngine.getGiocatoreAI().getPunti()));
        leftTop.add(avatar2Label);
        leftTop.add(name2);
      

        // destra: bottone Menù
        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        rightTop.setOpaque(false);
        JButton btnMenu = new JButton("⬅ Torna al Menù");

        btnMenu.addActionListener(e -> {
            AudioManager.getInstance().stop();
            AudioManager.getInstance().play("audio/button.wav");
            int scl = JOptionPane.showConfirmDialog(
                    this,
                    "Tornare al menù principale?\nLa partita in corso andrà persa.",
                    "Abbandonare la nave..",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (scl == JOptionPane.YES_OPTION) {
                AudioManager.getInstance().play("audio/button.wav");
                try {
                    gameEngine.getGiocatoreUmano().setOnCartaSceltaListener(null);
                } catch (Exception ignore) {}
                GameView1v1.disposeInstance(); // azzera il singleton
                gameEngine.visualizzaMenu(); // torna al menu
                gameEngine.reset();
            } else {
                AudioManager.getInstance().play("audio/button.wav");
            }
        });
        rightTop.add(btnMenu);

        topHUD.add(leftTop, BorderLayout.WEST);
        topHUD.add(rightTop, BorderLayout.EAST);
        root.add(topHUD, BorderLayout.NORTH);

        // --- BOTTOM HUD: giocatore ---
        bottomHUD = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        bottomHUD.setOpaque(false);
        avatar1Label = new JLabel(scale(player1Avatar, 60, 60));
        JLabel name1 = new JLabel(player1Name, SwingConstants.LEFT);
        name1.setFont(name1.getFont().deriveFont(Font.BOLD, 14f));
        score1Label = new JLabel(" - Punti: " + Math.round(gameEngine.getGiocatoreUmano().getPunti()));
        btnGioca = new JButton("Gioca");
        btnGioca.addActionListener(e -> giocaCartaSelezionata());
        bottomHUD.add(avatar1Label);
        bottomHUD.add(name1);
        bottomHUD.add(Box.createHorizontalStrut(16));
        bottomHUD.add(btnGioca);
        bottomHUD.add(score1Label);
        root.add(bottomHUD, BorderLayout.SOUTH);

        // --- CENTER: HelperGrafico (canvas carte) ---
        gameSupp = new HelperGrafico(player1Hand, player2Hand);
        root.add(gameSupp, BorderLayout.CENTER);

        // terreno iniziale vuoto
        gameSupp.setTerreno(new ArrayList<>());
    }

    /**
    * Ridimensiona un'icona immagine alle dimensioni specificate.
    */
    private ImageIcon scale(ImageIcon icon, int w, int h) {
        if (icon == null || icon.getImage() == null) return icon;
        Image scaled = icon.getImage().getScaledInstance(w, h, Image.SCALE_AREA_AVERAGING);
        return new ImageIcon(scaled);
    }

    /* ============== HIGHLIGHTER ============== */

    /** Evidenzia con un bordo l'avatar del giocatore corrente (per nome). */
    public void highlightAvatar(String name) {
        boolean p1 = name != null && name.equalsIgnoreCase(player1Name);
        boolean p2 = name != null && name.equalsIgnoreCase(player2Name);
        Border hi = BorderFactory.createLineBorder(Color.CYAN, 2, true);

        if (avatar1Label != null) avatar1Label.setBorder(p1 ? hi : null);
        if (avatar2Label != null) avatar2Label.setBorder(p2 ? hi : null);
    }

    /** Rimuove qualsiasi evidenziazione. */
    public void clearHighlight() {
        if (avatar1Label != null) avatar1Label.setBorder(null);
        if (avatar2Label != null) avatar2Label.setBorder(null);
    }

    /* ============== RENDER/UPDATE ============== */

    /** Ridisegna l'interfaccia grafica. */
    public void render() {
        gameSupp.setHands(player1Hand, player2Hand);
        gameSupp.setTerreno(terreno);
        gameSupp.refresh();

        // aggiorna i punteggi
        score1Label.setText(" - Punti: " + Math.round(gameEngine.getGiocatoreUmano().getPunti()));
        score2Label.setText(" - Punti: " + Math.round(gameEngine.getGiocatoreAI().getPunti()));

        // dopo il refresh assicuriamo una selezione valida
        SwingUtilities.invokeLater(() -> gameSupp.selectFirstIfNone());
    }

    /** Aggiorna le mani e ridisegna. */
    public void refreshHands(List<Carta> newP1, List<Carta> newP2) {
        this.player1Hand = newP1;
        this.player2Hand = newP2;
        render();
    }

    /** Imposta la lista di carte sul terreno e ridisegna. */
    public void setTerreno(List<Carta> terreno) {
        this.terreno = terreno;
        render();
    }

    /** Compatibilità: aggiorna il terreno e ridisegna. */
    public void refreshTerreno(List<Carta> terreno) {
        this.terreno = terreno;
        render();
    }

    /** Azzera l'istanza singleton. */
    public static void disposeInstance() {
        instance = null;
    }

    /* ============== Azioni ============== */

    /** Gestisce il click su "Gioca" notificando il controller. */
    private void giocaCartaSelezionata() {
        // Debounce: se è già stato premuto, ignora
        if (playLocked) return;

        Carta sel = gameSupp.getSelected();
        if (sel == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Seleziona una carta prima di giocare.",
                    "Nessuna carta selezionata",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        // blocca il pulsante Gioca finché non riceviamo l'aggiornamento dal controller
        playLocked = true;

        // notifico al GiocatoreUmano (vero) che l’utente ha scelto questa carta
        gameEngine.getGiocatoreUmano().notificaCartaScelta(sel);
    }

    /**
     * Sblocca il pulsante "Gioca" dopo che il controller ha processato la mossa.
     */
    public void unlockPlay() {
        playLocked = false;
        if (btnGioca != null) btnGioca.setEnabled(true);
    }
}
