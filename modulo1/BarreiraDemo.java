package modulo1;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class BarreiraDemo {
    private static final int NUM_THREADS = 3;

    private static final CyclicBarrier barreira =
            new CyclicBarrier(NUM_THREADS);

    private static void executarTarefa(int id) {
        try {
            Thread.sleep(id * 150L);

            System.out.println(
                "Thread " + id
                + " concluiu a fase 1 e chegou a barreira."
            );

            barreira.await();

            System.out.println(
                "Thread " + id
                + " iniciou a fase 2."
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

        } catch (BrokenBarrierException e) {
            System.err.println(
                "Barreira interrompida."
            );
        }
    }

    public static void main(String[] args)
            throws InterruptedException {

        Thread[] threads = new Thread[NUM_THREADS];

        for (int i = 0; i < NUM_THREADS; i++) {
            final int id = i + 1;

            threads[i] = new Thread(
                () -> executarTarefa(id)
            );

            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println(
            "Todas as threads finalizaram."
        );
    }
}
