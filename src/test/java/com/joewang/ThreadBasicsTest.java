package com.joewang;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ThreadBasicsTest {
    @Test
    void testRun() {
        Assertions.assertDoesNotThrow(ThreadBasics::run);
    }
}
