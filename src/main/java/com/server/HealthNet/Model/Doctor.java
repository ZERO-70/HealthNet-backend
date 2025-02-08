package com.server.HealthNet.Model;

import java.time.LocalDate;

public class Doctor extends Person{

    public Doctor() {
    }


    
    public Doctor(byte[] image, String image_type, String name, String gender, Integer age, LocalDate birthdate,
            String contact_info, String address, String specialization) {
        super(image, image_type, name, gender, age, birthdate, contact_info, address);
        this.specialization = specialization;
    }

    private String specialization;

    public String getSpecialization() {
        return specialization;
    }



    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
}
