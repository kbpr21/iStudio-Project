package com.devops.feedback.servlet;

import com.devops.feedback.model.Feedback;
import com.devops.feedback.service.FeedbackService;
import com.devops.feedback.util.CsvUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Servlet for displaying and filtering feedback records, dataset view, and CSV export.
 */
@WebServlet(name = "DataViewerServlet", urlPatterns = {"/feedback-list", "/data", "/api/data"})
public class DataViewerServlet extends HttpServlet {

    private final FeedbackService feedbackService = FeedbackService.getInstance();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String searchQuery = req.getParameter("q");
        String ratingFilterStr = req.getParameter("minRating");
        String format = req.getParameter("format");

        List<Feedback> feedbackList;

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            feedbackList = feedbackService.searchFeedback(searchQuery.trim());
        } else if (ratingFilterStr != null && !ratingFilterStr.trim().isEmpty()) {
            try {
                int minRating = Integer.parseInt(ratingFilterStr.trim());
                feedbackList = feedbackService.filterByRating(minRating);
            } catch (NumberFormatException e) {
                feedbackList = feedbackService.getAllFeedback();
            }
        } else {
            feedbackList = feedbackService.getAllFeedback();
        }

        // CSV export
        if ("csv".equalsIgnoreCase(format)) {
            resp.setContentType("text/csv");
            resp.setHeader("Content-Disposition", "attachment; filename=\"feedback-export.csv\"");
            try (PrintWriter out = resp.getWriter()) {
                out.println(CsvUtil.getCsvHeader());
                for (Feedback fb : feedbackList) {
                    out.println(CsvUtil.toCsvRow(fb));
                }
            }
            return;
        }

        // JSON export
        if ("json".equalsIgnoreCase(format) || req.getServletPath().startsWith("/api/")) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            try (PrintWriter out = resp.getWriter()) {
                out.print(gson.toJson(feedbackList));
            }
            return;
        }

        // JSP view
        req.setAttribute("feedbackList", feedbackList);
        req.setAttribute("totalRecords", feedbackService.getTotalCount());
        req.setAttribute("averageRating", Math.round(feedbackService.getAverageRating() * 100.0) / 100.0);
        req.setAttribute("ratingDistribution", feedbackService.getRatingDistribution());
        req.setAttribute("searchQuery", searchQuery);
        req.setAttribute("selectedMinRating", ratingFilterStr);

        req.getRequestDispatcher("/feedback-list.jsp").forward(req, resp);
    }
}
