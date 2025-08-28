package view;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.*;
import javax.swing.*;

public final class Celebrations {

    private Celebrations() {}

    public static void showVictoryDialog(Frame owner, String title, String subtitle, Runnable onClose) {
        SwingUtilities.invokeLater(() -> {
            JDialog dialog = new JDialog(owner, "🎉 Partita Terminata 🎉", true);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setLayout(new BorderLayout());
            dialog.setMinimumSize(new Dimension(560, 380));
            dialog.setLocationRelativeTo(owner);

            // Header (titolo)
            JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
            lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 26f));
            lblTitle.setForeground(new Color(255, 230, 120));
            lblTitle.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));

            // Sottotitolo
            JLabel lblSub = new JLabel(subtitle, SwingConstants.CENTER);
            lblSub.setFont(lblSub.getFont().deriveFont(Font.BOLD, 20f));
            lblSub.setForeground(Color.WHITE);
            lblSub.setBorder(BorderFactory.createEmptyBorder(0, 16, 12, 16));

            // Pannello confetti (centro)
            ConfettiPanel confettiPanel = new ConfettiPanel();
            confettiPanel.setPreferredSize(new Dimension(560, 220));

            // Footer con bottone
            JButton btnOk = new JButton("Torna al menu");
            btnOk.setFont(btnOk.getFont().deriveFont(Font.BOLD, 16f));
            btnOk.setFocusPainted(false);
            btnOk.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
            JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
            footer.setOpaque(false);
            footer.add(btnOk);

            // Wrapper con sfondo “tavolo verde”
            JPanel wrapper = new JPanel(new BorderLayout()) {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    int w = getWidth(), h = getHeight();
                    GradientPaint gp = new GradientPaint(0, 0, new Color(0, 90, 0), 0, h, new Color(0, 60, 0));
                    g2.setPaint(gp);
                    g2.fillRect(0, 0, w, h);
                    g2.dispose();
                }
            };
            wrapper.setBorder(BorderFactory.createEmptyBorder(8, 8, 12, 8));
            wrapper.add(lblTitle, BorderLayout.NORTH);

            JPanel center = new JPanel(new BorderLayout());
            center.setOpaque(false);
            center.add(lblSub, BorderLayout.NORTH);
            center.add(confettiPanel, BorderLayout.CENTER);

            wrapper.add(center, BorderLayout.CENTER);
            wrapper.add(footer, BorderLayout.SOUTH);

            dialog.setContentPane(wrapper);

            // Azioni
            btnOk.addActionListener(e -> {
                confettiPanel.stop();
                dialog.dispose();
                if (onClose != null) onClose.run();
            });

            // Avvia animazione quando visibile
            dialog.addWindowListener(new WindowAdapter() {
                @Override public void windowOpened(WindowEvent e) {
                    confettiPanel.start();
                }
                @Override public void windowClosing(WindowEvent e) {
                    confettiPanel.stop();
                }
            });

            dialog.pack();
            dialog.setVisible(true);
        });
    }

    // ----- Pannello con confetti animati -----
    private static final class ConfettiPanel extends JPanel {
        private static final int TARGET_FPS = 60;
        private static final int MAX_CONFETTI = 220;
        private static final float GRAVITY = 0.18f;
        private static final float AIR_DRAG = 0.995f;
        private static final int BATCH_SPAWN = 12;

        private final java.util.List<Confetto> confetti = new ArrayList<>();
        private final Random rnd = new Random();
        private javax.swing.Timer timer = null;
        private long lastSpawn = 0L;

        ConfettiPanel() {
            setOpaque(false);
            setDoubleBuffered(true);

            // Ricomincia/pausa quando la finestra perde focus (opzionale)
            addHierarchyListener(e -> {
                if ((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0 && !isDisplayable()) {
                    stop();
                }
            });
        }

        void start() {
            if (timer != null && timer.isRunning()) return;
            int delay = Math.max(5, 1000 / TARGET_FPS);
            timer = new javax.swing.Timer(delay, e -> onTick());
            timer.start();
        }

        void stop() {
            if (timer != null) {
                timer.stop();
                timer = null;
            }
        }

        private void onTick() {
            long now = System.currentTimeMillis();

            // Spawning iniziale e continuato per ~2.5s
            if (confetti.size() < MAX_CONFETTI && (now - lastSpawn > 60) ) {
                spawnConfetti(BATCH_SPAWN);
                lastSpawn = now;
            }

            // Update fisica
            for (Iterator<Confetto> it = confetti.iterator(); it.hasNext();) {
                Confetto c = it.next();
                c.vy += GRAVITY;
                c.vx *= AIR_DRAG;
                c.vy *= AIR_DRAG;
                c.x += c.vx;
                c.y += c.vy;

                c.rot += c.rotSpeed;

                // fuori schermo -> ricicla dal top
                if (c.y - c.size > getHeight()) {
                    // ricicla da sopra con nuova randomizzazione
                    resetFromTop(c);
                }

                // rimbalzi laterali
                if (c.x < -c.size) c.x = getWidth() + c.size;
                if (c.x > getWidth() + c.size) c.x = -c.size;
            }

            repaint();
        }

        private void spawnConfetti(int n) {
            for (int i = 0; i < n; i++) {
                confetti.add(randomConfetto());
            }
        }

        private Confetto randomConfetto() {
            int w = Math.max(getWidth(), 1);
            // parte dall'alto con leggera dispersione
            float x = rnd.nextInt(w);
            float y = -rnd.nextInt(120) - 20;
            float speed = 2.0f + rnd.nextFloat() * 3.0f;
            float angle = (float) (Math.toRadians(80 + rnd.nextInt(20)) * (rnd.nextBoolean() ? 1 : -1));
            float vx = (float) (Math.cos(angle) * speed);
            float vy = (float) (Math.sin(angle) * speed) + 1.5f;

            int size = 6 + rnd.nextInt(10);
            float rot = rnd.nextFloat() * (float) Math.PI * 2f;
            float rotSpeed = (rnd.nextFloat() - 0.5f) * 0.25f;

            // palette festosa
            Color[] palette = {
                new Color(255, 92, 87),   // rosso corallo
                new Color(255, 185, 64),  // arancio caldo
                new Color(255, 234, 0),   // giallo
                new Color(0, 214, 163),   // verde acqua
                new Color(66, 135, 245),  // blu
                new Color(171, 92, 255)   // viola
            };
            Color col = palette[rnd.nextInt(palette.length)];

            // forma casuale: cerchio, quadrato, rettangolo, triangolo
            ShapeType type = ShapeType.values()[rnd.nextInt(ShapeType.values().length)];

            return new Confetto(x, y, vx, vy, size, rot, rotSpeed, col, type);
        }

        private void resetFromTop(Confetto c) {
            int w = Math.max(getWidth(), 1);
            c.x = rnd.nextInt(w);
            c.y = -rnd.nextInt(80) - 20;
            float speed = 2.0f + rnd.nextFloat() * 3.0f;
            float angle = (float) (Math.toRadians(70 + rnd.nextInt(40)) * (rnd.nextBoolean() ? 1 : -1));
            c.vx = (float) (Math.cos(angle) * speed);
            c.vy = (float) (Math.sin(angle) * speed) + 1.2f;
            c.rot = rnd.nextFloat() * (float) Math.PI * 2f;
            c.rotSpeed = (rnd.nextFloat() - 0.5f) * 0.25f;
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // leggera vignettatura per profondità
            int w = getWidth(), h = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, new Color(0,0,0,40), 0, h, new Color(0,0,0,100));
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);

            // disegno confetti
            for (Confetto c : confetti) {
                AffineTransform old = g2.getTransform();
                g2.translate(c.x, c.y);
                g2.rotate(c.rot);
                g2.setColor(c.color);

                switch (c.type) {
                    case CIRCLE -> g2.fillOval(-c.size/2, -c.size/2, c.size, c.size);
                    case SQUARE -> g2.fillRect(-c.size/2, -c.size/2, c.size, c.size);
                    case RECT -> g2.fillRoundRect(-c.size/2, -c.size/3, c.size, (int)(c.size*0.66), 3, 3);
                    case TRIANGLE -> {
                        int s = c.size;
                        Polygon tri = new Polygon(
                                new int[]{-s/2, s/2, 0},
                                new int[]{s/2, s/2, -s/2},
                                3);
                        g2.fillPolygon(tri);
                    }
                }
                g2.setTransform(old);
            }

            g2.dispose();
        }

        // ---- Data ----
        private enum ShapeType { CIRCLE, SQUARE, RECT, TRIANGLE }

        private static final class Confetto {
            float x, y, vx, vy, rot, rotSpeed;
            int size;
            Color color;
            ShapeType type;

            Confetto(float x, float y, float vx, float vy, int size, float rot, float rotSpeed, Color color, ShapeType type) {
                this.x = x; this.y = y; this.vx = vx; this.vy = vy;
                this.size = size; this.rot = rot; this.rotSpeed = rotSpeed;
                this.color = color; this.type = type;
            }
        }
    }
}
