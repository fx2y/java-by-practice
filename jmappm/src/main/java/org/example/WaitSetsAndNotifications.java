package org.example;

import java.util.LinkedList;
import java.util.Queue;

public class WaitSetsAndNotifications {
    private static final Object obj = new Object();

    public static void main(String[] args) {
        Queue<String> queue = new LinkedList<>();

        var producer = new Thread(new Producer(queue));
        var consumer = new Thread(new Consumer(queue));

        producer.start();
        consumer.start();
    }

    static class Producer implements Runnable {
        private final Queue<String> queue;

        public Producer(Queue<String> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    produceData();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void produceData() throws InterruptedException {
            synchronized (queue) {
                if (queue.size() == 10) {
                    System.out.println("In producer, waiting...");
                    queue.wait();
                }

                Thread.sleep(1000);

                System.out.println("Producing data with id " + queue.size());
                queue.add("element_" + queue.size());

                if (queue.size() == 1) {
                    queue.notify();
                }
            }
        }
    }

    static class Consumer implements Runnable {

        private final Queue<String> queue;

        public Consumer(Queue<String> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    consumeData();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void consumeData() throws InterruptedException {
            synchronized (queue) {
                if (queue.isEmpty()) {
                    System.out.println("In consumer, waiting...");
                    queue.wait();
                }

                Thread.sleep(700);

                var data = queue.remove();
                System.out.println("Consumed data " + data);

                if (queue.size() == 9) {
                    queue.notify();
                }
            }
        }
    }
}
