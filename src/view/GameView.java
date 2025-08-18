package view;

import controller.GameEngine;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import model.Carta;
import model.Giocatore;
import model.GiocatoreAI;
import model.GiocatoreUmano;
import model.Partita1v1;

public class GameView extends JFrame {

    private static GameView instance = null;

    // === campi preesistenti ===
    private String player1Name;
    private ImageIcon player1Avatar;
    private String player2Name;
    private ImageIcon player2Avatar;
    private List<Carta> player1Hand;
    private List<Carta> player2Hand;
    private GameEngine gameEngine;

    // === nuovi ===
    private JPanel root;       // BorderLayout
    private JPanel topHUD;     // avatar/nome avversario
    private JPanel bottomHUD;  // avatar/nome giocatore + pulsanti (se vuoi)
    private GameSupport gameSupp; // canvas centrale

    public static final Color BACKGROUND_GREEN = new Color(0, 128, 0);

    public static GameView getInstance(
            GameEngine gameEngine,
            String player1Name, ImageIcon player1Avatar,
            String player2Name, ImageIcon player2Avatar,
            List<Carta> player1Hand, List<Carta> player2Hand) {

        if (instance == null) {
            instance = new GameView(gameEngine, player1Name, player1Avatar, player2Name, player2Avatar, player1Hand, player2Hand);
        }
        return instance;
    }

    private GameView(
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

        setTitle("JTressette - 1v1");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(new Dimension(960, 640));
        setResizable(false);
        setLocationRelativeTo(null);

        buildUI();
        render(); // primo disegno

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                // torna al menu e libera il singleton
                gameEngine.visualizzaMenu();
                instance = null;
            }
        });

        setVisible(true);
    }

    /* ================= UI ================= */

    private void buildUI() {
        // root con BorderLayout
        root = new JPanel(new BorderLayout());
        root.setBackground(BACKGROUND_GREEN);
        setContentPane(root);

        // --- TOP HUD: avversario ---
        topHUD = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        topHUD.setOpaque(false);

        JLabel avatar2 = new JLabel(scale(player2Avatar, 60, 60));
        JLabel name2   = new JLabel(player2Name, SwingConstants.LEFT);
        name2.setFont(name2.getFont().deriveFont(Font.BOLD, 14f));
        topHUD.add(avatar2);
        topHUD.add(name2);

        root.add(topHUD, BorderLayout.NORTH);

        // --- BOTTOM HUD: giocatore ---
        bottomHUD = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        bottomHUD.setOpaque(false);

        JLabel avatar1 = new JLabel(scale(player1Avatar, 60, 60));
        JLabel name1   = new JLabel(player1Name, SwingConstants.LEFT);
        name1.setFont(name1.getFont().deriveFont(Font.BOLD, 14f));

        // (opzionale) pulsante Gioca che usa la selezione del GamePane
        JButton btnGioca = new JButton("Gioca");
        btnGioca.addActionListener(e -> giocaCartaSelezionata());

        bottomHUD.add(avatar1);
        bottomHUD.add(name1);
        bottomHUD.add(Box.createHorizontalStrut(16));
        bottomHUD.add(btnGioca);

        root.add(bottomHUD, BorderLayout.SOUTH);

        // --- CENTER: GamePane (canvas carte) ---
        gameSupp = new GameSupport(player1Hand, player2Hand);
        // niente setBounds: BorderLayout lo ridimensiona in automatico
        root.add(gameSupp, BorderLayout.CENTER);
    }

    private ImageIcon scale(ImageIcon icon, int w, int h) {
        if (icon == null || icon.getImage() == null) return icon;
        Image scaled = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    /* ============== RENDER/UPDATE ============== */

    /** Aggiorna mani e (se lo disegni nel GamePane) anche il terreno. */
    private void render() {
        gameSupp.setHands(player1Hand, player2Hand);
        // se il tuo GamePane mostra anche il terreno, passa la lista:
        try {
            gameSupp.getClass().getMethod("setTerreno", java.util.List.class)
                    .invoke(gameSupp, getTerrenoSafe());
        } catch (Exception ignore) {}
        gameSupp.refresh();
    }

    @SuppressWarnings("unchecked")
    private java.util.List<Carta> getTerrenoSafe() {
        try {
            // se Partita1v1 ha getTerreno(), usalo. Altrimenti torna lista vuota.
            java.lang.reflect.Method m = findPartitaTerrenoGetter();
            if (m != null) {
                Object list = m.invoke(findPartitaInstance());
                return (java.util.List<Carta>) list;
            }
        } catch (Exception ignored) {}
        return java.util.Collections.emptyList();
    }

    // Stub per mantenere compatibilità: se non vuoi reflection,
    // passa direttamente il terreno dal chiamante e rimuovi questi metodi.
    private Object findPartitaInstance() { return null; }
    private java.lang.reflect.Method findPartitaTerrenoGetter() { return null; }

    /** Chiamala quando il model cambia le mani. */
    public void refreshHands(List<Carta> newP1, List<Carta> newP2) {
        this.player1Hand = newP1;
        this.player2Hand = newP2;
        render();
    }

    /* ============== Azioni ============== */

    /* Non so qua cosa si sia fumato chatGPT
     * a breve stacco tutto
     */
    private void giocaCartaSelezionata() {
        Carta sel = gameSupp.getSelected();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Seleziona una carta prima di giocare.", "Nessuna carta selezionata", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // recupera i riferimenti reali ai giocatori se li hai altrove
        GiocatoreUmano p1 = new GiocatoreUmano(player1Name); // <-- se già creato altrove, usa quello
        GiocatoreAI      p2 = new GiocatoreAI() {};               // placeholder se ti serve

        // applica la mossa al model
        // (usa i tuoi oggetti reali: p1/p2/partita)
        // engine.giocaCarta(p1, sel, partita);

        // aggiorna le mani dal model; se non hai ancora il model qui, togli questi 2 e usa refreshHands dal controller
        // player1Hand = p1.getMano();
        // player2Hand = p2.getMano();
        render();
    }
}
