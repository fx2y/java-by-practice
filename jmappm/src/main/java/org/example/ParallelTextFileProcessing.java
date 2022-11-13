package org.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;

public class ParallelTextFileProcessing {
    public static void main(String[] args) {
        new Thread(new Watcher()).start();
    }

    static class ExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            System.out.println("Exception in thread: " + t.getName() + " exception: " + e.getMessage());
        }
    }

    static class Watcher implements Runnable {
        @Override
        public void run() {
            var inputDirectory = new File("./src/main/resources");

            while (true) {
                if (Objects.requireNonNull(inputDirectory.listFiles()).length != 0) {
                    Arrays.stream(Objects.requireNonNull(inputDirectory.listFiles())).forEach(file -> {
                        var t = new Thread(new FileProcessor(file));
                        t.setUncaughtExceptionHandler(new ExceptionHandler());
                        t.start();
                    });
                }

                sleep();
            }
        }

        private void sleep() {
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class FileProcessor implements Runnable {
        private final static String OUTPUT_PATH = "./src/main/output/";
        private final File file;

        public FileProcessor(File file) {
            this.file = file;
        }

        @Override
        public void run() {
            try {
                var writer = new BufferedWriter(new FileWriter(OUTPUT_PATH + file.getName()));

                Files.lines(Path.of(file.getCanonicalPath()))
                        .map(this::hash)
                        .map(line -> line + "\n")
                        .forEach(line -> {
                            try {
                                writer.write(line);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });

                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " finished processing file: " + file.getName());
        }

        private String hash(String input) {
            if (input.equals("")) {
                throw new RuntimeException("The line is empty, hashing cannot be done");
            }

            try {
                final MessageDigest digest = MessageDigest.getInstance("SHA3-256");
                final byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
                return bytesToHex(hashBytes);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return "";
            }
        }

        private String bytesToHex(byte[] hash) {
            var hexString = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        }
    }
}
