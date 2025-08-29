package view;

import controller.GameEngine;
import data.UserRepository;
import data.UtentePojo;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

import ui.AudioManager;
import ui.UIConstants;
import ui.UISettings;

/**
 * Schermata di login come JFrame.
 * @author 1957447
 */

public class LoginView extends JFrame{

    private static final long serialVersionUID = 1L;
	private final JTextField txtUsername = new JTextField();
    private final JButton btnEntra = UISettings.createButton("Entra", UISettings.ButtonVariant.PRIMARY);
    private final JButton btnEsci  = UISettings.createButton("Esci", UISettings.ButtonVariant.TERTIARY);
    private final JLabel  info     = new JLabel("Nickname (3–20 caratteri)");

    /**
     * Crea la finestra di login.
     */
    public LoginView() {
        setTitle("JTressette - Login");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int scl = JOptionPane.showConfirmDialog(
                        LoginView.this,
                        "Sei sicuro di voler uscire?",
                        "Conferma",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (scl == JOptionPane.YES_OPTION) {
                    AudioManager.getInstance().play("src/audio/button.wav");
                    System.exit(0);
                }
            }
        });

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIConstants.ACCENT_YELLOW);
        setContentPane(root);

        JLabel titolo = UISettings.createTitleLabel("Benvenuto!");
        JPanel header = new JPanel();
        header.setLayout(new BorderLayout());
        header.setOpaque(false);
        header.add(titolo, BorderLayout.CENTER);
        root.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new GridBagLayout());

        UISettings.applyTextFieldStyle(txtUsername);
        txtUsername.setColumns(18);

        info.setForeground(UIConstants.TEXT_MUTED);
        info.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel form = new JPanel(new GridLayout(0,1, UIConstants.GAP, UIConstants.GAP));
        form.setOpaque(false);
        form.add(txtUsername);

        JPanel buttons = new JPanel(new GridLayout(1,0, UIConstants.GAP, UIConstants.GAP));
        buttons.setOpaque(false);
        buttons.add(btnEntra);
        buttons.add(btnEsci);

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx=0; gc.gridy=0; gc.fill=GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(UIConstants.GAP, UIConstants.GAP, UIConstants.GAP, UIConstants.GAP);
        center.add(form, gc);

        gc.gridy=1; center.add(buttons, gc);
        gc.gridy=2; center.add(info, gc);

        root.add(center, BorderLayout.CENTER);

        // Carico la classe che mi legge il file txt con i profili
        UserRepository repo = new UserRepository();

        // Actions
        btnEntra.addActionListener(e -> {
            AudioManager.getInstance().play("audio/button.wav");
            String username = txtUsername.getText().trim();

            if (!UserRepository.isValidUsername(username)) {
                JOptionPane.showMessageDialog(this,
                        "Nickname non valido.\nAmmessi: 3–20 caratteri, lettere/numeri, _ - .",
                        "Attenzione", JOptionPane.WARNING_MESSAGE);
                AudioManager.getInstance().play("audio/button.wav");
                return;
            }

            UtentePojo existed = repo.loginOrCreate(username);
            JOptionPane.showMessageDialog(this,
                    existed != null ? "Bentornato, " + username + "!" : "Creato nuovo utente: " + username,
                    "Accesso", JOptionPane.INFORMATION_MESSAGE);
            AudioManager.getInstance().play("audio/button.wav");
            GameEngine engine = GameEngine.getInstance(existed);
            engine.avviaUIPrincipale();    // crea la JFrame principale e mostra il menu
            dispose();                    // chiudi il login
        });

        btnEsci.addActionListener(e -> {
                AudioManager.getInstance().play("audio/button.wav");
                System.exit(0);
        });

        setSize(400, 240);
        setResizable(false);
        getRootPane().setDefaultButton(btnEntra);
        setLocationRelativeTo(null);
    }
}
