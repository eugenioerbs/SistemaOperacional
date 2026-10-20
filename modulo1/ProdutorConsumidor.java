package modulo1;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class ProdutorConsumidor {
    private static final int CAPACIDADE = 5;
    private static final int TOTAL_ITENS = 20;

    private static final int[] buffer =
            new int[CAPACIDADE];

    private static int entrada = 0;
    private static int saida = 0;

    private static final ReentrantLock mutex =
            new ReentrantLock();

    private static final Semaphore vazios =
            new Semaphore(CAPACIDADE, true);

    private static final Semaphore cheios =
            new Semaphore(0, true);

    private static void produzir() {
        try {
            for (int item = 1; item <= TOTAL_ITENS; item++) {
                vazios.acquire();

                mutex.lock();

                try {
                    buffer[entrada] = item;

                    System.out.println(
                        "Produzido: " + item
                    );

                    entrada = (entrada + 1) % CAPACIDADE;
                } finally {
                    mutex.unlock();
                }

                cheios.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void consumir() {
        try {
            for (int i = 0; i < TOTAL_ITENS; i++) {
                cheios.acquire();

                mutex.lock();

                try {
                    int item = buffer[saida];

                    System.out.println(
                        "Consumido: " + item
                    );

                    saida = (saida + 1) % CAPACIDADE;
                } finally {
                    mutex.unlock();
                }

                vazios.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args)
            throws InterruptedException {

        Thread produtor =
                new Thread(ProdutorConsumidor::produzir);

        Thread consumidor =
                new Thread(ProdutorConsumidor::consumir);

        produtor.start();
        consumidor.start();

        produtor.join();
        consumidor.join();

        System.out.println(
            "Producao e consumo finalizados."
        );
    }
}
