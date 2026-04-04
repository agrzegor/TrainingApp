package pl.coderslab.trainingapp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    /**
     * Wyciąga nazwę użytkownika (subject) zakodowaną w tokenie JWT.
     *
     * @param token Ciąg znaków reprezentujący token JWT.
     * @return Nazwa użytkownika (np. email).
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Uniwersalna metoda do wyciągania dowolnej informacji (claim) z tokena.
     *
     * @param token Ciąg znaków reprezentujący token JWT.
     * @param claimsResolver Funkcja określająca, jaki konkretnie claim ma zostać pobrany.
     * @param <T> Typ zwracanej informacji.
     * @return Zwraca pojedynczy parametr wyciągnięty z tokena.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);

    }

    /**
     *
     * @param userDetails
     * @return
     * Tworzenie tokenu bez dodatkowych danych
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }


    /**
     * Generuje nowy token JWT dla podanego użytkownika.
     *
     * @param userDetails Obiekt zawierający dane o zalogowanym użytkowniku.
     * @return Zaszyfrowany token JWT w formie Stringa.
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    public long getExpirationTime() {
        return jwtExpiration;
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Sprawdza, czy token jest poprawny technicznie i czy należy do danego użytkownika.
     *
     * @param token Ciąg znaków reprezentujący token JWT.
     * @param userDetails Obiekt użytkownika pobrany z bazy/kontekstu security.
     * @return true, jeśli token jest ważny i dane się zgadzają; false w przeciwnym razie.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

