package com.deliverx.user_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;

@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 120)
    private String firstName;

    @Column(length = 120)
    private String lastName;

    @Column(length = 32)
    private String phone;

    @Column(length = 255)
    private String address;

    public UserProfile() {
    }

    public UserProfile(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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
