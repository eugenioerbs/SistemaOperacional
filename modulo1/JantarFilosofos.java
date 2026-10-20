package modulo1;

import java.util.concurrent.locks.ReentrantLock;

public class JantarFilosofos {
    private static final int N = 5;
    private static final int RODADAS = 3;

    private static final ReentrantLock[] hashis =
            new ReentrantLock[N];

    static {
        for (int i = 0; i < N; i++) {
            hashis[i] = new ReentrantLock(true);
        }
    }

    private static void executarFilosofo(int id) {
        int esquerdo = id;
        int direito = (id + 1) % N;

        int primeiro = Math.min(esquerdo, direito);
        int segundo = Math.max(esquerdo, direito);

        try {
            for (int rodada = 0; rodada < RODADAS; rodada++) {

                System.out.println(
                    "Filosofo " + id + " esta pensando."
                );

                Thread.sleep(100);

                hashis[primeiro].lock();
                hashis[segundo].lock();

                try {
                    System.out.println(
                        "Filosofo " + id + " esta comendo."
                    );

                    Thread.sleep(100);
                } finally {
                    hashis[segundo].unlock();
                    hashis[primeiro].unlock();
                }
            }

            System.out.println(
                "Filosofo " + id + " terminou."
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args)
            throws InterruptedException {

        Thread[] filosofos = new Thread[N];

        for (int i = 0; i < N; i++) {
            final int id = i;

            filosofos[i] = new Thread(
                () -> executarFilosofo(id),
                "Filosofo-" + id
            );

            filosofos[i].start();
        }

        for (Thread filosofo : filosofos) {
            filosofo.join();
        }

        System.out.println(
            "Todos os filosofos terminaram."
        );
    }
}
