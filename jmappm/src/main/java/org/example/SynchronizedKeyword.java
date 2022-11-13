package org.example;

import java.util.ArrayList;
import java.util.List;

public class SynchronizedKeyword {
    private static final Object obj = new Object();
    private static int globalCounter = 0;

    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();

        var group = new ThreadGroup("Group1");

        for (int i = 1; i <= 1000; i++) {
            var t = new Thread(group, new MyThread());
            t.start();
            threads.add(t);
        }

        group.interrupt();

        threads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        System.out.println("Total = " + globalCounter);
    }

    static class MyThread implements Runnable {
        private static void staticIncrement() {
            synchronized (MyThread.class) {
            }
        }

        @Override
        public void run() {
            try {
                Thread.sleep(99999);
            } catch (InterruptedException ignored) {
            }
            synchronized (obj) {
                globalCounter++;
            }
        }

        private void increment() {
            synchronized (this) {
            }
        }
    }
}
