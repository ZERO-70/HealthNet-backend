package com.server.HealthNet.Model;

import java.time.LocalDateTime;

public class Chat {
    private Long messageId;
    private Long personId;
    private String request;
    private String response;
    private LocalDateTime timestamp;

    public Chat() {
    }

    public Chat(Long personId, String request, String response, LocalDateTime timestamp) {
        this.personId = personId;
        this.request = request;
        this.response = response;
        this.timestamp = timestamp;
    }

    // getters and setters
    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Chat [messageId=" + messageId + ", personId=" + personId +
                ", request=" + request + ", response=" + response + ", timestamp=" + timestamp + "]";
    }
}