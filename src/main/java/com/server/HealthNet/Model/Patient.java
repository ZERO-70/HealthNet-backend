package com.server.HealthNet.Model;

import java.time.LocalDate;

public class Patient extends Person {

    private String weight;
    private String height;
    public Patient() {
    }
    public Patient(byte[] image, String image_type, String name, String gender, Integer age, LocalDate birthdate,
            String contact_info, String address, String weight, String height) {
        super(image, image_type, name, gender, age, birthdate, contact_info, address);
        this.weight = weight;
        this.height = height;
    }
    public String getWeight() {
        return weight;
    }
    public void setWeight(String weight) {
        this.weight = weight;
    }
    public String getHeight() {
        return height;
    }
    public void setHeight(String height) {
        this.height = height;
    }
}
