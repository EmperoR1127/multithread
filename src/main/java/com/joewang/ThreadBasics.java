package com.joewang;

public class ThreadBasics {
  public static void run() throws InterruptedException {
    Runnable task =
        () -> {
          String threadName = Thread.currentThread().getName();
          System.out.println("Running on: " + threadName);
        };

    Thread thread = new Thread(task, "worker-1");
    thread.start();
    thread.join();
    System.out.println("Worker threads have finished.");
  }
}
