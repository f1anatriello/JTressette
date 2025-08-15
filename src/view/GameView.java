package view;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;
import ui.UIConstants;
import ui.UISettings;

public class GameView extends JFrame{

    private static GameView instance = null;
    public static GameView getInstance() {
		if (instance == null) {
			instance = new GameView();
		}
		return instance;
	}
    
    private GameView() {
        setTitle("JTressette");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int scl = JOptionPane.showConfirmDialog(
                        GameView.this,
                        "Sei sicuro di voler uscire?",
                        "Già te ne vai? :(",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (scl == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

    }
}
