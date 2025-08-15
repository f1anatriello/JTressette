package ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.Border;

/**
 * Factory e helper per creare/stilizzare componenti UI Swing in modo coerente,
 * secondo lo stile impostato dalle costanti in UIConstants.
 */
public final class UISettings {

    private UISettings() {}

    // Varianti per i pulsanti
    public enum ButtonVariant { PRIMARY, SECONDARY, TERTIARY }

    // ---------------------------------------------------------
    // CREATION METHODS
    // ---------------------------------------------------------

    /** Crea un JButton già stilizzato (con varianti e hover/press). */
    public static JButton createButton(String text, ButtonVariant variant) {
        JButton b = new JButton(text);
        applyButtonStyle(b, variant);
        return b;
    }

    /** Crea un JLabel per il titolo principale. */
    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(UIConstants.FONT_MAIN_TITLE);
        label.setForeground(UIConstants.ACCENT_COLOR);
        label.setBorder(BorderFactory.createEmptyBorder(UIConstants.PADDING, UIConstants.PADDING,
                                                        UIConstants.PADDING, UIConstants.PADDING));
        return label;
    }

    /** Crea un pannello base (es. contenitore schermata). */
    public static JPanel createPanel(String titleIfAny) {
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(UIConstants.BACKGROUND_PANEL);
        Border border = titleIfAny == null || titleIfAny.isEmpty()
                ? UIConstants.BORDER_ROUNDED
                : BorderFactory.createTitledBorder(UIConstants.BORDER_TITLE_PANEL, titleIfAny);
        panel.setBorder(border);
        return panel;
    }

    // ---------------------------------------------------------
    // STYLE APPLIERS
    // ---------------------------------------------------------

    /** Applica lo stile standard a un pulsante con variante (hover/press). */
    public static void applyButtonStyle(JButton button, ButtonVariant variant) {
        button.setFont(UIConstants.FONT_REGULAR);
        button.setForeground(UIConstants.TEXT_COLOR);
        button.setCursor(UIConstants.CURSOR_HAND);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBorder(UIConstants.BORDER_ROUNDED);
        button.setPreferredSize(new Dimension(UIConstants.BUTTON_WIDTH, UIConstants.BUTTON_HEIGHT));

        Color base = baseColorFor(variant);
        Color hover = hoverColorFor(variant);
        button.setBackground(base);

        // Effetti hover/press
        button.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { button.setBackground(hover); }
            @Override public void mouseExited(MouseEvent e)  { button.setBackground(base); }
            @Override public void mousePressed(MouseEvent e) { button.setBackground(darken(hover, 12)); }
            @Override public void mouseReleased(MouseEvent e){ 
                // se il mouse è ancora dentro, torna ad hover; altrimenti al base
                Point p = e.getPoint();
                if (p.x >= 0 && p.y >= 0 && p.x <= button.getWidth() && p.y <= button.getHeight()) {
                    button.setBackground(hover);
                } else {
                    button.setBackground(base);
                }
            }
        });
    }

    /** Versione compatibile con l'esempio originale (default: PRIMARY). */
    public static void applyButtonStyle(JButton button) {
        applyButtonStyle(button, ButtonVariant.PRIMARY);
    }

    /** Applica stile coerente a JTextField. */
    public static void applyTextFieldStyle(JTextField field) {
        field.setFont(UIConstants.FONT_REGULAR);
        field.setForeground(UIConstants.INPUT_FG);
        field.setBackground(UIConstants.INPUT_BG);
        field.setCaretColor(UIConstants.INPUT_FG);
        field.setBorder(UIConstants.BORDER_INPUT);
        field.setSelectionColor(new Color(255, 255, 255, 60));
        field.setSelectedTextColor(UIConstants.TEXT_COLOR);
        field.setMargin(new Insets(8, 12, 8, 12));

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(UIConstants.BORDER_INPUT_FOCUSED);
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                field.setBorder(UIConstants.BORDER_INPUT);
            }
        });
    }

    /** Applica stile base a un JPanel già creato. */
    public static void applyPanelStyle(JPanel panel) {
        panel.setOpaque(true);
        panel.setBackground(UIConstants.BACKGROUND_PANEL);
        if (panel.getBorder() == null) {
            panel.setBorder(UIConstants.BORDER_ROUNDED);
        }
    }

    // ---------------------------------------------------------
    // HELPERS
    // ---------------------------------------------------------

    private static Color baseColorFor(ButtonVariant v) {
        switch (v) {
            case SECONDARY: return UIConstants.BUTTON_SECONDARY;
            case TERTIARY:    return UIConstants.BUTTON_TERTIARY;
            default:        return UIConstants.BUTTON_PRIMARY;
        }
    }

    private static Color hoverColorFor(ButtonVariant v) {
        switch (v) {
            case SECONDARY: return UIConstants.BUTTON_SECONDARY_HOVER;
            case TERTIARY:    return UIConstants.BUTTON_TERTIARY_HOVER;
            default:        return UIConstants.BUTTON_PRIMARY_HOVER;
        }
    }

    /** Schiarisce un colore di delta (0–255) senza superare i limiti. */
    @SuppressWarnings("unused")
    private static Color lighten(Color c, int delta) {
        int r = Math.min(255, c.getRed()   + delta);
        int g = Math.min(255, c.getGreen() + delta);
        int b = Math.min(255, c.getBlue()  + delta);
        return new Color(r, g, b, c.getAlpha());
    }

    /** Scurisce un colore di delta (0–255) senza scendere sotto zero. */
    private static Color darken(Color c, int delta) {
        int r = Math.max(0, c.getRed()   - delta);
        int g = Math.max(0, c.getGreen() - delta);
        int b = Math.max(0, c.getBlue()  - delta);
        return new Color(r, g, b, c.getAlpha());
    }
}
