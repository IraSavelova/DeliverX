package com.deliverx.user_service.dto;

public class UserProfileResponse {

    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;

    public UserProfileResponse(String email, String firstName, String lastName, String phone, String address) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }
}
