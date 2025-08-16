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
    public boolean loginOrCreate(String nickname) {
        Map<String, UtentePojo> utenti = leggiTutti();
        if (utenti.containsKey(nickname)) {
            return true; // già esistente
        }
        utenti.put(nickname, creaProfiloDiDefault(nickname));
        return salvaTutti(utenti);
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
        p.incrementaVinte();
        utenti.put(nickname, p);
        salvaTutti(utenti);
    }

    public void registraSconfitta(String nickname) {
        Map<String, UtentePojo> utenti = leggiTutti();
        UtentePojo p = utenti.getOrDefault(nickname, creaProfiloDiDefault(nickname));
        p.incrementaPerse();
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
        UtentePojo p = new UtentePojo(nickname, DEFAULT_AVATAR);
        p.setPartiteGiocate(0);
        p.setPartiteVinte(0);
        p.setPartitePerse(0);
        return p;
    }

    /** Legge tutto il file in memoria */
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
                UtentePojo p = new UtentePojo(nick, avatar);
                p.setPartiteGiocate(giocate);
                p.setPartiteVinte(vinte);
                p.setPartitePerse(perse);
                map.put(nick, p);
            }
        } catch (IOException e) {
            System.err.println("Errore lettura utenti.txt: " + e.getMessage());
        }
        return map;
    }

    /** Sovrascrive il file con tutti gli utenti */
    private boolean salvaTutti(Map<String, UtentePojo> utenti) {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (UtentePojo p : utenti.values()) {
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
}
