package view;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public final class Celebrations {
 
    private Celebrations() {}

    public static void showVictoryDialog(Window owner, String title, String subtitle, Runnable onClose) {
        // dialog modale con owner corretto (niente glitch)
        final JDialog dlg = new JDialog(owner, "🎉 Partita Terminata", Dialog.ModalityType.DOCUMENT_MODAL);
        dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dlg.setResizable(false);

        // wrapper con sfondo verde a gradiente
        JPanel wrapper = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                int w = getWidth(), h = getHeight();
                g2.setPaint(new GradientPaint(0, 0, new Color(0,110,0), 0, h, new Color(0,70,0)));
                g2.fillRect(0, 0, w, h);
                g2.dispose();
            }
        };
        wrapper.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // header: titolo grande
        JLabel lblTitle = new JLabel("🎉 " + Objects.toString(title, "Fine Partita!") + " 🎉", SwingConstants.CENTER);
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 26f));
        lblTitle.setForeground(new Color(255, 230, 120));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        // testo centrale
        JLabel lblMsg = new JLabel(Objects.toString(subtitle, ""), SwingConstants.CENTER);
        lblMsg.setFont(lblMsg.getFont().deriveFont(Font.PLAIN, 18f));
        lblMsg.setForeground(Color.WHITE);
        lblMsg.setBorder(BorderFactory.createEmptyBorder(6, 0, 14, 0));

        // pulsante
        JButton btnOk = new JButton("Torna al Menù");
        btnOk.setFont(btnOk.getFont().deriveFont(Font.BOLD, 14f));
        btnOk.addActionListener(e -> {
            dlg.dispose();
            if (onClose != null) onClose.run();
        });

        // layout semplice
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnOk.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(lblTitle);
        center.add(lblMsg);
        center.add(Box.createVerticalStrut(8));
        center.add(btnOk);

        wrapper.add(center, BorderLayout.CENTER);
        dlg.setContentPane(wrapper);
        dlg.pack();
        dlg.setSize(new Dimension(420, 220)); // dimensione chiara e costante
        dlg.setLocationRelativeTo(owner);
        dlg.setVisible(true);
    }
}
