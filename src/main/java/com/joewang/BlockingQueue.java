package com.joewang;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingQueue<T> {
  private final Queue<T> queue;
  private final int maxSize;
  private final ReentrantLock lock = new ReentrantLock();
  private final Condition notFull = lock.newCondition();
  private final Condition notEmpty = lock.newCondition();
  private boolean isShutDown = false;

  public BlockingQueue(int size) {
    maxSize = size;
    queue = new ArrayDeque<>();
  }

  public void put(T item) throws InterruptedException {
    if (item == null) {
      throw new IllegalArgumentException("Item cannot be null");
    }
    lock.lock();
    try {
      while (queue.size() >= maxSize && !isShutDown) {
        notFull.await();
      }
      if (isShutDown) {
        throw new IllegalStateException("Queue has been shut down.");
      }
      queue.offer(item);
      notEmpty.signalAll();
    } finally {
      lock.unlock();
    }
  }

  public T take() throws InterruptedException {
    lock.lock();
    try {
      while (queue.isEmpty() && !isShutDown) {
        notEmpty.await();
      }
      if (queue.isEmpty() && isShutDown) {
        return null;
      }
      T item = queue.poll();
      notFull.signalAll();
      return item;
    } finally {
      lock.unlock();
    }
  }

  public void shutDown() {
    lock.lock();
    try {
      isShutDown = true;
      notFull.signalAll();
      notEmpty.signalAll();
    } finally {
      lock.unlock();
    }
  }

  public boolean isShutDown() {
    return isShutDown;
  }
}
