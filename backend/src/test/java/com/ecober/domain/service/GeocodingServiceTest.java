package com.ecober.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

        ResponseEntity<String> response = new ResponseEntity<>(fakeJson, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

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

        ResponseEntity<String> response = new ResponseEntity<>(emptyJson, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

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

        ResponseEntity<String> response = new ResponseEntity<>(malformedJson, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

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

        ResponseEntity<String> response = new ResponseEntity<>(emptyJson, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        assertThrows(RuntimeException.class, () -> geocodingService.getLatAndLong("Nowhere"));
    }
}
