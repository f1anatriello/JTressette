package ui;

import data.UtentePojo;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import model.Carta;
import model.Seme;
import model.Valore;

/**
 * Gestisce le risorse grafiche dell'interfaccia utente.
 * Implementa il pattern Singleton per garantire una sola istanza di UIAssets.
 * Carica e fornisce accesso alle immagini delle carte, sfondi, loghi e avatar.
 * @author 1957447
 */

public class UIAssets {

    private static UIAssets instance;

    private Map<String, BufferedImage> immaginiCarte = new HashMap<>();
    private BufferedImage immagineSfondoTavolo;
    private BufferedImage immagineRetroCarta;
    private BufferedImage introLogo;
    private BufferedImage menuLogo;
    private BufferedImage avatarDefault;

    /**
     * Costruttore privato per il pattern Singleton.
     * Carica tutte le risorse grafiche.
     */
    private UIAssets() {
        caricaImmaginiCarte();
        caricaImmagineTavolo();
        caricaImmagineRetroCarta();
        caricaMenuLogo();
        caricaIntroLogo();
        avatarDefault = controlloBuffer("images/avatars/avatar1.png", "avatar default");
    }

    /**
    * Restituisce l'istanza singleton di UIAssets.
    * Se l'istanza non esiste, la crea e carica le risorse.
    * @return Istanza singleton di UIAssets.
    */  
	public static UIAssets getInstance() {
        if (instance == null) instance = new UIAssets();
        return instance;
    }

    /**
     * Carica le immagini delle carte da file.
     * Le immagini sono mappate in base al valore e al seme della carta.
     * Le immagini devono essere nominate secondo il formato "numero_seme.jpg".
     * Esempio: "1_coppe.jpg" per l'Asso di Coppe.
     */
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

    /**
     * Carica l'immagine di sfondo del tavolo da file.
     */
    private void caricaImmagineTavolo() {
        String path = "images/background/tavolo.jpg";
        immagineSfondoTavolo = controlloBuffer(path, "sfondo tavolo");
    }

    /**
     * Carica l'immagine del retro delle carte da file.
     */
    private void caricaImmagineRetroCarta() {
        String path = "images/background/napoletane_retro.jpg";
        immagineRetroCarta = controlloBuffer(path, "retro carta");
    }

    /**
     * Carica il logo del menu da file.
     */
    private void caricaMenuLogo() {
        String path = "images/logos/intro_logo.png";
        introLogo = controlloBuffer(path, "menu");
    }
    
    /**
     * Carica il logo introduttivo da file.
     */
    private void caricaIntroLogo() {
    	String path = "images/logos/sch_principale.png";
        menuLogo = controlloBuffer(path, "intro");
	}

    /**
     * Restituisce l'immagine di una carta specifica.
     * @param c La carta di cui si vuole l'immagine.
     * @return L'immagine della carta, o null se la carta è null o non trovata.
     */
    public Image getImmagineCarta(Carta c) {
        if (c == null) return null;
        return immaginiCarte.get(c.getValore().name() + "_" + c.getSeme().name());
    }

    /**
     * Restituisce l'immagine dell'avatar di un utente.
     * @param profilo Il profilo utente di cui si vuole l'avatar.
     * @return L'immagine dell'avatar, o null se il percorso è invalido o l'immagine non trovata.
     */
    public BufferedImage getImmagineAvatar(UtentePojo profilo) {
        System.out.println(profilo.getAvatarPath());
        return controlloBuffer(profilo.getAvatarPath(), "avatar");
    }

    /**
     * Restituisce una mappa di tutte le immagini degli avatar disponibili.
     * Le chiavi sono le immagini, i valori sono i percorsi dei file.
     * @return Mappa di immagini degli avatar e i loro percorsi.
     */
    public Map<BufferedImage, String> getAllAvatar() {
        Map<BufferedImage, String> avatars = new HashMap<>();
        for (int i = 1; i < 7; i++) {
            BufferedImage img = controlloBuffer("images/avatars/avatar" + i + ".png", "avatar");
            if (img != null) {
                avatars.put(img, "images/avatars/avatar" + i + ".png");
            }
        }
        return avatars;
    }

    // ---- getter per le immagini singole ----
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

    public BufferedImage getDefaultAvatar() {
        return avatarDefault;
    }

    /**
     * Controlla e carica un'immagine da file.
     * Stampa messaggi di errore se il file non esiste o non è caricabile.
     * @param path Il percorso del file immagine.
     * @param lbl Una label descrittiva per i messaggi di log.
     * @return L'immagine caricata, o null in caso di errore.
     */
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
