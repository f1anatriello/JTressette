package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;

import model.Carta;
import ui.UIAssets;

/**
 * Canvas grafico leggero per partite 1v1 e 2v2.
 * - Sud cliccabile con sollevamento e bordo dorato (disegnata per ultima, sopra).
 * - Nord/Est/Ovest con retro (o fronte in debug); Est/Ovest ruotate orizzontali.
 * - Terreno al centro.
 * - Layout e hit-test basati su INDICI, non su identità/equals() di Carta.
 */
public class HelperGrafico extends JPanel {

    /* ====== CONFIG FISSA (schermata 960x640) ====== */
    private static final int CARD_H        = 90;         // altezza carta "in piedi"
    private static final double RATIO      = 0.66;       // ~2:3
    private static final int CARD_W        = (int) (CARD_H * RATIO);
    private static final int X_DELTA       = 50;         // sovrapposizione orizzontale (Nord/Sud)
    private static final int Y_DELTA       = CARD_W / 2; // sovrapposizione verticale (Est/Ovest)
    private static final int PAD_X         = 10;         // padding laterale
    private static final int PAD_Y         = 10;         // padding alto
    private static final int BOTTOM_PAD    = 10;         // margine basso
    private static final int LIFT_SELECTED = 20;         // sollevamento selezione

    // bounding box per carte ruotate (Est/Ovest), sdraiate orizzontali
    private static final int SIDE_CARD_W   = CARD_H;     // larghezza bbox ruotata
    private static final int SIDE_CARD_H   = CARD_W;     // altezza  bbox ruotata

    /* ===== Stato mani ===== */
    private List<Carta> manoSud   = Collections.emptyList();
    private List<Carta> manoNord  = Collections.emptyList();
    private List<Carta> manoEst   = Collections.emptyList();
    private List<Carta> manoOvest = Collections.emptyList();

    /* ===== Terreno ===== */
    private List<Carta> terreno   = Collections.emptyList();

    /* ===== Selezione (solo Sud) ===== */
    private int selectedIndex = -1; // indice nella mano Sud

    /* ===== Bounds (per indici) ===== */
    private final List<Rectangle> southBounds = new ArrayList<>();
    private final List<Rectangle> northBounds = new ArrayList<>();
    private final List<Rectangle> eastBounds  = new ArrayList<>();
    private final List<Rectangle> westBounds  = new ArrayList<>();

    /* ===== Altro ===== */
    private boolean showOpponentsFaceUp = false;      // debug
    private Dimension lastLaidOutSize = new Dimension(0, 0);

