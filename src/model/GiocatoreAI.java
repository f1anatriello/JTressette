package model;

import java.util.Random;

/**
 * Rappresenta un giocatore controllato dall'IA in una partita di Tressette.
 * Estende la classe Giocatore e implementa una semplice logica per scegliere le carte da giocare.
 * La scelta della carta tiene conto del seme da seguire se possibile.
 * Il nome del giocatore IA è generato casualmente da un insieme predefinito di nomi.
 * Simula un ritardo tra 3 e 7 secondi prima di giocare una carta per imitare il tempo di riflessione di un giocatore umano.
 * @author 1957447
 */
public class GiocatoreAI extends Giocatore{

    /**
     * Costruisce un nuovo giocatore IA con un nome generato casualmente.
     */
    public GiocatoreAI() {
        super(generaNomeCasuale());
    }

    /**
     * Genera un nome casuale per il giocatore IA.
     * @return Un nome casuale scelto da un insieme predefinito di nomi.
     */
    private static String generaNomeCasuale() {
        String[] nomi = { "Bob", "Matteo", "Alice", "Marta", "Alessandro", "Giulia", "Luca", "Francesco", "Domenico", "Sara"};
        int i = (int) (Math.random() * nomi.length);
        Random rand = new Random();
        return nomi[i] + rand.nextInt(900);
    }

    /**
     * Sceglie una carta da giocare in base alla logica dell'IA.
     * Se è il primo a giocare, può scegliere qualsiasi carta.
     * Altrimenti, deve seguire il seme della prima carta sul tavolo se possibile.
     * Simula un ritardo tra 3 e 7 secondi prima di restituire la carta scelta.
     * @param p
     * @return La carta scelta dall'IA.
     */
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
            // Controlla se il giocatore ha almeno una carta del seme richiesto nella sua mano
            boolean haSeme = getMano().stream().anyMatch(c -> c.getSeme().equals(semeDaSeguire)); 
            if (haSeme) {
                if (carta.getSeme().equals(semeDaSeguire)) {
                    System.out.println("Carta giocata: " + carta + " da " + getNome());
                    return carta;
                }
            } else {
                // Se non ha il seme, può giocare qualsiasi carta
                System.out.println("Carta diverso seme: " + carta + " da " + getNome());
                return carta;
            }
        }
        // Se non trova nessuna carta (caso raro), restituisce null
        return null;
    }

    /**
     * Restituisce il nome del giocatore IA, prefissato con "AI_".
     * @return Il nome del giocatore IA.
     */
    @Override
    public String getNome() {
        return "AI_" + super.getNome();
    }

}
