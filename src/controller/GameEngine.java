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
import ui.UIConstants;
import view.ClassificaView;
import view.GameView1v1;
import view.GameView2v2;
import view.MenuPrincipaleView;
import view.ProfiloView;

/**
 * Controller dell'applicazione: gestisce le azioni dell'interfaccia utente,
 * l'avvio delle partite e il ciclo di gioco. È un Observer in attesa
 * dell'evoluzione del modello (non ancora implementato completamente).
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

    private GameEngine(UtentePojo player) {
        this.player = player;
    }

    public static GameEngine getInstance(UtentePojo player) {
        if (instance == null) {
            instance = new GameEngine(player);
        }
        return instance;
    }

    /* ===================== OBSERVER ===================== */

    @Override
    public void update(java.util.Observable o, Object arg) {
        if (arg instanceof Partita1v1) {
            Giocatore vincitore = (Giocatore) o;
            if (vincitore instanceof GiocatoreUmano) {
                player.incrementaVinte();
            } else {
                player.incrementaPerse();
            }
        } else if (arg instanceof Partita2v2) {
            Giocatore vincitore = (Giocatore) o;
            if (vincitore instanceof GiocatoreUmano) {
                player.incrementaVinte();
            } else {
                player.incrementaPerse();
            }
        }
    }

    /* ================================================================
       =                       PARTITA 1v1                            =
       ================================================================ */

    /**
     * Avvia la partita 1v1 inizializzando la GameView e il primo turno. Prepara
     * i riferimenti a {@link GiocatoreUmano} e {@link GiocatoreAI} e lascia
     * partire il turno dell'umano.
     */
    public void iniziaPartita1v1(Partita1v1 p) {
        gameView = GameView1v1.getInstance(
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
        setOrder1v1(p, playerUmano, ai); // parte l'umano per la prima mano

        // Avvia il primo turno: parte l’umano
        giocaTurnoUmano1V1(p, playerUmano, ai);
    }

    /**
     * Turno con umano che inizia.
     */
    private void giocaTurnoUmano1V1(Partita1v1 partita, GiocatoreUmano umano, GiocatoreAI ai) {
        gameView.unlockPlay();
        setOrder1v1(partita, umano, ai);

        AudioManager.getInstance().playLoop("src/audio/timer.wav");
        umano.setOnCartaSceltaListener(carta -> {
            if (carta == null) return;

            AudioManager.getInstance().stop();
            // 1) l'umano gioca e mostri SUBITO la 1ª carta sul terreno
            giocaCarta(umano, carta, partita);
            showTerreno(partita);

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
                        AudioManager.getInstance().play("src/audio/win.wav"); // esempio di utilizzo di AudioManager
                        giocaTurnoUmano1V1(partita, umano, ai);
                    } else {
                        giocaTurnoAI1V1(partita, umano, ai);
                    }
                });
            });
        });
    }

    /**
     * Turno con AI che inizia.
     */
    private void giocaTurnoAI1V1(Partita1v1 partita, GiocatoreUmano umano, GiocatoreAI ai) {
        gameView.unlockPlay();

        setOrder1v1(partita, ai, umano);
        Carta cartaAI = ai.scegliCarta(partita);
        giocaCarta(ai, cartaAI, partita);
        showTerreno(partita);

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
                    AudioManager.getInstance().play("src/audio/win.wav"); // esempio di utilizzo di AudioManager
                    giocaTurnoUmano1V1(partita, umano, ai);
                } else {
                    giocaTurnoAI1V1(partita, umano, ai);
                }
            });
        });
    }

    /**
     * Imposta l'ordine dei giocatori (1v1) in modo che first giochi per primo.
     */
    private void setOrder1v1(Partita p, Giocatore first, Giocatore second) {
        List<Giocatore> g = p.getGiocatori();
        if (g.size() == 2 && (g.get(0) != first || g.get(1) != second)) {
            g.set(0, first);
            g.set(1, second);
        }
    }

    /**
     * Dialogo di fine partita 1v1.
     */
    private void finePartita1v1(Partita1v1 p) {
        AudioManager.getInstance().stop();
        Giocatore vincitore = p.vincitore1v1();

        update(vincitore, p);

        JOptionPane.showMessageDialog(
            mainFrame,
            "La partita è finita! Il vincitore è: " + vincitore.getNome(),
            "Partita Terminata",
            JOptionPane.INFORMATION_MESSAGE
        );
        GameView1v1.disposeInstance();
        // AudioManager.getInstance().playLoop("src/audio/menu-princ.wav");
        visualizzaMenu();
        reset();
    }

    /* ================================================================
       =                        PARTITA 2v2                           =
       ================================================================ */

    /**
     * Inizia una nuova partita 2v2.
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

        GiocatoreUmano giocatoreSud  = (GiocatoreUmano) p.getGiocatori().get(0);
        GiocatoreAI aiNord           = (GiocatoreAI) p.getGiocatori().get(1);
        GiocatoreAI aiEst            = (GiocatoreAI) p.getGiocatori().get(2);
        GiocatoreAI aiOvest          = (GiocatoreAI) p.getGiocatori().get(3);

        // Ordine di gioco di partenza: Sud, Ovest, Nord, Est
        List<Giocatore> g = List.of(giocatoreSud, aiOvest, aiNord, aiEst);
        
        giocaTurnoUmano2v2(p, g, giocatoreSud);
        showTerreno(p);
    }

    private void giocaTurnoUmano2v2(Partita2v2 partita, List<Giocatore> ordineTurno, GiocatoreUmano umano) {
        gameView2v2.unlockPlay();
        AudioManager.getInstance().playLoop("src/audio/timer.wav"); // esempio di utilizzo di AudioManager

        umano.setOnCartaSceltaListener(carta -> {
            if (carta == null) return;

            AudioManager.getInstance().stop();
            giocaCarta(umano, carta, partita);
            // Dopo l’umano giocano gli altri
            giocaSequenzaRec(partita, ordineTurno, ordineTurno.indexOf(umano) + 1);
        });
    }

    private void giocaTurnoAI2v2(Partita2v2 partita, List<Giocatore> giocatori, GiocatoreAI aiCorrente) {
        gameView2v2.unlockPlay();
        Carta cartaAI = aiCorrente.scegliCarta(partita);
        giocaCarta(aiCorrente, cartaAI, partita);

        showTerreno(partita);

        // Poi gli altri a giro
        giocaSequenzaRec(partita, giocatori, giocatori.indexOf(aiCorrente) + 1);
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
                showTerreno(partita);
                giocaSequenzaRec(partita, giocatori, index + 1);
            });

        } else if (corrente instanceof GiocatoreUmano) {
            AudioManager.getInstance().playLoop("src/audio/timer.wav"); // esempio di utilizzo di AudioManager
            ((GiocatoreUmano) corrente).setOnCartaSceltaListener(carta -> {
                if (carta == null) return;
                AudioManager.getInstance().stop();
                giocaCarta(corrente, carta, partita);
                giocaSequenzaRec(partita, giocatori, index + 1);
            });
        }
    }

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

                if (vincente instanceof GiocatoreUmano) {
                    AudioManager.getInstance().play("src/audio/win.wav"); // esempio di utilizzo di AudioManager
                    giocaTurnoUmano2v2(partita, ordineGioco2v2, (GiocatoreUmano) vincente);
                } else {
                    giocaTurnoAI2v2(partita, ordineGioco2v2, (GiocatoreAI) vincente);
                }
            });
        });
    }


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

    private void finePartita2v2(Partita2v2 p) {
        AudioManager.getInstance().stop();

        Giocatore vincitore = p.vincitore2v2();

        if (vincitore == null) {
            JOptionPane.showMessageDialog(
                mainFrame,
                "La partita è finita! La partita è finita in pareggio!",
                "Partita Terminata",
                JOptionPane.INFORMATION_MESSAGE
            );
            GameView2v2.disposeInstance();
            // AudioManager.getInstance().playLoop("src/audio/menu-princ.wav");
            visualizzaMenu();
            reset();
            return;
        }

        update(vincitore, p);

        JOptionPane.showMessageDialog(
            mainFrame,
            "La partita è finita! Il vincitore è: " + vincitore.getSquadra().getNome(),
            "Partita Terminata",
            JOptionPane.INFORMATION_MESSAGE
        );
        visualizzaMenu();
        reset();
    }

    /* ================================================================
       =                         UTILITIES                            =
       ================================================================ */

    /**
     * Avvia una nuova partita. Se {@code numPlayer == 2} avvia una 1v1; altrimenti 2v2.
     */
    public void avviaNuovaPartita(int numPlayer) {
        if (numPlayer == 2) {
            List<Giocatore> giocatori = new ArrayList<>();
            playerUmano = new GiocatoreUmano(this.player.getUsername());
            giocatori.add(playerUmano);
            ai = new GiocatoreAI();
            giocatori.add(ai);
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
            giocatori.add(playerEst);
            giocatori.add(ai);
            giocatori.add(playerOvest);
            partita = new Partita2v2(giocatori);
            partita.inizializzaPartita();
            iniziaPartita2v2((Partita2v2) partita);
        }
    }

    /**
     * Solo logica di stato: rimuove la carta dalla mano e la mette sul terreno.
     */
    public void giocaCarta(Giocatore g, Carta c, Partita p) {
        if (c == null) return;
        List<Carta> mano = g.getMano();
        c.setGiocatore(g);
        mano.remove(c);
        g.setMano(mano);
        p.getTerreno().add(c);
        AudioManager.getInstance().play("src/audio/card-sound.wav"); // esempio di utilizzo di AudioManager
    }


    /** Delay helper. */
    private void after(int ms, Runnable action) {
        new Timer(ms, e -> {
            ((Timer) e.getSource()).stop();
            action.run();
        }).start();
    }

    /** Mostra lo stato attuale del terreno. */
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

    /** Getter utente loggato. */
    public UtentePojo getPlayer() {
        return player;
    }

    /** Getter umano corrente. */
    public GiocatoreUmano getGiocatoreUmano() {
        return this.playerUmano;
    }

    /** Getter AI corrente. */
    public GiocatoreAI getGiocatoreAI() {
        return this.ai;
    }

    public GiocatoreAI getGiocatoreEst() {
        return this.playerEst;
    }

    public GiocatoreAI getGiocatoreOvest() {
        return this.playerOvest;
    }

    public Partita getPartita() {
        return this.partita;
    }

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

    private boolean isFixedSizedView(JPanel panel) {
        return !(panel instanceof MenuPrincipaleView ||
                 panel instanceof ProfiloView);
    }

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
