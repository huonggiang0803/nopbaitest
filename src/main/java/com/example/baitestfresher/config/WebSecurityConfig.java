package com.example.baitestfresher.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import lombok.RequiredArgsConstructor;
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;
   
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(requests -> requests
            .requestMatchers(
                    "/api/users/updateUser/**", "/api/users/loginUser", 
                    "/api/users/registerUser","/api/users/getUserId/**",  "/api/users/logout"
                ).permitAll()
            .requestMatchers("/api/users/getAllUsers").permitAll()
            .requestMatchers("/api/users/deleteUser/**").permitAll()
            .anyRequest().authenticated()
            );

        return http.build();
    }
}
