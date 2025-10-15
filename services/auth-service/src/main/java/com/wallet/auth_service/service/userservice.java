package com.wallet.auth_service.service;


import com.wallet.auth_service.model.Role;
import com.wallet.auth_service.model.User;
import com.wallet.auth_service.model.VerificationToken;
import com.wallet.auth_service.Repository.UserRepository;
import com.wallet.auth_service.Repository.VerificationTokenRepository;
import com.wallet.auth_service.dtos.RegisterRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


@Service
public class userservice {

@Autowired
private UserRepository userRepository;


@Autowired
private VerificationTokenRepository tokenRepository;


@Autowired
private PasswordEncoder passwordEncoder;


public User register(RegisterRequest request, Set<Role> roles) {
if (userRepository.existsByEmail(request.getEmail())) {
    throw new IllegalArgumentException("Email already in use");
}
if(userRepository.existsByPhone(request.getPhone())) {
    throw new IllegalArgumentException("Phone already exists");
}

User u = new User();
u.setEmail(request.getEmail());
u.setPhone(request.getPhone());
u.setName(request.getName());
u.setPassword(passwordEncoder.encode(request.getPassword()));
u.setRoles(roles);
u.setEmailVerified(false);
return userRepository.save(u);
}
public User registerGoogleUser(String email, String name, String googleId ) {
    User user = new User();
    user.setEmail(email);
    user.setName(name);
    user.setGoogleId(googleId);
    user.setEmailVerified(true); // Google verified email
    user.setRoles(Set.of(Role.CUSTOMER));        
    return userRepository.save(user);
}
public Optional<User> findByEmail(String email) {
return userRepository.findByEmail(email);
}
public Optional<User> findById(Long id) {
    return userRepository.findById(id);
}
    public User save(User user) {
        return userRepository.save(user);
    }



public List<User> findAllUsers() {
        return userRepository.findAll();
    }

public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

public VerificationToken createVerificationToken(User user, long minutesValid) {
VerificationToken token = new VerificationToken();
token.setToken(UUID.randomUUID().toString());
token.setUser(user);
token.setExpiry(Instant.now().plusSeconds(minutesValid * 60));
return tokenRepository.save(token);
}


public Optional<User> verifyEmail(String tokenStr) {
Optional<VerificationToken> opt = tokenRepository.findByToken(tokenStr);
if (opt.isEmpty()) return Optional.empty();
VerificationToken t = opt.get();
if (t.getExpiry().isBefore(Instant.now())) return Optional.empty();
User u = t.getUser();
u.setEmailVerified(true);
userRepository.save(u);
tokenRepository.delete(t);
return Optional.of(u);
}
public VerificationToken createPasswordResetToken(String email) {
    User user = userRepository.findByEmail(email)
                 .orElseThrow(() -> new RuntimeException("User not found"));

    // Reuse VerificationToken but indicate it’s for password reset
    VerificationToken token = new VerificationToken();
    token.setUser(user);
    token.setToken(UUID.randomUUID().toString());
    token.setExpiry(Instant.now().plusSeconds(3600));
    tokenRepository.save(token);
    return token;
}
    public boolean resetPassword(String token, String newPassword) {
        VerificationToken resetToken = tokenRepository.findByToken(token).orElse(null);
        if (resetToken == null || !(resetToken.getExpiry().isBefore(Instant.now()))) return false;
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(resetToken);
        return true;
}

}
