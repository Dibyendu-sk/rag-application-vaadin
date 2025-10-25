package com.dibyendu.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TABLE_USER")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "USER_ID")
    private String id;

    @Column(name = "USER_NAME")
    @NotNull
    private String name;

    @Column(name = "COUNTRY_CODE")
    private String countryCode = "+91";

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @Column(name = "EMAIL", unique = true)
    @Email
    private String email;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "USER_ROLE")
    private String role = "user";
}
