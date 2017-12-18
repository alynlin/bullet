package com.unique.bullet.dto;

import java.io.Serializable;

public class Car implements Serializable{

    private static final long serialVersionUID = -4152214911601106874L;
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Car{");
        sb.append('}');
        return sb.toString();
    }
}
