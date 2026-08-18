package com.devops.feedback.servlet;

import com.devops.feedback.model.Feedback;
import com.devops.feedback.service.FeedbackService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet handling Student Feedback submissions and validations.
 */
@WebServlet(name = "FeedbackServlet", urlPatterns = {"/feedback", "/submit"})
public class FeedbackServlet extends HttpServlet {

    private final FeedbackService feedbackService = FeedbackService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String feedbackText = req.getParameter("feedback");
        String ratingStr = req.getParameter("rating");
        String category = req.getParameter("category");

        List<String> errors = new ArrayList<>();

        if (name == null || name.trim().length() < 2) {
            errors.add("Please enter a valid student name (at least 2 characters).");
        }

        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            errors.add("Please provide a valid email address.");
        }

        if (feedbackText == null || feedbackText.trim().length() < 5) {
            errors.add("Please provide meaningful feedback (at least 5 characters).");
        }

        int rating = 5;
        try {
            if (ratingStr != null) {
                rating = Integer.parseInt(ratingStr.trim());
                if (rating < 1 || rating > 5) {
                    errors.add("Rating must be an integer between 1 and 5.");
                }
            } else {
                errors.add("Please select a rating.");
            }
        } catch (NumberFormatException e) {
            errors.add("Invalid rating value.");
        }

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("prevName", name);
            req.setAttribute("prevEmail", email);
            req.setAttribute("prevFeedback", feedbackText);
            req.setAttribute("prevRating", ratingStr);
            req.setAttribute("prevCategory", category);
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
            return;
        }

        String currentDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        Feedback newFeedback = new Feedback(name, email, feedbackText, rating, currentDate, category);

        boolean added = feedbackService.addFeedback(newFeedback);
        if (added) {
            req.getSession().setAttribute("submittedFeedback", newFeedback);
            resp.sendRedirect(req.getContextPath() + "/success.jsp");
        } else {
            errors.add("Failed to save feedback record. Please verify your inputs.");
            req.setAttribute("errors", errors);
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
        }
    }
}
