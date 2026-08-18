<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.devops.feedback.service.FeedbackService" %>
<%@ page import="com.devops.feedback.model.Feedback" %>
<%@ page import="java.util.List" %>
<%
    FeedbackService service = FeedbackService.getInstance();
    int totalFeedbacks = service.getTotalCount();
    double avgRating = Math.round(service.getAverageRating() * 100.0) / 100.0;
    List<Feedback> recentList = service.getAllFeedback();
    if (recentList.size() > 4) {
        recentList = recentList.subList(0, 4);
    }
    List<String> errors = (List<String>) request.getAttribute("errors");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Feedback Portal | DevOps CI/CD Pipeline</title>
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
                <li><a href="${pageContext.request.contextPath}/" class="active">Home</a></li>
                <li><a href="${pageContext.request.contextPath}/feedback-list">Feedback Dashboard</a></li>
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
        
        <!-- Hero Section -->
        <section class="hero-banner">
            <h1>Automated CI/CD Delivery Pipeline</h1>
            <p>
                An end-to-end DevOps pipeline for a Java Web Application built with Maven, automated through Jenkins declarative stages, deployed onto Apache Tomcat, and verified with automated health checks.
            </p>
            <div class="pipeline-tags">
                <span class="pipeline-badge">Git Version Control</span>
                <span class="pipeline-badge">Maven Build & Test</span>
                <span class="pipeline-badge">Jenkins Declarative CI/CD</span>
                <span class="pipeline-badge">Apache Tomcat (Port 8081)</span>
                <span class="pipeline-badge">Automated Rollback</span>
            </div>
        </section>

        <!-- Stats Counter Section -->
        <section class="stats-grid">
            <div class="stat-box">
                <div class="stat-number"><%= totalFeedbacks %></div>
                <div class="stat-label">Total Feedback Records</div>
            </div>
            <div class="stat-box">
                <div class="stat-number"><%= avgRating %> / 5.0</div>
                <div class="stat-label">Average Satisfaction</div>
            </div>
            <div class="stat-box">
                <div class="stat-number" style="color: var(--color-success);">100%</div>
                <div class="stat-label">Automated Test Coverage</div>
            </div>
            <div class="stat-box">
                <div class="stat-number" style="color: #6366f1;">v1.0.0</div>
                <div class="stat-label">Active Release Build</div>
            </div>
        </section>

        <!-- Content Grid: Form + Recent Insights -->
        <div class="content-grid">
            
            <!-- Feedback Submission Form Card -->
            <div class="card">
                <h2 class="card-title">Submit Student Feedback</h2>
                <p class="card-subtitle">Share your feedback on mentorship, curriculum, or DevOps sessions.</p>

                <% if (errors != null && !errors.isEmpty()) { %>
                    <div class="alert alert-error">
                        <strong>Please resolve the following errors:</strong>
                        <ul style="margin-left: 1.25rem; margin-top: 0.5rem;">
                            <% for (String err : errors) { %>
                                <li><%= err %></li>
                            <% } %>
                        </ul>
                    </div>
                <% } %>

                <form id="feedbackForm" action="${pageContext.request.contextPath}/submit" method="POST">
                    <div class="form-group">
                        <label class="form-label" for="name">Student Name *</label>
                        <input type="text" id="name" name="name" class="form-control" placeholder="e.g. Asha Sharma" required 
                               value="<%= request.getAttribute("prevName") != null ? request.getAttribute("prevName") : "" %>">
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="email">Student Email *</label>
                        <input type="email" id="email" name="email" class="form-control" placeholder="e.g. asha@example.com" required
                               value="<%= request.getAttribute("prevEmail") != null ? request.getAttribute("prevEmail") : "" %>">
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="category">Category</label>
                        <select id="category" name="category" class="form-select">
                            <option value="Mentorship & Coaching">Mentorship & Coaching</option>
                            <option value="CI/CD & Jenkins Pipeline">CI/CD & Jenkins Pipeline</option>
                            <option value="Maven & Java Packaging">Maven & Java Packaging</option>
                            <option value="Tomcat Deployment & Linux">Tomcat Deployment & Linux</option>
                            <option value="General Feedback">General Feedback</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Rating *</label>
                        <div class="rating-group">
                            <div class="rating-option">
                                <input type="radio" id="star1" name="rating" value="1">
                                <label for="star1">1 ★</label>
                            </div>
                            <div class="rating-option">
                                <input type="radio" id="star2" name="rating" value="2">
                                <label for="star2">2 ★</label>
                            </div>
                            <div class="rating-option">
                                <input type="radio" id="star3" name="rating" value="3">
                                <label for="star3">3 ★</label>
                            </div>
                            <div class="rating-option">
                                <input type="radio" id="star4" name="rating" value="4">
                                <label for="star4">4 ★</label>
                            </div>
                            <div class="rating-option">
                                <input type="radio" id="star5" name="rating" value="5" checked>
                                <label for="star5">5 ★</label>
                            </div>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="feedback">Your Feedback / Review *</label>
                        <textarea id="feedback" name="feedback" rows="4" class="form-textarea" placeholder="Describe your experience with the learning modules and deployment pipelines..." required><%= request.getAttribute("prevFeedback") != null ? request.getAttribute("prevFeedback") : "" %></textarea>
                    </div>

                    <button type="submit" class="btn btn-primary btn-block">
                        Submit Feedback Record
                    </button>
                </form>
            </div>

            <!-- Recent Records & Architecture Card -->
            <div class="card">
                <h2 class="card-title">Recent Submissions</h2>
                <p class="card-subtitle">Showing latest records from in-memory store and CSV preload.</p>

                <div class="table-container" style="margin-bottom: 1.5rem;">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Student</th>
                                <th>Rating</th>
                                <th>Feedback</th>
                                <th>Date</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Feedback fb : recentList) { %>
                                <tr>
                                    <td><strong><%= fb.getName() %></strong></td>
                                    <td>
                                        <span class="stars">
                                            <% for(int i=0; i < fb.getRating(); i++) { %>★<% } %>
                                        </span>
                                    </td>
                                    <td><%= fb.getFeedback() %></td>
                                    <td><code><%= fb.getDate() %></code></td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>

                <div style="display: flex; gap: 0.75rem;">
                    <a href="${pageContext.request.contextPath}/feedback-list" class="btn btn-secondary btn-block">
                        View All Records (<%= totalFeedbacks %>)
                    </a>
                    <a href="${pageContext.request.contextPath}/feedback-list?format=csv" class="btn btn-secondary">
                        Download CSV
                    </a>
                </div>

                <div style="margin-top: 2rem;">
                    <h3 style="font-size: 1.05rem; font-weight: 700; margin-bottom: 0.5rem;">CI/CD Pipeline Stages</h3>
                    <div class="pipeline-flow">
                        <div class="pipeline-step">1. Checkout</div>
                        <span class="pipeline-arrow">➔</span>
                        <div class="pipeline-step">2. Compile</div>
                        <span class="pipeline-arrow">➔</span>
                        <div class="pipeline-step">3. Test</div>
                        <span class="pipeline-arrow">➔</span>
                        <div class="pipeline-step">4. Package</div>
                        <span class="pipeline-arrow">➔</span>
                        <div class="pipeline-step">5. Archive</div>
                        <span class="pipeline-arrow">➔</span>
                        <div class="pipeline-step">6. Deploy</div>
                        <span class="pipeline-arrow">➔</span>
                        <div class="pipeline-step">7. Verify</div>
                    </div>
                </div>
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
                Health Endpoint: <a href="${pageContext.request.contextPath}/health" target="_blank" style="color: var(--color-primary); font-weight: 600;">/health</a>
            </div>
        </div>
    </footer>

    <script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
