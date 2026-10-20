package modulo1;

import java.util.concurrent.locks.ReentrantLock;

public class MutexDemo {
    private static final int ITERACOES = 100_000;

    private static long contador = 0;

    private static final ReentrantLock mutex =
            new ReentrantLock(true);

    public static void main(String[] args)
            throws InterruptedException {

        Runnable tarefa = () -> {
            for (int i = 0; i < ITERACOES; i++) {
                mutex.lock();

                try {
                    contador++;
                } finally {
                    mutex.unlock();
                }
            }
        };

        Thread t1 = new Thread(tarefa, "Thread-1");
        Thread t2 = new Thread(tarefa, "Thread-2");

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Contador final: " + contador);
        System.out.println(
            "Valor esperado: " + (2L * ITERACOES)
        );
    }
}
