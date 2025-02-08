package com.server.HealthNet.Model;

import java.time.LocalDate;

public class Inventory {
    private Long inventory_id;
    private String name;
    private Long quantity;
    private LocalDate expiryDate;
    private Long department_id;
    public Inventory() {
    }
    public Inventory(String name, Long quantity, LocalDate expiryDate, Long department_id) {
        this.name = name;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.department_id = department_id;
    }
    public Long getInventory_id() {
        return inventory_id;
    }
    public void setInventory_id(Long inventory_id) {
        this.inventory_id = inventory_id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Long getQuantity() {
        return quantity;
    }
    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
    public LocalDate getExpiryDate() {
        return expiryDate;
    }
    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
    public Long getDepartment_id() {
        return department_id;
    }
    public void setDepartment_id(Long department_id) {
        this.department_id = department_id;
    }
    
}
