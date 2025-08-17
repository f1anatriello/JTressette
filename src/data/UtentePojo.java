package data;

/**
 * Rappresenta il profilo di un utente con statistiche di gioco e avatar.
 */
public class UtentePojo {

    private String username;
    private int partiteGiocate;
    private int partiteVinte;
    private int partitePerse;
    private String avatarPath;

    /**
     * Crea un profilo utente con contatori a zero e senza avatar.
     *
     * @param username nome dell'utente
     */
    public UtentePojo(String username, String avatarPath, int partiteGiocate, int partiteVinte, int partitePerse) {
        this.username = username;
        this.partiteGiocate = partiteGiocate != 0 ? partiteGiocate : 0;
        this.partiteVinte = partiteVinte != 0 ? partiteVinte : 0;
        this.partitePerse = partitePerse != 0 ? partitePerse : 0;
        this.avatarPath = avatarPath;
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
    public void setPartiteGiocate(int partiteGiocate) { this.partiteGiocate = getPartiteGiocate() + 1; }
    public void setPartiteVinte(int partiteVinte) { this.partiteVinte = getPartiteVinte() + 1; }
    public void setPartitePerse(int partitePerse) { this.partitePerse = getPartitePerse() + 1; }
    public void setAvatarPath(String avatarPath) { this.avatarPath = avatarPath == null ? UserRepository.DEFAULT_AVATAR : avatarPath; }
    

    /** Incrementa il numero di partite giocate e di vittorie. */
    public void incrementaVinte() {
        partiteGiocate++;
        partiteVinte++;
        UserRepository.getInstance().salvaProfilo(this);
    }

    /** Incrementa il numero di partite giocate e di sconfitte. */
    public void incrementaPerse() {
        partiteGiocate++;
        partitePerse++;
        UserRepository.getInstance().salvaProfilo(this);
    }
}
