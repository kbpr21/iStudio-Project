package com.devops.feedback;

import com.devops.feedback.model.Feedback;
import com.devops.feedback.service.FeedbackService;
import com.devops.feedback.servlet.DataViewerServlet;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DataViewerServlet handling HTML views, search filters, and JSON/CSV data export.
 */
public class DataViewerServletTest {

    private TestableDataViewerServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private RequestDispatcher dispatcher;

    @BeforeEach
    public void setUp() {
        servlet = new TestableDataViewerServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        dispatcher = mock(RequestDispatcher.class);

        when(request.getRequestDispatcher(any())).thenReturn(dispatcher);
        when(request.getServletPath()).thenReturn("/feedback-list");

        FeedbackService.getInstance().resetWithData(Arrays.asList(
                new Feedback("Asha", "asha@example.com", "Great mentoring support.", 5, "2026-07-01"),
                new Feedback("Rahul Sharma", "rahul@example.com", "Jenkins pipeline sessions were great.", 4, "2026-07-02")
        ));
    }

    @Test
    @DisplayName("Should forward to feedback-list.jsp with feedback attributes")
    public void testDoGetJspView() throws ServletException, IOException {
        servlet.executeDoGet(request, response);

        verify(request).setAttribute(eq("feedbackList"), any(List.class));
        verify(request).setAttribute(eq("totalRecords"), any());
        verify(request).getRequestDispatcher("/feedback-list.jsp");
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("Should export data as CSV when format=csv is requested")
    public void testDoGetCsvExport() throws ServletException, IOException {
        when(request.getParameter("format")).thenReturn("csv");
        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        servlet.executeDoGet(request, response);

        verify(response).setContentType("text/csv");
        String output = sw.toString();
        assertTrue(output.contains("name,email,feedback,rating,date"), "CSV should contain header");
        assertTrue(output.contains("Asha"), "CSV should contain Asha record");
    }

    @Test
    @DisplayName("Should export data as JSON when format=json is requested")
    public void testDoGetJsonExport() throws ServletException, IOException {
        when(request.getParameter("format")).thenReturn("json");
        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        servlet.executeDoGet(request, response);

        verify(response).setContentType("application/json");
        String output = sw.toString();
        assertTrue(output.contains("Rahul Sharma"), "JSON should contain Rahul record");
    }

    private static class TestableDataViewerServlet extends DataViewerServlet {
        public void executeDoGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            super.doGet(req, resp);
        }
    }
}
