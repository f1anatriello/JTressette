package view;

import controller.AudioManager;
    import controller.GameEngine;
    import data.UtentePojo;
    import java.awt.*;
    import java.util.List;
    import javax.swing.*;
    import javax.swing.table.DefaultTableCellRenderer;
    import javax.swing.table.DefaultTableModel;
    import javax.swing.table.JTableHeader; // Ensure this matches the package where AudioManager is located
    import ui.UIConstants;
    import ui.UISettings;

public class ClassificaView extends JPanel {
    private final JLabel titolo;
    private final JTable classificaTable;
    private final JButton btnMenu;

    public ClassificaView(GameEngine gameEngine) {
        // Usa un layout con gap costanti e applica lo stile base del pannello
        setLayout(new BorderLayout(UIConstants.GAP, UIConstants.GAP));
        UISettings.applyPanelStyle(this);

        // Titolo centrato e stilizzato
        titolo = UISettings.createTitleLabel("Classifica");
        add(titolo, BorderLayout.NORTH);

        // Tabella per la classifica: più ordinata di una JTextArea
        classificaTable = new JTable();
        classificaTable.setFont(UIConstants.FONT_MONOSPACE);
        classificaTable.setForeground(UIConstants.TEXT_COLOR);
        classificaTable.setRowHeight(32);
        classificaTable.setShowGrid(false);
        classificaTable.setFillsViewportHeight(true);
        classificaTable.setSelectionBackground(UISettings.darken(UIConstants.ACCENT_YELLOW,30));
        classificaTable.setSelectionForeground(UIConstants.TEXT_COLOR);
        classificaTable.setOpaque(false);

        // Header della tabella: personalizziamo font e colori
        JTableHeader header = classificaTable.getTableHeader();
        header.setFont(UIConstants.FONT_REGULAR);
        header.setBackground(UIConstants.ACCENT_YELLOW);
        header.setForeground(UIConstants.TEXT_COLOR);
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        // Renderer per alternare i colori delle righe e centrare i valori
        classificaTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setForeground(UIConstants.TEXT_COLOR);
                    // Riga pari e dispari con colori diversi
                    if (row % 2 == 0) {
                        c.setBackground(UIConstants.INPUT_BG);
                    } else {
                        c.setBackground(UISettings.lighten(UIConstants.ACCENT_YELLOW,50));
                    }
                }
                // Centra tutte le colonne tranne quella dei nickname
                setHorizontalAlignment(column == 1 ? JLabel.LEFT : JLabel.CENTER);
                return c;
            }
        });

        // ScrollPane con bordo coerente
        JScrollPane scroll = new JScrollPane(classificaTable);
        scroll.setBorder(BorderFactory.createLineBorder(UIConstants.ACCENT_YELLOW, 1));
        scroll.setPreferredSize(new Dimension(800, 400)); 

        // Pannello contenitore per centrare la tabella
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, UIConstants.GAP, UIConstants.GAP));
        centerPanel.setOpaque(false);
        centerPanel.add(scroll);
        add(centerPanel, BorderLayout.CENTER);


        // Bottone per tornare al menù, stilizzato con variante TERTIARY
        btnMenu = UISettings.createButton("Torna al Menù", UISettings.ButtonVariant.TERTIARY);
        btnMenu.addActionListener(e -> {
            gameEngine.visualizzaMenu();
            AudioManager.getInstance().play("src/audio/button.wav");
        });
        JPanel south = new JPanel();
        south.setOpaque(false);
        south.add(btnMenu);
        add(south, BorderLayout.SOUTH);
    }

    /**
     * Carica e mostra la classifica prendendo i dati dalla classe UserRepository
     */
    public void mostraClassifica(List<UtentePojo> classifica) {
        String[] columns = { "Rank", "Nickname", "P. Giocate", "P. Vinte", "P. Perse" };
        Object[][] data;
        if (classifica == null || classifica.isEmpty()) {
            data = new Object[][] { { "-", "Nessun dato di classifica", "-", "-", "-" } };
        } else {
            data = new Object[classifica.size()][5];
            for (int i = 0; i < classifica.size(); i++) {
                UtentePojo p = classifica.get(i);
                data[i][0] = i + 1;
                data[i][1] = p.getUsername();
                data[i][2] = p.getPartiteGiocate();
                data[i][3] = p.getPartiteVinte();
                data[i][4] = p.getPartitePerse();
            }
        }
        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        classificaTable.setModel(model);

        // Imposta larghezze preferite per le colonne
        classificaTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        classificaTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        classificaTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        classificaTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        classificaTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        classificaTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }


}
