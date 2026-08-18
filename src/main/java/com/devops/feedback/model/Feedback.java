package com.devops.feedback.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Model class representing a Student Feedback record.
 */
public class Feedback implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private String id;
    private String name;
    private String email;
    private String feedback;
    private int rating;
    private String date;
    private String category;

    public Feedback() {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        this.category = "General";
    }

    public Feedback(String name, String email, String feedback, int rating, String date) {
        this();
        this.name = name != null ? name.trim() : "";
        this.email = email != null ? email.trim() : "";
        this.feedback = feedback != null ? feedback.trim() : "";
        this.rating = rating;
        if (date != null && !date.trim().isEmpty()) {
            this.date = date.trim();
        }
    }

    public Feedback(String name, String email, String feedback, int rating, String date, String category) {
        this(name, email, feedback, rating, date);
        if (category != null && !category.trim().isEmpty()) {
            this.category = category.trim();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name != null ? name.trim() : "";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim() : "";
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback != null ? feedback.trim() : "";
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date != null ? date.trim() : "";
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category != null ? category.trim() : "General";
    }

    /**
     * Validates feedback data integrity according to business rules.
     * @return true if all required fields are valid, false otherwise.
     */
    public boolean isValid() {
        if (name == null || name.trim().length() < 2) {
            return false;
        }
        if (email == null || !EMAIL_PATTERN.matcher(email.trim()).matches()) {
            return false;
        }
        if (feedback == null || feedback.trim().length() < 5) {
            return false;
        }
        if (rating < 1 || rating > 5) {
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Feedback)) return false;
        Feedback feedback1 = (Feedback) o;
        return rating == feedback1.rating &&
                Objects.equals(name, feedback1.name) &&
                Objects.equals(email, feedback1.email) &&
                Objects.equals(feedback, feedback1.feedback) &&
                Objects.equals(date, feedback1.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, feedback, rating, date);
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", rating=" + rating +
                ", date='" + date + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
