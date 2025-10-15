package com.wallet.auth_service.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
public class VerificationToken {

   @Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;


@Column(unique = true)
private String token;


private Instant expiry;



@ManyToOne
@JoinColumn(name = "user_id")
private User user;


public VerificationToken() {}

public Long getId() { return id; }
public void setId(Long id) { this.id = id; }
public String getToken() { return token; }
public void setToken(String token) { this.token = token; }
public Instant getExpiry() { return expiry; }
public void setExpiry(Instant expiry) { this.expiry = expiry; }
public User getUser() { return user; }
public void setUser(User user) { this.user = user; }
}
    


    // other fields and methods

    

  