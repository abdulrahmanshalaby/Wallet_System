package com.wallet.auth_service.config;
import com.wallet.auth_service.Security.JwtAuthFilter;
import com.wallet.auth_service.Security.OAuth2SuccessHandler;
import com.wallet.auth_service.service.JwtService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class securityconfig {
    private final JwtService jwtService;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
       http
    .cors().and()
    .csrf().disable()
    .authorizeHttpRequests(auth -> auth
        .requestMatchers("/", "/auth/**", "/oauth2/**").permitAll()
        .requestMatchers("/auth/admin/**").hasRole("ADMIN")
        .anyRequest().authenticated()
        
  
    ).addFilterBefore(new JwtAuthFilter(jwtService), UsernamePasswordAuthenticationFilter.class); // ✅ Add this line



        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
      @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173")); // your frontend
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
