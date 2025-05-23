package com.server.HealthNet.Model;

public class SubscriptionResponse {
    private String subscription;

    public SubscriptionResponse() {
    }

    public SubscriptionResponse(String subscription) {
        this.subscription = subscription;
    }

    public String getSubscription() {
        return subscription;
    }

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }
}
