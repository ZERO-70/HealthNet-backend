package com.server.HealthNet.Model;

public class ApiQueryRequest {
    private String patient_id;
    private String doctor_id;
    private String role;
    private String query;
    private ModelType model;

    public ApiQueryRequest() {
    }

    public ApiQueryRequest(String role, String query) {
        this.role = role;
        this.query = query;
        this.model = ModelType.FAST; // Default to FAST
    }

    // Constructor for doctor or patient
    public ApiQueryRequest(String id, String role, String query) {
        if (role.equals("PATIENT")) {
            this.patient_id = id;
        } else if (role.equals("DOCTOR")) {
            this.doctor_id = id;
        }
        this.role = role.toLowerCase();
        this.query = query;
        this.model = ModelType.FAST; // Default to FAST
    }

    // Constructor with model type specified
    public ApiQueryRequest(String id, String role, String query, ModelType model) {
        if (role.equals("PATIENT")) {
            this.patient_id = id;
        } else if (role.equals("DOCTOR")) {
            this.doctor_id = id;
        }
        this.role = role.toLowerCase();
        this.query = query;
        this.model = model;
    }

    // Constructor without ID but with model type
    public ApiQueryRequest(String role, String query, ModelType model) {
        this.role = role;
        this.query = query;
        this.model = model;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    public String getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(String doctor_id) {
        this.doctor_id = doctor_id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public ModelType getModel() {
        return model;
    }

    public void setModel(ModelType model) {
        this.model = model;
    }
}
