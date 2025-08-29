package data;

import java.io.*;
import java.util.*;

/**
 * Repository per la gestione dei profili utente.
 * I dati sono salvati in un file di testo "utenti.txt" con il seguente formato per riga:
 * nickname;avatarPath;partiteGiocate;partiteVinte;partitePerse
 * Esempio:
 * mario;images/avatars/avatar2.png;10;7;3
 * Lettura e scrittura avvengono ad ogni operazione per garantire la persistenza.
 * Il repository segue il pattern Singleton.
 * @author 1957447
 */

public final class UserRepository {

    private static UserRepository instance;
    public static final String FILE_NAME = "utenti.txt";
    public static final String DEFAULT_AVATAR = "images/avatars/avatar1.png";

    /**
     * Costruttore privato per il pattern Singleton.
     * Crea il file utenti.txt se non esiste.
     * @throws IOException se si verifica un errore di I/O durante la creazione del file
     */
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

    /**
     * Restituisce l'istanza singleton del repository.
     * @return L'istanza di UserRepository
     * @throws IOException se si verifica un errore di I/O durante la creazione del file
     */
    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    /** Login o creazione di un nuovo profilo 
     * @param nickname Il nickname dell'utente
     * @return Il profilo utente esistente o appena creato
     * */
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

    /**
     * Salva o aggiorna un profilo utente.
     * @param profilo
     * @return true se l'operazione ha avuto successo, false altrimenti
     */
    public boolean salvaProfilo(UtentePojo profilo) {
        Map<String, UtentePojo> utenti = leggiTutti();
        utenti.put(profilo.getUsername(), profilo);
        return salvaTutti(utenti);
    }

    /**
     * Carica un profilo utente dato il nickname.
     * @param nickname
     * @return Il profilo utente o null se non esiste
     */
    public UtentePojo caricaProfilo(String nickname) {
        return leggiTutti().get(nickname);
    }

    /**
     * Registra una vittoria per l'utente specificato.
     * @param nickname
     */
    public void registraVittoria(String nickname) {
        Map<String, UtentePojo> utenti = leggiTutti();
        UtentePojo p = utenti.getOrDefault(nickname, creaProfiloDiDefault(nickname));
        utenti.put(nickname, p);
        salvaTutti(utenti);
    }

    /**
     * Registra una sconfitta per l'utente specificato.
     * @param nickname
     */
    public void registraSconfitta(String nickname) {
        Map<String, UtentePojo> utenti = leggiTutti();
        UtentePojo p = utenti.getOrDefault(nickname, creaProfiloDiDefault(nickname));
        utenti.put(nickname, p);
        salvaTutti(utenti);
    }

    /**
     * Aggiorna il percorso dell'avatar per l'utente specificato.
     * @param nickname
     * @param avatarPath
     */
    public void aggiornaAvatar(String nickname, String avatarPath) {
        Map<String, UtentePojo> utenti = leggiTutti();
        UtentePojo p = utenti.getOrDefault(nickname, creaProfiloDiDefault(nickname));
        p.setAvatarPath((avatarPath == null || avatarPath.isBlank()) ? DEFAULT_AVATAR : avatarPath.trim());
        utenti.put(nickname, p);
        salvaTutti(utenti);
    }

    /**
     * Restituisce la classifica degli utenti ordinata per partite vinte in ordine decrescente.
     * @return Lista degli utenti ordinata per vittorie
     */
    public static List<UtentePojo> getClassifica() {
        List<UtentePojo> list = new ArrayList<>(leggiTutti().values());
        list.sort(Comparator.comparingInt(UtentePojo::getPartiteVinte).reversed());
        return list;
    }

    /**
     * Verifica se un username è valido. Un username valido:
     * - Non è vuoto
     * - Ha una lunghezza tra 3 e 20 caratteri
     * - Contiene solo lettere, numeri, spazi, underscore, trattini o punti
     * @param u
     * @return true se l'username è valido, false altrimenti
     */
    public static boolean isValidUsername(String u) {
        if (u == null) return false;
        String s = u.trim();
        return s.length() >= 3 && s.length() <= 20 && s.matches("[A-Za-z0-9_.\\- ]+");
    }

    /* ===== Utilities ===== */

    /**
     * Crea un profilo utente di default con avatar predefinito e statistiche a zero.
     * @param nickname
     * @return Il profilo utente appena creato
     */
    private UtentePojo creaProfiloDiDefault(String nickname) {
        UtentePojo p = new UtentePojo(nickname, DEFAULT_AVATAR, 0, 0, 0);
        return p;
    }

    /**
     * Legge tutti i profili utente dal file e li restituisce in una mappa.
     * @return Mappa dei profili utente con nickname come chiave e UtentePojo come valore
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

    /**
     * Salva tutti i profili utente nella mappa nel file, sovrascrivendo il contenuto esistente.
     * @param utenti Mappa dei profili utente da salvare
     * @return true se l'operazione ha avuto successo, false altrimenti
     */
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

    /**
     * Recupera e stampa i dati dell'utente specificato.
     * @param nickname
     */
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
