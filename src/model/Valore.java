package model;

/**
 * Rappresenta i valori nominali delle carte nel gioco del Tressette.
 * Ogni valore ha un punteggio associato e un ordine di presa.
 * I valori sono: TRE, DUE, ASSO, RE, CAVALLO, FANTE, SETTE, SEI, CINQUE, QUATTRO.
 * @author 1957447
 */
public enum Valore {
    
    /** Il Tre, uno dei "carichi", vale 1/3 di punto e ha il massimo ordine di presa. */
    TRE(0.3333, 10),
    
    /** Il Due, l'altro "carico", vale 1/3 di punto e ha il secondo ordine di presa. */
    DUE(0.3333, 9),
    
    /** L'Asso, la carta di maggior punteggio, vale 1 punto e ha il terzo ordine di presa. */
    ASSO(1.0, 8),
    
    /** Il Re, una "figura", vale 1/3 di punto. */
    RE(0.3333, 7),
    
    /** Il Cavallo, una "figura", vale 1/3 di punto. */
    CAVALLO(0.3333, 6),
    
    /** Il Fante, una "figura", vale 1/3 di punto. */
    FANTE(0.3333, 5),
    
    /** Il Sette, uno "scarto", non ha valore in punti. */
    SETTE(0.0, 4),
    
    /** Il Sei, uno "scarto", non ha valore in punti. */
    SEI(0.0, 3),
    
    /** Il Cinque, uno "scarto", non ha valore in punti. */
    CINQUE(0.0, 2),
    
    /** Il Quattro, uno "scarto", ha il minimo ordine di presa e non ha valore in punti. */
    QUATTRO(0.0, 1);

    private final double punti;
    private final int ordinePresa;

    /**
     * Costruttore privato per l'enum Valore.
     *
     * @param punti        Il punteggio associato a questo valore.
     * @param ordinePresa  L'ordine gerarchico per la presa (più alto è più forte).
     */
    Valore(double punti, int ordinePresa) {
        this.punti = punti;
        this.ordinePresa = ordinePresa;
    }

    /**
     * Restituisce il punteggio associato a questo valore di carta.
     *
     * @return I punti della carta come valore {@code double}.
     */
    public double getPunti() {
        return punti;
    }

    /**
     * Restituisce l'ordine di presa di questo valore di carta.
     * Utilizzato per determinare quale carta vince una mano.
     *
     * @return L'ordine di presa della carta come valore intero.
     */
    public int getOrdinePresa() {
        return ordinePresa;
    }
}