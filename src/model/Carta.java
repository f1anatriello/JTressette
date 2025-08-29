package model;

/**
 * Rappresenta una singola carta da gioco, definita da un Seme e un Valore.
 * Fornisce metodi per accedere al seme, al valore e ai punti associati alla carta.
 * La carta può essere associata a un giocatore che la possiede.
 * @author 1957447
 */
public class Carta {
    private final Seme seme;
    private final Valore valore;
    private Giocatore giocatore;

    /**
     * Costruisce una nuova istanza di Carta.
     * @param seme Il {@link Seme} della carta (es. BASTONI, COPPE). Non può essere nullo.
     * @param valore Il {@link Valore} della carta (es. ASSO, TRE). Non può essere nullo.
     */
    public Carta(Seme seme, Valore valore) {
        if (seme == null || valore == null) {
            throw new IllegalArgumentException("Seme e Valore non possono essere nulli.");
        }
        this.seme = seme;
        this.valore = valore;
    }

    /**
     * Restituisce il seme di questa carta.
     * @return L'enum {@link Seme} che rappresenta il seme della carta.
     */
    public Seme getSeme() {
        return seme;
    }

    /**
     * Restituisce il valore nominale di questa carta.
     * @return L'enum {@link Valore} che rappresenta il valore della carta.
     */
    public Valore getValore() {
        return valore;
    }

    /**
     * Restituisce il punteggio che la carta conferisce quando viene presa.
     * Questo valore è delegato all'enum {@link Valore}.
     * @return Il punteggio della carta come valore {@code double}.
     */
    public double getPunti() {
        return valore.getPunti();
    }

    public void setGiocatore(Giocatore g) {
        this.giocatore = g;
    }

    public Giocatore getGiocatore() {
        return giocatore;
    }

    /**
     * Fornisce una rappresentazione testuale della carta.
     * @return Una stringa nel formato "VALORE di SEME" (es. "ASSO di BASTONI").
     */
    @Override
    public String toString() {
        return valore + " di " + seme;
    }
    
    
}