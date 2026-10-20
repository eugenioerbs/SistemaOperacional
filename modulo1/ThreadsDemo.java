package modulo1;

public class ThreadsDemo {
    public static void main(String[] args)
            throws InterruptedException {

        Runnable tarefa = () -> {
            String nome = Thread.currentThread().getName();

            for (int i = 1; i <= 3; i++) {
                System.out.printf(
                    "%s executando etapa %d%n", nome, i
                );
            }
        };

        Thread t1 = new Thread(tarefa, "Thread-1");
        Thread t2 = new Thread(tarefa, "Thread-2");
        Thread t3 = new Thread(tarefa, "Thread-3");

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        System.out.println("Todas as threads terminaram.");
    }
}