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
import javax.swing.Timer;
import javax.swing.WindowConstants;
import model.*;
import ui.AudioManager;
import ui.UIConstants;
import view.DialogCelebrations;
import view.ClassificaView;
import view.GameView1v1;
import view.GameView2v2;
import view.MenuPrincipaleView;
import view.ProfiloView;

/**
 * Controller dell'applicazione: gestisce le azioni dell'interfaccia utente,
 * l'avvio delle partite e il ciclo di gioco. È un Observer in attesa
 * dell'evoluzione del modello (non ancora implementato completamente).
 * Implementa il pattern Singleton per garantire una singola istanza
 * dell'engine di gioco.
 * @see Observer
 * @author 1957447
 */
public class GameEngine implements Observer {

    /* ===================== CAMPI ===================== */
    private static GameEngine instance = null;
    private ClassificaView cv;
    private MenuPrincipaleView mpv;
    private ProfiloView pv;
    private UtentePojo player;
    private GiocatoreAI ai;
    private GiocatoreUmano playerUmano;
    private GiocatoreAI playerEst;
    private GiocatoreAI playerOvest;
    private GameView1v1 gameView;
    private GameView2v2 gameView2v2;
    private JFrame mainFrame;
    private Partita partita;
    private List<Giocatore> ordineGioco2v2;


    /* =========== COSTRUTTORI - SINGLETON =============== */

    /**
     * Costruttore privato per il pattern Singleton.
     * @param player L'utente che ha effettuato il login.
     */
    private GameEngine(UtentePojo player) {
        this.player = player;
    }

    /**
     * Restituisce l'istanza singleton del GameEngine.
     * @param player L'utente che ha effettuato il login.
     * @return L'istanza singleton di GameEngine.
     */
    public static GameEngine getInstance(UtentePojo player) {
        if (instance == null) {
            instance = new GameEngine(player);
        }
        return instance;
    }

    /* ===================== OBSERVER ===================== */

    /**
     * Aggiorna le statistiche dell'utente quando la partita termina.
     * @param o   L'oggetto osservato (GiocatoreUmano).
     * @param arg L'argomento passato (Boolean che indica se ha vinto).
     */
    @Override
    public void update(java.util.Observable o, Object arg) {
        if (o == playerUmano && arg instanceof Boolean) {
            boolean haVinto = (Boolean) arg;
            if (haVinto) player.incrementaVinte();
            else         player.incrementaPerse();

            // opzionale: mi tolgo dall’osservazione finita la partita
            playerUmano.deleteObserver(this);
        }
    }

    /* ================================================================
       =                       PARTITA 1v1                            =
       ================================================================ */

    /**
     * Avvia la partita 1v1 inizializzando la GameView e il primo turno. Prepara
     * i riferimenti a {@link GiocatoreUmano} e {@link GiocatoreAI} e lascia
     * partire il turno dell'umano.
     * @param p La partita 1v1 da avviare.
     */
    public void iniziaPartita1v1(Partita1v1 p) {
        gameView = GameView1v1.getInstance(
            this,
            player.getUsername(),
            new ImageIcon(player.getAvatarPath()),
            p.getGiocatori().get(1).getNome(),
            new ImageIcon("images/avatars/avatar"+ (1 + (int)(Math.random() * 6)) +".png"),
            p.getGiocatori().get(0).getMano(),
            p.getGiocatori().get(1).getMano()
        );

        setScreen(gameView);

        playerUmano = (GiocatoreUmano) p.getGiocatori().get(0);
        ai = (GiocatoreAI) p.getGiocatori().get(1);
        setOrder1v1(p, playerUmano, ai); // parte l'umano per la prima mano

        // Avvia il primo turno: parte l’umano
        giocaTurnoUmano1V1(p, playerUmano, ai);
    }

