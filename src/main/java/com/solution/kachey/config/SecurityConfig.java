package com.solution.kachey.config;

import com.solution.kachey.config.jwt.JwtTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.solution.kachey.user_manager.Constants;

@Configuration
public class SecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtTokenFilter jwtTokenFilter, UserDetailsService userDetailsService) {
        this.jwtTokenFilter = jwtTokenFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)  // Disable CSRF protection
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()  // Allow access to auth endpoints
                        .requestMatchers("/**").hasAnyRole(Constants.ROLE_SUPER_ADMIN)
                        .requestMatchers("/api/users/profile").authenticated()
                        .requestMatchers("/api/permission/add-permission").hasAnyRole(Constants.ROLE_SUPER_ADMIN, Constants.ROLE_ADMIN)
                        .anyRequest().authenticated()  // All other requests require authentication
                )
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);  // Add JWT filter before the username/password filter

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder auth =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return auth.build();
    }

//    private boolean hasRoleInt(String requestUri) {
//        // Logic to check if the user has the ROLE_SUPER_ADMIN based on the int value
//        String roleName = Role.toRoleName(ROLE_SUPER_ADMIN_INT); // Convert int to role name
//        return SecurityContextHolder.getContext().getAuthentication()
//            .getAuthorities().stream()
//            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(roleName));
//    }
}
