package controller;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;


public class AudioManager {

	private static AudioManager instance;
	private Clip currentClip;

	public static AudioManager getInstance() {
		if (instance == null)
			instance = new AudioManager();
		return instance;
	}

	private AudioManager() {

	}
	

	/**
	 * in questo metodo mi importo il file audio e
	 * successivamente ad averlo aperto lo riproduco
	 * @param fileName nome file audio 
	 */
	
    public void play(String fileName) {
        try {
            File soundFile = new File(fileName);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            currentClip = (Clip) AudioSystem.getLine(new DataLine.Info(Clip.class, audioIn.getFormat()));
            currentClip.open(audioIn);
            currentClip.start(); // Avvia la riproduzione
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	public void playLoop(String fileName) {
        try {
            File soundFile = new File(fileName);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            currentClip = (Clip) AudioSystem.getLine(new DataLine.Info(Clip.class, audioIn.getFormat()));
            currentClip.open(audioIn);
			currentClip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (currentClip != null && currentClip.isRunning()) {
            currentClip.stop(); // Ferma il Clip in riproduzione
            currentClip.close(); // Chiude il Clip
            currentClip = null; // Libera la risorsa
        }
    }
}
