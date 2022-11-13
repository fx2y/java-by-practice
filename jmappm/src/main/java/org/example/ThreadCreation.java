package org.example;

public class ThreadCreation {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("[1] Current thread: " + Thread.currentThread().getName());
        Thread thread = new Thread(() -> System.out.println("[2] Current thread: " + Thread.currentThread().getName()));
        thread.setName("MyThread");
        thread.start();
        thread.join();
        System.out.println("[3] Current thread: " + Thread.currentThread().getName());
    }
}