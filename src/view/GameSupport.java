package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import javax.swing.*;
import model.Carta;
import ui.UIAssets;

/**
 * Classe di aiuto per grafica leggera durante la partita.
 * Responsabile del disegno della mano del giocatore, della mano dell'avversario
 * e del terreno di gioco. Inoltre gestisce la selezione di una carta nella mano
 * del giocatore tramite un semplice listener di mouse.
 */
public class GameSupport extends JPanel {

    // ====== CONFIG FISSA (schermata 960x640) ======
    private static final int CARD_H        = 90;        // altezza carta
    private static final double RATIO      = 0.66;      // ~ 2:3
    private static final int CARD_W        = (int) (CARD_H * RATIO);
    private static final int X_DELTA       = 50;        // sovrapposizione orizzontale
    private static final int PAD_X         = 10;        // padding laterale
    private static final int TOP_Y         = 10;        // y mano avversario
    private static final int BOTTOM_PAD    = 10;        // margine basso
    private static final int LIFT_SELECTED = 20;        // sollevamento selezione

    private List<Carta> manoGiocatore;
    private List<Carta> manoAvversario;
    private List<Carta> terreno = Collections.emptyList();

    // solo per mano giocatore
    private final Map<Carta, Rectangle> mapCards = new LinkedHashMap<>();
    private Carta selected;

    public GameSupport(List<Carta> manoGiocatore, List<Carta> manoAvversario) {
        this.manoGiocatore = (manoGiocatore != null) ? manoGiocatore : Collections.emptyList();
        this.manoAvversario = (manoAvversario != null) ? manoAvversario : Collections.emptyList();

        setBackground(new Color(0, 128, 0));
        setDoubleBuffered(true);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // riabbassa la precedente selezione
                if (selected != null) {
                    Rectangle r = mapCards.get(selected);
                    if (r != null) {
                        r.y += LIFT_SELECTED;
                        repaint();
                    }
                    selected = null;
                }
                // cerca dall'ultima alla prima (z-order)
                for (int i = manoGiocatore.size() - 1; i >= 0; i--) {
                    Carta c = manoGiocatore.get(i);
                    Rectangle r = mapCards.get(c);
                    if (r != null && r.contains(e.getPoint())) {
                        selected = c;
                        r.y -= LIFT_SELECTED; // alza
                        repaint();
                        break;
                    }
                }
            }
        });
    }

    /**
     * Aggiorna le mani dall'esterno. Dopo aver impostato le mani, chiama {@link #refresh()}
     * dalla vista per forzare il ricalcolo del layout e il ridisegno.
     */
    public void setHands(List<Carta> manoGiocatore, List<Carta> manoAvversario) {
        this.manoGiocatore = (manoGiocatore != null) ? manoGiocatore : Collections.emptyList();
        this.manoAvversario = (manoAvversario != null) ? manoAvversario : Collections.emptyList();
    }

    /**
     * Restituisce la carta attualmente selezionata dall'utente. Può essere null se nessuna
     * carta è selezionata.
     */
    public Carta getSelected() {
        return selected;
    }

    /**
     * Ricalcola il layout e ridisegna il componente. Da chiamare dopo aver
     * modificato le mani o il terreno.
     */
    public void refresh() {
        revalidate();
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(900, 700);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        recomputeLayout();
    }

    /**
     * Calcola le posizioni delle carte della mano del giocatore nella parte bassa
     * del pannello. Viene richiamato automaticamente da {@link #invalidate()}.
     */
    private void recomputeLayout() {
        mapCards.clear();
        if (getHeight() <= 0) return;

        // mano giocatore (in basso)
        int totW = CARD_W + Math.max(0, (manoGiocatore.size() - 1)) * X_DELTA;
        int x = Math.max(PAD_X, (getWidth() - totW) / 2);     // centraggio dinamico
        int y = getHeight() - BOTTOM_PAD - CARD_H;            // fila bassa fissa

        for (Carta c : manoGiocatore) {
            mapCards.put(c, new Rectangle(x, y, CARD_W, CARD_H));
            x += X_DELTA;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        UIAssets assets = UIAssets.getInstance();

        // ===== mano avversario (retro) in alto =====
        if (!manoAvversario.isEmpty()) {
            int totW = CARD_W + (manoAvversario.size() - 1) * X_DELTA;
            int x = Math.max(PAD_X, (getWidth() - totW) / 2); // centraggio dinamico
            int y = TOP_Y;
            Image retro = assets.getImmagineRetroCarta();
            for (int i = 0; i < manoAvversario.size(); i++) {
                g2.drawImage(retro, x, y, CARD_W, CARD_H, this);
                g2.setColor(Color.DARK_GRAY);
                g2.drawRect(x, y, CARD_W, CARD_H);
                x += X_DELTA;
            }
        }
        // ===== terreno (carte sul tavolo, al centro) =====
        if (!terreno.isEmpty()) {
            int totW = CARD_W + (terreno.size() - 1) * X_DELTA;
            int x = Math.max(PAD_X, (getWidth() - totW) / 2);
            int y = getHeight() / 2 - CARD_H / 2;
            for (Carta c : terreno) {
                Image img = assets.getImmagineCarta(c);
                g2.drawImage(img, x, y, CARD_W, CARD_H, this);
                g2.setColor(Color.BLACK);
                g2.drawRect(x, y, CARD_W, CARD_H);
                x += X_DELTA;
            }
        }

        // ===== mano giocatore (fronte) in basso =====
        for (Carta c : manoGiocatore) {
            Rectangle r = mapCards.get(c);
            if (r == null) continue;
            Image img = assets.getImmagineCarta(c);
            g2.drawImage(img, r.x, r.y, r.width, r.height, this);
            g2.setColor(Color.BLACK);
            g2.drawRect(r.x, r.y, r.width, r.height);

            if (c.equals(selected)) {
                g2.setColor(new Color(255, 215, 0));
                g2.setStroke(new BasicStroke(3f));
                g2.drawRect(r.x + 1, r.y + 1, r.width - 3, r.height - 3);
            }
        }

        g2.dispose();
    }

    // dentro GameSupport.java (aggiungi in fondo alla classe, prima della chiusura)

    public boolean selectAtIndex(int idx) {
        if (manoGiocatore == null || manoGiocatore.isEmpty()) return false;
        if (idx < 0 || idx >= manoGiocatore.size()) return false;

        // abbassa l’eventuale selezione precedente
        if (selected != null) {
            Rectangle old = mapCards.get(selected);
            if (old != null) old.y += LIFT_SELECTED;
        }

        selected = manoGiocatore.get(idx);
        Rectangle r = mapCards.get(selected);
        if (r != null) r.y -= LIFT_SELECTED;

        repaint();
        return true;
    }

    /** Se non c'è selezione (o la carta selezionata non è più in mano) seleziona l’indice 0. */
    public boolean selectFirstIfNone() {
        if (selected == null || !manoGiocatore.contains(selected)) {
            return selectAtIndex(0);
        }
        return true; // già valida
    }


    /**
     * Imposta l'elenco delle carte sul terreno. Se viene passato null, il terreno
     * viene considerato vuoto.
     * @param terreno elenco di carte attualmente sul tavolo
     */
    public void setTerreno(List<Carta> terreno) {
        this.terreno = (terreno != null) ? terreno : Collections.emptyList();
    }
}