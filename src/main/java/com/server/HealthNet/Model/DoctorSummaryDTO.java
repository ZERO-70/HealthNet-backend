package com.server.HealthNet.Model;

/**
 * Data Transfer Object for doctor summary information
 * Contains only essential information (ID and name) for efficient data transfer
 */
public class DoctorSummaryDTO {
    private Long id;
    private String name;

    // Default constructor
    public DoctorSummaryDTO() {
    }

    // Constructor with parameters
    public DoctorSummaryDTO(Long id, String name) {
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
        return "DoctorSummaryDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
