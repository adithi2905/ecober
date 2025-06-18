package com.ecober.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GeocodingServiceTest {

    private RestTemplate restTemplate;
    private GeocodingService geocodingService;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        geocodingService = new GeocodingService(restTemplate);
    }

    @Test
    void testGetLatAndLong_success() {
        String fakeJson = """
            {
              "results": [
                {
                  "geometry": {
                    "location": {
                      "lat": 12.9716,
                      "lng": 77.5946
                    }
                  }
                }
              ]
            }
        """;

        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(fakeJson);

        double[] result = geocodingService.getLatAndLong("Bangalore");

        assertEquals(12.9716, result[0], 0.0001);
        assertEquals(77.5946, result[1], 0.0001);
    }

    @Test
    void testGetLatAndLong_emptyResults_throwsException() {
        String emptyJson = """
            {
              "results": []
            }
        """;

        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(emptyJson);

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> geocodingService.getLatAndLong("Nowhere"));

        String message = exception.getMessage();
        boolean containsExpectedContent = message != null &&
            (message.contains("geocoding") ||
             message.contains("result") ||
             message.contains("Nowhere") ||
             message.contains("No") ||
             message.contains("empty"));

        assertTrue(containsExpectedContent,
            "Expected exception message to contain geocoding-related keywords, but got: '" + message + "'");
    }

    @Test
    void testGetLatAndLong_malformedJson_throwsException() {
        String malformedJson = "invalid json";

        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(malformedJson);

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> geocodingService.getLatAndLong("Broken"));

        String message = exception.getMessage();
        boolean containsExpectedContent = message != null &&
            (message.contains("Error parsing") ||
             message.contains("parsing") ||
             message.contains("geocoding") ||
             message.contains("JSON") ||
             message.contains("json"));

        assertTrue(containsExpectedContent,
            "Expected exception message to contain parsing-related keywords, but got: '" + message + "'");
    }

    @Test
    void testGetLatAndLong_emptyResults_throwsException_simple() {
        String emptyJson = """
            {
              "results": []
            }
        """;

        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(emptyJson);

        assertThrows(RuntimeException.class, () -> geocodingService.getLatAndLong("Nowhere"));
    }
}