    /* ===== Costruttori ===== */
    public HelperGrafico(List<Carta> manoSud, List<Carta> manoNord, List<Carta> manoEst, List<Carta> manoOvest) {
        setHands(manoSud, manoNord, manoEst, manoOvest);

        setBackground(new Color(0, 128, 0));
        setDoubleBuffered(true);

        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { handleSouthClick(e.getPoint()); }
        });

        // Re-layout sicuro su resize
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) {
                if (!getSize().equals(lastLaidOutSize)) { recomputeLayout(); repaint(); }
            }
        });
    }

    /** Overload 1v1. */
    public HelperGrafico(List<Carta> manoSud, List<Carta> manoNord) {
        this(manoSud, manoNord, Collections.emptyList(), Collections.emptyList());
    }

    /* ================= API ================= */

    /** 2v2 */
    public final void setHands(List<Carta> manoSud, List<Carta> manoNord, List<Carta> manoEst, List<Carta> manoOvest) {
        this.manoSud   = (manoSud   != null) ? new ArrayList<>(manoSud)   : Collections.emptyList();
        this.manoNord  = (manoNord  != null) ? new ArrayList<>(manoNord)  : Collections.emptyList();
        this.manoEst   = (manoEst   != null) ? new ArrayList<>(manoEst)   : Collections.emptyList();
        this.manoOvest = (manoOvest != null) ? new ArrayList<>(manoOvest) : Collections.emptyList();

        if (selectedIndex >= this.manoSud.size()) selectedIndex = -1;
        recomputeLayout();
        repaint();
    }

    /** 1v1 */
    public final void setHands(List<Carta> manoSud, List<Carta> manoNord) {
        setHands(manoSud, manoNord, Collections.emptyList(), Collections.emptyList());
    }

    public void setTerreno(List<Carta> terreno) {
        this.terreno = (terreno != null) ? new ArrayList<>(terreno) : Collections.emptyList();
        repaint();
    }

    public void refresh() { revalidate(); repaint(); }

    /** Se nessuna carta è selezionata, seleziona la prima della mano Sud. */
    public void selectFirstIfNone() {
        if (selectedIndex < 0 && !manoSud.isEmpty()) { selectedIndex = 0; repaint(); }
    }

    /** Seleziona una carta per indice nella mano Sud. */
    public boolean selectAtIndex(int idx) {
        if (idx < 0 || idx >= manoSud.size()) return false;
        selectedIndex = idx; repaint(); return true;
    }

    /** Carta attualmente selezionata nella mano Sud (può essere null). */
    public Carta getSelected() {
        return (selectedIndex >= 0 && selectedIndex < manoSud.size()) ? manoSud.get(selectedIndex) : null;
    }

    /** Mostra fronte avversari (debug). */
    public void setShowOpponentsFaceUp(boolean show) { this.showOpponentsFaceUp = show; repaint(); }

    @Override public Dimension getPreferredSize() { return new Dimension(900, 700); }

    @Override public void addNotify() {
        super.addNotify();
        SwingUtilities.invokeLater(() -> { recomputeLayout(); repaint(); });
    }

    @Override public void invalidate() {
        super.invalidate();
        recomputeLayout();
    }

    /* ================= Layout ================= */

    private void recomputeLayout() {
        southBounds.clear();
        northBounds.clear();
        eastBounds.clear();
        westBounds.clear();

        if (getWidth() <= 0 || getHeight() <= 0) return;

        // Sud (orizzontale in basso)
        int totW_S = CARD_W + Math.max(0, (manoSud.size() - 1)) * X_DELTA;
        int sx = Math.max(PAD_X, (getWidth() - totW_S) / 2);
        int sy = getHeight() - BOTTOM_PAD - CARD_H;
        for (int i = 0; i < manoSud.size(); i++) {
            southBounds.add(new Rectangle(sx, sy, CARD_W, CARD_H));
            sx += X_DELTA;
        }

        // Nord (orizzontale in alto)
        int totW_N = CARD_W + Math.max(0, (manoNord.size() - 1)) * X_DELTA;
        int nx = Math.max(PAD_X, (getWidth() - totW_N) / 2);
        int ny = PAD_Y;
        for (int i = 0; i < manoNord.size(); i++) {
            northBounds.add(new Rectangle(nx, ny, CARD_W, CARD_H));
            nx += X_DELTA;
        }

        // Est (colonna destra, carte orizzontali ruotate)
        int totH_E = SIDE_CARD_H + Math.max(0, (manoEst.size() - 1)) * Y_DELTA;
        int ex = getWidth() - PAD_X - SIDE_CARD_W;
        int ey = Math.max(PAD_Y, (getHeight() - totH_E) / 2);
        for (int i = 0; i < manoEst.size(); i++) {
            eastBounds.add(new Rectangle(ex, ey, SIDE_CARD_W, SIDE_CARD_H));
            ey += Y_DELTA;
        }

        // Ovest (colonna sinistra, carte orizzontali ruotate)
        int totH_W = SIDE_CARD_H + Math.max(0, (manoOvest.size() - 1)) * Y_DELTA;
        int wx = PAD_X;
        int wy = Math.max(PAD_Y, (getHeight() - totH_W) / 2);
        for (int i = 0; i < manoOvest.size(); i++) {
            westBounds.add(new Rectangle(wx, wy, SIDE_CARD_W, SIDE_CARD_H));
            wy += Y_DELTA;
        }

        lastLaidOutSize = getSize();
    }

    /* ================= Rendering ================= */

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!getSize().equals(lastLaidOutSize)) recomputeLayout();

        final Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        final UIAssets assets = UIAssets.getInstance();
        final Image back = assets.getImmagineRetroCarta();

        // Terreno (fila orizzontale al centro)
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

        // Nord
        for (int i = 0; i < northBounds.size(); i++) {
            Rectangle r = northBounds.get(i);
            Image img = showOpponentsFaceUp ? assets.getImmagineCarta(manoNord.get(i)) : back;
            g2.drawImage(img, r.x, r.y, r.width, r.height, (ImageObserver) null);
            g2.setColor(showOpponentsFaceUp ? Color.BLACK : Color.DARK_GRAY);
            g2.drawRect(r.x, r.y, r.width, r.height);
        }

        // Ovest (ruotata -90°)
        for (int i = 0; i < westBounds.size(); i++) {
            Rectangle r = westBounds.get(i);
            Image img = showOpponentsFaceUp ? assets.getImmagineCarta(manoOvest.get(i)) : back;
            drawCardRotated(g2, img, r, /*clockwise=*/false);
            g2.setColor(showOpponentsFaceUp ? Color.BLACK : Color.DARK_GRAY);
            g2.drawRect(r.x, r.y, r.width, r.height);
        }

        // Est (ruotata +90°)
        for (int i = 0; i < eastBounds.size(); i++) {
            Rectangle r = eastBounds.get(i);
            Image img = showOpponentsFaceUp ? assets.getImmagineCarta(manoEst.get(i)) : back;
            drawCardRotated(g2, img, r, /*clockwise=*/true);
            g2.setColor(showOpponentsFaceUp ? Color.BLACK : Color.DARK_GRAY);
            g2.drawRect(r.x, r.y, r.width, r.height);
        }

        // === Sud: prima non selezionate (dietro), poi la selezionata (sopra, sollevata) ===
        for (int i = 0; i < manoSud.size(); i++) {
            if (i == selectedIndex) continue;
            Rectangle r = southBounds.get(i);
            Image img = assets.getImmagineCarta(manoSud.get(i));
            g2.drawImage(img, r.x, r.y, r.width, r.height, this);
            g2.setColor(Color.BLACK);
            g2.drawRect(r.x, r.y, r.width, r.height);
        }
        if (selectedIndex >= 0 && selectedIndex < manoSud.size()) {
            Rectangle r = southBounds.get(selectedIndex);
            int drawY = r.y - LIFT_SELECTED;
            Image img = assets.getImmagineCarta(manoSud.get(selectedIndex));
            g2.drawImage(img, r.x, drawY, r.width, r.height, this);
            g2.setColor(Color.BLACK);
            g2.drawRect(r.x, drawY, r.width, r.height);

            // bordo dorato completo intorno alla carta selezionata (sta sopra, niente clip)
            g2.setColor(new Color(255, 215, 0));
            g2.setStroke(new BasicStroke(3f));
            g2.drawRect(r.x + 1, drawY + 1, r.width - 3, r.height - 3);
        }

        g2.dispose();
    }

    /* ================= Helpers ================= */

    private void handleSouthClick(Point p) {
        int newSel = -1;
        // cerca da destra a sinistra per rispettare lo z-order dell'overlap
        for (int i = southBounds.size() - 1; i >= 0; i--) {
            Rectangle r = southBounds.get(i);
            // se è quella selezionata, considera anche l'area sollevata
            Rectangle hit = (i == selectedIndex)
                    ? new Rectangle(r.x, r.y - LIFT_SELECTED, r.width, r.height)
                    : r;
            if (hit.contains(p)) { newSel = i; break; }
        }
        if (newSel != selectedIndex) { selectedIndex = newSel; repaint(); }
    }

    /** Disegna una carta ruotata di 90° (clockwise se true, anti se false). */
    private void drawCardRotated(Graphics2D g2, Image img, Rectangle r, boolean clockwise) {
        if (img == null) { // fallback semplice
            g2.setColor(Color.WHITE); g2.fillRect(r.x, r.y, r.width, r.height);
            return;
        }
        double cx = r.getCenterX(), cy = r.getCenterY();
        double theta = clockwise ? Math.PI / 2 : -Math.PI / 2;

        AffineTransform old = g2.getTransform();
        g2.rotate(theta, cx, cy);

        // Dopo la rotazione, l'immagine "dritta" ha dimensioni (CARD_W, CARD_H)
        int w = CARD_W, h = CARD_H;
        int x = (int) Math.round(cx - w / 2.0);
        int y = (int) Math.round(cy - h / 2.0);

        g2.drawImage(img, x, y, w, h, this);
        g2.setTransform(old);
    }

    public void clearSelection() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'clearSelection'");
    }
}
