package ui;

import model.Carta;
import model.Seme;
import model.Valore;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

import data.UtentePojo;

public class UIAssets {

    private static UIAssets instance;

    private Map<String, BufferedImage> immaginiCarte = new HashMap<>();
    private BufferedImage immagineSfondoTavolo;
    private BufferedImage immagineRetroCarta;
    private BufferedImage introLogo;
    private BufferedImage menuLogo;

    private UIAssets() {
        caricaImmaginiCarte();
        caricaImmagineTavolo();
        caricaImmagineRetroCarta();
        caricaMenuLogo();
        caricaIntroLogo();
    }


	public static UIAssets getInstance() {
        if (instance == null) instance = new UIAssets();
        return instance;
    }

    private void caricaImmaginiCarte() {
        Map<Valore, Integer> valoreToNumero = new HashMap<>();
        valoreToNumero.put(Valore.ASSO, 1);
        valoreToNumero.put(Valore.DUE, 2);
        valoreToNumero.put(Valore.TRE, 3);
        valoreToNumero.put(Valore.QUATTRO, 4);
        valoreToNumero.put(Valore.CINQUE, 5);
        valoreToNumero.put(Valore.SEI, 6);
        valoreToNumero.put(Valore.SETTE, 7);
        valoreToNumero.put(Valore.FANTE, 8);
        valoreToNumero.put(Valore.CAVALLO, 9);
        valoreToNumero.put(Valore.RE, 10);

        int count = 0;
        for (Seme seme : Seme.values()) {
            String semeLower = seme.name().toLowerCase();

            for (Valore valore : Valore.values()) {
                Integer n = valoreToNumero.get(valore);
                if (n == null) {
                    System.err.println("Mappatura valore-numero mancante per: " + valore);
                    continue;
                }

                String path = "images/cards/" + n + "_" + semeLower + ".jpg";

                try {
                    File f = new File(path);
                    if (!f.exists()) {
                        System.err.println("File carta non trovato: " + path);
                        continue;
                    }
                    BufferedImage img = ImageIO.read(f);
                    if (img == null) {
                        System.err.println("File carta non caricabile: " + path);
                        continue;
                    }
                    immaginiCarte.put(valore.name() + "_" + seme.name(), img);
                    count++;
                } catch (IOException e) {
                    System.err.println("Errore I/O caricamento carta: " + path + " - " + e.getMessage());
                }
            }
        }

        System.out.println("Caricate " + count + " immagini di carte."); //così capisco quante carte mi ha caricato
    }

    private void caricaImmagineTavolo() {
        String path = "images/background/tavolo.jpg";
        immagineSfondoTavolo = controlloBuffer(path, "sfondo tavolo");
    }

    private void caricaImmagineRetroCarta() {
        String path = "images/background/napoletane_retro.jpg";
        immagineRetroCarta = controlloBuffer(path, "retro carta");
    }

    private void caricaMenuLogo() {
        String path = "images/logos/intro_logo.png";
        introLogo = controlloBuffer(path, "menu");
    }
    
    private void caricaIntroLogo() {
    	String path = "images/logos/sch_principale.png";
        menuLogo = controlloBuffer(path, "intro");
		
	}

    public Image getImmagineCarta(Carta c) {
        if (c == null) return null;
        return immaginiCarte.get(c.getValore().name() + "_" + c.getSeme().name());
    }

    public BufferedImage getImmagineAvatar(UtentePojo profilo) {
        if (profilo.getUsername() == null || profilo.getUsername().isEmpty()) return null;
        String path = "images/avatars/" + new File(profilo.getUsername()).getName();
        return controlloBuffer(path, "avatar");
    }

    public BufferedImage getImmagineSfondoTavolo() {
        return immagineSfondoTavolo;
    }

    public BufferedImage getImmagineRetroCarta() {
        return immagineRetroCarta;
    }

    public BufferedImage getIntroLogo() {
        return introLogo;
    }
    
    public BufferedImage getMenuLogo() {
        return menuLogo;
    }

    // ---- helper semplice che ritorna direttamente BufferedImage ----
    private BufferedImage controlloBuffer(String path, String lbl) {
        try {
            File f = new File(path);
            if (!f.exists()) {
                System.err.println("File " + lbl + " non trovato: " + path);
                return null;
            }
            BufferedImage img = ImageIO.read(f);
            if (img == null) {
                System.err.println("File " + lbl + " non caricabile: " + path);
                return null;
            }
            System.out.println("Caricato " + lbl + ": " + path);
            return img;
        } catch (IOException e) {
            System.err.println("Errore I/O caricamento " + lbl + ": " + path + " - " + e.getMessage());
            return null;
        }
    }
}
