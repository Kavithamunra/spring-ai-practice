package com.example.chatbot.tools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class InformationDeskTest {

    private InformationDesk informationDesk;

    @BeforeEach
    void setUp() {
        informationDesk = new InformationDesk();
    }

    @Test
    void testGetMyName() {
        // Given
        String expectedName = "I am Kavitha";

        // When
        String actualName = informationDesk.getMyName();

        // Then
        assertEquals(expectedName, actualName, "The name returned should match the expected value");
    }

    @Test
    void testGetMyAge() {
        // Given
        String expectedAge = "I am 30 years old ";

        // When
        String actualAge = informationDesk.getMyAge();

        // Then
        assertEquals(expectedAge, actualAge, "The age returned should match the expected value");
    }

    @Test
    void testGetMyHobbies() {
        // Given
        String expectedHobbies = "I like sketching and reading books. I enjoy playing with young kids and teaching them about the world.";

        // When
        String actualHobbies = informationDesk.getMyHobbies();

        // Then
        assertEquals(expectedHobbies, actualHobbies, "The hobbies returned should match the expected value");
    }

    @Test
    void testGetRailroadEmployeeCounts() {
        // Given
        Long expectedCount = 100L;
        String state = "California";
        String month = "January";
        String year = "2023";

        // When
        Long actualCount = informationDesk.getRailroadEmployeeCounts(expectedCount, state, month, year);

        // Then
        assertEquals(expectedCount, actualCount, "The employee count should be returned as provided");
    }

    @Test
    void testGetRailroadEmployeeCountsWithNullInput() {
        // Given
        Long expectedCount = null;
        String state = null;
        String month = null;
        String year = null;

        // When
        Long actualCount = informationDesk.getRailroadEmployeeCounts(expectedCount, state, month, year);

        // Then
        assertNull(actualCount, "Should return null when null count is provided");
    }
}
