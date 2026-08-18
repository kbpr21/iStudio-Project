package com.devops.feedback;

import com.devops.feedback.servlet.HealthCheckServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Unit test for HealthCheckServlet to verify that /health responds with HTTP 200 and JSON status UP.
 */
public class HealthCheckServletTest {

    @Test
    @DisplayName("Should return HTTP 200 and status UP with application details")
    public void testHealthCheckServletReturns200AndJson() throws IOException {
        HealthCheckServlet servlet = new HealthCheckServlet();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        // Invoke doGet via reflection or package-private helper if needed, or by testing service and servlet directly
        // HealthCheckServlet doGet is protected, we can subclass or test direct invocation
        TestableHealthCheckServlet testableServlet = new TestableHealthCheckServlet();
        testableServlet.executeDoGet(request, response);

        verify(response).setContentType("application/json");
        verify(response).setStatus(HttpServletResponse.SC_OK);

        printWriter.flush();
        String jsonOutput = stringWriter.toString();

        assertTrue(jsonOutput.contains("\"status\": \"UP\""), "Response should contain status UP");
        assertTrue(jsonOutput.contains("Student Feedback Portal"), "Response should mention application name");
        assertTrue(jsonOutput.contains("\"version\": \"1.0.0\""), "Response should contain version 1.0.0");
    }

    private static class TestableHealthCheckServlet extends HealthCheckServlet {
        public void executeDoGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            super.doGet(req, resp);
        }
    }
}
