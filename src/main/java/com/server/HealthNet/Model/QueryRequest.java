package com.server.HealthNet.Model;

public class QueryRequest {
    private String query;
    private ModelType model;

    public QueryRequest() {
        this.model = ModelType.FAST; // Default to FAST
    }

    public QueryRequest(String query) {
        this.query = query;
        this.model = ModelType.FAST; // Default to FAST
    }

    public QueryRequest(String query, ModelType model) {
        this.query = query;
        this.model = model;
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