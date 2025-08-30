package launcher;

import javax.swing.SwingUtilities;
import ui.AudioManager;
import view.IntroView;

/**
 * Classe principale per avviare l'applicazione JTressette.
 * Inizializza la schermata introduttiva e gestisce il flusso iniziale.
 * @author 1957447
 */
public class JTressette {
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	SwingUtilities.invokeLater(() -> {
                    new IntroView(5000).setVisible(true); // splash di 3 secondi.
                    AudioManager.getInstance().play("audio/intro.wav");
                });
            }
        });
        System.out.println("Applicazione Tressette avviata.");
            
    }

}


