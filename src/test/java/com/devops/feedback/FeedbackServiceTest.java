package com.devops.feedback;

import com.devops.feedback.model.Feedback;
import com.devops.feedback.service.FeedbackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FeedbackService logic and data integrity.
 * Executed during the Maven Test stage of the Jenkins CI/CD pipeline.
 */
public class FeedbackServiceTest {

    private FeedbackService service;

    @BeforeEach
    public void setUp() {
        service = FeedbackService.getInstance();
        service.resetWithData(Arrays.asList(
                new Feedback("Asha", "asha@example.com", "Great mentoring support.", 5, "2026-07-01"),
                new Feedback("Rahul Sharma", "rahul@example.com", "Loved Jenkins pipeline sessions.", 4, "2026-07-02"),
                new Feedback("Priya Patel", "priya@example.com", "Tomcat deployment was easy.", 5, "2026-07-03")
        ));
    }

    @Test
    @DisplayName("Should successfully add a valid feedback record")
    public void testAddValidFeedback() {
        Feedback fb = new Feedback("Karan Verma", "karan@example.com", "Awesome CI/CD testing workflow.", 5, "2026-07-04");
        boolean added = service.addFeedback(fb);

        assertTrue(added, "Valid feedback should be accepted");
        assertEquals(4, service.getTotalCount(), "Total count should increment to 4");
    }

    @Test
    @DisplayName("Should reject invalid feedback (e.g. invalid email, short text)")
    public void testAddInvalidFeedback() {
        Feedback invalidEmail = new Feedback("Sneha", "invalid-email", "Great course", 5, "2026-07-01");
        Feedback shortText = new Feedback("Sneha", "sneha@example.com", "Bad", 5, "2026-07-01");
        Feedback invalidRating = new Feedback("Sneha", "sneha@example.com", "Valid message text here", 10, "2026-07-01");

        assertFalse(service.addFeedback(invalidEmail), "Invalid email must fail validation");
        assertFalse(service.addFeedback(shortText), "Short text must fail validation");
        assertFalse(service.addFeedback(invalidRating), "Rating > 5 must fail validation");
        assertFalse(service.addFeedback(null), "Null feedback must fail validation");
    }

    @Test
    @DisplayName("Should search records by student name or keywords")
    public void testSearchFeedback() {
        List<Feedback> searchAsha = service.searchFeedback("Asha");
        assertEquals(1, searchAsha.size(), "Should find 1 record for 'Asha'");
        assertEquals("Asha", searchAsha.get(0).getName());

        List<Feedback> searchJenkins = service.searchFeedback("Jenkins");
        assertEquals(1, searchJenkins.size(), "Should find 1 record mentioning 'Jenkins'");
    }

    @Test
    @DisplayName("Should filter records by minimum rating")
    public void testFilterByRating() {
        List<Feedback> fiveStarsOnly = service.filterByRating(5);
        assertEquals(2, fiveStarsOnly.size(), "Should have 2 records with 5-star rating");

        List<Feedback> fourStarsAndAbove = service.filterByRating(4);
        assertEquals(3, fourStarsAndAbove.size(), "Should have 3 records with >= 4 rating");
    }

    @Test
    @DisplayName("Should compute accurate average rating")
    public void testAverageRating() {
        // Ratings: 5, 4, 5 -> sum = 14, count = 3 -> avg = 4.666...
        double avg = service.getAverageRating();
        assertTrue(avg >= 4.66 && avg <= 4.67, "Average should be approximately 4.67");
    }

    @Test
    @DisplayName("Should calculate rating distribution properly")
    public void testRatingDistribution() {
        Map<Integer, Long> distribution = service.getRatingDistribution();
        assertEquals(2L, distribution.get(5), "Should have 2 ratings of 5");
        assertEquals(1L, distribution.get(4), "Should have 1 rating of 4");
        assertEquals(0L, distribution.get(3), "Should have 0 ratings of 3");
    }

    @Test
    @DisplayName("Should verify service health is active")
    public void testHealthStatus() {
        assertTrue(service.isHealthy(), "Service should report healthy when data exists");
    }
}
