package com.devops.feedback;

import com.devops.feedback.model.Feedback;
import com.devops.feedback.util.CsvUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CSV parsing, serialization, and edge-case handling.
 */
public class CsvUtilTest {

    @Test
    @DisplayName("Should correctly parse standard comma-separated line")
    public void testParseLineStandard() {
        String line = "Asha,asha@example.com,Great mentoring support,5,2026-07-01";
        Feedback fb = CsvUtil.parseLine(line);

        assertNotNull(fb, "Parsed feedback must not be null");
        assertEquals("Asha", fb.getName());
        assertEquals("asha@example.com", fb.getEmail());
        assertEquals("Great mentoring support", fb.getFeedback());
        assertEquals(5, fb.getRating());
        assertEquals("2026-07-01", fb.getDate());
    }

    @Test
    @DisplayName("Should correctly parse CSV lines with quoted commas")
    public void testParseLineWithQuotedCommas() {
        String line = "Asha,asha@example.com,\"Great mentoring, hands-on labs, and fast support!\",5,2026-07-01";
        Feedback fb = CsvUtil.parseLine(line);

        assertNotNull(fb);
        assertEquals("Asha", fb.getName());
        assertEquals("Great mentoring, hands-on labs, and fast support!", fb.getFeedback());
        assertEquals(5, fb.getRating());
    }

    @Test
    @DisplayName("Should parse a full multi-line CSV string ignoring headers and empty lines")
    public void testParseFullCsv() throws IOException {
        String csvContent = "name,email,feedback,rating,date\n" +
                "Rahul Sharma,rahul@example.com,Awesome pipeline setup,5,2026-07-02\n" +
                "# this is a comment line\n" +
                "\n" +
                "Priya Patel,priya@example.com,Tomcat auto deploy works,4,2026-07-03\n";

        List<Feedback> list = CsvUtil.parseCsv(new StringReader(csvContent));
        assertEquals(2, list.size(), "Should parse 2 feedback records, skipping comments & headers");
        assertEquals("Rahul Sharma", list.get(0).getName());
        assertEquals("Priya Patel", list.get(1).getName());
    }

    @Test
    @DisplayName("Should serialize Feedback object to CSV format")
    public void testToCsvRow() {
        Feedback fb = new Feedback("Asha", "asha@example.com", "Great work", 5, "2026-07-01");
        String row = CsvUtil.toCsvRow(fb);

        assertTrue(row.startsWith("Asha,asha@example.com"));
        assertTrue(row.contains("\"Great work\""));
        assertTrue(row.endsWith(",5,2026-07-01"));
    }

    @Test
    @DisplayName("Should return null for malformed CSV input")
    public void testInvalidLines() {
        assertNull(CsvUtil.parseLine(""));
        assertNull(CsvUtil.parseLine("invalid,only_two_tokens"));
        assertNull(CsvUtil.parseLine("Name,email,message,not_a_number,2026-07-01"));
    }
}
