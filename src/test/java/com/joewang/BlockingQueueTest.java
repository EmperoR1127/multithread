package com.joewang;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.Test;

public class BlockingQueueTest {
  @Test
  public void testBlockingQueue() throws InterruptedException {
    int producerCount = 5;
    int consumerCount = 3;
    BlockingQueue<Integer> queue = new BlockingQueue<>(10);
    Random ran = new Random();
    CountDownLatch producerLatch = new CountDownLatch(producerCount);
    CountDownLatch consumerLatch = new CountDownLatch(consumerCount);
    Thread[] producers = new Thread[producerCount];
    for (int i = 0; i < producers.length; i++) {
      producers[i] =
          new Thread(
              () -> {
                try {
                  int item = ran.nextInt(100);
                  queue.put(item);
                  System.out.println("put item: " + item);
                } catch (InterruptedException e) {
                  Thread.currentThread().interrupt();
                } finally {
                  producerLatch.countDown();
                }
              },
              "producer-" + i);
      producers[i].start();
    }
    Thread[] consumers = new Thread[consumerCount];
    for (int i = 0; i < consumers.length; i++) {
      consumers[i] =
          new Thread(
              () -> {
                while (true) {
                  try {
                    if (queue.isShutDown()) {
                      return;
                    }
                    final Integer item = queue.take();
                    if (item == null) {
                      break;
                    }
                    System.out.println("take item: " + item);
                  } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                  } finally {
                    consumerLatch.countDown();
                  }
                }
              },
              "consumer-" + i);
      consumers[i].start();
    }
    producerLatch.await();
    System.out.println("=== Producers complete, closing queue ===");
    queue.shutDown();
    consumerLatch.await();
    System.out.println("=== Test complete ===");
  }
}
