package com.devops.lab;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppTest {

    @Test
    public void testAppGreeting() {
        App app = new App();
        assertEquals("Hello World!", app.getGreeting());
    }
}
