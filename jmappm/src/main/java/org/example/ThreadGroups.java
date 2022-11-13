package org.example;

public class ThreadGroups {
    public static void main(String[] args) throws InterruptedException {
        var group = new ThreadGroup("Group1");
        group.setMaxPriority(7);

        var parent = group.getParent();
        System.out.println("Parent name: " + parent.getName() + " priority: " + parent.getMaxPriority());

        var thread1 = new Thread(group, new MyThread(), "Thread1");
        var thread2 = new Thread(group, new MyThread(), "Thread2");
        var thread3 = new Thread(group, new MyThread(), "Thread3");

        thread1.setPriority(Thread.MAX_PRIORITY);

        thread1.start();
        thread2.start();
        thread3.start();

        System.out.println("Sleeping for 3 seconds...");
        Thread.sleep(3000);

        group.interrupt();
    }

    static class MyThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    var currentThread = Thread.currentThread();
                    System.out.println("Name: " + currentThread.getName() + " priority = " + currentThread.getPriority());
                }
            }
        }
    }
}
