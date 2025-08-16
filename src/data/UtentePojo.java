package data;

import ui.UIAssets;

/**
 * Rappresenta il profilo di un utente con statistiche di gioco e avatar.
 */
public class UtentePojo {

    private String username;
    private int partiteGiocate;
    private int partiteVinte;
    private int partitePerse;
    /** Percorso relativo/assoluto dell'avatar (o URL). */
    private String avatarPath;

    /**
     * Crea un profilo utente con contatori a zero e senza avatar.
     *
     * @param username nome dell'utente
     */
    public UtentePojo(String username, String avatarPath) {
        this(username, 0, 0, 0, avatarPath);
    }

    /**
     * Crea un profilo utente con valori iniziali specificati.
     *
     * @param username        nome dell'utente
     * @param partiteGiocate  numero di partite giocate
     * @param partiteVinte    numero di partite vinte
     * @param partitePerse    numero di partite perse
     * @param avatarPath      percorso/URL dell'avatar (può essere vuoto)
     */
    public UtentePojo(String username, int partiteGiocate, int partiteVinte, int partitePerse, String avatarPath) {
        this.username = username;
        this.partiteGiocate = partiteGiocate;
        this.partiteVinte = partiteVinte;
        this.partitePerse = partitePerse;
        this.avatarPath = avatarPath == null ? UserRepository.DEFAULT_AVATAR : avatarPath;
    }

    /** @return il nome utente */
    public String getUsername() { return username; }
    /** @return numero di partite giocate */
    public int getPartiteGiocate() { return partiteGiocate; }
    /** @return numero di partite vinte */
    public int getPartiteVinte() { return partiteVinte; }
    /** @return numero di partite perse */
    public int getPartitePerse() { return partitePerse; }
    /** @return percorso/URL dell'avatar (stringa vuota se non impostato) */
    public String getAvatarPath() { return avatarPath; }

    public void setUsername(String username) { this.username = username; }
    public void setPartiteGiocate(int partiteGiocate) { this.partiteGiocate = partiteGiocate; }
    public void setPartiteVinte(int partiteVinte) { this.partiteVinte = partiteVinte; }
    public void setPartitePerse(int partitePerse) { this.partitePerse = partitePerse; }
    public void setAvatarPath(String avatarPath) { this.avatarPath = avatarPath == null ? "" : avatarPath; }
    

    /** Incrementa il numero di partite giocate e di vittorie. */
    public void incrementaVinte() {
        partiteGiocate++;
        partiteVinte++;
    }

    /** Incrementa il numero di partite giocate e di sconfitte. */
    public void incrementaPerse() {
        partiteGiocate++;
        partitePerse++;
    }

    @Override
    public String toString() {
        return "ProfiloUtente{" +
                "username='" + username + '\'' +
                ", partiteGiocate=" + partiteGiocate +
                ", partiteVinte=" + partiteVinte +
                ", partitePerse=" + partitePerse +
                ", avatarPath='" + avatarPath + '\'' +
                '}';
    }

}
