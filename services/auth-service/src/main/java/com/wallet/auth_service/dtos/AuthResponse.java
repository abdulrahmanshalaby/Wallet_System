package com.wallet.auth_service.dtos;
import com.wallet.auth_service.model.Role;
import java.util.Set;



public class AuthResponse {
    private String token;
    private Long userId;
    private String name;
    private Set<Role> roles;

    public AuthResponse(String token,Long userId,String name,Set<Role> roles) {
        this.token=token;
        this.userId=userId;
        this.name=name;
        this.roles=roles;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Set<Role> getRoles() {
        return roles;
    }
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }


   
}
