package model;

public class GiocatoreAI extends Giocatore{

    public GiocatoreAI() {
        super(generaNomeCasuale());
    }

    private static String generaNomeCasuale() {
        String[] nomi = { "Bob", "Matteo", "Alice"};
        int i = (int) (Math.random() * nomi.length);
        return nomi[i] + (100 + (int)(Math.random() * 900));
    }

    public Carta scegliCarta(Partita p) {
        // Implementa una semplice logica per scegliere una carta
        // Ad esempio, può scegliere la prima carta disponibile nella mano
        try {
            Thread.sleep(3000 + (int)(Math.random() * 4000)); // Simula una pausa tra 3 e 4 secondi
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        for (Carta carta : getMano()) {
            // Se è il primo a giocare, può scegliere qualsiasi carta
            if (p.getTerreno().isEmpty()) {
                return carta;
            }
            // Altrimenti deve seguire il seme della prima carta sul tavolo se possibile
            Seme semeDaSeguire = p.getTerreno().get(0).getSeme();
            boolean haSeme = getMano().stream().anyMatch(c -> c.getSeme().equals(semeDaSeguire));
            if (haSeme) {
                if (carta.getSeme().equals(semeDaSeguire)) {
                    System.out.println("Carta giocata: " + carta);
                    return carta;
                }
            } else {
                // Se non ha il seme, può giocare qualsiasi carta
                System.out.println("Carta diverso seme: " + carta);
                return carta;
            }
        }
        // Se non trova nessuna carta (caso raro), restituisce null
        return null;
    }

    @Override
    public String getNome() {
        return "AI_" + super.getNome();
    }

}
