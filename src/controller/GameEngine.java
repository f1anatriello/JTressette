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
    private GiocatoreAI ai;
    GiocatoreUmano playerUmano;
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

	public void avviaNuovaPartita(int numPlayer) {
        if(numPlayer == 2){
            List<Giocatore> giocatori = new ArrayList();
            playerUmano = new GiocatoreUmano(this.player.getUsername());
            ai = new GiocatoreAI();
            giocatori.add(playerUmano);
            giocatori.add(ai);
            Partita1v1 partita = new Partita1v1(giocatori);
            partita.inizializzaPartita();
            iniziaPartita1v1(partita);
        } else {
            List<Giocatore> giocatori = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                giocatori.add(new GiocatoreAI());
            }
            giocatori.add(new GiocatoreUmano(player.getUsername()));
            Partita2v2 partita = new Partita2v2(giocatori);
            // chiamo il campo del 2v2
        }
    }
    
    public void giocaCarta(Giocatore g, Carta c, Partita p) {
    	List<Carta> manoNuova = g.getMano();
    	manoNuova.remove(manoNuova.indexOf(c));
    	g.setMano(manoNuova);
        p.getTerreno().add(c);
        gameView.setTerreno(p.getTerreno());
        gameView.refreshTerreno(p.getTerreno());
        gameView.refreshHands(playerUmano.getMano(), ai.getMano());
    }

    public void iniziaPartita1v1(Partita1v1 p) {
        gameView = GameView.getInstance(
            this,
            player.getUsername(),
            new ImageIcon(player.getAvatarPath()),
            p.getGiocatori().get(1).getNome(),
            new ImageIcon("images/avatars/avatar5.png"),
            p.getGiocatori().get(0).getMano(),
            p.getGiocatori().get(1).getMano()
        );

        playerUmano = (GiocatoreUmano) p.getGiocatori().get(0);
        ai = (GiocatoreAI) p.getGiocatori().get(1);

        // Avvia il primo turno: parte l’umano
        giocaTurnoUmano(p, playerUmano, ai);
    }

    private void giocaTurnoUmano(Partita1v1 partita, GiocatoreUmano umano, GiocatoreAI ai) {
        umano.setOnCartaSceltaListener(carta -> {
            giocaCarta(umano, carta, partita);
            giocaCarta(ai, ai.scegliCarta(partita), partita);

            Giocatore vincente = partita.manoVintaDa(partita.getTerreno());
            vincente.aggiungiPunti(partita.getTerreno().stream().mapToDouble(Carta::getPunti).sum());

            if (!partita.isMazzoVuoto()) {
                if (vincente.equals(ai)) {
                    ai.riceviCarta(partita.getMazzo().pesca());
                    umano.riceviCarta(partita.getMazzo().pesca());
                }else if (vincente.equals(umano)) {
                    umano.riceviCarta(partita.getMazzo().pesca());
                    ai.riceviCarta(partita.getMazzo().pesca());
                }
            }

            partita.clearTerreno();
            gameView.setTerreno(partita.getTerreno());
            gameView.refreshTerreno(partita.getTerreno());
            gameView.refreshHands(umano.getMano(), ai.getMano());
            
            // Verifica fine partita
            if (partita.isMazzoVuoto() && umano.getMano().isEmpty() && ai.getMano().isEmpty()) {
                finePartita(partita);
                return;
            }

            // Avvia il turno successivo
            if (vincente instanceof GiocatoreUmano) {
                giocaTurnoUmano(partita, umano, ai); // riparte umano
            } else {
                giocaTurnoAI(partita, umano, ai);    // parte AI
            }
        });
    }
    private void giocaTurnoAI(Partita1v1 partita, GiocatoreUmano umano, GiocatoreAI ai) {
        umano.setOnCartaSceltaListener(carta -> {
            giocaCarta(ai, ai.scegliCarta(partita), partita);
            giocaCarta(umano, carta, partita);

            Giocatore vincente = partita.manoVintaDa(partita.getTerreno());
            vincente.aggiungiPunti(partita.getTerreno().stream().mapToDouble(Carta::getPunti).sum());

            if (!partita.isMazzoVuoto()) {
                umano.riceviCarta(partita.getMazzo().pesca());
                ai.riceviCarta(partita.getMazzo().pesca());
            }

            partita.clearTerreno();
            gameView.setTerreno(partita.getTerreno());
            gameView.refreshHands(umano.getMano(), ai.getMano());
            
            // Verifica fine partita
            if (partita.isMazzoVuoto() && umano.getMano().isEmpty() && ai.getMano().isEmpty()) {
                finePartita(partita);
                return;
            }

            // Avvia il turno successivo
            if (vincente instanceof GiocatoreUmano) {
                giocaTurnoUmano(partita, umano, ai); // riparte umano
            } else {
                giocaTurnoAI(partita, umano, ai);    // parte AI
            }
        });
    }

    private void finePartita(Partita1v1 p) {
        String vincitore = p.vincitore1v1();
        JOptionPane.showMessageDialog(
            mainFrame,
            "La partita è finita! Il vincitore è: " + vincitore,
            "Partita Terminata",
            JOptionPane.INFORMATION_MESSAGE
        );
        visualizzaMenu();
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

    public GiocatoreUmano getGiocatoreUmano() {
    // supponendo che in avviaNuovaPartita tu abbia già salvato il riferimento
    return this.playerUmano;
    }

    public GiocatoreAI getGiocatoreAI() {
        // supponendo che in avviaNuovaPartita tu abbia già salvato il riferimento
        return this.ai;
    }
}