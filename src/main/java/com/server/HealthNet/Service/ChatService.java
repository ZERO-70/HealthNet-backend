package com.server.HealthNet.Service;

import com.server.HealthNet.Model.Chat;
import com.server.HealthNet.Model.ApiQueryRequest;
import com.server.HealthNet.Model.ApiQueryResponse;
import com.server.HealthNet.Model.ModelType;
import com.server.HealthNet.Repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@Service
public class ChatService {

    private static final Logger logger = Logger.getLogger(ChatService.class.getName());

    @Autowired
    private ChatRepository chatRepository;

    private final String QUERY_API_URL = "http://159.89.49.64:7898/api/query";

    public List<Chat> getAllChats() {
        return chatRepository.findAll();
    }

    public Chat getChatById(Long id) {
        return chatRepository.findById(id);
    }

    public List<Chat> getChatsByPersonId(Long personId) {
        return chatRepository.findByPersonId(personId);
    }

    public int createChat(Chat chat) {
        return chatRepository.save(chat);
    }

    public int updateChat(Chat chat) {
        return chatRepository.update(chat);
    }

    public int deleteChat(Long id) {
        return chatRepository.deleteById(id);
    }

    public ApiQueryResponse processQuery(ApiQueryRequest request, Long personId) {
        try {
            // Debug: Print the full request to the console
            System.out.println("==== ChatService: Processing API Query Request ====");
            System.out.println("Patient ID: " + request.getPatient_id());
            System.out.println("Doctor ID: " + request.getDoctor_id());
            System.out.println("Role: " + request.getRole());
            System.out.println("Query: " + request.getQuery());
            System.out.println("Model: " + request.getModel());
            System.out.println("================================================");

            // Create RestTemplate with 10 minute timeout
            RestTemplate restTemplate = new RestTemplate();

            // Configure request factory with 10 minute timeouts
            org.springframework.http.client.SimpleClientHttpRequestFactory factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(600000); // 10 minutes connection timeout
            factory.setReadTimeout(600000); // 10 minutes read timeout
            restTemplate.setRequestFactory(factory);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<ApiQueryRequest> entity = new HttpEntity<>(request, headers);

            ApiQueryResponse response = restTemplate.postForObject(QUERY_API_URL, entity, ApiQueryResponse.class);

            // Save the chat in the database
            Chat chat = new Chat();
            chat.setPersonId(personId);
            chat.setRequest(request.getQuery());
            chat.setResponse(response != null ? response.getResponse() : "Error: No response from server");
            chat.setTimestamp(LocalDateTime.now());

            createChat(chat);

            return response;
        } catch (Exception e) {
            // Handle any exceptions (network issues, API errors, etc.)
            ApiQueryResponse errorResponse = new ApiQueryResponse("Error processing query: " + e.getMessage());

            // Still save the failed attempt in the database
            Chat chat = new Chat();
            chat.setPersonId(personId);
            chat.setRequest(request.getQuery());
            chat.setResponse(errorResponse.getResponse());
            chat.setTimestamp(LocalDateTime.now());

            createChat(chat);

            return errorResponse;
        }
    }

