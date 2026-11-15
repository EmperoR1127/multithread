package com.joewang;

import java.util.concurrent.atomic.AtomicInteger;

public class Philosopher implements Runnable {

  private final int id;
  private final Fork left;
  private final Fork right;
  private final AtomicInteger meal;

  public Philosopher(int id, Fork left, Fork right, AtomicInteger meal) {
    this.id = id;
    this.left = left;
    this.right = right;
    this.meal = meal;
  }

  @Override
  public void run() {
    try {
      for (int i = 0; i < 3; i++) {
        think();
        eat();
        meal.incrementAndGet();
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private void think() throws InterruptedException {
    System.out.println("Philosopher " + id + " is thinking");
    Thread.sleep(50);
  }

  private void eat() throws InterruptedException {
    // Philosopher takes the lower fork first.
    // That breaks the circle that causes deadlock.
    Fork first = left.getId() < right.getId() ? left : right;
    Fork second = left.getId() < right.getId() ? right : left;
    synchronized (first) {
      System.out.println("Philosopher " + id + " picked up the first fork: " + first.getId());
      synchronized (second) {
        System.out.println("Philosopher " + id + " picked up the second fork: " + second.getId());
        System.out.println("Philosopher " + id + " is eating");
        Thread.sleep(50);
      }
    }
    System.out.println("Philosopher " + id + " finished eating");
    Thread.sleep(50);
  }
}

class Fork {
  private final int id;

  public Fork(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }
}
