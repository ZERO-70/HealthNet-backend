package com.server.HealthNet.Model;

import java.time.LocalDateTime;

public class Suggestion {
    private Long suggestionId;
    private Long personId;
    private String suggestionText;
    private LocalDateTime createdAt;

    public Suggestion() {}

    public Suggestion(Long personId, String suggestionText, LocalDateTime createdAt) {
        this.personId = personId;
        this.suggestionText = suggestionText;
        this.createdAt = createdAt;
    }

    // getters and setters
    public Long getSuggestionId() { return suggestionId; }
    public void setSuggestionId(Long suggestionId) { this.suggestionId = suggestionId; }
    public Long getPersonId() { return personId; }
    public void setPersonId(Long personId) { this.personId = personId; }
    public String getSuggestionText() { return suggestionText; }
    public void setSuggestionText(String suggestionText) { this.suggestionText = suggestionText; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Suggestion [suggestionId=" + suggestionId + ", personId=" + personId + 
               ", suggestionText=" + suggestionText + ", createdAt=" + createdAt + "]";
    }
}