package com.server.HealthNet.Model;

import java.time.LocalDate;

public class Staff extends Person{
    private String proffession;

    public Staff() {
    }

    public Staff(byte[] image, String image_type, String name, String gender, Integer age, LocalDate birthdate,
            String contact_info, String address, String proffession) {
        super(image, image_type, name, gender, age, birthdate, contact_info, address);
        this.proffession = proffession;
    }

    public String getProffession() {
        return proffession;
    }

    public void setProffession(String proffession) {
        this.proffession = proffession;
    }

    
}
