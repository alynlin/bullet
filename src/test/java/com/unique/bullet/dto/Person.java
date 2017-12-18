package com.unique.bullet.dto;

import java.io.Serializable;
import java.util.List;

public class Person implements Serializable{
    private static final long serialVersionUID = -370813107579297292L;

    private String name;

    private int age;

    private String address;
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    private List<Car> cars;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<Car> getCars() {
        return cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }



}
