package model;

public class GiocatoreAI extends Giocatore{

    public GiocatoreAI() {
        super(generaNomeCasuale());
    }

    private static String generaNomeCasuale() {
        String[] nomi = { "Bob", "Matteo", "Alice"};
        int i = (int) (Math.random() * nomi.length);
        return nomi[i] + "_" + (100 + (int)(Math.random() * 900));
    }

    // TO DO: Implementa la strategia di gioco
    
}
