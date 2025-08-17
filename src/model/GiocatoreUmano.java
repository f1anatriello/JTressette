package model;

import java.util.Scanner;

public class GiocatoreUmano extends Giocatore {

	public GiocatoreUmano(String nome) {
        super(nome);
    }

    public Carta scegliCarta() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Scegli una carta:");
        for (int i = 0; i < mano.size(); i++) {
            System.out.println(i + 1 + ": " + mano.get(i));
        }
        int scelta = scanner.nextInt() - 1;
        return mano.get(scelta);
    }
}
