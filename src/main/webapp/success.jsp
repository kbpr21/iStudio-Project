<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.devops.feedback.model.Feedback" %>
<%
    Feedback fb = (Feedback) session.getAttribute("submittedFeedback");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Feedback Submitted Successfully | DevOps CI/CD Project</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
</head>
<body>

    <!-- Header Navigation -->
    <header class="header-bar">
        <div class="header-container">
            <a href="${pageContext.request.contextPath}/" class="brand">
                <div class="brand-icon">CI</div>
                <span>Student Feedback Portal</span>
            </a>
            <ul class="nav-links">
                <li><a href="${pageContext.request.contextPath}/">Home</a></li>
                <li><a href="${pageContext.request.contextPath}/feedback-list">Feedback Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/health" target="_blank">Health API</a></li>
            </ul>
        </div>
    </header>

    <!-- Main Content Area -->
    <main class="main-content">
        <div class="card success-card">
            <div class="success-icon">✓</div>
            <h1 style="font-size: 1.8rem; font-weight: 800; letter-spacing: -0.02em; margin-bottom: 0.5rem;">
                Feedback Submitted Successfully!
            </h1>
            <p style="color: var(--color-text-muted); font-size: 0.95rem;">
                Your feedback has been validated and committed to the live application store.
            </p>

            <% if (fb != null) { %>
                <div class="success-details">
                    <div class="success-details-row">
                        <strong>Student Name:</strong>
                        <span><%= fb.getName() %></span>
                    </div>
                    <div class="success-details-row">
                        <strong>Email Address:</strong>
                        <span><code><%= fb.getEmail() %></code></span>
                    </div>
                    <div class="success-details-row">
                        <strong>Category:</strong>
                        <span><%= fb.getCategory() %></span>
                    </div>
                    <div class="success-details-row">
                        <strong>Rating:</strong>
                        <span class="stars"><% for(int i=0; i<fb.getRating(); i++){ %>★<% } %></span>
                    </div>
                    <div class="success-details-row">
                        <strong>Comments:</strong>
                        <span style="max-width: 60%; text-align: right;"><%= fb.getFeedback() %></span>
                    </div>
                    <div class="success-details-row">
                        <strong>Timestamp / Date:</strong>
                        <code><%= fb.getDate() %></code>
                    </div>
                </div>
            <% } %>

            <div style="display: flex; gap: 1rem; justify-content: center; margin-top: 1.5rem; flex-wrap: wrap;">
                <a href="${pageContext.request.contextPath}/" class="btn btn-primary">Submit Another Feedback</a>
                <a href="${pageContext.request.contextPath}/feedback-list" class="btn btn-secondary">View All Submissions</a>
            </div>
        </div>
    </main>

    <!-- Footer -->
    <footer class="app-footer">
        <div class="footer-container">
            <div>
                <strong>DevOps Internship Project</strong> &bull; Java Web App &bull; Maven &bull; Jenkins &bull; Apache Tomcat
            </div>
            <div>
                Verification API: <a href="${pageContext.request.contextPath}/health" target="_blank" style="color: var(--color-primary); font-weight: 600;">/health</a>
            </div>
        </div>
    </footer>

</body>
</html>
