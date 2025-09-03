package view;

import ui.UIAssets;
import ui.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Schermata introduttiva (splash) come JFrame.
 * - Dimensione fissa 960x640
 * - Sfondo color UIConstants.BACKGROUND_PANEL
 * - Logo centrato, scalato al 70% della finestra
 * - Dopo la durata indicata, chiude e apre LoginView.
 * @author 1957447
 */
public class IntroView extends JFrame {

    private final BufferedImage logo;
    private final int durationMs;

    /**
     * Crea la finestra di intro.
     * @param durationMs Durata in millisecondi prima di chiudere e aprire LoginView.
     */
    public IntroView(int durationMs) {
        this.durationMs = durationMs;

        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 640);
        setLocationRelativeTo(null);

        // carico il logo dagli asset
        logo = UIAssets.getInstance().getIntroLogo();

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                g.setColor(UIConstants.BACKGROUND_DARK);
                g.fillRect(0, 0, getWidth(), getHeight());

                if (logo != null) {
                    int panelW = getWidth();
                    int panelH = getHeight();

                    // scala il logo al 70% della finestra
                    int newW = (int) (panelW * 0.7);
                    int newH = (int) (panelH * 0.7);

                    int x = (panelW - newW) / 2;
                    int y = (panelH - newH) / 2;

                    ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                                      RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    g.drawImage(logo, x, y, newW, newH, null);
                }
            }
        };

        setContentPane(panel);

        // timer per chiudere intro e aprire login
        Timer t = new Timer(this.durationMs, e -> {
            dispose();
            new LoginView().setVisible(true);
        });
        t.setRepeats(false);
        t.start();
    }

    /**
     * Crea la finestra di intro con durata di 3 secondi.
     */
    public IntroView() {
        this(3000); // default 3 secondi
    }
}
