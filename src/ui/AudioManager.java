package ui;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

/**
 * Gestisce la riproduzione audio nell'applicazione.
 * Implementa il pattern Singleton per garantire una sola istanza di AudioManager.
 * Permette di riprodurre, mettere in loop e fermare file audio.
 * @author 1957447
 */

public class AudioManager {

	private static AudioManager instance;
	private Clip currentClip;

    /**
     * Restituisce l'istanza singleton di AudioManager.
     * Se l'istanza non esiste, la crea.
     * @return Istanza singleton di AudioManager.
     */
	public static AudioManager getInstance() {
		if (instance == null)
			instance = new AudioManager();
		return instance;
	}

	private AudioManager() {	}

    /**
     * Resetta lo stato dell'AudioManager, permettendo di iniziare una nuova sessione audio.
     * Imposta l'istanza singleton a null.
     */
	public void reset() {
		instance = null;
	}
	

	/**
     * Importo il file audio e dopo averlo aperto, lo riproduco
	 * @param fileName nome file audio da riprodurre
	 */
	
    public void play(String fileName) {
        try {
            File soundFile = new File(fileName);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            currentClip = (Clip) AudioSystem.getLine(new DataLine.Info(Clip.class, audioIn.getFormat()));
            currentClip.open(audioIn);
            currentClip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Importo il file audio e dopo averlo aperto, lo riproduco in loop continuo
     * @param fileName nome file audio da riprodurre in loop
     */
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

    /**
     * Ferma la riproduzione del file audio corrente, se in riproduzione.
     * Chiude il Clip e libera le risorse.
     */
    public void stop() {
        if (currentClip != null && currentClip.isRunning()) {
            currentClip.stop(); // Ferma il Clip in riproduzione
            currentClip.close(); // Chiude il Clip
            currentClip = null; // Libera la risorsa
        }
    }
}
