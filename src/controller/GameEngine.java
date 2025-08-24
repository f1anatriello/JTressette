package controller;

import data.UserRepository;
import data.UtentePojo;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import javax.swing.Timer;

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
import view.GameView2v2;
import view.MenuPrincipaleView;
import view.ProfiloView;

/**
 * Controller dell'applicazione: gestisce le azioni dell'interfaccia utente,
 * l'avvio delle partite e il ciclo di gioco. È un Observer in attesa
 * dell'evoluzione del modello (non ancora implementato completamente).
 */
public class GameEngine implements Observer {

    private static GameEngine instance = null;
    private ClassificaView cv;
    private MenuPrincipaleView mpv;
    private ProfiloView pv;
    private UtentePojo player;
    private GiocatoreAI ai;
    private GiocatoreUmano playerUmano;
    private GiocatoreAI playerEst;
    private GiocatoreAI playerOvest;
    private GameView gameView;
    private GameView2v2 gameView2v2;
    private JFrame mainFrame;

    @Override
    public void update(java.util.Observable o, Object arg) {
        // TODO: implement update logic if needed
    }

    private GameEngine(UtentePojo player) {
        this.player = player;
        this.ai = new GiocatoreAI();
        this.playerEst = new GiocatoreAI();
        this.playerOvest = new GiocatoreAI();
    }

    public static GameEngine getInstance(UtentePojo player) {
        if (instance == null) {
            instance = new GameEngine(player);
        }
        return instance;
    }

    /**
     * Avvia una nuova partita. Se {@code numPlayer == 2} avvia una 1v1; altrimenti
     * prepara una 2v2 (non ancora implementata). Inizializza i giocatori e
     * l'oggetto {@link Partita1v1}, quindi chiama {@link #iniziaPartita1v1}.
     */
    public void avviaNuovaPartita(int numPlayer) {
        if (numPlayer == 2) {
            List<Giocatore> giocatori = new ArrayList<>();
            playerUmano = new GiocatoreUmano(this.player.getUsername());
            giocatori.add(playerUmano);
            giocatori.add(ai);
            Partita1v1 partita = new Partita1v1(giocatori);
            partita.inizializzaPartita();
            iniziaPartita1v1(partita);
        } else {
            List<Giocatore> giocatori = new ArrayList<>();
            playerUmano = new GiocatoreUmano(this.player.getUsername());
            giocatori.add(playerUmano);
            giocatori.add(playerEst);
            giocatori.add(ai);
            giocatori.add(playerOvest);
            Partita2v2 partita = new Partita2v2(giocatori);
            // chiamo il campo del 2v2
            partita.inizializzaPartita();
            iniziaPartita2v2(partita);
        }
    }

    /**
     * Fa giocare una carta al giocatore specificato, rimuovendola dalla sua mano
     * e aggiungendola al terreno della partita. Non aggiorna la vista: l'aggiornamento
     * deve essere eseguito a fine turno dopo che entrambi i giocatori hanno
     * giocato e il terreno viene svuotato.
     */

    public void giocaCarta(Giocatore g, Carta c, Partita p) {
        // solo logica di stato, nessun refresh grafico qui
        List<Carta> mano = g.getMano();
        if (c == null) return;            
        mano.remove(c);                   
        g.setMano(mano);
        p.getTerreno().add(c);            
    }


    /**
     * Avvia la partita 1v1 inizializzando la GameView e il primo turno. Prepara
     * i riferimenti a {@link GiocatoreUmano} e {@link GiocatoreAI} e lascia
     * partire il turno dell'umano.
     */
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

        setScreen(gameView);

        playerUmano = (GiocatoreUmano) p.getGiocatori().get(0);
        ai = (GiocatoreAI) p.getGiocatori().get(1);
        setOrder(p, playerUmano, ai); // parte l'umano per la prima mano

