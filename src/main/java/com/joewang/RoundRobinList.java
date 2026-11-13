package com.joewang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RoundRobinList {
  private final int n;
  private final int numThreads;

  public RoundRobinList(int n, int numThreads) {
    if (n <= 0 || numThreads <= 0) {
      throw new IllegalArgumentException("Input should be greater than 0");
    }
    this.n = n;
    this.numThreads = numThreads;
  }

  public List<Integer> genList() throws InterruptedException {
    ExecutionContext context = new ExecutionContext(n, numThreads);
    return context.execute();
  }

  private static class ExecutionContext {
    private final int n;
    private final int numThreads;
    private final Object lock = new Object();
    private final List<Integer> res = new ArrayList<>();
    private int currentNumber = 1;
    private int currentTurn = 0;
    private volatile boolean aborted = false;

    ExecutionContext(int n, int numThreads) {
      this.n = n;
      this.numThreads = numThreads;
    }

    List<Integer> execute() throws InterruptedException {
      Thread[] threads = new Thread[numThreads];
      for (int i = 0; i < numThreads; i++) {
        final int threadId = i;
        threads[i] = new Thread(() -> worker(threadId), "worker-" + threadId);
        threads[i].start();
      }

      try {
        for (Thread thread : threads) {
          thread.join();
        }
      } catch (InterruptedException e) {
        aborted = true;
        for (Thread thread : threads) {
          thread.interrupt();
        }
        throw e;
      }

      if (aborted) {
        throw new InterruptedException("Execution was aborted");
      }

      return Collections.unmodifiableList(new ArrayList<>(res));
    }

    private void worker(int id) {
      while (true) {
        synchronized (lock) {
          while (!aborted && currentTurn != id && currentNumber <= n) {
            try {
              lock.wait();
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
              aborted = true;
              lock.notifyAll();
              return;
            }
          }

          if (aborted || currentNumber > n) {
            lock.notifyAll();
            return;
          }

          res.add(currentNumber);
          currentNumber++;
          currentTurn = (currentTurn + 1) % numThreads;
          lock.notifyAll();
        }
      }
    }
  }
}
