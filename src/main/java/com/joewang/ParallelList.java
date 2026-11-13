package com.joewang;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ParallelList {
  public static List<Integer> genList(int n, int threadSize) throws InterruptedException {
    if (threadSize <= 0 || n < threadSize) {
      throw new IllegalArgumentException("illegal params");
    }
    Integer[] res = new Integer[n];
    ExecutorService executor = Executors.newFixedThreadPool(threadSize);
    int chunk = (n + threadSize - 1) / threadSize;
    for (int i = 0; i < threadSize; i++) {
      int start = i * chunk;
      int end = Math.min(start + chunk, n);
      executor.submit(() -> {
        for (int j = start; j < end; j++) {
          res[j] = j + 1;
        }
      });
    }
    shutdown(executor);
    return Arrays.asList(res);
  }

  private static void shutdown(ExecutorService executor) {
    // 1. Disable new tasks from being submitted
    executor.shutdown();
    try {
      // 2. Wait a while for existing tasks to terminate
      if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
        // 3. Cancel currently executing tasks if timeout expires
        executor.shutdownNow();
        // 4. Wait a while for tasks to respond to being cancelled
        if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
          System.err.println("Pool did not terminate");
        }
      }
    } catch (InterruptedException ie) {
      // 5. (Re-)Cancel if current thread also interrupted
      executor.shutdownNow();
      // 6. Preserve interrupt status
      Thread.currentThread().interrupt();
    }
  }
}
