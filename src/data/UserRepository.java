package data;

import java.io.*;
import java.util.*;

/*
 * Classe che fa da tramite tra la base dati 
 * (in questo caso, un file .txt) ed il gioco 
 */

public final class UserRepository {

    private static UserRepository instance;
    public static final String FILE_NAME = "utenti.txt";
    public static final String DEFAULT_AVATAR = "images/avatars/avatar1.png";

    public UserRepository() {
        File f = new File(FILE_NAME);
        try {
            if (!f.exists()) {
                f.createNewFile(); // crea file vuoto
            }
        } catch (IOException e) {
            System.err.println("Errore creazione file utenti.txt: " + e.getMessage());
        }
    }

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    /** Login o crea nuovo profilo */
    public UtentePojo loginOrCreate(String nickname) {
        Map<String, UtentePojo> utenti = leggiTutti();
        if (utenti.containsKey(nickname)) {
            // Se esiste recupero tutte le informazioni dal file txt
            UtentePojo p = utenti.get(nickname);
            recuperaDatiUtente(nickname);
            return p;
        }
        utenti.put(nickname, creaProfiloDiDefault(nickname));
        salvaTutti(utenti);
        return utenti.get(nickname);
    }

    public boolean salvaProfilo(UtentePojo profilo) {
        Map<String, UtentePojo> utenti = leggiTutti();
        utenti.put(profilo.getUsername(), profilo);
        return salvaTutti(utenti);
    }

    public UtentePojo caricaProfilo(String nickname) {
        return leggiTutti().get(nickname);
    }

    public void registraVittoria(String nickname) {
        Map<String, UtentePojo> utenti = leggiTutti();
        UtentePojo p = utenti.getOrDefault(nickname, creaProfiloDiDefault(nickname));
        utenti.put(nickname, p);
        salvaTutti(utenti);
    }

    public void registraSconfitta(String nickname) {
        Map<String, UtentePojo> utenti = leggiTutti();
        UtentePojo p = utenti.getOrDefault(nickname, creaProfiloDiDefault(nickname));
        utenti.put(nickname, p);
        salvaTutti(utenti);
    }

    public void aggiornaAvatar(String nickname, String avatarPath) {
        Map<String, UtentePojo> utenti = leggiTutti();
        UtentePojo p = utenti.getOrDefault(nickname, creaProfiloDiDefault(nickname));
        p.setAvatarPath((avatarPath == null || avatarPath.isBlank()) ? DEFAULT_AVATAR : avatarPath.trim());
        utenti.put(nickname, p);
        salvaTutti(utenti);
    }

    public static List<UtentePojo> getClassifica() {
        List<UtentePojo> list = new ArrayList<>(leggiTutti().values());
        list.sort(Comparator.comparingInt(UtentePojo::getPartiteVinte).reversed());
        return list;
    }

    public static boolean isValidUsername(String u) {
        if (u == null) return false;
        String s = u.trim();
        return s.length() >= 3 && s.length() <= 20 && s.matches("[A-Za-z0-9_.\\- ]+");
    }

    /* ===== Utilities ===== */

    private UtentePojo creaProfiloDiDefault(String nickname) {
        UtentePojo p = new UtentePojo(nickname, DEFAULT_AVATAR, 0, 0, 0);
        return p;
    }

    /** Legge tutto il file in memoria */
    /**
     * Legge tutti gli utenti dal file di testo e li restituisce come mappa.
     * @return Una mappa contenente tutti gli utenti, identificati dal loro nickname.
     */
    private static Map<String, UtentePojo> leggiTutti() {
        Map<String, UtentePojo> map = new HashMap<>();
        try (BufferedReader r = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] campi = line.split(";", -1); // nickname;avatar;giocate;vinte;perse
                if (campi.length != 5) continue;
                String nick = campi[0];
                String avatar = campi[1].isBlank() ? DEFAULT_AVATAR : campi[1];
                int giocate = Integer.parseInt(campi[2]);
                int vinte = Integer.parseInt(campi[3]);
                int perse = Integer.parseInt(campi[4]);
                UtentePojo p = new UtentePojo(nick, avatar, giocate, vinte, perse);
                map.put(nick, p);
            }
        } catch (FileNotFoundException e) {
            System.err.println("File non trovato: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Errore lettura file: " + e.getMessage());
        }
        return map;
    }

    /** Aggiunge o aggiorna un utente nel file senza cancellare gli altri */
    private boolean salvaTutti(Map<String, UtentePojo> utenti) {
        Map<String, UtentePojo> esistenti = leggiTutti();
        // Aggiorna o aggiunge i dati degli utenti passati
        for (Map.Entry<String, UtentePojo> entry : utenti.entrySet()) {
            esistenti.put(entry.getKey(), entry.getValue());
        }
        // Scrive tutti gli utenti (vecchi + nuovi/aggiornati) nel file
        try (BufferedWriter w = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (UtentePojo p : esistenti.values()) {
                w.write(p.getUsername() + ";" + p.getAvatarPath() + ";" +
                        p.getPartiteGiocate() + ";" + p.getPartiteVinte() + ";" + p.getPartitePerse());
                w.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("Errore scrittura utenti.txt: " + e.getMessage());
            return false;
        }
    }

    public void recuperaDatiUtente(String nickname) {
        try (BufferedReader r = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] campi = line.split(";", -1); // nickname;avatar;giocate;vinte;perse
                if (campi.length != 5) continue;
                String nick = campi[0];
                String avatar = campi[1].isBlank() ? DEFAULT_AVATAR : campi[1];
                int giocate = Integer.parseInt(campi[2]);
                int vinte = Integer.parseInt(campi[3]);
                int perse = Integer.parseInt(campi[4]);
                System.out.println("RECUPERA DATI UTENTE");
                System.out.println("Giocate: " + giocate);
                System.out.println("Vinte: " + vinte);
                System.out.println("Perse: " + perse);
                if (nick.equals(nickname)) {
                    UtentePojo p = new UtentePojo(nick, avatar, giocate, vinte, perse);
                    
                    System.out.println("Utente trovato: " + p.getUsername());
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("File non trovato: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Errore lettura file: " + e.getMessage());
        }
    }
}
