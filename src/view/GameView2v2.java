package view;

import controller.GameEngine;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;
import model.Carta;
import model.Giocatore;
import ui.AudioManager;
import ui.UIConstants;

/**
 * View per la modalità 2v2.
 * Implementa il pattern Singleton: usare {@link #getInstance(GameEngine, String, ImageIcon, String, ImageIcon, List, List)} per ottenere l'istanza.
 * Usa {@link HelperGrafico} come canvas centrale per disegnare le carte.
 * Comunica con il controller tramite {@link controller.GiocatoreUmano#notificaCartaScelta(Carta)}.
 * 
 * Mapping GameEngine (2v2):
 * - Sud  -> getGiocatoreUmano()
 * - Nord -> getGiocatoreAI()     (campo 'ai' nel GameEngine)
 * - Est  -> getGiocatoreEst()
 * - Ovest-> getGiocatoreOvest()
 */
public class GameView2v2 extends JPanel {

    private static GameView2v2 instance = null;

    private String playerSouthName;
    private ImageIcon playerSouthAvatar;
    private List<Carta> playerSouthHand;

    private String playerNorthName;
    private ImageIcon playerNorthAvatar;
    private List<Carta> playerNorthHand;

    private String playerEastName;
    private ImageIcon playerEastAvatar;
    private List<Carta> playerEastHand;

    private String playerWestName;
    private ImageIcon playerWestAvatar;
    private List<Carta> playerWestHand;

    private GameEngine gameEngine;

    private JPanel root;
    private JPanel topHUD;
    private JPanel bottomHUD;
    private JPanel leftHUD;
    private JPanel rightHUD;

    private HelperGrafico gameSupp;
    private List<Carta> terreno;

    private JLabel scoreSouthLabel;
    private JLabel scoreNorthLabel;
    private JLabel scoreEastLabel;
    private JLabel scoreWestLabel;

    private JButton btnGioca; 
    private boolean playLocked = false; // per evitare doppi click sul bottone Gioca

    // --- Avatar labels per bordo/glow ---
    private JLabel avatarSLabel; // South (umano)
    private JLabel avatarNLabel; // North (AI)
    private JLabel avatarELabel; // East  (AI)
    private JLabel avatarWLabel; // West  (AI)


    /**
     * Restituisce l'istanza singleton di GameView2v2, creandola se non esiste.
     */
    public static GameView2v2 getInstance(
            GameEngine gameEngine,
            String southName, ImageIcon southAvatar, List<Carta> southHand,
            String northName, ImageIcon northAvatar, List<Carta> northHand,
            String eastName,  ImageIcon eastAvatar,  List<Carta> eastHand,
            String westName,  ImageIcon westAvatar,  List<Carta> westHand) {

        if (instance == null) {
            instance = new GameView2v2(
                gameEngine,
                southName, southAvatar, southHand,
                northName, northAvatar, northHand,
                eastName, eastAvatar, eastHand,
                westName, westAvatar, westHand
            );
        }
        return instance;
    }

    /**
     * Costruttore privato per il pattern Singleton.
     */
    private GameView2v2(
            GameEngine gameEngine,
            String southName, ImageIcon southAvatar, List<Carta> southHand,
            String northName, ImageIcon northAvatar, List<Carta> northHand,
            String eastName,  ImageIcon eastAvatar,  List<Carta> eastHand,
            String westName,  ImageIcon westAvatar,  List<Carta> westHand) {

        this.gameEngine = gameEngine;

        this.playerSouthName = southName;
        this.playerSouthAvatar = southAvatar;
        this.playerSouthHand = southHand;

        this.playerNorthName = northName;
        this.playerNorthAvatar = northAvatar;
        this.playerNorthHand = northHand;

        this.playerEastName = eastName;
        this.playerEastAvatar = eastAvatar;
        this.playerEastHand = eastHand;

        this.playerWestName = westName;
        this.playerWestAvatar = westAvatar;
        this.playerWestHand = westHand;

        this.terreno = new ArrayList<>();

        setLayout(new BorderLayout());
        setBackground(UIConstants.BACKGROUND_GREEN);

        buildUI();
        render();
    }

    /* ================= UI ================= */