        // Avvia il primo turno: parte l’umano
        giocaTurnoUmano(p, playerUmano, ai);
    }

    /**
     * Gestisce un turno in cui l'umano inizia. Imposta il listener di selezione
     * della carta sul GiocatoreUmano; quando la carta viene scelta vengono
     * eseguite le mosse dell'umano e dell'AI, calcolato il vincitore della mano,
     * attribuiti i punti e gestite le pescate. Infine viene ripulito il terreno
     * e la vista aggiornata.
     */
    // GameEngine.java

    private void giocaTurnoUmano(Partita1v1 partita, GiocatoreUmano umano, GiocatoreAI ai) {
        setOrder(partita, umano, ai);
        umano.setOnCartaSceltaListener(carta -> {
            if (carta == null) return;

            // 1) l'umano gioca e mostri SUBITO la 1ª carta sul terreno
            giocaCarta(umano, carta, partita);
            showTerreno(partita);

            // 2) dopo un attimo, gioca l'AI e mostri entrambe
            after(180, () -> {
                Carta cartaAI = ai.scegliCarta(partita);
                giocaCarta(ai, cartaAI, partita);
                showTerreno(partita);

                // 3) dopo un altro attimo, risolvi la mano (punti, pesca, clear)
                after(260, () -> {
                    Giocatore vincente = partita.manoVintaDa(partita.getTerreno());
                    double puntiMano = partita.getTerreno().stream().mapToDouble(Carta::getPunti).sum();
                    vincente.aggiungiPunti(puntiMano);

                    if (!partita.isMazzoVuoto()) {
                        if (vincente.equals(umano)) {
                            umano.riceviCarta(partita.getMazzo().pesca());
                            ai.riceviCarta(partita.getMazzo().pesca());
                        } else {
                            ai.riceviCarta(partita.getMazzo().pesca());
                            umano.riceviCarta(partita.getMazzo().pesca());
                        }
                    }

                    partita.clearTerreno();
                    showTerreno(partita);
                    gameView.refreshHands(umano.getMano(), ai.getMano());

                    if (partita.isMazzoVuoto() && umano.getMano().isEmpty() && ai.getMano().isEmpty()) {
                        finePartita(partita);
                        return;
                    }

                    if (vincente instanceof GiocatoreUmano) {
                        giocaTurnoUmano(partita, umano, ai);
                    } else {
                        giocaTurnoAI(partita, umano, ai);
                    }
                });
            });
        });
    }



    /**
     * Gestisce un turno in cui l'AI inizia. Il resto della logica è identica
     * a {@link #giocaTurnoUmano}, ma l'ordine delle giocate è invertito.
     */
    // GameEngine.java

    private void giocaTurnoAI(Partita1v1 partita, GiocatoreUmano umano, GiocatoreAI ai) {
        setOrder(partita, ai, umano);
        Carta cartaAI = ai.scegliCarta(partita);
        giocaCarta(ai, cartaAI, partita);
        showTerreno(partita);

        // poi attendi l'umano
        umano.setOnCartaSceltaListener(carta -> {
            if (carta == null) return; // safety

            // 1) gioca l'umano e mostra entrambe
            giocaCarta(umano, carta, partita);
            showTerreno(partita);

            // 2) dopo un attimo, risolvi
            after(260, () -> {
                Giocatore vincente = partita.manoVintaDa(partita.getTerreno());
                double puntiMano = partita.getTerreno().stream().mapToDouble(Carta::getPunti).sum();
                vincente.aggiungiPunti(puntiMano);

                if (!partita.isMazzoVuoto()) {
                    if (vincente.equals(umano)) {
                        umano.riceviCarta(partita.getMazzo().pesca());
                        ai.riceviCarta(partita.getMazzo().pesca());
                    } else {
                        ai.riceviCarta(partita.getMazzo().pesca());
                        umano.riceviCarta(partita.getMazzo().pesca());
                    }
                }

                partita.clearTerreno();
                showTerreno(partita);
                gameView.refreshHands(umano.getMano(), ai.getMano());

                if (partita.isMazzoVuoto() && umano.getMano().isEmpty() && ai.getMano().isEmpty()) {
                    finePartita(partita);
                    return;
                }

                if (vincente instanceof GiocatoreUmano) {
                    giocaTurnoUmano(partita, umano, ai);
                } else {
                    giocaTurnoAI(partita, umano, ai);
                }
            });
        });
    }



    /**
     * Mostra un dialogo di fine partita indicando il vincitore e torna al menu.
     */
    private void finePartita(Partita1v1 p) {
        Giocatore vincitore = p.vincitore1v1();
        if(vincitore.getClass() == GiocatoreUmano.class)
        {
            player.incrementaVinte();  
        } 
        else player.incrementaPerse();

        JOptionPane.showMessageDialog(
            mainFrame,
            "La partita è finita! Il vincitore è: " + vincitore.getNome(),
            "Partita Terminata",
            JOptionPane.INFORMATION_MESSAGE
        );
        GameView.disposeInstance();

        visualizzaMenu();
    }

    private void finePartita2v2(Partita2v2 p) {
        String vincitore = p.vincitore2v2();
        JOptionPane.showMessageDialog(
            mainFrame,
            "La partita è finita! Il vincitore è: " + vincitore,
            "Partita Terminata",
            JOptionPane.INFORMATION_MESSAGE
        );
        visualizzaMenu();
    }

    /**
     * Inizia una nuova partita 2v2 (non implementato). Viene mantenuto per
     * compatibilità con l'interfaccia ma restituisce il vincitore in stampa.
     */
    public void iniziaPartita2v2(Partita2v2 p) {
        gameView2v2 = GameView2v2.getInstance(
            this,
            // Giocatore Sud (umano principale)
            player.getUsername(),
            new ImageIcon(player.getAvatarPath()),
            p.getGiocatori().get(0).getMano(),
            // Giocatore est (avversario 1)
            p.getGiocatori().get(1).getNome(),
            new ImageIcon("images/avatars/avatar5.png"),
            p.getGiocatori().get(1).getMano(),
            // Giocatore nord (compagno di squadra)
            p.getGiocatori().get(2).getNome(),
            new ImageIcon("images/avatars/avatar4.png"),
            p.getGiocatori().get(2).getMano(),
            // Giocatore Ovest (avversario 2)
            p.getGiocatori().get(3).getNome(),
            new ImageIcon("images/avatars/avatar3.png"),
            p.getGiocatori().get(3).getMano()
        );

        setScreen(gameView2v2);

        // Cast a seconda di chi è umano/AI
        GiocatoreUmano giocatoreSud  = (GiocatoreUmano) p.getGiocatori().get(0);
        GiocatoreAI aiNord               = (GiocatoreAI) p.getGiocatori().get(1); 
        GiocatoreAI aiEst               = (GiocatoreAI) p.getGiocatori().get(2);
        GiocatoreAI aiOvest             = (GiocatoreAI) p.getGiocatori().get(3);

        // Avvia il primo turno: parte il giocatore Sud (umano)
        List<Giocatore> g = List.of(giocatoreSud, aiOvest, aiNord, aiEst);
        giocaTurnoUmano2v2(p, g);
    }

    private void giocaTurnoUmano2v2(Partita2v2 partita, List<Giocatore> ordineTurno) {
        GiocatoreUmano umano = (GiocatoreUmano) ordineTurno.get(0); // il primo è sempre l’umano di turno
        umano.setOnCartaSceltaListener(carta -> {
            if (carta == null) return;

            giocaCarta(umano, carta, partita);
            
            // Dopo che l’umano ha giocato, fanno le mosse gli altri (dal secondo in poi)
            giocaSequenzaRec(partita, ordineTurno, 1);
        });
    }

    private void giocaSequenzaRec(Partita2v2 partita, List<Giocatore> giocatori, int index) {
        if (index >= giocatori.size()) {
            after(400, () -> risolviMano(partita, giocatori));
            return;
        }

        Giocatore corrente = giocatori.get(index);

        if (corrente instanceof GiocatoreAI) {
            after(400, () -> {
                Carta cartaAI = ((GiocatoreAI) corrente).scegliCarta(partita);
                giocaCarta(corrente, cartaAI, partita);
                showTerreno2v2(partita);
                giocaSequenzaRec(partita, giocatori, index + 1);
            });

        } else if (corrente instanceof GiocatoreUmano) {
            ((GiocatoreUmano) corrente).setOnCartaSceltaListener(carta -> {
                if (carta == null) return;
                giocaCarta(corrente, carta, partita);
                
                giocaSequenzaRec(partita, giocatori, index + 1);
            });
        }
    }




    private void risolviMano(Partita2v2 partita, List<Giocatore> giocatori) {
        // Lascia visibili le carte sul terreno per un po'
        after(400, () -> {
            Giocatore vincente = partita.manoVintaDa(partita.getTerreno());
            double puntiMano = partita.getTerreno().stream()
                    .mapToDouble(Carta::getPunti)
                    .sum();
            vincente.aggiungiPunti(puntiMano);

            partita.getTerreno().clear();

            // pesca carte
            if (!partita.isMazzoVuoto()) {
                for (int i = 0; i < giocatori.size(); i++) {
                    Giocatore g = giocatori.get((i + giocatori.indexOf(vincente)) % giocatori.size());
                    g.riceviCarta(partita.getMazzo().pesca());
                }
            }

            // aggiorna le mani dei 4 giocatori
            gameView2v2.refreshHands(
                giocatori.get(0).getMano(),
                giocatori.get(1).getMano(),
                giocatori.get(2).getMano(),
                giocatori.get(3).getMano()
            );

            // Dopo un altro attimo, pulisci il terreno e avvia il nuovo turno
            after(400, () -> {
                partita.clearTerreno();
                showTerreno2v2(partita);

                // Fine partita?
                boolean finita = partita.isMazzoVuoto() && 
                                giocatori.get(0).getMano().isEmpty() &&
                                giocatori.get(1).getMano().isEmpty() &&
                                giocatori.get(2).getMano().isEmpty() &&
                                giocatori.get(3).getMano().isEmpty();
                if (finita) {
                    finePartita2v2(partita);
                    return;
                }

                // Nuovo turno: parte il vincente
                List<Giocatore> gioc = giocatoriSenza(vincente, giocatori);
                if (vincente instanceof GiocatoreUmano) {
                    giocaTurnoUmano2v2(partita, gioc);
                } else {
                    giocaTurnoAI2v2(partita, gioc);
                }
            });
        });
    }


    private void giocaTurnoAI2v2(Partita2v2 partita, List<Giocatore> giocatori) {
        GiocatoreAI aiCorrente = (GiocatoreAI) giocatori.get(0);
        Carta cartaAI = aiCorrente.scegliCarta(partita);
        giocaCarta(aiCorrente, cartaAI, partita);
        showTerreno2v2(partita);

        // Poi gli altri a giro
        giocaSequenzaRec(partita, giocatori, 1);
    }

    private List<Giocatore> giocatoriSenza(Giocatore vincente, List<Giocatore> giocatori) {

        List<Giocatore> result = new ArrayList<>();
        int index = giocatori.indexOf(vincente);
        result.add(vincente);
        for (int i = 0; i < 3; i++) {
            result.add(giocatori.get((index + i + 1) % giocatori.size()));
        }
        for(Giocatore g : result){
            System.out.println(g.getNome());
        }
        return result;
    }




    /**
     * Crea il frame principale e visualizza il menu dopo il login. Imposta il comportamento
     * della finestra alla chiusura.
     */
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

    /** Avvia la view di modifica profilo. */
    public void avviaModificaProfilo() {
        pv = new ProfiloView(this);
        pv.modificaProfilo(player);
        setScreen(pv);
    }

    /** Visualizza il profilo dell'utente. */
    public void visualizzaProfilo() {
        pv = new ProfiloView(this);
        pv.mostraProfilo(player);
        setScreen(pv);
    }

    /** Visualizza la classifica. */
    public void visualizzaStatistiche() {
        cv = new ClassificaView(this);
        cv.mostraClassifica(UserRepository.getClassifica());
        setScreen(cv);
    }

    /** Visualizza il menu principale. */
    public void visualizzaMenu() {
        mpv = MenuPrincipaleView.getInstance(this);
        setScreen(mpv);
    }

    /* ===== UTILITIES ===== */
    private boolean isFixedSizedView(JPanel panel) {
        return !(panel instanceof MenuPrincipaleView ||
                 panel instanceof ProfiloView);
    }

    public void registerMainFrame(JFrame frame) {
        this.mainFrame = frame;
    }


    /**
     * Imposta l'ordine dei giocatori in una partita (al momento solo 1v1) in modo che
     * il giocatore "first" sia il primo a giocare e "second" il secondo
     * @param p
     * @param first
     * @param second
     */
    private void setOrder(Partita p, Giocatore first, Giocatore second) {
    List<Giocatore> g = p.getGiocatori();
    if (g.size() == 2 && (g.get(0) != first || g.get(1) != second)) {
        g.set(0, first);
        g.set(1, second);
    }
}

    /**
     * Cambia la schermata mostrata nel frame principale adattando le dimensioni
     * in base al tipo di vista (fissa o menu).
     */
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

    /**
     * Ritorna alla schermata principale dopo un'azione, con un piccolo delay
     * per evitare flickering.
     */
    private void after(int ms, Runnable action) {
        new Timer(ms, e -> {
            ((Timer) e.getSource()).stop();
            action.run();
        }).start();
    }


    // Mostra lo stato attuale del terreno (usa già il tuo render della GameView)
    private void showTerreno(Partita p) {
        gameView.setTerreno(p.getTerreno());
    }

    private void showTerreno2v2(Partita p) {
        gameView2v2.setTerreno(p.getTerreno());
    }

    /** Restituisce l'utente loggato. */
    public UtentePojo getPlayer() {
        return player;
    }

    /** Restituisce il riferimento al GiocatoreUmano della partita corrente. */
    public GiocatoreUmano getGiocatoreUmano() {
        return this.playerUmano;
    }

    /** Restituisce il riferimento al GiocatoreAI della partita corrente. */
    public GiocatoreAI getGiocatoreAI() {
        return this.ai;
    }

    public GiocatoreAI getGiocatoreEst() {
        return this.playerEst;
    }

    public GiocatoreAI getGiocatoreOvest() {
        return this.playerOvest;
    }
}