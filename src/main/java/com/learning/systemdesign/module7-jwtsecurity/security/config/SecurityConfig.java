package com.learning.systemdesign.module7.security.config;

import com.learning.systemdesign.module7.security.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // Disable CSRF (Stateless)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v7/auth/**").permitAll() // Allow Login
                .requestMatchers("/api/v7/users").permitAll()   // Allow Registration (Create User)
                .requestMatchers("/actuator/**").permitAll()    // Allow Actuator Endpoints (Monitoring)
                .requestMatchers("/api/v10/**").permitAll()     // Allow Module 10 (Circuit Breaker Demo)
                .requestMatchers("/api/v11/**").permitAll()     // Allow Module 11 (Events Demo)
                .requestMatchers("/api/v7/**").authenticated() // Secure everything else
                .anyRequest().permitAll() // Allow other modules (v1-v6) to keep working
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // No Sessions
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
