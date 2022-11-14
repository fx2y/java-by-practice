package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLocksAndSpinlocks {
    private static final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private static final Lock readLock = readWriteLock.readLock();
    private static final Lock writeLock = readWriteLock.writeLock();

    private static final List<Integer> list = new ArrayList<>();

    public static void main(String[] args) {
        var writer = new Thread(new WriterThread());

        var reader1 = new Thread(new ReaderThread());
        var reader2 = new Thread(new ReaderThread());
        var reader3 = new Thread(new ReaderThread());
        var reader4 = new Thread(new ReaderThread());

        writer.start();
        reader1.start();
        reader2.start();
        reader3.start();
        reader4.start();
    }

    static class WriterThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    writeData();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void writeData() throws InterruptedException {
            Thread.sleep(10000);

            writeLock.lock();

            int value = (int) (Math.random() * 10);
            System.out.println("Producing data: " + value);

            Thread.sleep(3000);

            list.add(value);

            writeLock.unlock();
        }
    }

    static class ReaderThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    readData();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void readData() throws InterruptedException {
            Thread.sleep(3000);

            while (true) {
                var acquired = readLock.tryLock();
                if (acquired) {
                    break;
                } else {
                    System.out.println("Waiting for read lock...");
                }
            }

            System.out.println("List is: " + list);
            readLock.unlock();
        }
    }
}