    /**
     * Turno con umano che inizia nella partita1v1.
     * @param partita La partita in corso.
     * @param umano   Il giocatore umano.
     * @param ai      L'avversario AI.
     */
    private void giocaTurnoUmano1V1(Partita1v1 partita, GiocatoreUmano umano, GiocatoreAI ai) {
        gameView.unlockPlay();
        setOrder1v1(partita, umano, ai);
        gameView.highlightAvatar(umano.getNome());

        umano.setOnCartaSceltaListener(carta -> {
            if (carta == null) return;

            AudioManager.getInstance().stop();
            // 1) l'umano gioca e mostri SUBITO la 1ª carta sul terreno
            giocaCarta(umano, carta, partita);
            showTerreno(partita);
            gameView.highlightAvatar(ai.getNome());

            // 2) dopo un attimo, gioca l'AI e mostri entrambe
            after(180, () -> {
                Carta cartaAI = ai.scegliCarta(partita);
                giocaCarta(ai, cartaAI, partita);
                showTerreno(partita);

                // 3) risoluzione mano
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
                        finePartita1v1(partita);
                        return;
                    }

                    if (vincente instanceof GiocatoreUmano) {
                        AudioManager.getInstance().play("audio/win.wav"); // esempio di utilizzo di AudioManager
                        giocaTurnoUmano1V1(partita, umano, ai);
                    } else {
                        giocaTurnoAI1V1(partita, umano, ai);
                    }
                });
            });
        });
    }

    /**
     * Turno con AI che inizia nella partita1v1.
     * @param partita La partita in corso.
     * @param umano   Il giocatore umano.
     * @param ai      L'avversario AI.
     */
    private void giocaTurnoAI1V1(Partita1v1 partita, GiocatoreUmano umano, GiocatoreAI ai) {
        gameView.unlockPlay();
        setOrder1v1(partita, ai, umano);
        gameView.highlightAvatar(ai.getNome());

        Carta cartaAI = ai.scegliCarta(partita);
        giocaCarta(ai, cartaAI, partita);
        showTerreno(partita);

        gameView.highlightAvatar(umano.getNome());
        umano.setOnCartaSceltaListener(carta -> {
            if (carta == null) return;
            giocaCarta(umano, carta, partita);
            showTerreno(partita);

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
                    finePartita1v1(partita);
                    return;
                }

                if (vincente instanceof GiocatoreUmano) {
                    AudioManager.getInstance().play("audio/win.wav"); // esempio di utilizzo di AudioManager
                    giocaTurnoUmano1V1(partita, umano, ai);
                } else {
                    giocaTurnoAI1V1(partita, umano, ai);
                }
            });
        });
    }

    /**
     * Imposta l'ordine dei giocatori (1v1) in modo che first giochi per primo.
     * @param p     La partita in corso.
     * @param first  Il giocatore che deve iniziare.
     * @param second L'altro giocatore.
     */
    private void setOrder1v1(Partita p, Giocatore first, Giocatore second) {
        List<Giocatore> g = p.getGiocatori();
        if (g.size() == 2 && (g.get(0) != first || g.get(1) != second)) {
            g.set(0, first);
            g.set(1, second);
        }
    }

    /**
     * Verifica la fine della partita 1v1, notifica il giocatore umano
     * e mostra il dialogo di vittoria/sconfitta.
     * @param p La partita 1v1 appena terminata.
     */
    private void finePartita1v1(Partita1v1 p) {
        AudioManager.getInstance().playLoop("audio/bigwin.wav");
        Giocatore vincitore = p.vincitore1v1();

        // Notifica al giocatore umano se ha vinto o perso
        boolean haVintoUmano = (vincitore instanceof GiocatoreUmano);
        playerUmano.notificaFinePartita(haVintoUmano);

        // Dialog celebrativo con confetti
        DialogCelebrations.showVictoryDialog(
            mainFrame,
            "La partita è finita!",
            "🏆 Vincitore: " + vincitore.getNome() + " 🏆",
            () -> {
                AudioManager.getInstance().stop();
                AudioManager.getInstance().play("audio/button.wav");
                GameView1v1.disposeInstance();
                visualizzaMenu();
            }
        );
    }

    /* ================================================================
       =                        PARTITA 2v2                           =
       ================================================================ */

    
    /**
     * Avvia la partita 2v2 inizializzando la GameView e il primo turno. Prepara
     * i riferimenti a {@link GiocatoreUmano} e {@link GiocatoreAI} e lascia
     * partire il turno dell'umano. Prepara anche la lista con l'ordine di gioco.
     * @param p La partita 2v2 da avviare.
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
            new ImageIcon("images/avatars/avatar" + (1 + (int)(Math.random() * 6)) +".png"),
            p.getGiocatori().get(1).getMano(),
            // Giocatore nord (compagno di squadra)
            p.getGiocatori().get(2).getNome(),
            new ImageIcon("images/avatars/avatar" + (1 + (int)(Math.random() * 6)) + ".png"),
            p.getGiocatori().get(2).getMano(),
            // Giocatore Ovest (avversario 2)
            p.getGiocatori().get(3).getNome(),
            new ImageIcon("images/avatars/avatar" + (1 + (int)(Math.random() * 6)) + ".png"),
            p.getGiocatori().get(3).getMano()
        );

        setScreen(gameView2v2);

        GiocatoreUmano giocatoreSud  = (GiocatoreUmano) p.getGiocatori().get(0);
        GiocatoreAI aiNord           = (GiocatoreAI) p.getGiocatori().get(1);
        GiocatoreAI aiEst            = (GiocatoreAI) p.getGiocatori().get(2);
        GiocatoreAI aiOvest          = (GiocatoreAI) p.getGiocatori().get(3);

        // Ordine di gioco di partenza: Sud, Ovest, Nord, Est
        List<Giocatore> g = List.of(giocatoreSud, aiOvest, aiNord, aiEst);
        
        giocaTurnoUmano2v2(p, g, giocatoreSud);
        showTerreno(p);
    }

    /**
     * Turno con umano che inizia nella partita2v2.
     * @param partita
     * @param ordineTurno
     * @param umano
     */
    private void giocaTurnoUmano2v2(Partita2v2 partita, List<Giocatore> ordineTurno, GiocatoreUmano umano) {
        gameView2v2.unlockPlay();
        gameView2v2.highlightAvatar(umano.getNome());

        umano.setOnCartaSceltaListener(carta -> {
            if (carta == null) return;

            AudioManager.getInstance().stop();
            giocaCarta(umano, carta, partita);
            // Dopo l’umano giocano gli altri
            giocaSequenzaRec(partita, ordineTurno, ordineTurno.indexOf(umano) + 1);
        });
    }

    /**
     * Turno con AI che inizia nella partita2v2.
     * @param partita
     * @param giocatori
     * @param aiCorrente
     */
    private void giocaTurnoAI2v2(Partita2v2 partita, List<Giocatore> giocatori, GiocatoreAI aiCorrente) {
        gameView2v2.unlockPlay();
        gameView2v2.highlightAvatar(aiCorrente.getNome());
        Carta cartaAI = aiCorrente.scegliCarta(partita);
        giocaCarta(aiCorrente, cartaAI, partita);

        showTerreno(partita);

        // Poi gli altri a giro
        giocaSequenzaRec(partita, giocatori, giocatori.indexOf(aiCorrente) + 1);
    }


    /**
     * Sequenza ricorsiva per far giocare a turno i giocatori in ordine.
     * @param partita
     * @param giocatori
     * @param index
     */
    private void giocaSequenzaRec(Partita2v2 partita, List<Giocatore> giocatori, int index) {
        if (index >= giocatori.size()) {
            after(400, () -> risolviMano(partita, giocatori));
            return;
        }

        Giocatore corrente = giocatori.get(index);
        gameView2v2.highlightAvatar(corrente.getNome());
        if (corrente instanceof GiocatoreAI) {
            after(400, () -> {
                Carta cartaAI = ((GiocatoreAI) corrente).scegliCarta(partita);
                giocaCarta(corrente, cartaAI, partita);
                showTerreno(partita);
                giocaSequenzaRec(partita, giocatori, index + 1);
            });

        } else if (corrente instanceof GiocatoreUmano) {
            ((GiocatoreUmano) corrente).setOnCartaSceltaListener(carta -> {
                if (carta == null) return;
                AudioManager.getInstance().stop();
                giocaCarta(corrente, carta, partita);
                giocaSequenzaRec(partita, giocatori, index + 1);
            });
        }
    }

    /**
     * Risolvi la mano dopo che tutti hanno giocato, assegna i punti,
     * fa pescare e avvia il turno successivo o la fine partita.
     * @param partita La partita in corso.
     * @param giocatori La lista dei giocatori in ordine di gioco.
     */
    private void risolviMano(Partita2v2 partita, List<Giocatore> giocatori) {
        after(400, () -> {
            Giocatore vincente = partita.manoVintaDa(partita.getTerreno());
            double puntiMano = partita.getTerreno().stream()
                    .mapToDouble(Carta::getPunti)
                    .sum();
            vincente.aggiungiPunti(puntiMano);
            vincente.getSquadra().aggiungiPunti(puntiMano);

            partita.getTerreno().clear();

            // Riordina partendo dal vincente
            ordineGioco2v2 = giocatoriSenza(vincente, giocatori);

            // Pesca
            if (!partita.isMazzoVuoto()) {
                for (int i = 0; i < ordineGioco2v2.size(); i++) {
                    Giocatore g = ordineGioco2v2.get(i);
                    g.riceviCarta(partita.getMazzo().pesca());
                }
            }

            // Aggiorna le mani dei 4
            gameView2v2.refreshHands(
                giocatori.get(0).getMano(),
                giocatori.get(1).getMano(),
                giocatori.get(2).getMano(),
                giocatori.get(3).getMano()
            );

            // Pulisci terreno e avvia nuovo turno
            after(400, () -> {
                partita.clearTerreno();
                showTerreno(partita);

                boolean finita = partita.isMazzoVuoto() &&
                                 ordineGioco2v2.get(0).getMano().isEmpty() &&
                                 ordineGioco2v2.get(1).getMano().isEmpty() &&
                                 ordineGioco2v2.get(2).getMano().isEmpty() &&
                                 ordineGioco2v2.get(3).getMano().isEmpty();
                if (finita) {
                    finePartita2v2(partita);
                    return;
                }

                if (vincente instanceof GiocatoreUmano || vincente.getSquadra().equals(playerUmano.getSquadra())) {
                    AudioManager.getInstance().play("audio/win.wav"); 
                    giocaTurnoUmano2v2(partita, ordineGioco2v2, (GiocatoreUmano) vincente);
                } else {
                    giocaTurnoAI2v2(partita, ordineGioco2v2, (GiocatoreAI) vincente);
                }
            });
        });
    }


    /**
     * Ritorna la lista dei giocatori iniziando da vincente e proseguendo in ordine circolare.
     * @param vincente
     * @param giocatori
     * @return La lista dei giocatori iniziando dal vincente.
     */
    private List<Giocatore> giocatoriSenza(Giocatore vincente, List<Giocatore> giocatori) {
        List<Giocatore> result = new ArrayList<>();
        int index = giocatori.indexOf(vincente);
        result.add(vincente);
        for (int i = 0; i < 3; i++) {
            result.add(giocatori.get((index + i + 1) % giocatori.size()));
        }
        for (Giocatore g : result) {
            System.out.println(g.getNome());
        }
        return result;
    }

    /**
     * Verifica la fine della partita 2v2, notifica il giocatore umano
     * e mostra il dialogo di vittoria/sconfitta.
     * @param p La partita 2v2 appena terminata.
     */
    private void finePartita2v2(Partita2v2 p) {
        AudioManager.getInstance().playLoop("audio/bigwin.wav");
        Giocatore vincitore = p.vincitore2v2();

        // Notifica al giocatore umano se ha vinto o perso
        boolean haVintoUmano = (vincitore instanceof GiocatoreUmano);
        playerUmano.notificaFinePartita(haVintoUmano);

        // Dialog celebrativo
        DialogCelebrations.showVictoryDialog(
            mainFrame,
            "La Partita è Finita!",
            "🏆 Vincitore: " + vincitore.getSquadra().getNome() + " 🏆",
            () -> {
                AudioManager.getInstance().stop();
                AudioManager.getInstance().play("audio/button.wav");
                GameView2v2.disposeInstance();
                visualizzaMenu();
            }
        );
    }

    /* ================================================================
       =                         UTILITIES                            =
       ================================================================ */

    /**
     * Avvia una nuova partita. Se {@code numPlayer == 2} avvia una 1v1; altrimenti 2v2.
     * @param numPlayer Numero di giocatori (2 o 4).
     */
    public void avviaNuovaPartita(int numPlayer) {
        if (numPlayer == 2) {
            List<Giocatore> giocatori = new ArrayList<>();
            playerUmano = new GiocatoreUmano(this.player.getUsername());
            giocatori.add(playerUmano);
            ai = new GiocatoreAI();
            giocatori.add(ai);

            playerUmano.addObserver(this);

            partita = new Partita1v1(giocatori);
            partita.inizializzaPartita();
            iniziaPartita1v1((Partita1v1) partita);
        } else {
            List<Giocatore> giocatori = new ArrayList<>();
            playerUmano = new GiocatoreUmano(this.player.getUsername());
            ai = new GiocatoreAI();
            playerEst = new GiocatoreAI();
            playerOvest = new GiocatoreAI();
            giocatori.add(playerUmano);
            giocatori.add(ai);
            giocatori.add(playerEst);
            giocatori.add(playerOvest);

            playerUmano.addObserver(this);

            partita = new Partita2v2(giocatori);
            partita.inizializzaPartita();
            iniziaPartita2v2((Partita2v2) partita);
        }
    }

    /**
     * Gestisce il gioco di una carta da parte di un giocatore,
     * aggiornando la mano del giocatore e il terreno della partita.
     * @param g Giocatore che gioca la carta.
     * @param c Carta giocata.
     */
    public void giocaCarta(Giocatore g, Carta c, Partita p) {
        if (c == null) return;
        List<Carta> mano = g.getMano();
        c.setGiocatore(g);
        mano.remove(c);
        g.setMano(mano);
        p.getTerreno().add(c);
        AudioManager.getInstance().play("audio/card-sound.wav"); // esempio di utilizzo di AudioManager
    }


    /** 
     * Esegue un'azione dopo un ritardo specificato in millisecondi.
     * Serve per dare un po' di respiro visivo tra le azioni, una sorta di
     * simulazione di "turno" anche per l'AI.
     * @param ms     Ritardo in millisecondi.
     * @param action Azione da eseguire dopo il ritardo.
     */
    private void after(int ms, Runnable action) {
        new Timer(ms, e -> {
            ((Timer) e.getSource()).stop();
            action.run();
        }).start();
    }

    /** 
     * Mostra il terreno di gioco aggiornato nella GameView.
     * @param p La partita in corso.
     */
    private void showTerreno(Partita p) {
        if (p instanceof Partita1v1) {
            gameView.setTerreno(p.getTerreno());
        } else if (p instanceof Partita2v2) {
            gameView2v2.setTerreno(p.getTerreno());
        }
    }

    /** Reset stato partita e riferimenti AI. */
    public void reset() {
        this.partita.reset();
        this.ai = null;
        this.playerEst = null;
        this.playerOvest = null;
    }

    /** 
     * Getter utente loggato.
     * @return L'istanza di UtentePojo che rappresenta l'utente loggato.
     */
    public UtentePojo getPlayer() {
        return player;
    }

    /** 
     * Getter umano corrente. 
     * @return L'istanza di GiocatoreUmano che rappresenta il giocatore umano.
     * */
    public GiocatoreUmano getGiocatoreUmano() {
        return this.playerUmano;
    }

    /** 
     * Getter AI corrente. 
     * @return L'istanza di GiocatoreAI che rappresenta l'avversario AI.
    */
    public GiocatoreAI getGiocatoreAI() {
        return this.ai;
    }

    /**
     * Getter AI Est (2v2).
     * @return L'istanza di GiocatoreAI che rappresenta il giocatore Est.
     */
    public GiocatoreAI getGiocatoreEst() {
        return this.playerEst;
    }

    /**
     * Getter AI Ovest (2v2).
     * @return L'istanza di GiocatoreAI che rappresenta il giocatore Ovest.
     */
    public GiocatoreAI getGiocatoreOvest() {
        return this.playerOvest;
    }

    /** 
     * Getter partita corrente. 
     * @return L'istanza di Partita che rappresenta la partita in corso.
     */
    public Partita getPartita() {
        return this.partita;
    }

    /**
     * Registra il JFrame principale dell'applicazione.
     * @param frame Il JFrame principale.
     */
    public void registerMainFrame(JFrame frame) {
        this.mainFrame = frame;
    }

    /* ================================================================
       =                     CAMBIO SCHERMATE / UI                    =
       ================================================================ */

    /** Avvia il frame principale e mostra il menu dopo login. */
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


    /** Visualizza il menu principale. */
    public void visualizzaMenu() {
        mpv = MenuPrincipaleView.getInstance(this);
        setScreen(mpv);
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

    /** Visualizza la classifica/statistiche. */
    public void visualizzaStatistiche() {
        cv = new ClassificaView(this);
        cv.mostraClassifica(UserRepository.getClassifica());
        setScreen(cv);
    }

    /* ---------- Gestione dimensioni e cambio pannello ---------- */

    /**
     * Determina se una vista ha dimensioni fisse o adattabili.
     * @param panel
     * @return True se la vista ha dimensioni fisse, false altrimenti.
     */
    private boolean isFixedSizedView(JPanel panel) {
        return !(panel instanceof MenuPrincipaleView ||
                 panel instanceof ProfiloView);
    }

    /**
     * Imposta il pannello corrente nel JFrame principale, adattando
     * le dimensioni in base al tipo di pannello.
     * @param panel Il pannello da visualizzare.
     */
    private void setScreen(JPanel panel) {
        boolean fixed = isFixedSizedView(panel);

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
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

}
