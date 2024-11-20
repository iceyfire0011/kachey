package com.solution.kachey.config.jwt;

import com.solution.kachey.user_manager.model.User;
import com.solution.kachey.user_manager.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class JwtTokenUtil {

    private final String secret = "gRGQqUbVn+FoQm+z2f8UfOSdxSiNIChVTsKGoH0Jftc8Kpm6z/B6cfetvWPDusPmKAArUDL4GuYvfMC2HO7WQg===";
    private final SecretKey signingKey = Keys.hmacShaKeyFor(secret.getBytes());

    @Autowired
    private UserService userService;

    // Generate the JWT token including roles and permissions
    public String generateToken(UserDetails userDetails) {
        // Retrieve the user from the service only once
        Optional<User> userOptional = userService.findByUsername(userDetails.getUsername());
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found: " + userDetails.getUsername());
        }

        User user = userOptional.get();
        Map<String, Object> claims = new HashMap<>();

        // Set roles and permissions from the user object
        claims.put("role", user.getRole());  // Assuming Role is a simple object or String
        claims.put("permissions", user.getPermissions());  // Assuming permissions is a list or set

        // Create the JWT token with subject (username), expiration time, etc.
        // 1 hour
        long expiration = 3600;
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(expiration)))  // Add expiration in a cleaner way
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
    }

    // Validate the JWT token
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Get the username from the JWT token
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // Extract a claim from the token
    public <T> T getClaimFromToken(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // Get all claims from the token
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Check if the token is expired
    private Boolean isTokenExpired(String token) {
        final Date expiration = getClaimFromToken(token, Claims::getExpiration);
        return expiration.before(new Date());
    }
}
