public class Es_Bagno {

    public static void main(String[] args) {
        Toilette toilette = new Toilette(3); // Capienza massima di 3 persone

        // Crea e avvia vari thread
        for (int i = 0; i < 6; i++) {
            String genere = (i % 2 == 0) ? "uomo" : "donna";
            new Persona(toilette, genere).start();
        }
    }
}

// Monitor Toilette
class Toilette {
    private final int CAPACITY;
    private int count = 0;
    private int donneInAttesa = 0;
    private String occupatoDa = "";

    public Toilette(int capacity) {
        this.CAPACITY = capacity;
    }

    public synchronized void entra(String genere) throws InterruptedException {
        if (genere.equals("donna")) {
            donneInAttesa++;
            while ((!"".equals(occupatoDa) && !"donna".equals(occupatoDa)) || count >= CAPACITY) {
                wait();
            }
            donneInAttesa--;
            occupatoDa = "donna";
            count++;
        } else {
            while ((!"".equals(occupatoDa) && !"uomo".equals(occupatoDa)) || count >= CAPACITY || donneInAttesa > 0) {
                wait();
            }
            occupatoDa = "uomo";
            count++;
        }
        System.out.println(genere + " è entrato. Totale dentro: " + count);
    }

    public synchronized void esci(String genere) {
        count--;
        System.out.println(genere + " è uscito. Totale dentro: " + count);
        if (count == 0) {
            occupatoDa = "";
        }
        notifyAll();
    }
}

// Thread Persona
class Persona extends Thread {
    private Toilette toilette;
    private String genere;

    public Persona(Toilette toilette, String genere) {
        this.toilette = toilette;
        this.genere = genere;
    }

    @Override
    public void run() {
        try {
            toilette.entra(genere);
            Thread.sleep((long)(Math.random() * 2000) + 500);
            toilette.esci(genere);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
