package com.server.HealthNet.Model;

public class Treatement {
    private Long treatement_id;
    private String name;
    private Long doctor_id;
    private Long department_id;
    public Treatement() {
    }
    public Treatement(String name, Long doctor_id, Long department_id) {
        this.name = name;
        this.doctor_id = doctor_id;
        this.department_id = department_id;
    }
    public Long getTreatement_id() {
        return treatement_id;
    }
    public void setTreatement_id(Long treatement_id) {
        this.treatement_id = treatement_id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Long getDoctor_id() {
        return doctor_id;
    }
    public void setDoctor_id(Long doctor_id) {
        this.doctor_id = doctor_id;
    }
    public Long getDepartment_id() {
        return department_id;
    }
    public void setDepartment_id(Long department_id) {
        this.department_id = department_id;
    }
    
}
