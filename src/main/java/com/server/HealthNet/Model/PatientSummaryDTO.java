package com.server.HealthNet.Model;

/**
 * Data Transfer Object for patient summary information
 * Contains only essential information (ID and name) for efficient data transfer
 */
public class PatientSummaryDTO {
    private Long id;
    private String name;

    // Default constructor
    public PatientSummaryDTO() {
    }

    // Constructor with parameters
    public PatientSummaryDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "PatientSummaryDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
