<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.devops.feedback.model.Feedback" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%
    List<Feedback> feedbackList = (List<Feedback>) request.getAttribute("feedbackList");
    Integer totalRecords = (Integer) request.getAttribute("totalRecords");
    Double averageRating = (Double) request.getAttribute("averageRating");
    Map<Integer, Long> ratingDistribution = (Map<Integer, Long>) request.getAttribute("ratingDistribution");
    String searchQuery = (String) request.getAttribute("searchQuery");
    String selectedMinRating = (String) request.getAttribute("selectedMinRating");

    if (totalRecords == null) totalRecords = 0;
    if (averageRating == null) averageRating = 0.0;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Feedback Dataset & Insights | Student Feedback Portal</title>
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
                <li><a href="${pageContext.request.contextPath}/feedback-list" class="active">Feedback Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/health" target="_blank">Health API</a></li>
                <li>
                    <div id="live-health-badge" class="status-pill">
                        <span class="status-dot"></span>
                        <span id="live-health-text">Status: Checking...</span>
                    </div>
                </li>
            </ul>
        </div>
    </header>

    <!-- Main Content Area -->
    <main class="main-content">

        <!-- Top Header & Action Controls -->
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem; flex-wrap: wrap; gap: 1rem;">
            <div>
                <h1 style="font-size: 1.8rem; font-weight: 800; letter-spacing: -0.02em;">Feedback Dataset & Insights</h1>
                <p style="color: var(--color-text-muted); font-size: 0.95rem;">
                    Browsing verified student feedback loaded from <code>sample-feedback.csv</code> and runtime submissions.
                </p>
            </div>
            <div style="display: flex; gap: 0.75rem; flex-wrap: wrap;">
                <a href="${pageContext.request.contextPath}/" class="btn btn-primary">+ Submit Feedback</a>
                <a href="${pageContext.request.contextPath}/data?format=csv" class="btn btn-secondary">Export CSV</a>
                <a href="${pageContext.request.contextPath}/data?format=json" target="_blank" class="btn btn-secondary">JSON API</a>
            </div>
        </div>

        <!-- Metrics Strip -->
        <section class="stats-grid">
            <div class="stat-box">
                <div class="stat-number"><%= totalRecords %></div>
                <div class="stat-label">Total Submissions</div>
            </div>
            <div class="stat-box">
                <div class="stat-number"><%= averageRating %> ★</div>
                <div class="stat-label">Overall Average Rating</div>
            </div>
            <div class="stat-box">
                <div class="stat-number" style="color: var(--color-success);">
                    <%= (ratingDistribution != null && ratingDistribution.get(5) != null) ? ratingDistribution.get(5) : 0 %>
                </div>
                <div class="stat-label">5-Star Reviews</div>
            </div>
            <div class="stat-box">
                <div class="stat-number" style="color: #6366f1;">
                    <%= (ratingDistribution != null && ratingDistribution.get(4) != null) ? ratingDistribution.get(4) : 0 %>
                </div>
                <div class="stat-label">4-Star Reviews</div>
            </div>
        </section>

        <!-- Search & Filter Controls -->
        <div class="card" style="margin-bottom: 1.5rem; padding: 1.25rem;">
            <form action="${pageContext.request.contextPath}/feedback-list" method="GET" style="display: flex; gap: 1rem; flex-wrap: wrap; align-items: center;">
                <div style="flex: 1; min-width: 250px;">
                    <input type="text" name="q" id="instantSearchInput" class="form-control" 
                           placeholder="Type to filter instantly by student name, email, or keywords..." 
                           value="<%= searchQuery != null ? searchQuery : "" %>">
                </div>
                <div style="width: 200px;">
                    <select name="minRating" class="form-select" onchange="this.form.submit()">
                        <option value="">All Ratings</option>
                        <option value="5" <%= "5".equals(selectedMinRating) ? "selected" : "" %>>5 Stars Only</option>
                        <option value="4" <%= "4".equals(selectedMinRating) ? "selected" : "" %>>4 Stars & Above</option>
                        <option value="3" <%= "3".equals(selectedMinRating) ? "selected" : "" %>>3 Stars & Above</option>
                    </select>
                </div>
                <button type="submit" class="btn btn-secondary">Filter</button>
                <% if (searchQuery != null || selectedMinRating != null) { %>
                    <a href="${pageContext.request.contextPath}/feedback-list" class="btn btn-secondary">Reset</a>
                <% } %>
            </form>
        </div>

        <!-- Table Card -->
        <div class="card">
            <div class="table-container">
                <table class="data-table" id="feedbackTable">
                    <thead>
                        <tr>
                            <th style="width: 60px;">#</th>
                            <th>Student Name</th>
                            <th>Email Address</th>
                            <th>Category</th>
                            <th>Rating</th>
                            <th>Feedback Remarks</th>
                            <th>Date</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (feedbackList != null && !feedbackList.isEmpty()) { 
                               int index = 1;
                               for (Feedback fb : feedbackList) { %>
                            <tr>
                                <td><span style="color: var(--color-text-subtle); font-size: 0.8rem;"><%= index++ %></span></td>
                                <td><strong><%= fb.getName() %></strong></td>
                                <td><code><%= fb.getEmail() %></code></td>
                                <td>
                                    <span style="background: var(--color-surface-subtle); border: 1px solid var(--color-border); padding: 0.2rem 0.5rem; border-radius: 4px; font-size: 0.8rem;">
                                        <%= fb.getCategory() != null ? fb.getCategory() : "General" %>
                                    </span>
                                </td>
                                <td>
                                    <span class="stars">
                                        <% for(int i=0; i < fb.getRating(); i++) { %>★<% } %>
                                    </span>
                                    <span style="font-size: 0.8rem; color: var(--color-text-muted);">(<%= fb.getRating() %>/5)</span>
                                </td>
                                <td><%= fb.getFeedback() %></td>
                                <td><code style="font-size: 0.85rem;"><%= fb.getDate() %></code></td>
                            </tr>
                        <%   } 
                           } else { %>
                            <tr>
                                <td colspan="7" style="text-align: center; padding: 2rem; color: var(--color-text-muted);">
                                    No feedback records found matching your filter criteria.
                                </td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
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

    <script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
