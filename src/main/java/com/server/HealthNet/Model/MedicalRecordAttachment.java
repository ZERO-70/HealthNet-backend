package com.server.HealthNet.Model;

import java.time.LocalDateTime;

public class MedicalRecordAttachment {
    private Long attachmentId;
    private Long recordId;
    private String fileName;
    private String fileType;
    private String contentType;
    private Long fileSize;
    private byte[] fileData;
    private String filePath;
    private LocalDateTime uploadedAt;
    private String description;

    // Constructors
    public MedicalRecordAttachment() {
    }

    public MedicalRecordAttachment(Long recordId, String fileName, String fileType, String contentType,
            Long fileSize, byte[] fileData, String description) {
        this.recordId = recordId;
        this.fileName = fileName;
        this.fileType = fileType;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.fileData = fileData;
        this.description = description;
    }

    // Alternative constructor for file path-based storage
    public MedicalRecordAttachment(Long recordId, String fileName, String fileType, String contentType,
            Long fileSize, String filePath, String description) {
        this.recordId = recordId;
        this.fileName = fileName;
        this.fileType = fileType;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.filePath = filePath;
        this.description = description;
    }

    // Getters and setters
    public Long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}