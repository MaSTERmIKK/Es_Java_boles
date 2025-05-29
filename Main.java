class Toilette {
    private final int CAPACITY; // Capacità massima della toilette
    private int count = 0;      // Numero di persone attualmente dentro
    private int donneInAttesa = 0; // Donne in attesa
    private String occupatoDa = ""; // "uomo" o "donna" o ""

    public Toilette(int capacity) {
        this.CAPACITY = capacity;
    }

    // Metodo per entrare in toilette
    public synchronized void entra(String genere) throws InterruptedException {
        if (genere.equals("donna")) {
            donneInAttesa++;
            // Aspetta finché dentro ci sono uomini o toilette piena
            while ((!"".equals(occupatoDa) && !"donna".equals(occupatoDa)) || count >= CAPACITY) {
                wait();
            }
            donneInAttesa--;
            occupatoDa = "donna";
            count++;
        } else { // uomo
            // Aspetta se ci sono donne dentro, toilette piena, o donne in attesa (priorità donne)
            while ((!"".equals(occupatoDa) && !"uomo".equals(occupatoDa)) || count >= CAPACITY || donneInAttesa > 0) {
                wait();
            }
            occupatoDa = "uomo";
            count++;
        }
        System.out.println(genere + " è entrato. Totale dentro: " + count);
    }

    // Metodo per uscire dalla toilette
    public synchronized void esci(String genere) {
        count--;
        System.out.println(genere + " è uscito. Totale dentro: " + count);
        if (count == 0) {
            occupatoDa = "";
        }
        notifyAll();
    }
}

// Simulazione di una persona che usa la toilette
class Persona extends Thread {
    private Toilette toilette;
    private String genere;

    public Persona(Toilette t, String genere) {
        this.toilette = t;
        this.genere = genere;
    }

    @Override
    public void run() {
        try {
            toilette.entra(genere);
            Thread.sleep((long)(Math.random() * 2000) + 500); // Simula tempo in toilette
            toilette.esci(genere);
        } catch (InterruptedException e) { }
    }
}

// Main di test
public class Main {
    public static void main(String[] args) {
        Toilette toilette = new Toilette(3); // Capienza massima di 3 persone
        // Crea e avvia vari thread
        for (int i = 0; i < 6; i++) {
            new Persona(toilette, (i % 2 == 0) ? "uomo" : "donna").start();
        }
    }
}
