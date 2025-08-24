package view;

import controller.GameEngine;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import model.Carta;

/**
 * Vista principale della partita 2v2.
 * Usa HelperGrafico come canvas centrale.
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

    public static final Color BACKGROUND_GREEN = new Color(0, 128, 0);

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
        setBackground(BACKGROUND_GREEN);

        buildUI();
        render();
    }

    /* ================= UI ================= */

    private void buildUI() {
        root = new JPanel(new BorderLayout());
        root.setBackground(BACKGROUND_GREEN);
        add(root, BorderLayout.CENTER);

        // --- TOP HUD (NORD) ---
        topHUD = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        topHUD.setOpaque(false);
        JLabel avatarN = new JLabel(scale(playerNorthAvatar, 50, 50));
        JLabel nameN = new JLabel(playerNorthName);
        scoreNorthLabel = new JLabel(" - Punti: " + gameEngine.getGiocatoreAI().getPunti());
        JButton btnMenu = new JButton("⬅ Torna al Menù");
        btnMenu.addActionListener(e -> {
            int scl = JOptionPane.showConfirmDialog(
                    this,
                    "Tornare al menù principale?\nLa partita in corso andrà persa.",
                    "Abbandonare la nave..",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (scl == JOptionPane.YES_OPTION) {
                try {
                    gameEngine.getGiocatoreUmano().setOnCartaSceltaListener(null);
                } catch (Exception ignore) {}
                GameView2v2.disposeInstance(); // <— importantissimo: azzera il singleton
                gameEngine.visualizzaMenu(); // torna al menu
            }
        });
        topHUD.add(avatarN);
        topHUD.add(nameN);
        topHUD.add(scoreNorthLabel);
        
        root.add(topHUD, BorderLayout.NORTH);

        // --- BOTTOM HUD (SUD, umano) ---
        bottomHUD = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        bottomHUD.setOpaque(false);
        JLabel avatarS = new JLabel(scale(playerSouthAvatar, 60, 60));
        JLabel nameS = new JLabel(playerSouthName);
        scoreSouthLabel = new JLabel(" - Punti: " + gameEngine.getGiocatoreUmano().getPunti());
        JButton btnGioca = new JButton("Gioca");
        btnGioca.addActionListener(e -> giocaCartaSelezionata());
        bottomHUD.add(avatarS);
        bottomHUD.add(nameS);
        bottomHUD.add(btnGioca);
        bottomHUD.add(scoreSouthLabel);
        root.add(bottomHUD, BorderLayout.SOUTH);

        // --- LEFT HUD (OVEST) ---
        leftHUD = new JPanel();
        leftHUD.setOpaque(false);
        leftHUD.setLayout(new BoxLayout(leftHUD, BoxLayout.Y_AXIS));
        leftHUD.setBorder(BorderFactory.createEmptyBorder(150, 0, 0, 0));

        JLabel avatarW = new JLabel(scale(playerWestAvatar, 50, 50));
        avatarW.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel nameW = new JLabel(playerWestName);
        nameW.setAlignmentX(Component.CENTER_ALIGNMENT);
        scoreWestLabel = new JLabel(" - Punti: " + gameEngine.getGiocatoreOvest().getPunti());
        scoreWestLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftHUD.add(avatarW);
        leftHUD.add(Box.createVerticalStrut(8));
        leftHUD.add(nameW);
        leftHUD.add(Box.createVerticalStrut(8));
        leftHUD.add(scoreWestLabel);
        root.add(leftHUD, BorderLayout.WEST);

        // --- RIGHT HUD (EST) ---
        rightHUD = new JPanel();
        rightHUD.setLayout(new BoxLayout(rightHUD, BoxLayout.Y_AXIS));
        rightHUD.setOpaque(false);
        rightHUD.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0)); // meno spazio dall'alto

        // avatar + info
        JLabel avatarE = new JLabel(scale(playerEastAvatar, 50, 50));
        avatarE.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel nameE = new JLabel(playerEastName);
        nameE.setAlignmentX(Component.CENTER_ALIGNMENT);
        scoreEastLabel = new JLabel(" - Punti: " + gameEngine.getGiocatoreEst().getPunti());
        scoreEastLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // aggiunta in ordine verticale
        rightHUD.add(btnMenu);
        rightHUD.add(Box.createVerticalStrut(12));
        rightHUD.add(avatarE);
        rightHUD.add(Box.createVerticalStrut(8));
        rightHUD.add(nameE);
        rightHUD.add(Box.createVerticalStrut(8));
        rightHUD.add(scoreEastLabel);

        root.add(rightHUD, BorderLayout.EAST);

        // --- CENTER: GameSupport2v2 (2v2) ---
        gameSupp = new HelperGrafico(playerSouthHand, playerNorthHand, playerEastHand, playerWestHand);
        root.add(gameSupp, BorderLayout.CENTER);
        gameSupp.setTerreno(terreno);
    }

    private ImageIcon scale(ImageIcon icon, int w, int h) {
        if (icon == null || icon.getImage() == null) return icon;
        Image scaled = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    /* ============== RENDER/UPDATE ============== */

    public void render() {
        gameSupp.setHands(playerSouthHand, playerNorthHand, playerEastHand, playerWestHand);
        gameSupp.setTerreno(terreno);
        gameSupp.refresh();

        // aggiornamento punteggi (adatta ai nomi/metodi del tuo GameEngine)
        scoreSouthLabel.setText(" - Punti: " + Math.round(gameEngine.getGiocatoreUmano().getPunti()));
        scoreNorthLabel.setText(" - Punti: " + Math.round(gameEngine.getGiocatoreAI().getPunti()));
        scoreEastLabel.setText(" - Punti: " + Math.round(gameEngine.getGiocatoreEst().getPunti()));
        scoreWestLabel.setText(" - Punti: " + Math.round(gameEngine.getGiocatoreOvest().getPunti()));

        SwingUtilities.invokeLater(() -> gameSupp.selectFirstIfNone());
    }

    public void refreshHands(List<Carta> south, List<Carta> north, List<Carta> east, List<Carta> west) {
        this.playerSouthHand = south;
        this.playerNorthHand = north;
        this.playerEastHand = east;
        this.playerWestHand = west;
        render();
    }

    public void setTerreno(List<Carta> terreno) {
        this.terreno = (terreno == null) ? new ArrayList<>() : terreno;
        render();
    }

    public void refreshTerreno(List<Carta> terreno) {
        this.terreno = (terreno == null) ? new ArrayList<>() : terreno;
        render();
    }

    public static void disposeInstance() { instance = null; }

    /* ============== Azioni ============== */

    private void giocaCartaSelezionata() {
        Carta sel = gameSupp.getSelected();
        if (sel == null) {
            JOptionPane.showMessageDialog(
                this, "Seleziona una carta prima di giocare.",
                "Nessuna carta selezionata", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Notifica al motore di gioco
        gameEngine.getGiocatoreUmano().notificaCartaScelta(sel);

        // Aggiorna il terreno e rimuovi la carta dalla mano dell'umano
        terreno.add(sel);
        playerSouthHand.remove(sel);

        // Refresh grafico
        render();
    }
}
