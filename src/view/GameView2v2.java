package view;

import controller.GameEngine;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import model.Carta;

/**
 * Vista principale della partita 2v2. Mostra la mano del giocatore
 * e i pannelli HUD dei 3 compagni/avversari attorno al tavolo.
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
    private GameSupport2v2 gameSupp; 
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
        scoreNorthLabel = new JLabel(" - Punti: 0");
        topHUD.add(avatarN);
        topHUD.add(nameN);
        topHUD.add(scoreNorthLabel);
        root.add(topHUD, BorderLayout.NORTH);

        // --- BOTTOM HUD (SUD, umano) ---
        bottomHUD = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        bottomHUD.setOpaque(false);
        JLabel avatarS = new JLabel(scale(playerSouthAvatar, 60, 60));
        JLabel nameS = new JLabel(playerSouthName);
        scoreSouthLabel = new JLabel(" - Punti: 0");
        JButton btnGioca = new JButton("Gioca");
        btnGioca.addActionListener(e -> giocaCartaSelezionata());
        bottomHUD.add(avatarS);
        bottomHUD.add(nameS);
        bottomHUD.add(btnGioca);
        bottomHUD.add(scoreSouthLabel);
        root.add(bottomHUD, BorderLayout.SOUTH);

        // --- LEFT HUD (OVEST) ---
        leftHUD = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        leftHUD.setOpaque(false);
        JLabel avatarW = new JLabel(scale(playerWestAvatar, 50, 50));
        JLabel nameW = new JLabel(playerWestName);
        scoreWestLabel = new JLabel(" - Punti: 0");
        leftHUD.add(avatarW);
        leftHUD.add(nameW);
        leftHUD.add(scoreWestLabel);
        root.add(leftHUD, BorderLayout.WEST);

        // --- RIGHT HUD (EST) ---
        rightHUD = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        rightHUD.setOpaque(false);
        JLabel avatarE = new JLabel(scale(playerEastAvatar, 50, 50));
        JLabel nameE = new JLabel(playerEastName);
        scoreEastLabel = new JLabel(" - Punti: 0");
        rightHUD.add(avatarE);
        rightHUD.add(nameE);
        rightHUD.add(scoreEastLabel);
        root.add(rightHUD, BorderLayout.EAST);

        // --- CENTER: GameSupport ---
        gameSupp = new GameSupport2v2(playerSouthHand, playerNorthHand, playerEastHand, playerWestHand);
        root.add(gameSupp, BorderLayout.CENTER);
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

        // TODO: aggiornare i punteggi dai giocatori del gameEngine
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
        this.terreno = terreno != null ? terreno : new ArrayList<>();
        render();
    }

    public void refreshTerreno(List<Carta> terreno) {
        this.terreno = terreno != null ? terreno : new ArrayList<>();
        render();
    }

    public static void disposeInstance() { instance = null; }

    /* ============== Azioni ============== */

    private void giocaCartaSelezionata() {
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
        gameEngine.getGiocatoreUmano().notificaCartaScelta(sel);
    }
}
