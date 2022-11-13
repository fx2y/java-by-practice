package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Locks {
    private static final int[] array = new int[10];
    private static final int arrayLength = 10;
    private static final int numberOfThreads = 2;
    private static final ReentrantLock mutex = new ReentrantLock();
    private static int sum = 0;

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < arrayLength; i++) {
            array[i] = 10;
        }

        List<Thread> threads = new ArrayList<>();
        int threadSlice = arrayLength / numberOfThreads;

        for (int i = 0; i < numberOfThreads; i++) {
            var t = new Thread(new MyThread(i * threadSlice, (i + 1) * threadSlice));
            t.start();
            threads.add(t);
        }

        for (Thread t : threads) {
            t.join();
        }

        System.out.println("Sum is: " + sum);
    }

    static class MyThread implements Runnable {
        private final int left;
        private final int right;

        public MyThread(int left, int right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public void run() {
            for (int i = left; i < right; i++) {
                mutex.lock();
                sum = sum + array[i];
                mutex.unlock();
            }
        }
    }
}
