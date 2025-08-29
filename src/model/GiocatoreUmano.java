package model;

/**
 * Rappresenta un giocatore umano in una partita di Tressette.
 * Estende la classe Giocatore e fornisce un'interfaccia per la selezione delle carte tramite GUI.
 * Utilizza un listener per notificare quando una carta è stata scelta dall'utente.
 * @author 1957447
 */
public class GiocatoreUmano extends Giocatore {

    private OnCartaSceltaListener listener;
    /**
     * Costruisce un nuovo giocatore umano con il nome specificato.
     * @param nome Il nome del giocatore.
     */
    public GiocatoreUmano(String nome) {
        super(nome);
    }

    /**
     * Interfaccia per il listener che gestisce la selezione delle carte.
     */
    public interface OnCartaSceltaListener {
        void onCartaScelta(Carta c);
    }

    /**
     * Imposta il listener per la selezione delle carte.
     * @param listener Il listener da impostare.
     */
    public void setOnCartaSceltaListener(OnCartaSceltaListener listener) {
        this.listener = listener;
    }

    /**
     * Notifica al listener che una carta è stata scelta.
     * @param c La carta scelta.
     */
    public void notificaCartaScelta(Carta c) {
        if (listener != null) {
            listener.onCartaScelta(c);
        }
    }
}
