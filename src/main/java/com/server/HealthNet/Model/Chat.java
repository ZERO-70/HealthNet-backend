package com.server.HealthNet.Model;

import java.time.LocalDateTime;

public class Chat {
    private Long messageId;
    private Long personId;
    private String messageText;
    private LocalDateTime timestamp;

    public Chat() {}

    public Chat(Long personId, String messageText, LocalDateTime timestamp) {
        this.personId = personId;
        this.messageText = messageText;
        this.timestamp = timestamp;
    }

    // getters and setters
    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }
    public Long getPersonId() { return personId; }
    public void setPersonId(Long personId) { this.personId = personId; }
    public String getMessageText() { return messageText; }
    public void setMessageText(String messageText) { this.messageText = messageText; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "Chat [messageId=" + messageId + ", personId=" + personId + 
               ", messageText=" + messageText + ", timestamp=" + timestamp + "]";
    }
}