    /**
     * Process a query without authentication - does not store in database
     * Sets role and patient_id to null
     * 
     * @param query The user query text
     * @return API response
     */
    public ApiQueryResponse processUnauthenticatedQuery(String query) {
        try {
            // Create RestTemplate with 10 minute timeout
            RestTemplate restTemplate = new RestTemplate();

            // Configure request factory with 10 minute timeouts
            org.springframework.http.client.SimpleClientHttpRequestFactory factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(600000); // 10 minutes connection timeout
            factory.setReadTimeout(600000); // 10 minutes read timeout
            restTemplate.setRequestFactory(factory);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create request with null patient_id and role
            ApiQueryRequest apiRequest = new ApiQueryRequest();
            apiRequest.setQuery(query);
            apiRequest.setRole(null);
            apiRequest.setPatient_id(null);
            apiRequest.setDoctor_id(null);

            // Debug: Print the unauthenticated request to console
            System.out.println("==== ChatService: Processing Unauthenticated Query ====");
            System.out.println("Patient ID: " + apiRequest.getPatient_id());
            System.out.println("Doctor ID: " + apiRequest.getDoctor_id());
            System.out.println("Role: " + apiRequest.getRole());
            System.out.println("Query: " + apiRequest.getQuery());
            System.out.println("Model: " + apiRequest.getModel());
            System.out.println("==================================================");

            HttpEntity<ApiQueryRequest> entity = new HttpEntity<>(apiRequest, headers);

            ApiQueryResponse response = restTemplate.postForObject(QUERY_API_URL, entity, ApiQueryResponse.class);

            // Print response to console
            logger.info("Unauthenticated query: " + query);
            logger.info("Response: " + (response != null ? response.getResponse() : "No response"));

            return response;
        } catch (Exception e) {
            // Handle any exceptions (network issues, API errors, etc.)
            ApiQueryResponse errorResponse = new ApiQueryResponse("Error processing query: " + e.getMessage());

            // Print error to console logger.severe("Error processing unauthenticated query:
            // " + e.getMessage());

            return errorResponse;
        }
    }

    // Add new method with ModelType parameter
    public ApiQueryResponse processUnauthenticatedQuery(String query, ModelType model) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // Configure request factory with 10 minute timeouts
            org.springframework.http.client.SimpleClientHttpRequestFactory factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(600000); // 10 minutes connection timeout
            factory.setReadTimeout(600000); // 10 minutes read timeout
            restTemplate.setRequestFactory(factory);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create request with null patient_id and role
            ApiQueryRequest apiRequest = new ApiQueryRequest();
            apiRequest.setQuery(query);
            apiRequest.setRole(null);
            apiRequest.setPatient_id(null);
            apiRequest.setDoctor_id(null);
            apiRequest.setModel(model); // Set the specified model

            // Debug: Print the unauthenticated request to console
            System.out.println("==== ChatService: Processing Unauthenticated Query ====");
            System.out.println("Patient ID: " + apiRequest.getPatient_id());
            System.out.println("Doctor ID: " + apiRequest.getDoctor_id());
            System.out.println("Role: " + apiRequest.getRole());
            System.out.println("Query: " + apiRequest.getQuery());
            System.out.println("Model: " + apiRequest.getModel());
            System.out.println("==================================================");

            HttpEntity<ApiQueryRequest> entity = new HttpEntity<>(apiRequest, headers);

            ApiQueryResponse response = restTemplate.postForObject(QUERY_API_URL, entity, ApiQueryResponse.class);

            // Print response to console
            logger.info("Unauthenticated query: " + query);
            logger.info("Response: " + (response != null ? response.getResponse() : "No response"));

            return response;
        } catch (Exception e) {
            // Handle any exceptions (network issues, API errors, etc.)
            ApiQueryResponse errorResponse = new ApiQueryResponse("Error processing query: " + e.getMessage());

            // Print error to console
            logger.severe("Error processing unauthenticated query: " + e.getMessage());

            return errorResponse;
        }
    }

    /**
     * Deletes chats older than the specified number of days
     * 
     * @param days The number of days threshold for deletion
     * @return Number of records deleted
     */
    public int deleteChatsOlderThanDays(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        logger.info("Deleting chats older than: " + cutoffDate);
        int deletedCount = chatRepository.deleteOlderThan(cutoffDate);
        logger.info("Deleted " + deletedCount + " old chat records");
        return deletedCount;
    }

    /**
     * Scheduled task that runs daily at 1:00 AM to delete chats older than 3 days
     */
    @Scheduled(cron = "0 0 1 * * ?") // Run at 1:00 AM daily
    public void scheduledChatCleanup() {
        logger.info("Running scheduled chat cleanup task");
        deleteChatsOlderThanDays(3);
    }
}