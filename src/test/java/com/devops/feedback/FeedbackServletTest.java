package com.devops.feedback;

import com.devops.feedback.model.Feedback;
import com.devops.feedback.service.FeedbackService;
import com.devops.feedback.servlet.FeedbackServlet;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for FeedbackServlet request handling, parameter validation, and redirection.
 */
public class FeedbackServletTest {

    private TestableFeedbackServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private RequestDispatcher dispatcher;

    @BeforeEach
    public void setUp() {
        servlet = new TestableFeedbackServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        dispatcher = mock(RequestDispatcher.class);

        when(request.getSession()).thenReturn(session);
        when(request.getContextPath()).thenReturn("/student-feedback-portal");
        when(request.getRequestDispatcher(any())).thenReturn(dispatcher);
    }

    @Test
    @DisplayName("Should successfully process valid feedback and redirect to success.jsp")
    public void testDoPostSuccess() throws ServletException, IOException {
        when(request.getParameter("name")).thenReturn("Ananya Desai");
        when(request.getParameter("email")).thenReturn("ananya.d@example.com");
        when(request.getParameter("feedback")).thenReturn("Excellent CI/CD pipeline automation walkthrough.");
        when(request.getParameter("rating")).thenReturn("5");
        when(request.getParameter("category")).thenReturn("CI/CD & Jenkins Pipeline");

        servlet.executeDoPost(request, response);

        verify(session).setAttribute(eq("submittedFeedback"), any(Feedback.class));
        verify(response).sendRedirect("/student-feedback-portal/success.jsp");
    }

    @Test
    @DisplayName("Should detect invalid form input and forward back to index.jsp with errors")
    public void testDoPostValidationErrors() throws ServletException, IOException {
        when(request.getParameter("name")).thenReturn("");
        when(request.getParameter("email")).thenReturn("invalid-email");
        when(request.getParameter("feedback")).thenReturn("Bad");
        when(request.getParameter("rating")).thenReturn("10");

        servlet.executeDoPost(request, response);

        verify(request).setAttribute(eq("errors"), any());
        verify(request).getRequestDispatcher("/index.jsp");
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("Should forward doGet to index.jsp")
    public void testDoGet() throws ServletException, IOException {
        servlet.executeDoGet(request, response);

        verify(request).getRequestDispatcher("/index.jsp");
        verify(dispatcher).forward(request, response);
    }

    private static class TestableFeedbackServlet extends FeedbackServlet {
        public void executeDoPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            super.doPost(req, resp);
        }
        public void executeDoGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            super.doGet(req, resp);
        }
    }
}
