package com.server.HealthNet.Model;

public class LabResult {
    private Long resultId;
    private Long recordId;
    private String testName;
    private String testValue;
    private String testUnit;
    private String referenceRange;
    private Boolean isAbnormal;
    private String notes;
    
    // Constructors
    public LabResult() {
    }
    
    public LabResult(Long recordId, String testName, String testValue, String testUnit, 
                    String referenceRange, Boolean isAbnormal, String notes) {
        this.recordId = recordId;
        this.testName = testName;
        this.testValue = testValue;
        this.testUnit = testUnit;
        this.referenceRange = referenceRange;
        this.isAbnormal = isAbnormal;
        this.notes = notes;
    }
    
    // Getters and setters
    public Long getResultId() {
        return resultId;
    }

    public void setResultId(Long resultId) {
        this.resultId = resultId;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getTestValue() {
        return testValue;
    }

    public void setTestValue(String testValue) {
        this.testValue = testValue;
    }

    public String getTestUnit() {
        return testUnit;
    }

    public void setTestUnit(String testUnit) {
        this.testUnit = testUnit;
    }

    public String getReferenceRange() {
        return referenceRange;
    }

    public void setReferenceRange(String referenceRange) {
        this.referenceRange = referenceRange;
    }

    public Boolean getIsAbnormal() {
        return isAbnormal;
    }

    public void setIsAbnormal(Boolean isAbnormal) {
        this.isAbnormal = isAbnormal;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}