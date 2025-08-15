package launcher;

import view.LoginView;

public class JTressette {
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	LoginView login = new LoginView();
                login.setVisible(true);
            }
        });
        System.out.println("Applicazione Tressette avviata.");
            
    }

}


