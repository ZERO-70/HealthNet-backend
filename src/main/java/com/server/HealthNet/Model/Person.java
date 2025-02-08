package com.server.HealthNet.Model;

import java.time.LocalDate;
import java.util.Arrays;

public class Person {
    
    public Person() {
    }

    public Person(byte[] image, String image_type, String name, String gender, Integer age, LocalDate birthdate,
                  String contact_info, String address) {
        this.image = image;
        this.image_type = image_type;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.birthdate = birthdate;
        this.contact_info = contact_info;
        this.address = address;
    }

    private Long person_id; // Primary Key
    private byte[] image;
    private String image_type;
    private String name;
    private String gender;
    private Integer age;
    private LocalDate birthdate;
    private String contact_info;
    private String address;

    // Getters and Setters
    public Long getId() {
        return person_id;
    }
    public void setId(Long id) {
        this.person_id = id;
    }
    public byte[] getImage() {
        return image;
    }
    public void setImage(byte[] image) {
        this.image = image;
    }
    public String getImage_type() {
        return image_type;
    }
    public void setImage_type(String image_type) {
        this.image_type = image_type;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
    public LocalDate getBirthdate() {
        return birthdate;
    }
    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }
    public String getContact_info() {
        return contact_info;
    }
    public void setContact_info(String contact_info) {
        this.contact_info = contact_info;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Person [person_id=" + person_id + ", image=" + Arrays.toString(image) + ", image_type=" + image_type
                + ", name=" + name + ", gender=" + gender + ", age=" + age + ", birthdate=" + birthdate
                + ", contact_info=" + contact_info + ", address=" + address + "]";
    }
}