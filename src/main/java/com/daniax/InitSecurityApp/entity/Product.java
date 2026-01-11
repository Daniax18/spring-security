package com.daniax.InitSecurityApp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String name;

    private Double price;

    // Constructors


    public Product() {
    }

    public Product(String name, Double price) throws Exception{
        this.setName(name);
        this.setPrice(price);
    }


    // Getters and setters

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) throws Exception{
        if(price <= 0) throw new Exception("Price can not be negatove");
        this.price = price;
    }
}
