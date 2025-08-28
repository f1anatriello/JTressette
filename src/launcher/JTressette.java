package launcher;

import javax.swing.SwingUtilities;

import controller.GameEngine;
import ui.AudioManager;
import view.IntroView;
import view.LoginView;

public class JTressette {
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	SwingUtilities.invokeLater(() -> {
                    new IntroView(3000).setVisible(true); // splash di 3 secondi.
                    AudioManager.getInstance().play("src/audio/intro.wav");
                });
            }
        });
        System.out.println("Applicazione Tressette avviata.");
            
    }

}


