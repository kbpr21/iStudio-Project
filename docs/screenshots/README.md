# Screenshots & Evidence Catalogue

This directory contains visual evidence of successful builds, tests, deployment, and health verification required for project submission.

---

## Recommended Screenshots to Place Here:

1. **`01-git-commit-history.png`**
   - Output of `git log --oneline --graph` showing `main`, `dev`, and `feature/*` commits.
2. **`02-maven-test-success.png`**
   - Terminal showing `mvn clean test` passing 13/13 tests with `BUILD SUCCESS`.
3. **`03-jenkins-pipeline-success.png`**
   - Jenkins pipeline visualization showing all 7 stages green (Checkout -> Compile -> Test -> Package -> Archive -> Deploy -> Health Check).
4. **`04-jenkins-archived-artifacts.png`**
   - Jenkins build summary showing `student-feedback-portal.war` archived with fingerprint.
5. **`05-tomcat-status-and-port.png`**
   - Terminal showing `sudo systemctl status tomcat` or Tomcat manager running on port `8081`.
6. **`06-app-homepage.png`**
   - Browser showing `http://localhost:8081/student-feedback-portal/` with green health badge and stats.
7. **`07-app-feedback-dashboard.png`**
   - Browser showing `http://localhost:8081/student-feedback-portal/feedback-list` with 25+ loaded records.
8. **`08-app-health-json.png`**
   - Browser / cURL output showing JSON response from `http://localhost:8081/student-feedback-portal/health`.
9. **`09-quality-gate-test-failure.png`**
   - Jenkins build showing pipeline failure at Stage 3 when unit test fails.
10. **`10-rollback-restoration.png`**
    - Jenkins console log showing post-failure rollback restoration of `.previous` WAR.
