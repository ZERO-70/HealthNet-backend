package com.server.HealthNet.Service;

import java.util.function.Function;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTservice {

    private static String mykey = "";

    JWTservice(){
        try {
            KeyGenerator keygen =KeyGenerator.getInstance("HmacSHA256");
            SecretKey key = keygen.generateKey(); 
            mykey = Base64.getEncoder().encodeToString(key.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    
        public String generateToken(String username,String Role) {
            Map<String,Object> claims = new HashMap<>();
            claims.put("role", "ROLE_" + Role);
            return Jwts.builder().claims().add(claims).
            subject(username).
            issuedAt(new Date(System.currentTimeMillis())).
            expiration(new Date(System.currentTimeMillis()+24*60*60*100)).and()
            .signWith(getKey()).compact();
        }

        public List<String> extractRoles(String token) {
            Claims claims = extractAllClaims(token);
            return claims.get("role", List.class); // Cast "roles" claim to a List<String>
        }

    
        private SecretKey getKey() {
            byte[] keybytes = Decoders.BASE64.decode(mykey);
            return Keys.hmacShaKeyFor(keybytes);
        }

        public String extractusername(String token) {
            return extractClaim(token, Claims::getSubject);
        }

        private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
            final Claims claims = extractAllClaims(token);
            return claimResolver.apply(claims);
        }

        private Claims extractAllClaims(String token) {
            return Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }

        public boolean validateToken(String token, UserDetails userDetails) {
            final String userName = extractusername(token);
            return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
        }
        private boolean isTokenExpired(String token) {
            return extractExpiration(token).before(new Date());
        }
        private Date extractExpiration(String token) {
            return extractClaim(token, Claims::getExpiration);
        }
}
