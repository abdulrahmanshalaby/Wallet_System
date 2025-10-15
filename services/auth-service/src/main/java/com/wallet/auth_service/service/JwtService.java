package com.wallet.auth_service.service;


import com.wallet.auth_service.model.Role;
import com.wallet.auth_service.model.User;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class JwtService {


private final Key key;
private final long validitySeconds;


public JwtService(@Value("${app.jwt.secret}") String secret,
@Value("${app.jwt.expiration-seconds:3600}") long validitySeconds) {
this.key = Keys.hmacShaKeyFor(secret.getBytes());
this.validitySeconds = validitySeconds;
}


public String generateToken(User user, Set<Role> roles) {
    Instant now = Instant.now();
    String rolesCsv = roles.stream()
                           .map(Enum::name)
                           .collect(Collectors.joining(","));

    return Jwts.builder()
            .setSubject(user.getId().toString())      
            .claim("name", user.getName())
            .claim("roles", rolesCsv)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plusSeconds(validitySeconds)))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
}
public Jws<Claims> parseToken(String token) {
return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
}

}



