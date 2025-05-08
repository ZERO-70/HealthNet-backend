package com.server.HealthNet.Model;

import java.time.LocalDateTime;

public class MedicalRecordAudit {
    private Long auditId;
    private Long recordId;
    private Long userId;
    private String actionType;
    private LocalDateTime actionTimestamp;
    private String actionDetails;

    // Constructors
    public MedicalRecordAudit() {
    }

    public MedicalRecordAudit(Long recordId, Long userId, String actionType, String actionDetails) {
        this.recordId = recordId;
        this.userId = userId;
        this.actionType = actionType;
        this.actionDetails = actionDetails;
    }

    // Getters and setters
    public Long getAuditId() {
        return auditId;
    }

    public void setAuditId(Long auditId) {
        this.auditId = auditId;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public LocalDateTime getActionTimestamp() {
        return actionTimestamp;
    }

    public void setActionTimestamp(LocalDateTime actionTimestamp) {
        this.actionTimestamp = actionTimestamp;
    }

    public String getActionDetails() {
        return actionDetails;
    }

    public void setActionDetails(String actionDetails) {
        this.actionDetails = actionDetails;
    }
}