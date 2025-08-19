package model;

public class GiocatoreUmano extends Giocatore {

    private OnCartaSceltaListener listener;

    public GiocatoreUmano(String nome) {
        super(nome);
    }

    // interfaccia funzionale
    public interface OnCartaSceltaListener {
        void onCartaScelta(Carta c);
    }

    public void setOnCartaSceltaListener(OnCartaSceltaListener listener) {
        this.listener = listener;
    }

    // metodo chiamato quando l’utente sceglie una carta dalla GUI
    public void notificaCartaScelta(Carta c) {
        if (listener != null) {
            listener.onCartaScelta(c);
        }
    }
}
