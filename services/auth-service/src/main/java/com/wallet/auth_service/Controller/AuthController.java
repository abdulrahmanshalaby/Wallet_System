package com.wallet.auth_service.Controller;

import com.wallet.auth_service.dtos.AuthResponse;
import com.wallet.auth_service.dtos.LoginRequest;

import com.wallet.auth_service.dtos.RegisterRequest;
import com.wallet.auth_service.dtos.ProfileResponse;
import org.springframework.security.core.Authentication;
import com.wallet.auth_service.model.Role;
import com.wallet.auth_service.model.User;
import io.jsonwebtoken.*;

import com.wallet.auth_service.model.VerificationToken;
import com.wallet.auth_service.service.JwtService;
import com.wallet.auth_service.service.EmailService;
import com.wallet.auth_service.service.userservice;

import jakarta.validation.Valid;

import org.apache.catalina.connector.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

import java.util.Map;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


@RestController
@RequestMapping("/auth")
class AuthController {
@Autowired
private userservice userService;
@Autowired
private JwtService jwtService;


@Autowired
private PasswordEncoder passwordEncoder;


@Autowired
private EmailService emailService;


@PostMapping("/register")
public ResponseEntity<Map<String,String>>register(@RequestBody @Valid RegisterRequest request) {
     System.out.println("========= CONTROLLER REACHED =========");
User user = userService.register(request,      
Set.of(request.getRole() != null ? request.getRole() : Role.CUSTOMER)        );
VerificationToken token = userService.createVerificationToken(user, 60);
emailService.sendEmailVerification(user.getEmail(), token.getToken());
return ResponseEntity.ok(Map.of("message", "registered, verify email"));
}

@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
       Optional<User> optionalUser = userService.findByEmail(request.getEmail());
    if (optionalUser.isEmpty()) {
        throw new IllegalArgumentException("Invalid credentials");
    }

    User user = optionalUser.get();

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        throw new IllegalArgumentException("Invalid credentials");
    }

    String token = jwtService.generateToken(user, user.getRoles());
    return ResponseEntity.ok(new AuthResponse(token, user.getId(),user.getName(),user.getRoles()));
}

@GetMapping("/verify")
public ResponseEntity<?> verify(@RequestParam("token") String token) {

return userService.verifyEmail(token)
.map(u -> ResponseEntity.ok(Map.of("message", "email verified")))
.orElseThrow(()-> new IllegalArgumentException("Invalid or expired token"));
}

@PostMapping("/forgot-password")
public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
     VerificationToken token = userService.createPasswordResetToken(request.email());
    emailService.sendPasswordResetEmail(request.email(), token.getToken());

    return ResponseEntity.ok("Password reset email sent.");}

@PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        boolean success = userService.resetPassword(request.token(), request.newPassword());
        return success ? ResponseEntity.ok("Password reset successful")
                       : ResponseEntity.badRequest().body("Invalid token");
    }
@GetMapping("/Profile")
public ResponseEntity<?> getProfile(Authentication authentication) {
    Long userId = Long.parseLong(authentication.getName()); // assuming user ID is stored in principal name

    Optional<User> userOpt = userService.findById(userId);
    if (userOpt.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

    User user = userOpt.get();
    return ResponseEntity.ok(new ProfileResponse(user.getName(), user.getEmail(), user.getPhone()));
}

@GetMapping("/Me") 
public ResponseEntity<?> getme(@RequestHeader("Authorization") String authHeader) { String token = authHeader.replace("Bearer ", "");
Jws<Claims> jws = jwtService.parseToken(token);

Long userId = Long.parseLong(jws.getBody().getSubject()); String name = jws.getBody().get("name", String.class); String rolesCsv = jws.getBody().get("roles", String.class); Set<Role> roles = Arrays.stream(rolesCsv.split(",")) .map(Role::valueOf) .collect(Collectors.toSet());
return ResponseEntity.ok(new AuthResponse(token,userId, name,roles)); }


@PutMapping("/Profile")
public ResponseEntity<?> editProfile(
    Authentication authentication,
    @RequestBody ProfileResponse updateRequest
) {
    Long userId = Long.parseLong(authentication.getName());

    Optional<User> userOpt = userService.findById(userId);
    if (userOpt.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

    User user = userOpt.get();
    user.setName(updateRequest.getName());
    user.setEmail(updateRequest.getEmail());
    user.setPhone(updateRequest.getPhone());

    userService.save(user);
    String newToken = jwtService.generateToken(user,user.getRoles());

    return ResponseEntity.ok(Map.of("token", newToken));
}



public static record ResetPasswordRequest(String token, String newPassword) {}

public static record ForgotPasswordRequest(String email) {}
}
