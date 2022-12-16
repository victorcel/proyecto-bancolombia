package com.bancolombia.models;

import java.util.HashMap;
import java.util.Map;

public class RequestInput {

    private String name;

    private String email;

    private int age;

    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public Map<String, String> toResponse() {
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("name",name);
        responseBody.put("email",email);
        responseBody.put("age", String.valueOf(age));
        responseBody.put("address",address);

        return responseBody;
    }

}