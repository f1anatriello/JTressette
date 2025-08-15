package ui;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import java.awt.Cursor;
import java.awt.Dimension;

/**
 * Classe di appoggio per definire tutte le costanti 
 * necessarie per mantenere uno stile 
 * su tutte le schermate
 */

public final class UIConstants {

    private UIConstants() {}

    // =========================
    // PALETTE Standard Applic.
    // =========================
    public static final Color BACKGROUND_DARK        = new Color(255,215,0); 
    public static final Color BACKGROUND_PANEL       = new Color(230,190,0); 
    public static final Color TEXT_COLOR             = new Color(17, 17, 17);    
    public static final Color TEXT_MUTED             = new Color(80, 72, 60);
    public static final Color ACCENT_COLOR           = new Color(0,0,0);   

    // Colori simili alle carte napoletane
    public static final Color ACCENT_RED             = new Color(198, 51, 43);   
    public static final Color ACCENT_GREEN           = new Color(15, 138, 95);   
    public static final Color ACCENT_BLUE            = new Color(4,113,252);   

    // Bottoni
    public static final Color BUTTON_PRIMARY         = ACCENT_GREEN;
    public static final Color BUTTON_PRIMARY_HOVER   = new Color(8, 127, 87);

    public static final Color BUTTON_SECONDARY       = ACCENT_BLUE;
    public static final Color BUTTON_SECONDARY_HOVER = new Color(0, 106, 245);

    public static final Color BUTTON_TERTIARY          = ACCENT_RED;
    public static final Color BUTTON_TERTIARY_HOVER    = new Color(176, 40, 35);

    // Input
    public static final Color INPUT_BG               = new Color(252, 247, 232);
    public static final Color INPUT_FG               = TEXT_COLOR;
    public static final Color INPUT_PLACEHOLDER      = new Color(130, 120, 100);
    public static final Color INPUT_BORDER_COLOR     = ACCENT_COLOR;

    // Font misto tra un serif elegante e mono, simil LateX
    public static final Font FONT_MAIN_TITLE = new Font("Georgia", Font.BOLD, 36);
    public static final Font FONT_SUB_TITLE  = new Font("Georgia", Font.BOLD, 24);
    public static final Font FONT_REGULAR    = new Font("Georgia", Font.PLAIN, 16);
    public static final Font FONT_MONOSPACE  = new Font("Monospaced", Font.PLAIN, 14);

    // Dimensioni
    public static final int RADIUS        = 16;
    public static final int PADDING       = 16;
    public static final int GAP           = 10;
    public static final int BUTTON_HEIGHT = 48;
    public static final int BUTTON_WIDTH  = 220;
    public static final Dimension MENU_SIZE = new Dimension(400, 600); 
    public static final Dimension FIXED_VIEW_SIZE = new Dimension(960,640);

    // Bordi arrotondati e con un filo di colore
    public static final Border BORDER_ROUNDED =
            BorderFactory.createLineBorder(ACCENT_COLOR, 2, true);

    public static final Border BORDER_TITLE_PANEL = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 2, true),
            "",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            FONT_SUB_TITLE,
            ACCENT_COLOR
    );
    
    

    public static final Border BORDER_INPUT =
            BorderFactory.createLineBorder(INPUT_BORDER_COLOR, 2, true);

    public static final Border BORDER_INPUT_FOCUSED =
            BorderFactory.createLineBorder(ACCENT_BLUE, 2, true);

    // Cursori
    public static final Cursor CURSOR_HAND    = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    public static final Cursor CURSOR_TEXT    = Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR);
    public static final Cursor CURSOR_DEFAULT = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
}
