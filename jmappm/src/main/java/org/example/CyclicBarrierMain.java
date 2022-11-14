package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierMain {
    private static final CyclicBarrier cyclicBarrier = new CyclicBarrier(4, () -> System.out.println("Finished"));
    private static final int[][] array = {
            {1, 2, 3, 1},
            {2, 1, 2, 1},
            {1, 2, 1, 3},
            {1, 1, 1, 2},
    };

    public static void main(String[] args) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            var t = new Thread(new WorkerThread(i));
            t.start();
            threads.add(t);
        }

        for (Thread t : threads) {
            t.join();
        }

        System.out.println("The final array: " + Arrays.deepToString(array));
    }

    record WorkerThread(int columnId) implements Runnable {
        @Override
        public void run() {
            for (int i = 1; i < 4; i++) {
                int S = 0;

                for (int j = 0; j < 4; j++) {
                    S = S + array[i - 1][j];
                }

                array[i][columnId] = array[i][columnId] + S;

                try {
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
