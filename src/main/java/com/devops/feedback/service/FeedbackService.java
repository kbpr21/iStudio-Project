package com.devops.feedback.service;

import com.devops.feedback.model.Feedback;
import com.devops.feedback.util.CsvUtil;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Thread-safe singleton service managing the feedback repository and analytical queries.
 */
public class FeedbackService {

    private static final FeedbackService INSTANCE = new FeedbackService();
    private final List<Feedback> repository = new CopyOnWriteArrayList<>();
    private final long startTimeMillis = System.currentTimeMillis();

    private FeedbackService() {
        loadInitialData();
    }

    public static FeedbackService getInstance() {
        return INSTANCE;
    }

    /**
     * Loads the initial sample feedback dataset from the classpath or filesystem fallback.
     */
    public synchronized void loadInitialData() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("sample-feedback.csv");
        if (is == null) {
            is = getClass().getResourceAsStream("/sample-feedback.csv");
        }
        if (is == null) {
            java.io.File localFile = new java.io.File("data/sample-feedback.csv");
            if (localFile.exists()) {
                try {
                    is = new java.io.FileInputStream(localFile);
                } catch (Exception ignored) {}
            }
        }

        if (is != null) {
            try (InputStream input = is) {
                List<Feedback> initialList = CsvUtil.parseCsv(input);
                if (!initialList.isEmpty()) {
                    repository.clear();
                    repository.addAll(initialList);
                }
            } catch (Exception e) {
                // Ignore and use fallback if empty
            }
        }

        if (repository.isEmpty()) {
            repository.add(new Feedback("Asha", "asha@example.com", "Great mentoring support and clear CI/CD concepts explained.", 5, "2026-07-01"));
        }
    }

    /**
     * Adds a new feedback record after validating.
     * @param feedback the feedback to add
     * @return true if valid and added, false otherwise
     */
    public boolean addFeedback(Feedback feedback) {
        if (feedback == null || !feedback.isValid()) {
            return false;
        }
        // Insert at beginning for newest-first display
        repository.add(0, feedback);
        return true;
    }

    public List<Feedback> getAllFeedback() {
        return Collections.unmodifiableList(new ArrayList<>(repository));
    }

    public Optional<Feedback> getFeedbackById(String id) {
        if (id == null) return Optional.empty();
        return repository.stream()
                .filter(fb -> id.equalsIgnoreCase(fb.getId()))
                .findFirst();
    }

    public List<Feedback> searchFeedback(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllFeedback();
        }
        String q = query.toLowerCase().trim();
        return repository.stream()
                .filter(fb -> (fb.getName() != null && fb.getName().toLowerCase().contains(q)) ||
                              (fb.getEmail() != null && fb.getEmail().toLowerCase().contains(q)) ||
                              (fb.getFeedback() != null && fb.getFeedback().toLowerCase().contains(q)) ||
                              (fb.getCategory() != null && fb.getCategory().toLowerCase().contains(q)))
                .collect(Collectors.toList());
    }

    public List<Feedback> filterByRating(int minRating) {
        return repository.stream()
                .filter(fb -> fb.getRating() >= minRating)
                .collect(Collectors.toList());
    }

    public int getTotalCount() {
        return repository.size();
    }

    public double getAverageRating() {
        if (repository.isEmpty()) return 0.0;
        return repository.stream()
                .mapToInt(Feedback::getRating)
                .average()
                .orElse(0.0);
    }

    public Map<Integer, Long> getRatingDistribution() {
        Map<Integer, Long> distribution = new LinkedHashMap<>();
        for (int i = 5; i >= 1; i--) {
            int rating = i;
            long count = repository.stream().filter(fb -> fb.getRating() == rating).count();
            distribution.put(rating, count);
        }
        return distribution;
    }

    public long getUptimeSeconds() {
        return (System.currentTimeMillis() - startTimeMillis) / 1000;
    }

    public boolean isHealthy() {
        return repository != null && !repository.isEmpty();
    }

    public synchronized void resetWithData(List<Feedback> feedbackList) {
        repository.clear();
        if (feedbackList != null) {
            repository.addAll(feedbackList);
        }
    }
}
