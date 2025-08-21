package view; 

import controller.GameEngine;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*; 
import model.Carta; 
/** * Vista principale della partita 1v1. 
 * Mostra la mano del giocatore e quella 
 * * dell'avversario, oltre ai nomi/avatars. Delegando il disegno delle carte 
 * * al componente {@link GameSupport} e notificando il controller quando 
 * * l'utente seleziona e gioca una carta. 
 * */ 

 public class GameView extends JPanel { 
    
    private static GameView instance = null; 
    private String player1Name; 
    private ImageIcon player1Avatar; 
    private String player2Name; 
    private ImageIcon player2Avatar; 
    private List<Carta> player1Hand; 
    private List<Carta> player2Hand; 
    private GameEngine gameEngine; 
    private JPanel root;
    // BorderLayout 
    private JPanel topHUD; 
    // avatar/nome avversario 
    private JPanel bottomHUD; 
    // avatar/nome giocatore + pulsanti 
    private GameSupport gameSupp; 
    // canvas centrale 
    private List<Carta> terreno; 
    // terreno di gioco 
    private JLabel score1Label; 
    private JLabel score2Label; 
    private JPanel leftTop; 
    public static final Color BACKGROUND_GREEN = new Color(0, 128, 0); 
    
    public static GameView getInstance( 
        GameEngine gameEngine, 
        String player1Name, ImageIcon player1Avatar, 
        String player2Name, ImageIcon player2Avatar, 
        List<Carta> player1Hand, List<Carta> player2Hand) { 

        if (instance == null) { 
            instance = new GameView(
                gameEngine, 
                player1Name, player1Avatar, 
                player2Name, player2Avatar, player1Hand, player2Hand); 
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
            this.score1Label = new JLabel(); 
            this.score2Label = new JLabel(); 
            
            leftTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8)); 
            leftTop.setOpaque(false); 
            leftTop.add(score2Label); 
            setLayout(new BorderLayout()); 
            setBackground(BACKGROUND_GREEN); 
            buildUI(); 
            render(); 
        }
    // primo disegno  
    /* ================= UI ================= */ 
    private void buildUI() { 
        // root con BorderLayout 
        root = new JPanel(new BorderLayout()); 
        root.setBackground(BACKGROUND_GREEN); 
        add(root, BorderLayout.CENTER); 
        // --- TOP HUD: avversario (sx) + bottone Menù (dx) --- 
        topHUD = new JPanel(new BorderLayout(8, 8)); 
        topHUD.setOpaque(false); 
        // sinistra: avatar + nome avversario 
        JLabel avatar2 = new JLabel(scale(player2Avatar, 60, 60)); 
        JLabel name2 = new JLabel(player2Name, SwingConstants.LEFT); 
        name2.setFont(name2.getFont().deriveFont(Font.BOLD, 14f)); 
        score2Label = new JLabel(" - Punti: " + gameEngine.getGiocatoreAI().getPunti()); 
        leftTop.add(avatar2); leftTop.add(name2); 
        // destra: bottone Menù 
        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8)); 
        rightTop.setOpaque(false); JButton btnMenu = new JButton("⬅ Torna al Menù"); 
        btnMenu.addActionListener(e -> { 
            int scl = JOptionPane.showConfirmDialog( 
                this, "Tornare al menù principale?\nLa partita in corso andrà persa.", "Abbandonare la nave..", 
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE ); 
                if (scl == JOptionPane.YES_OPTION) { 
                    try { 
                        gameEngine.getGiocatoreUmano().setOnCartaSceltaListener(null); 
                    } catch (Exception ignore) {} 
                    GameView.disposeInstance(); // <— importantissimo: azzera il singleton 
                    gameEngine.visualizzaMenu(); // torna al menu 
                    } 
                }); 
                rightTop.add(btnMenu); 
                topHUD.add(leftTop, BorderLayout.WEST); 
                topHUD.add(rightTop, BorderLayout.EAST); 
                root.add(topHUD, BorderLayout.NORTH); 
                // --- BOTTOM HUD: giocatore --- 
                bottomHUD = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8)); 
                bottomHUD.setOpaque(false); 
                JLabel avatar1 = new JLabel(scale(player1Avatar, 60, 60)); 
                JLabel name1 = new JLabel(player1Name, SwingConstants.LEFT); 
                name1.setFont(name1.getFont().deriveFont(Font.BOLD, 14f)); 
                score1Label = new JLabel(" - Punti: " + gameEngine.getGiocatoreUmano().getPunti()); 
                JButton btnGioca = new JButton("Gioca"); btnGioca.addActionListener(e -> giocaCartaSelezionata()); 
                bottomHUD.add(avatar1); bottomHUD.add(name1); bottomHUD.add(Box.createHorizontalStrut(16)); 
                bottomHUD.add(btnGioca); bottomHUD.add(score1Label); root.add(bottomHUD, BorderLayout.SOUTH); 
                // --- CENTER: GameSupport (canvas carte) --- 
                gameSupp = new GameSupport(player1Hand, player2Hand); 
                root.add(gameSupp, BorderLayout.CENTER); // terreno iniziale vuoto 
                gameSupp.setTerreno(new ArrayList<>()); 
        }

    private ImageIcon scale(ImageIcon icon, int w, int h) { 
        if (icon == null || icon.getImage() == null) return icon; 
        Image scaled = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH); 
        return new ImageIcon(scaled); 
    } 
    /* ============== RENDER/UPDATE ============== */ 
    /** * Aggiorna le mani e il terreno sul GameSupport. Richiama sempre 
     * * {@link GameSupport#refresh()} affinché il layout venga ricalcolato e * ridisegnato. 
     * */ 
    public void render() { 
        gameSupp.setHands(player1Hand, player2Hand); 
        gameSupp.setTerreno(terreno); 
        gameSupp.refresh(); // aggiorna i punteggi 
        bottomHUD.remove(score1Label); 
        leftTop.remove(score2Label); 
        score1Label = new JLabel(" - Punti: " + Math.round(gameEngine.getGiocatoreUmano().getPunti())); 
        score2Label = new JLabel(" - Punti: " + Math.round(gameEngine.getGiocatoreAI().getPunti())); 
        bottomHUD.add(score1Label); 
        leftTop.add(score2Label); // dopo il refresh assicuriamo una selezione valida 
        SwingUtilities.invokeLater(() -> gameSupp.selectFirstIfNone()); 
    } 
    /** * Aggiorna le mani del giocatore e dell'avversario e ridisegna. */ 
    public void refreshHands(List<Carta> newP1, List<Carta> newP2) { 
        this.player1Hand = newP1; 
        this.player2Hand = newP2; 
        render(); 
    } 
    /** * Imposta la lista di carte sul terreno e ridisegna. Se il terreno è null, * verrà considerato vuoto. */ 
    public void setTerreno(List<Carta> terreno) { 
        this.terreno = terreno; 
        render(); 
    } 
    /** * Aggiornamento del terreno (mantiene compatibilità con il codice esistente). * Si limita a impostare la nuova lista e ridisegnare. */ 
    public void refreshTerreno(List<Carta> terreno) { 
        this.terreno = terreno; 
        render(); 
    } 
    public static void disposeInstance() { 
        instance = null; 
    } 
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
        // notifico al GiocatoreUmano (vero) che l’utente ha scelto questa carta 
        gameEngine.getGiocatoreUmano().notificaCartaScelta(sel); 
    } 
}