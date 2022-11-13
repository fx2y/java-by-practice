package org.example;

import java.util.ArrayList;
import java.util.List;

public class ThreadLocalVariables {
    private static int globalCounter = 0;
    private static ThreadLocal<String> threadLocal = ThreadLocal.withInitial(() -> "Initial value");

    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();

        var group = new ThreadGroup("Group");

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

//        var t1 = new Thread(new MyThread1());
//        var t2 = new Thread(new MyThread1());
//
//        t1.start();
//        t2.start();
    }

    static class MyThread implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(99999);
            } catch (InterruptedException ignored) {
            }
            globalCounter++;

//            int localCounter = globalCounter;
//            localCounter = localCounter + 1;
//            globalCounter = localCounter;
        }
    }

//    static class MyThread implements Runnable {
//        @Override
//        public void run() {
//            int counter = 0;
//            threadLocal.set("myValue");
//            threadLocal.get();
//        }
//    }
}
