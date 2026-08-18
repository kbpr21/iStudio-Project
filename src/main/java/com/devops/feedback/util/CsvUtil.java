package com.devops.feedback.util;

import com.devops.feedback.model.Feedback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility for parsing and serializing CSV data formatted for the Student Feedback application.
 * CSV Header Format: name,email,feedback,rating,date
 */
public class CsvUtil {

    private static final String CSV_HEADER = "name,email,feedback,rating,date";

    /**
     * Parses CSV content from an InputStream into a list of Feedback models.
     *
     * @param inputStream the input stream containing CSV content
     * @return list of parsed Feedback objects
     * @throws IOException on reading error
     */
    public static List<Feedback> parseCsv(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return new ArrayList<>();
        }
        return parseCsv(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }

    /**
     * Parses CSV content from a Reader into a list of Feedback models.
     *
     * @param reader the reader containing CSV content
     * @return list of parsed Feedback objects
     * @throws IOException on reading error
     */
    public static List<Feedback> parseCsv(Reader reader) throws IOException {
        List<Feedback> feedbackList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(reader)) {
            String line;
            boolean isHeader = true;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                if (isHeader) {
                    isHeader = false;
                    if (line.toLowerCase().startsWith("name,email")) {
                        continue;
                    }
                }
                Feedback feedback = parseLine(line);
                if (feedback != null) {
                    feedbackList.add(feedback);
                }
            }
        }
        return feedbackList;
    }

    /**
     * Parses a single CSV line into a Feedback object.
     * Handles quoted fields containing commas.
     */
    public static Feedback parseLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        List<String> tokens = parseCsvLineTokens(line);
        if (tokens.size() < 4) {
            return null;
        }

        try {
            String name = tokens.get(0).trim();
            String email = tokens.get(1).trim();
            String feedbackText = tokens.get(2).trim();
            int rating = Integer.parseInt(tokens.get(3).trim());
            String date = tokens.size() > 4 ? tokens.get(4).trim() : "";

            Feedback feedback = new Feedback(name, email, feedbackText, rating, date);
            return feedback;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Splits a CSV line by comma respecting double quotes.
     */
    public static List<String> parseCsvLineTokens(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    sb.append('"');
                    i++; // skip escaped quote
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString());
        return tokens;
    }

    /**
     * Formats a Feedback object as a single CSV row.
     */
    public static String toCsvRow(Feedback fb) {
        if (fb == null) return "";
        return String.format("%s,%s,\"%s\",%d,%s",
                escapeCsv(fb.getName()),
                escapeCsv(fb.getEmail()),
                fb.getFeedback().replace("\"", "\"\""),
                fb.getRating(),
                escapeCsv(fb.getDate()));
    }

    public static String getCsvHeader() {
        return CSV_HEADER;
    }

    private static String escapeCsv(String val) {
        if (val == null) return "";
        return val.contains(",") ? "\"" + val.replace("\"", "\"\"") + "\"" : val;
    }
}
