package com.wallet.auth_service.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Set;


@Entity
@Table(name = "users")
public class User {


@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
@Column(unique = true)
private String email;
private String name;


@Column(unique = true)
private String phone;



private String password; // bcrypt hashed


private boolean emailVerified = false;
private boolean phoneVerified = false;


private String googleId; // OAuth users


private Instant createdAt = Instant.now();


@ElementCollection(fetch = FetchType.EAGER)
@Enumerated(EnumType.STRING)
@CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
@Column(name = "role")
private Set<Role> roles;


// getters and setters


public User() {}


public Long getId() { return id; }
public void setId(Long id) { this.id = id; }
public String getEmail() { return email; }
public void setEmail(String email) { this.email = email; }
public String getName() { return name; }
public void setName(String name) { this.name = name; }
public String getPhone() { return phone; }
public void setPhone(String phone) { this.phone = phone; }
public String getPassword() { return password; }
public void setPassword(String password) { this.password = password; }
public boolean isEmailVerified() { return emailVerified; }
public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }
public boolean isPhoneVerified() { return phoneVerified; }
public void setPhoneVerified(boolean phoneVerified) { this.phoneVerified = phoneVerified; }
public Set<Role> getRoles() { return roles; }
public void setRoles(Set<Role> roles) { this.roles = roles; }
public String getGoogleId() { return googleId; }
public void setGoogleId(String googleId) { this.googleId = googleId; }
public Instant getCreatedAt() { return createdAt; }
public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}

