package com.server.HealthNet.Service;

import com.server.HealthNet.Model.Chat;
import com.server.HealthNet.Model.ApiQueryRequest;
import com.server.HealthNet.Model.ApiQueryResponse;
import com.server.HealthNet.Model.ModelType;
import com.server.HealthNet.Repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${healthnet.ai.enabled:false}")
    private boolean aiEnabled;

    @Value("${healthnet.ai.url:}")
    private String queryApiUrl;

    @Value("${healthnet.ai.timeout-ms:60000}")
    private int aiTimeoutMs;

    @Value("${healthnet.ai.unavailable-message:The AI assistant is currently unavailable.}")
    private String unavailableMessage;

    /** Shared RestTemplate configured with the AI service timeouts. */
    private RestTemplate buildRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        org.springframework.http.client.SimpleClientHttpRequestFactory factory =
                new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(aiTimeoutMs);
        factory.setReadTimeout(aiTimeoutMs);
        restTemplate.setRequestFactory(factory);
        return restTemplate;
    }

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
        if (!aiEnabled) {
            // Still record the attempt so the conversation history stays coherent.
            Chat chat = new Chat();
            chat.setPersonId(personId);
            chat.setRequest(request.getQuery());
            chat.setResponse(unavailableMessage);
            chat.setTimestamp(LocalDateTime.now());
            createChat(chat);
            return new ApiQueryResponse(unavailableMessage);
        }
        try {
            // Debug: Print the full request to the console
            System.out.println("==== ChatService: Processing API Query Request ====");
            System.out.println("Patient ID: " + request.getPatient_id());
            System.out.println("Doctor ID: " + request.getDoctor_id());
            System.out.println("Role: " + request.getRole());
            System.out.println("Query: " + request.getQuery());
            System.out.println("Model: " + request.getModel());
            System.out.println("================================================");

            RestTemplate restTemplate = buildRestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<ApiQueryRequest> entity = new HttpEntity<>(request, headers);

            ApiQueryResponse response = restTemplate.postForObject(queryApiUrl, entity, ApiQueryResponse.class);

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
        if (!aiEnabled) {
            return new ApiQueryResponse(unavailableMessage);
        }
        try {
            RestTemplate restTemplate = buildRestTemplate();

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

            ApiQueryResponse response = restTemplate.postForObject(queryApiUrl, entity, ApiQueryResponse.class);

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
        if (!aiEnabled) {
            return new ApiQueryResponse(unavailableMessage);
        }
        try {
            RestTemplate restTemplate = buildRestTemplate();

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

            ApiQueryResponse response = restTemplate.postForObject(queryApiUrl, entity, ApiQueryResponse.class);

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