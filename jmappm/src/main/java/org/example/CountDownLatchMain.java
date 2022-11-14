package org.example;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchMain {
    private static final int numberOfThreads = 2;
    private static final int numberToSearch = 8;
    private static final int[] array = new int[]{1, 2, 3, 4, 5, 6, 7, 8};
    private static final CountDownLatch countDownLatch = new CountDownLatch(numberOfThreads);
    private static int foundPosition = 0;

    public static void main(String[] args) throws InterruptedException {
        int threadSlice = array.length / numberOfThreads;

        for (int i = 0; i < numberOfThreads; i++) {
            var t = new Thread(new WorkerThread(i * threadSlice, (i + 1) * threadSlice));
            t.start();
        }

        countDownLatch.await();
        System.out.println("Found position: " + foundPosition);
    }

    static class WorkerThread implements Runnable {
        private final int left;
        private final int right;

        public WorkerThread(int left, int right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public void run() {
            for (int i = left; i < right; i++) {
                if (array[i] == numberToSearch) {
                    foundPosition = i;
                }
            }
            countDownLatch.countDown();
        }
    }
}
