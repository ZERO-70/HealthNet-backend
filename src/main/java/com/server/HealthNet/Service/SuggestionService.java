package com.server.HealthNet.Service;

import com.server.HealthNet.Model.Suggestion;
import com.server.HealthNet.Repository.SuggestionRepository;
import com.server.HealthNet.Repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SuggestionService {

    @Autowired
    private SuggestionRepository suggestionRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private RestTemplate restTemplate;

    // Global variable to track the date of last suggestion update
    private static LocalDate lastSuggestionUpdateDate = LocalDate.now();

    public List<Suggestion> getAllSuggestions() {
        return suggestionRepository.findAll();
    }

    public Suggestion getSuggestionById(Long id) {
        return suggestionRepository.findById(id);
    }

    public List<Suggestion> getSuggestionsByPersonId(Long personId) {
        return suggestionRepository.findByPersonId(personId);
    }

    public int createSuggestion(Suggestion suggestion) {
        return suggestionRepository.save(suggestion);
    }

    public int updateSuggestion(Suggestion suggestion) {
        return suggestionRepository.update(suggestion);
    }

    public int deleteSuggestion(Long id) {
        return suggestionRepository.deleteById(id);
    }

    /**
     * Returns the date when suggestions were last updated
     */
    public LocalDate getLastSuggestionUpdateDate() {
        return lastSuggestionUpdateDate;
    }

    /**
     * Fetches medical advice for a patient from external API and saves it as a
     * suggestion
     */
    public Suggestion fetchAndSaveMedicalAdvice(Long patientId) {
        String apiUrl = "http://159.89.49.64:7898/api/medical-advice";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("patient_id", patientId.toString());

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
        Suggestion suggestion = new Suggestion();
        try {
            Map<String, String> response = restTemplate.postForObject(apiUrl, request, Map.class);

            if (response != null && response.containsKey("advice")) {
                String adviceText = response.get("advice");

                suggestion.setPersonId(patientId);
                suggestion.setSuggestionText(adviceText);
                suggestion.setCreatedAt(LocalDateTime.now());

                createSuggestion(suggestion);
            }
        } catch (Exception e) {
            System.err.println("Error fetching medical advice for patient " + patientId + ": " + e.getMessage());
        }
        return suggestion;
    }

    /**
     * Scheduled task to run daily at midnight to fetch suggestions for all patients
     */
    @Scheduled(cron = "0 25 01 * * ?") // Run at midnight every day
    public void fetchDailySuggestions() {
        // First, delete all existing suggestions from the table
        int deletedCount = suggestionRepository.deleteAll();
        System.out.println("Deleted " + deletedCount + " existing suggestions");

        // Get all patient IDs from the database
        List<Long> patientIds = patientRepository.getAllPatientIds();

        // Process each patient ID
        for (Long patientId : patientIds) {

            Suggestion suggestion = fetchAndSaveMedicalAdvice(patientId);

            if (suggestion != null && patientId == 32) {
                try {
                    // Create a simple JSON representation for logging
                    String jsonLike = String.format(
                            "{\"personId\": %d, \"suggestionText\": \"%s\", \"createdAt\": \"%s\"}",
                            suggestion.getPersonId(),
                            suggestion.getSuggestionText().replace("\"", "\\\""),
                            suggestion.getCreatedAt());

                    // Print the JSON to console
                    System.out.println("Sending suggestion to MAI API: " + jsonLike);

                    // Create headers
                    HttpHeaders maiHeaders = new HttpHeaders();
                    maiHeaders.setContentType(MediaType.APPLICATION_JSON);

                    // Create request entity with suggestion object
                    HttpEntity<Suggestion> maiRequest = new HttpEntity<>(suggestion, maiHeaders);

                    // Send POST request to MAI endpoint
                    String response = restTemplate.postForObject("http://159.89.49.64:7898/api/email", maiRequest,
                            String.class);

                    System.out.println(
                            "Successfully sent suggestion for patient ID: " + patientId + ", Response: " + response);
                } catch (Exception e) {
                    System.err.println(
                            "Error sending suggestion to MAI API for patient " + patientId + ": " + e.getMessage());
                }
            }
        }

        // Update the last suggestion update date
        lastSuggestionUpdateDate = LocalDate.now();
    }
}