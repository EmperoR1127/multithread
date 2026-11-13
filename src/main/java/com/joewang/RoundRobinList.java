package com.joewang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

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
    private final List<Integer> res = new ArrayList<>();
    private int currentNumber = 1;
    private volatile boolean aborted = false;
    private final Semaphore[] semaphores;

    ExecutionContext(int n, int numThreads) {
      this.n = n;
      this.numThreads = numThreads;
      semaphores = new Semaphore[numThreads];
      for (int i = 0; i < numThreads; i++) {
        semaphores[i] = new Semaphore(i == 0 ? 1 : 0);
      }
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
      final int nextId = (id + 1) % numThreads;
      while (true) {
        try {
          semaphores[id].acquire();
          if (aborted || currentNumber > n) {
            semaphores[nextId].release();
            return;
          }
          res.add(currentNumber++);
          semaphores[nextId].release();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          aborted = true;
          semaphores[nextId].release();
          return;
        }
      }
    }
  }
}
