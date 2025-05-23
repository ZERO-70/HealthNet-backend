package com.server.HealthNet.Model;

public class UserAuthentication {
    private String username;
    private String password;
    private Role role;
    private Long personId;
    private Subscription subscription;

    public UserAuthentication() {
    }

    public UserAuthentication(String username, String password, Role role, Long personId) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.personId = personId;
        // Default subscription is DEFAULT
        this.subscription = Subscription.DEFAULT;

        // For ADMIN and STAFF, automatically set to PLUS
        if (role == Role.ADMIN || role == Role.STAFF) {
            this.subscription = Subscription.PLUS;
        }
    }

    public UserAuthentication(String username, String password, Role role, Long personId, Subscription subscription) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.personId = personId;
        this.subscription = subscription;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    @Override
    public String toString() {
        return "UserAuthentication [username=" + username + ", password=" + password + ", role=" + role + ", personId="
                + personId + ", subscription=" + subscription + "]";
    }

}
