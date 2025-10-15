package com.wallet.auth_service.Security;


import com.wallet.auth_service.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class JwtAuthFilter extends OncePerRequestFilter {


private final JwtService jwtService;


public JwtAuthFilter(JwtService jwtService) { this.jwtService = jwtService; }


@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
throws ServletException, IOException {
String auth = request.getHeader("Authorization");
if (StringUtils.hasText(auth) && auth.startsWith("Bearer ")) {
String token = auth.substring(7);
try {
Jws<Claims> jws = jwtService.parseToken(token);
String sub = jws.getBody().getSubject();
String rolesCsv = jws.getBody().get("roles", String.class);
List<SimpleGrantedAuthority> authorities = Arrays.stream(rolesCsv.split(","))
.filter(s -> !s.isBlank())
.map(r -> new SimpleGrantedAuthority("ROLE_" + r))
.collect(Collectors.toList());
UsernamePasswordAuthenticationToken authToken =
new UsernamePasswordAuthenticationToken(sub, null, authorities);
SecurityContextHolder.getContext().setAuthentication(authToken);
} catch (Exception e) {
// invalid token
response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
return;
}
}
filterChain.doFilter(request, response);
}
}