package com.joewang;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RoundRobinListTest {
  @Test
  void testGenList() throws InterruptedException {
    int n = 107;
    List<Integer> expected = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      expected.add(i + 1);
    }
    RoundRobinList rb = new RoundRobinList(n, 4);
    List<Integer> res = rb.genList();
    Assertions.assertIterableEquals(expected, res);
  }
}
