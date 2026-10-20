package modulo1;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LeitoresEscritores {
    private static final int NUM_LEITORES = 4;
    private static final int NUM_ESCRITORES = 2;

    private static int dadoCompartilhado = 0;

    private static final ReentrantReadWriteLock trava =
            new ReentrantReadWriteLock(true);

    private static void executarLeitor(int id) {
        try {
            for (int i = 0; i < 3; i++) {
                trava.readLock().lock();

                try {
                    System.out.println(
                        "Leitor " + id
                        + " leu: " + dadoCompartilhado
                    );

                    Thread.sleep(100);
                } finally {
                    trava.readLock().unlock();
                }

                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void executarEscritor(int id) {
        try {
            for (int i = 0; i < 3; i++) {
                trava.writeLock().lock();

                try {
                    dadoCompartilhado++;

                    System.out.println(
                        "Escritor " + id
                        + " atualizou para: "
                        + dadoCompartilhado
                    );

                    Thread.sleep(100);
                } finally {
                    trava.writeLock().unlock();
                }

                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args)
            throws InterruptedException {

        Thread[] leitores =
                new Thread[NUM_LEITORES];

        Thread[] escritores =
                new Thread[NUM_ESCRITORES];

        for (int i = 0; i < NUM_LEITORES; i++) {
            final int id = i + 1;

            leitores[i] = new Thread(
                () -> executarLeitor(id)
            );

            leitores[i].start();
        }

        for (int i = 0; i < NUM_ESCRITORES; i++) {
            final int id = i + 1;

            escritores[i] = new Thread(
                () -> executarEscritor(id)
            );

            escritores[i].start();
        }

        for (Thread leitor : leitores) {
            leitor.join();
        }

        for (Thread escritor : escritores) {
            escritor.join();
        }

        System.out.println(
            "Valor final: " + dadoCompartilhado
        );
    }
}
