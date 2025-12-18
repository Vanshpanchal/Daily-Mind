package spring.project.Daily.Mind.utility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {

    private static final String SECRET_KEY = "TaK+HaV^uvEJ#2sEVfypW#7g9^k*Z8$V";

    // Token validity: 30 minutes
    private static final long TOKEN_VALIDITY_MILLIS =10 * 24 * 60 * 60 * 1000;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    public String extractUsername(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            // Return null for expired tokens - let the filter handle it
            return null;
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    public Date extractExpiration(String token) {
        try {
            return extractAllClaims(token).getExpiration();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    private Claims extractAllClaims(String token) {
        // JJWT 0.12.x API
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        try {
            Date exp = extractExpiration(token);
            return exp == null || exp.before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return true;
        }
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        long now = System.currentTimeMillis();
        // JJWT 0.12.x API
        return Jwts.builder()
                .header().type("JWT").and()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(now))
                .expiration(new Date(now + TOKEN_VALIDITY_MILLIS))
                .signWith(getSigningKey())
                .compact();
    }

    public Boolean validateToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getSubject() != null && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
