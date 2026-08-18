/**
 * Student Feedback Portal - Client-side Interaction & Health Monitor
 */

document.addEventListener('DOMContentLoaded', () => {
    initHealthCheck();
    initFormValidation();
    initTableSearch();
});

/**
 * Periodically checks the /health endpoint to show live pipeline/server status.
 */
function initHealthCheck() {
    const healthBadge = document.getElementById('live-health-badge');
    const healthText = document.getElementById('live-health-text');
    if (!healthBadge) return;

    const checkStatus = () => {
        const basePath = window.location.pathname.substring(0, window.location.pathname.indexOf('/', 1)) || '';
        const healthUrl = basePath + '/health';

        fetch(healthUrl)
            .then(res => {
                if (res.ok) return res.json();
                throw new Error('Health check returned status ' + res.status);
            })
            .then(data => {
                if (data.status === 'UP') {
                    healthBadge.style.background = '#defbe6';
                    healthBadge.style.color = '#198038';
                    if (healthText) {
                        healthText.textContent = `Pipeline Status: UP (v${data.version || '1.0.0'})`;
                    }
                } else {
                    healthBadge.style.background = '#fff1f1';
                    healthBadge.style.color = '#da1e28';
                    if (healthText) healthText.textContent = 'Pipeline Status: DOWN';
                }
            })
            .catch(() => {
                healthBadge.style.background = '#fcf4d6';
                healthBadge.style.color = '#b28600';
                if (healthText) healthText.textContent = 'Server Check: Standby';
            });
    };

    checkStatus();
    // Poll every 30 seconds
    setInterval(checkStatus, 30000);
}

/**
 * Client side form validation
 */
function initFormValidation() {
    const form = document.getElementById('feedbackForm');
    if (!form) return;

    form.addEventListener('submit', (e) => {
        const name = document.getElementById('name')?.value.trim();
        const email = document.getElementById('email')?.value.trim();
        const feedback = document.getElementById('feedback')?.value.trim();
        const rating = document.querySelector('input[name="rating"]:checked');

        let valid = true;
        let errorMessage = '';

        if (!name || name.length < 2) {
            valid = false;
            errorMessage = 'Please enter your full name (at least 2 characters).';
        } else if (!email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
            valid = false;
            errorMessage = 'Please enter a valid email address.';
        } else if (!rating) {
            valid = false;
            errorMessage = 'Please select a rating between 1 and 5 stars.';
        } else if (!feedback || feedback.length < 5) {
            valid = false;
            errorMessage = 'Please enter detailed feedback (at least 5 characters).';
        }

        if (!valid) {
            e.preventDefault();
            alert(errorMessage);
        }
    });
}

/**
 * Instant filter for feedback list table
 */
function initTableSearch() {
    const searchInput = document.getElementById('instantSearchInput');
    const table = document.getElementById('feedbackTable');
    if (!searchInput || !table) return;

    searchInput.addEventListener('input', (e) => {
        const query = e.target.value.toLowerCase().trim();
        const rows = table.querySelectorAll('tbody tr');

        rows.forEach(row => {
            const text = row.textContent.toLowerCase();
            if (text.includes(query)) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        });
    });
}
