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
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Service
@EnableAsync
public class ChatService {

    private static final Logger logger = Logger.getLogger(ChatService.class.getName());

    // Store for tracking async operations
    private final ConcurrentHashMap<String, ApiQueryResponse> responseCache = new ConcurrentHashMap<>();

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

            // Create RestTemplate with shorter timeout for immediate response check
            RestTemplate restTemplate = new RestTemplate();

            // Configure request factory with shorter timeouts (15 seconds for quick
            // response)
            org.springframework.http.client.SimpleClientHttpRequestFactory factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(15000); // 15 seconds connection timeout
            factory.setReadTimeout(15000); // 15 seconds read timeout
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
            // If timeout occurs, start async processing and return pending response
            logger.info("Quick response failed, starting async processing: " + e.getMessage());

            // Start async processing
            processQueryAsync(request, personId);

            // Return immediate response indicating processing
            ApiQueryResponse pendingResponse = new ApiQueryResponse(
                    "Your query is being processed. This may take a few minutes due to the complexity of the request. "
                            +
                            "Please check back in a moment or refresh the chat to see the response.");

            // Save the pending response in the database
            Chat chat = new Chat();
            chat.setPersonId(personId);
            chat.setRequest(request.getQuery());
            chat.setResponse(pendingResponse.getResponse());
            chat.setTimestamp(LocalDateTime.now());

            createChat(chat);

            return pendingResponse;
        }
    }

    @Async("taskExecutor")
    public CompletableFuture<ApiQueryResponse> processQueryAsync(ApiQueryRequest request, Long personId) {
        try {
            logger.info("Starting async processing for query: " + request.getQuery());

            // Create RestTemplate with longer timeout for async processing
            RestTemplate restTemplate = new RestTemplate();

            // Configure request factory with longer timeouts (5 minutes)
            org.springframework.http.client.SimpleClientHttpRequestFactory factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(300000); // 5 minutes connection timeout
            factory.setReadTimeout(300000); // 5 minutes read timeout
            restTemplate.setRequestFactory(factory);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<ApiQueryRequest> entity = new HttpEntity<>(request, headers);

            ApiQueryResponse response = restTemplate.postForObject(QUERY_API_URL, entity, ApiQueryResponse.class);

            // Save the final response in the database
            Chat chat = new Chat();
            chat.setPersonId(personId);
            chat.setRequest(request.getQuery() + " [FINAL RESPONSE]");
            chat.setResponse(response != null ? response.getResponse() : "Error: No response from server");
            chat.setTimestamp(LocalDateTime.now());

            createChat(chat);

            logger.info("Async processing completed for query: " + request.getQuery());

            return CompletableFuture.completedFuture(response);
        } catch (Exception e) {
            logger.severe("Async processing failed: " + e.getMessage());

            // Save the error response
            ApiQueryResponse errorResponse = new ApiQueryResponse("Error processing query: " + e.getMessage());

            Chat chat = new Chat();
            chat.setPersonId(personId);
            chat.setRequest(request.getQuery() + " [ERROR]");
            chat.setResponse(errorResponse.getResponse());
            chat.setTimestamp(LocalDateTime.now());

            createChat(chat);

            return CompletableFuture.completedFuture(errorResponse);
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
            // Create RestTemplate with increased timeout
            RestTemplate restTemplate = new RestTemplate();

            // Configure request factory with longer timeouts (120 seconds)
            org.springframework.http.client.SimpleClientHttpRequestFactory factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(120000); // 120 seconds connection timeout
            factory.setReadTimeout(120000); // 120 seconds read timeout
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

            // Print error to console
            logger.severe("Error processing unauthenticated query: " + e.getMessage());

            return errorResponse;
        }
    } // Add new method with ModelType parameter

    public ApiQueryResponse processUnauthenticatedQuery(String query, ModelType model) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // Configure request factory with longer timeouts (120 seconds)
            org.springframework.http.client.SimpleClientHttpRequestFactory factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(120000); // 120 seconds connection timeout
            factory.setReadTimeout(120000); // 120 seconds read timeout
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