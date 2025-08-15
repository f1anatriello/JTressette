package controller;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import data.UserRepository;
import model.*;
import ui.UIConstants;
import ui.UISettings;
import view.ClassificaView;
import view.GameView;
import view.MenuPrincipaleView;

/**
 * Controller: gestisce le azioni sui pulsanti.
 */


public class GameEngine implements Observer {

    private static GameEngine instance = null;
    private GameView gameView;
    private ClassificaView cv;
    private MenuPrincipaleView mpv;
    private GiocatoreUmano player;
	private JFrame mainFrame;  // mi serve per switchare da una finestra all'altra

    @Override
    public void update(java.util.Observable o, Object arg) {
        // TODO: implement update logic if needed
    }

    public GameEngine(GiocatoreUmano player) {
        gameView = GameView.getInstance();
        this.player = player;
    }

    public GameEngine(GiocatoreUmano player, JFrame mainFrame) {
    		gameView = GameView.getInstance();
    		this.player = player;
    		this.mainFrame = mainFrame;	
    	}

	public int avviaNuovaPartita(int numPlayer) {
        if(numPlayer == 2){
            GiocatoreAI g1 = new GiocatoreAI();
            List<Giocatore> giocatori = new ArrayList();
            giocatori.add(g1);
            giocatori.add(player);
            Partita1v1 partita = new Partita1v1(giocatori);
            iniziaPartita1v1(partita);
            
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
    	while(true){
    		System.out.println("Tocca a te");
    		
    		
    		if(p.isFinita()) {
    			System.out.println("Il vincitore è: ");
    			return p.vincitore1v1();
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
    
    
    public void visualizzaProfilo() {
    }

    public void visualizzaStatistiche() {
    	cv = new ClassificaView(this);
    	cv.mostraClassifica(UserRepository.getClassifica()); 

    	setScreen(cv);
    }
    
    public void visualizzaMenu() {
    	mpv = new MenuPrincipaleView(this);
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
	    mainFrame.pack();                 // rispetta le preferred size appena impostate
	    mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);       // nel caso sia la prima volta
    }

}