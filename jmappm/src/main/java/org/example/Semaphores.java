package org.example;

import java.util.concurrent.Semaphore;

public class Semaphores {
    private static Semaphore semaphore = new Semaphore(2);

    public static void main(String[] args) throws InterruptedException {
        var executor = new Executor();

        executor.submit(new Job(4000));
        executor.submit(new Job(5000));
        executor.submit(new Job(3000));
    }

    static class Executor {
        public void submit(Job job) throws InterruptedException {
            System.out.println("Submitting job " + job.work());
            semaphore.acquire();

            var t = new Thread(() -> {
                try {
                    System.out.println("Executing job " + job.work());
                    Thread.sleep(job.work());

                    semaphore.release();
                    System.out.println("Finished job " + job.work());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            t.start();
        }
    }

    record Job(int work) {
    }
}
