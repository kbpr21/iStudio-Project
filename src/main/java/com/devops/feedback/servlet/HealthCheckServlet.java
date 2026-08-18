package com.devops.feedback.servlet;

import com.devops.feedback.service.FeedbackService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Health Check Endpoint (/health)
 * Used by Jenkins CI/CD pipeline Stage 7 and uptime monitors to verify application health.
 */
@WebServlet(name = "HealthCheckServlet", urlPatterns = {"/health", "/api/health"})
public class HealthCheckServlet extends HttpServlet {

    private final FeedbackService feedbackService = FeedbackService.getInstance();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        resp.setHeader("Pragma", "no-cache");
        resp.setHeader("Expires", "0");

        boolean healthy = feedbackService.isHealthy();
        int statusCode = healthy ? HttpServletResponse.SC_OK : HttpServletResponse.SC_SERVICE_UNAVAILABLE;
        resp.setStatus(statusCode);

        Runtime runtime = Runtime.getRuntime();
        long freeMemoryMb = runtime.freeMemory() / (1024 * 1024);
        long totalMemoryMb = runtime.totalMemory() / (1024 * 1024);
        long maxMemoryMb = runtime.maxMemory() / (1024 * 1024);

        Map<String, Object> healthData = new LinkedHashMap<>();
        healthData.put("status", healthy ? "UP" : "DOWN");
        healthData.put("application", "Student Feedback Portal");
        healthData.put("version", "1.0.0");
        healthData.put("environment", "production");
        healthData.put("statusCode", statusCode);
        healthData.put("uptimeSeconds", feedbackService.getUptimeSeconds());
        healthData.put("totalFeedbackRecords", feedbackService.getTotalCount());
        healthData.put("averageRating", Math.round(feedbackService.getAverageRating() * 100.0) / 100.0);

        Map<String, Object> memory = new LinkedHashMap<>();
        memory.put("freeMemoryMb", freeMemoryMb);
        memory.put("totalMemoryMb", totalMemoryMb);
        memory.put("maxMemoryMb", maxMemoryMb);
        healthData.put("jvmMemory", memory);

        healthData.put("timestamp", Instant.now().toString());

        try (PrintWriter out = resp.getWriter()) {
            out.print(gson.toJson(healthData));
            out.flush();
        }
    }
}
