package com.joewang;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PhilosopherTest {
  @Test
  public void testPhilosopher() throws InterruptedException {
    int n = 5;
    Fork[] forks = new Fork[n];
    AtomicInteger[] meals = new AtomicInteger[n];
    for (int i = 0; i < n; i++) {
      forks[i] = new Fork(i);
      meals[i] = new AtomicInteger(0);
    }
    Thread[] threads = new Thread[n];
    for (int i = 0; i < n; i++) {
      Runnable philosopher = new Philosopher(i, forks[i], forks[(i + 1) % n], meals[i]);
      threads[i] = new Thread(philosopher);
      threads[i].start();
    }
    for (Thread thread : threads) {
      thread.join(5_000);
      Assertions.assertFalse(thread.isAlive());
    }
    for (int i = 0; i < n; i++) {
      Assertions.assertEquals(3, meals[i].get());
    }
  }
}
