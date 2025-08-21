package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import javax.swing.*;
import model.Carta;
import ui.UIAssets;

/** Classe di aiuto per grafica leggera durante la partita 2v2. */
public class GameSupport2v2 extends JPanel {

    // ====== CONFIG FISSA (schermata 960x640) ======
    private static final int CARD_H       = 90;        // altezza carta
    private static final double RATIO     = 0.66;      // ~ 2:3
    private static final int CARD_W       = (int) (CARD_H * RATIO);
    private static final int X_DELTA      = 50;        // sovrapposizione orizzontale
    private static final int PAD_X        = 10;
    private static final int PAD_Y        = 10;
    private static final int BOTTOM_PAD   = 10;
    private static final int LIFT_SELECTED= 20;

    // 4 mani
    private List<Carta> manoSud;
    private List<Carta> manoNord;
    private List<Carta> manoEst;
    private List<Carta> manoOvest;
    private List<Carta> terreno = Collections.emptyList();

    // solo per mano giocatore (Sud)
    private final Map<Carta, Rectangle> mapCardsSud = new LinkedHashMap<>();
    private Carta selected;

    public GameSupport2v2(
            List<Carta> manoSud,
            List<Carta> manoNord,
            List<Carta> manoEst,
            List<Carta> manoOvest) {

        this.manoSud  = (manoSud  != null) ? manoSud  : Collections.emptyList();
        this.manoNord = (manoNord != null) ? manoNord : Collections.emptyList();
        this.manoEst  = (manoEst  != null) ? manoEst  : Collections.emptyList();
        this.manoOvest= (manoOvest!= null) ? manoOvest: Collections.emptyList();

        setBackground(new Color(0, 128, 0));
        setDoubleBuffered(true);

        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (selected != null) {
                    Rectangle r = mapCardsSud.get(selected);
                    if (r != null) { r.y += LIFT_SELECTED; repaint(); }
                    selected = null;
                }
                for (int i = manoSud.size() - 1; i >= 0; i--) {
                    Carta c = manoSud.get(i);
                    Rectangle r = mapCardsSud.get(c);
                    if (r != null && r.contains(e.getPoint())) {
                        selected = c;
                        r.y -= LIFT_SELECTED;
                        repaint();
                        break;
                    }
                }
            }
        });
    }

    /** Aggiorna le mani dall'esterno. */
    public void setHands(List<Carta> manoSud,
                         List<Carta> manoNord,
                         List<Carta> manoEst,
                         List<Carta> manoOvest) {
        this.manoSud  = (manoSud  != null) ? manoSud  : Collections.emptyList();
        this.manoNord = (manoNord != null) ? manoNord : Collections.emptyList();
        this.manoEst  = (manoEst  != null) ? manoEst  : Collections.emptyList();
        this.manoOvest= (manoOvest!= null) ? manoOvest: Collections.emptyList();
    }

    public Carta getSelected() { 
        return selected; 
    }
    
    public void refresh() { 
        revalidate(); 
        repaint(); 
    }

    @Override public Dimension getPreferredSize() { return new Dimension(900, 700); }

    @Override
    public void invalidate() {
        super.invalidate();
        recomputeLayout();
    }

    private void recomputeLayout() {
        mapCardsSud.clear();
        if (getHeight() <= 0) return;

        // mano Sud (scoperte, selezionabili)
        int totW = CARD_W + Math.max(0, (manoSud.size() - 1)) * X_DELTA;
        int x = Math.max(PAD_X, (getWidth() - totW) / 2);
        int y = getHeight() - BOTTOM_PAD - CARD_H;

        for (Carta c : manoSud) {
            mapCardsSud.put(c, new Rectangle(x, y, CARD_W, CARD_H));
            x += X_DELTA;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        UIAssets assets = UIAssets.getInstance();
        Image retro = assets.getImmagineRetroCarta();

        // ===== Terreno =====
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

        // ===== Nord (in alto, retro) =====
        if (!manoNord.isEmpty()) {
            int totW = CARD_W + (manoNord.size() - 1) * X_DELTA;
            int x = Math.max(PAD_X, (getWidth() - totW) / 2);
            int y = PAD_Y;
            for (int i = 0; i < manoNord.size(); i++) {
                g2.drawImage(retro, x, y, CARD_W, CARD_H, this);
                x += X_DELTA;
            }
        }

        // ===== Est (a destra, retro) =====
        if (!manoEst.isEmpty()) {
            int totH = CARD_H + (manoEst.size() - 1) * (CARD_W/2); // verticale
            int y = Math.max(PAD_Y, (getHeight() - totH) / 2);
            int x = getWidth() - PAD_X - CARD_H; // ruotiamo "in verticale"
            for (int i = 0; i < manoEst.size(); i++) {
                g2.drawImage(retro, x, y, CARD_H, CARD_W, this);
                y += CARD_W/2;
            }
        }

        // ===== Ovest (a sinistra, retro) =====
        if (!manoOvest.isEmpty()) {
            int totH = CARD_H + (manoOvest.size() - 1) * (CARD_W/2);
            int y = Math.max(PAD_Y, (getHeight() - totH) / 2);
            int x = PAD_X;
            for (int i = 0; i < manoOvest.size(); i++) {
                g2.drawImage(retro, x, y, CARD_H, CARD_W, this);
                y += CARD_W/2;
            }
        }

        // ===== Sud (in basso, fronte) =====
        for (Carta c : manoSud) {
            Rectangle r = mapCardsSud.get(c);
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

    /** Se nessuna carta è selezionata, seleziona la prima della mano Sud. */
    public void selectFirstIfNone() {
        if (selected == null && !manoSud.isEmpty()) {
            selected = manoSud.get(0);
            Rectangle r = mapCardsSud.get(selected);
            if (r != null) r.y -= LIFT_SELECTED;
        }
    }

    public void setTerreno(List<Carta> terreno) {
        this.terreno = (terreno != null) ? terreno : Collections.emptyList();
    }
}
