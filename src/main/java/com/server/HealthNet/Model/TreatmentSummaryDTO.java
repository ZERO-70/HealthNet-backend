package com.server.HealthNet.Model;

/**
 * DTO class for treatment summary information
 * Used for lightweight treatment listings in dropdowns and ID reference guides
 */
public class TreatmentSummaryDTO {
    private Long id;
    private String name;

    public TreatmentSummaryDTO() {
    }

    public TreatmentSummaryDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

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
}
