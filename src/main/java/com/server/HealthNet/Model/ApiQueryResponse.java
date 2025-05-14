package com.server.HealthNet.Model;

public class ApiQueryResponse {
    private String response;

    public ApiQueryResponse() {
    }

    public ApiQueryResponse(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
