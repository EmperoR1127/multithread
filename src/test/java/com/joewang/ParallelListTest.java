package com.joewang;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParallelListTest {
  @Test
  void testGenList() throws InterruptedException {
    int n = 107;
    List<Integer> expected = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      expected.add(i + 1);
    }
    List<Integer> res = ParallelList.genList(n, 4);
    Assertions.assertIterableEquals(expected, res);
  }
}
