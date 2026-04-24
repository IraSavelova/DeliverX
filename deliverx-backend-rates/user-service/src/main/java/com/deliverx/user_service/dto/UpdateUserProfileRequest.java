package com.deliverx.user_service.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UpdateUserProfileRequest {

    @Size(max = 120, message = "firstName must be at most 120 characters")
    private String firstName;

    @Size(max = 120, message = "lastName must be at most 120 characters")
    private String lastName;

    @Size(max = 32, message = "phone must be at most 32 characters")
    @Pattern(regexp = "^[+0-9()\\-\\s]{5,32}$", message = "phone contains unsupported characters")
    private String phone;

    @Size(max = 255, message = "address must be at most 255 characters")
    private String address;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
