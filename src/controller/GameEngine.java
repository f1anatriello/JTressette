package controller;

import data.UserRepository;
import data.UtentePojo;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import model.*;
import ui.UIConstants;
import view.ClassificaView;
import view.GameView;
import view.MenuPrincipaleView;
import view.ProfiloView;

/**
 * Controller: gestisce le azioni sui pulsanti.
 */


public class GameEngine implements Observer {

    private static GameEngine instance = null;
    private ClassificaView cv;
    private MenuPrincipaleView mpv;
    private ProfiloView pv;
    private UtentePojo player;
    private GameView gameView;
    private JFrame mainFrame;  // mi serve per switchare da una finestra all'altra

    @Override
    public void update(java.util.Observable o, Object arg) {
        // TODO: implement update logic if needed
    }

    private GameEngine(UtentePojo player) {
        this.player = player;
    }

    public static GameEngine getInstance(UtentePojo player) {
        if (instance == null) {
            instance = new GameEngine(player);
        }
        return instance;
    }

	public int avviaNuovaPartita(int numPlayer) {
        if(numPlayer == 2){
            List<Giocatore> giocatori = new ArrayList();
            GiocatoreUmano playerUmano = new GiocatoreUmano(this.player.getUsername());
            GiocatoreAI ai = new GiocatoreAI();
            giocatori.add(playerUmano);
            giocatori.add(ai);
            Partita1v1 partita = new Partita1v1(giocatori);

            gameView = GameView.getInstance(this,
                player.getUsername(),
                new ImageIcon(player.getAvatarPath()),
                partita.getGiocatori().get(1).getNome(),
                new ImageIcon("images/avatars/avatar5.png"),
                partita.getGiocatori().get(0).getMano(),
                partita.getGiocatori().get(1).getMano());
            // setScreen(gameView);
        } else {
            List<Giocatore> giocatori = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                giocatori.add(new GiocatoreAI());
            }
            giocatori.add(new GiocatoreUmano(player.getUsername()));
            Partita2v2 partita = new Partita2v2(giocatori);
            // chiamo il campo del 2v2
        }
        return 0;
    }
    
    public void giocaCarta(Giocatore g, Carta c, Partita p) {
    	List<Carta> manoNuova = g.getMano();
    	manoNuova.remove(manoNuova.indexOf(c));
    	g.setMano(manoNuova);
    	p.aggiungiAlTerreno(c);
    }


    public String iniziaPartita1v1(Partita1v1 p) {
        List<Giocatore> giocatori = p.getGiocatori();
        List<Carta> terreno = new ArrayList<>();
        String vincitore = null;
        while(true){
            System.out.println("Tocca a te");
            int indiceVincitore = 0;
            for (int i = indiceVincitore; i < 4; i = (i + 1) % 4) {
                Giocatore gioc = giocatori.get(i);
                if(gioc instanceof GiocatoreUmano) {
                    Carta scelta = ((GiocatoreUmano) gioc).scegliCarta();
                    giocaCarta(gioc, scelta, p); // mi aggiorna mano e terreno
                    if (gioc == giocatori.get(giocatori.size() - 1)) {
                        vincitore = p.manoVintaDa(terreno);
                        terreno.clear();
                        if (p.isMazzoVuoto()) {
                            p.setFinita(true);
                        }
                    }
                } else {
                    Carta scelta = ((GiocatoreAI) gioc).scegliCarta(p);
                    giocaCarta(gioc, scelta, p);
                    if (gioc == giocatori.get(giocatori.size() - 1)) {
                        vincitore = p.manoVintaDa(terreno);
                        terreno.clear();
                        if (p.isMazzoVuoto()) {
                            p.setFinita(true);
                        }
                    }
                }
            }
            indiceVincitore = giocatori.indexOf(vincitore);

            if(p.isFinita()) {
                System.out.println("Il vincitore è: ");
                return p.vincitore1v1();
            }
            
        }
    }

    /** Inizia una nuova partita 2v2 */
    public String iniziaPartita2v2(Partita2v2 p) {
        while(true){
            System.out.println("Tocca a te");

            if(p.isFinita()) {
                System.out.println("Il vincitore è: ");
                return p.vincitore2v2();
            }

        }
    }

    /** Crea il login (finestra principale) e mostra il menu dopo esser loggato*/
    public void avviaUIPrincipale() {
        SwingUtilities.invokeLater(() -> {
        	if (mainFrame != null) return;
            mainFrame = new JFrame("JTressette");
            mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            mainFrame.setMinimumSize(new Dimension(400, 600));
            mainFrame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    int scl = JOptionPane.showConfirmDialog(
                            mainFrame,
                            "Sei sicuro di voler uscire?",
                            "Già te ne vai? :(",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    );
                    if (scl == JOptionPane.YES_OPTION) System.exit(0);
                }
            });
            visualizzaMenu();                
        });
    }

    public void avviaModificaProfilo() {
        pv = new ProfiloView(this);
        pv.modificaProfilo(player);
        setScreen(pv);
    }
    
    
    public void visualizzaProfilo() {
        pv = new ProfiloView(this);
        pv.mostraProfilo(player);
        setScreen(pv);
    }

    public void visualizzaStatistiche() {
    	cv = new ClassificaView(this);
    	cv.mostraClassifica(UserRepository.getClassifica()); 
    	setScreen(cv);
    }
    
    public void visualizzaMenu() {
    	mpv = MenuPrincipaleView.getInstance(this);
    	setScreen(mpv);
    } 
    
    /* ===== UTILITIES ===== */
    private boolean isFixedSizedView(JPanel panel) {
        return !(panel instanceof MenuPrincipaleView);
    }

    public void registerMainFrame(JFrame frame) { this.mainFrame = frame; }

    private void setScreen(JPanel panel) {
	    boolean fixed = isFixedSizedView(panel);

	    // Imposta le dimensioni preferite del pannello
	    if (fixed) {
	        panel.setPreferredSize(UIConstants.FIXED_VIEW_SIZE);
	        mainFrame.setMinimumSize(UIConstants.FIXED_VIEW_SIZE);
	        mainFrame.setResizable(false);                
	    } else {
	        panel.setPreferredSize(UIConstants.MENU_SIZE);
	        mainFrame.setMinimumSize(UIConstants.MENU_SIZE);
	        mainFrame.setResizable(false);                  
	    }
	
	    mainFrame.setContentPane(panel);
	    mainFrame.pack();                 // rispetta le preferred size impostate
	    mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);       // nel caso sia la prima volta
    }

    public UtentePojo getPlayer() {
        return player;
    }

}