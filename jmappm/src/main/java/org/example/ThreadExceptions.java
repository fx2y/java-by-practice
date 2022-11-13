package org.example;

import java.util.List;

public class ThreadExceptions {
    public static void main(String[] args) throws InterruptedException {
        var thread = new Thread(new CustomThreadGroup("group"), new MyThread(1), "Thread");

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            System.out.println(e.getMessage());
        });

        thread.start();
        thread.join();
    }

    static class CustomThreadGroup extends ThreadGroup {
        public CustomThreadGroup(String name) {
            super(name);
        }

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            super.uncaughtException(t, e);
            System.out.println("Uncaught exception in thread: " + t.getName() + " exception: " + e.getMessage());
        }
    }

    static class MyThread implements Runnable {
        private final int numberOfSeconds;

        public MyThread(int numberOfSeconds) {
            this.numberOfSeconds = numberOfSeconds;
        }

        @Override
        public void run() {
            for (int i = 0; i < numberOfSeconds; i++) {
                try {
                    System.out.println("Sleeping for 1s, thread: " + Thread.currentThread().getName());
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                List<String> list = null;
                list.size();

                System.out.println("Result: " + 1 / 0);
            }
        }
    }
}