    /**
     * Costruisce l'interfaccia utente.
     * Usa BorderLayout con:
     * - NORTH: info giocatore Nord (AI)
     * - SOUTH: info giocatore Sud (umano)
     * - WEST: info giocatore Ovest (AI)
     * - EAST: info giocatore Est (AI)
     * - CENTER: {@link HelperGrafico} per disegnare carte e terreno
     */
    private void buildUI() {
        root = new JPanel(new BorderLayout());
        root.setBackground(UIConstants.BACKGROUND_GREEN);

        add(root, BorderLayout.CENTER);

        // --- TOP HUD (NORD) ---
        topHUD = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        topHUD.setOpaque(false);
        avatarNLabel = new JLabel(scale(playerNorthAvatar, 50, 50));
        JLabel nameN = new JLabel(playerNorthName);
        scoreNorthLabel = new JLabel(" - Punti: " + Math.round(gameEngine.getGiocatoreAI().getPunti()));
        JLabel squadLabel = new JLabel(" - " + gameEngine.getGiocatoreAI().getSquadra().getNome());

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
                AudioManager.getInstance().stop();
                AudioManager.getInstance().play("audio/button.wav");
                try {
                    gameEngine.getGiocatoreUmano().setOnCartaSceltaListener(null);
                } catch (Exception ignore) {}
                GameView2v2.disposeInstance(); // azzera il singleton
                gameEngine.visualizzaMenu();
                gameEngine.reset();
            } else {
                AudioManager.getInstance().play("audio/button.wav");
            }
        });
        topHUD.add(Box.createHorizontalStrut(200));
        topHUD.add(avatarNLabel);
        topHUD.add(nameN);
        topHUD.add(scoreNorthLabel);
        topHUD.add(squadLabel);
        topHUD.add(Box.createHorizontalStrut(200));
        topHUD.add(btnMenu);

        root.add(topHUD, BorderLayout.NORTH);

        // --- BOTTOM HUD (SUD, umano) ---
        bottomHUD = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        bottomHUD.setOpaque(false);
        avatarSLabel = new JLabel(scale(playerSouthAvatar, 60, 60));
        JLabel nameS = new JLabel(playerSouthName);
        scoreSouthLabel = new JLabel(" - Punti: " + Math.round(gameEngine.getGiocatoreUmano().getPunti()));
        JLabel squadLabelBot = new JLabel(" - " + gameEngine.getGiocatoreUmano().getSquadra().getNome());
        btnGioca = new JButton("Gioca");
        btnGioca.addActionListener(e -> giocaCartaSelezionata());

        bottomHUD.add(Box.createHorizontalStrut(200));
        bottomHUD.add(avatarSLabel);
        bottomHUD.add(nameS);
        bottomHUD.add(btnGioca);
        bottomHUD.add(scoreSouthLabel);
        bottomHUD.add(squadLabelBot);
        root.add(bottomHUD, BorderLayout.SOUTH);

        // --- LEFT HUD (OVEST) ---
        leftHUD = new JPanel();
        leftHUD.setOpaque(false);
        leftHUD.setLayout(new BoxLayout(leftHUD, BoxLayout.Y_AXIS));
        leftHUD.setBorder(BorderFactory.createEmptyBorder(150, 0, 0, 0));

        avatarWLabel = new JLabel(scale(playerWestAvatar, 50, 50));
        avatarWLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel nameW = new JLabel(playerWestName);
        nameW.setAlignmentX(Component.CENTER_ALIGNMENT);
        scoreWestLabel = new JLabel(" - Punti: " + Math.round(gameEngine.getGiocatoreOvest().getPunti()));
        scoreWestLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel squadLabelLeft = new JLabel(" - " + gameEngine.getGiocatoreOvest().getSquadra().getNome());
        squadLabelLeft.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftHUD.add(Box.createVerticalStrut(50));
        leftHUD.add(avatarWLabel);
        leftHUD.add(Box.createVerticalStrut(8));
        leftHUD.add(nameW);
        leftHUD.add(Box.createVerticalStrut(8));
        leftHUD.add(scoreWestLabel);
        leftHUD.add(squadLabelLeft);
        root.add(leftHUD, BorderLayout.WEST);

        // --- RIGHT HUD (EST) ---
        rightHUD = new JPanel();
        rightHUD.setLayout(new BoxLayout(rightHUD, BoxLayout.Y_AXIS));
        rightHUD.setOpaque(false);
        rightHUD.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0)); // meno spazio dall'alto

        rightHUD.add(Box.createVerticalStrut(150));
        avatarELabel = new JLabel(scale(playerEastAvatar, 50, 50));
        avatarELabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel nameE = new JLabel(playerEastName);
        nameE.setAlignmentX(Component.CENTER_ALIGNMENT);
        scoreEastLabel = new JLabel(" - Punti: " + Math.round(gameEngine.getGiocatoreEst().getPunti()));
        scoreEastLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel squadLabelRight = new JLabel(" - " + gameEngine.getGiocatoreEst().getSquadra().getNome());
        squadLabelRight.setAlignmentX(Component.CENTER_ALIGNMENT);

        rightHUD.add(Box.createVerticalStrut(12));
        rightHUD.add(avatarELabel);
        rightHUD.add(Box.createVerticalStrut(8));
        rightHUD.add(nameE);
        rightHUD.add(Box.createVerticalStrut(8));
        rightHUD.add(scoreEastLabel);
        rightHUD.add(squadLabelRight);

        root.add(rightHUD, BorderLayout.EAST);

        // --- CENTER: HelperGrafico ---
        gameSupp = new HelperGrafico(playerSouthHand, playerNorthHand, playerEastHand, playerWestHand);
        root.add(gameSupp, BorderLayout.CENTER);
        gameSupp.setTerreno(terreno);
    }

    /**
     * Scala un'ImageIcon alle dimensioni specificate.
     */
    private ImageIcon scale(ImageIcon icon, int w, int h) {
        if (icon == null || icon.getImage() == null) return icon;
        Image scaled = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    /* ============== HIGHLIGHTER ============== */

    /** Evidenzia con un bordo l'avatar del giocatore corrente (per nome). */
    public void highlightAvatar(String name) {
        boolean isSouth = name != null && name.equalsIgnoreCase(playerSouthName);
        boolean isNorth = name != null && name.equalsIgnoreCase(playerNorthName);
        boolean isEast  = name != null && name.equalsIgnoreCase(playerEastName);
        boolean isWest  = name != null && name.equalsIgnoreCase(playerWestName);

        Border hi = BorderFactory.createLineBorder(Color.CYAN, 2, true);

        if (avatarSLabel != null) avatarSLabel.setBorder(isSouth ? hi : null);
        if (avatarNLabel != null) avatarNLabel.setBorder(isNorth ? hi : null);
        if (avatarELabel != null) avatarELabel.setBorder(isEast  ? hi : null);
        if (avatarWLabel != null) avatarWLabel.setBorder(isWest  ? hi : null);
    }

    /** Rimuove qualsiasi evidenziazione. */
    public void clearHighlight() {
        if (avatarSLabel != null) avatarSLabel.setBorder(null);
        if (avatarNLabel != null) avatarNLabel.setBorder(null);
        if (avatarELabel != null) avatarELabel.setBorder(null);
        if (avatarWLabel != null) avatarWLabel.setBorder(null);
    }

    /* ============== RENDER/UPDATE ============== */

    /**
     * Ridisegna l'interfaccia grafica.
     * Aggiorna le mani dei 4, il terreno e i punteggi.
     */
    public void render() {
        List<Giocatore> giocatori = gameEngine.getPartita().getGiocatori();
        gameSupp.setHands(
            giocatori.get(0).getMano(),
            giocatori.get(1).getMano(),
            giocatori.get(2).getMano(),
            giocatori.get(3).getMano()
        );
        gameSupp.setTerreno(terreno);
        gameSupp.refresh();

        // aggiornamento punteggi coerente con i getter del GameEngine
        scoreSouthLabel.setText(" - Punti: " + Math.round(gameEngine.getGiocatoreUmano().getPunti()));
        scoreNorthLabel.setText(" - Punti: " + Math.round(gameEngine.getGiocatoreAI().getPunti()));
        scoreEastLabel.setText(" - Punti: " + Math.round(gameEngine.getGiocatoreEst().getPunti()));
        scoreWestLabel.setText(" - Punti: " + Math.round(gameEngine.getGiocatoreOvest().getPunti()));

        SwingUtilities.invokeLater(() -> gameSupp.selectFirstIfNone());
    }

    /** Aggiorna le mani dei giocatori e ridisegna l'interfaccia. */
    public void refreshHands(List<Carta> south, List<Carta> north, List<Carta> east, List<Carta> west) {
        this.playerSouthHand = south;
        this.playerNorthHand = north;
        this.playerEastHand = east;
        this.playerWestHand = west;
        render();
    }

    /** Imposta il terreno e ridisegna l'interfaccia. */
    public void setTerreno(List<Carta> terreno) {
        this.terreno = (terreno == null) ? new ArrayList<>() : terreno;
        render();
    }

    /** Aggiorna il terreno e ridisegna l'interfaccia. */
    public void refreshTerreno(List<Carta> terreno) {
        this.terreno = (terreno == null) ? new ArrayList<>() : terreno;
        render();
    }

    /** Azzera l'istanza singleton. */
    public static void disposeInstance() { instance = null; }

    /* ============== Azioni ============== */

    /**
     * Notifica al controller che l'utente vuole giocare la carta selezionata.
     */
    private void giocaCartaSelezionata() {
        // Debounce: se è già stato premuto, ignora
        if (playLocked) return;

        Carta sel = gameSupp.getSelected();
        if (sel == null) {
            JOptionPane.showMessageDialog(
                this, "Seleziona una carta prima di giocare.",
                "Nessuna carta selezionata", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // blocca il pulsante Gioca finché non riceviamo l'aggiornamento dal controller
        playLocked = true;

        // notifico al GiocatoreUmano (vero) che l’utente ha scelto questa carta
        gameEngine.getGiocatoreUmano().notificaCartaScelta(sel);

        // se non faccio questi due comandi, non mi aggiorna la partita come dovrebbe
        playerSouthHand.remove(sel);
        render();
    }

    /**
     * Sblocca il pulsante "Gioca" dopo che il controller ha processato la mossa.
     */
    public void unlockPlay() {
        playLocked = false;
        if (btnGioca != null) btnGioca.setEnabled(true);
    }
}
