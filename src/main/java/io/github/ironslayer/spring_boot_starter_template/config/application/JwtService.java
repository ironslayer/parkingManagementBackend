package io.github.ironslayer.spring_boot_starter_template.config.application;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final long TOKEN_EXPIRATION = 1000 * 60 * 60 * 24;
    private static final long REFRESH_WINDOW = 1000 * 60 * 60 * 24 * 7;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Long extractUserId(String token) {
        return extractClaim(token, claims -> {
            Object userIdObj = claims.get("user_id");
            if (userIdObj instanceof Number) {
                return ((Number) userIdObj).longValue();
            }
            return null;
        });
    }

    public List<String> extractAuthorities(String token) {
        return extractClaim(token, claims -> {
            List<?> authoritiesList = claims.get("authorities", List.class);
            if (authoritiesList == null) {
                return List.of();
            }
            return authoritiesList.stream()
                    .map(Object::toString)
                    .toList();
        });
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        // Añadir las authorities del usuario al token
        List<String> authorities = userDetails.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .toList();
        extraClaims.put("authorities", authorities);
        
        // Agregar user_id al token si UserDetails es una instancia de UserEntity
        if (userDetails instanceof io.github.ironslayer.spring_boot_starter_template.user.infrastructure.entity.UserEntity) {
            io.github.ironslayer.spring_boot_starter_template.user.infrastructure.entity.UserEntity userEntity = 
                (io.github.ironslayer.spring_boot_starter_template.user.infrastructure.entity.UserEntity) userDetails;
            extraClaims.put("user_id", userEntity.getId());
        }
        
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()));
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new RuntimeException("Invalid JWT token or mal formed", e);
        }
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean canTokenBeRenewed(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Date expiration = claims.getExpiration();
            long currentTime = System.currentTimeMillis();
            return expiration.before(new Date(currentTime)) &&
                    expiration.getTime() + REFRESH_WINDOW > currentTime;
        } catch (Exception e) {
            return false;
        }
    }

    public String renewToken(String token, UserDetails userDetails) {
        if (!canTokenBeRenewed(token)) {
            throw new IllegalArgumentException("The JWT couldn't be renewed");
        }
        return generateToken(userDetails);
    }

}
