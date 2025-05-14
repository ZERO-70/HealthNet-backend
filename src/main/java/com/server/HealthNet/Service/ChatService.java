package com.server.HealthNet.Service;

import com.server.HealthNet.Model.Chat;
import com.server.HealthNet.Model.ApiQueryRequest;
import com.server.HealthNet.Model.ApiQueryResponse;
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
            RestTemplate restTemplate = new RestTemplate();
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