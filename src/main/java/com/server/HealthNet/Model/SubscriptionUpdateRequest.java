package com.server.HealthNet.Model;

public class SubscriptionUpdateRequest {
    private String subscription;

    public SubscriptionUpdateRequest() {
    }

    public SubscriptionUpdateRequest(String subscription) {
        this.subscription = subscription;
    }

    public String getSubscription() {
        return subscription;
    }

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }
}
