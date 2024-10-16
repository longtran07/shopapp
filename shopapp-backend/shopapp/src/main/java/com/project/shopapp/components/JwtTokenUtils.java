package com.project.shopapp.components;

import com.project.shopapp.exceptions.InvalidParamException;
import com.project.shopapp.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
@Component
public class JwtTokenUtils {
    @Value("${jwt.expiration}")
    private long expeiration; // save to an enviroment variable

    @Value("${jwt.secretKey}")
    private String secretKey;

    public String generateToken(User user) throws  Exception{
        Map<String, Object> claims=new HashMap<>();
//        this.generateSecretKey();
        claims.put("phoneNumber" , user.getPhoneNumber());
        try {
            String token= Jwts.builder()
                    .setClaims(claims)
                    .setSubject(user.getPhoneNumber())
                    .setExpiration(new Date(System.currentTimeMillis() + expeiration * 1000L))
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();
            return token;
        }catch (Exception e){
            //you can "inject " loger
            throw new InvalidParamException("Cannot create JWT token, error: " + e.getMessage() );
//            return null;
        }
    }
    private Key getSignInKey(){
        byte[] bytes= Decoders.BASE64.decode(secretKey); // JCyj1zK3npwaFYv65alqdshGiImOQvjAoV7Fr4kHV9k=

        return Keys.hmacShaKeyFor(bytes);
    }

    private String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[32]; // 256-bit key
        random.nextBytes(keyBytes);
        String secretKey = Encoders.BASE64.encode(keyBytes);
        return secretKey;
    }

    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public  <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        final Claims claims=this.extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    //check expiration
    public boolean isTokenExpired(String token){
        Date expirationDate = this.extractClaim(token,Claims::getExpiration);
        return expirationDate.before(new Date());
    }

    public String extractPhoneNumber(String token){
        return extractClaim(token, Claims::getSubject);
    }
    public boolean validateToken(String token, User userDetails) {
        String phoneNumber = extractPhoneNumber(token);
        return (phoneNumber.equals(userDetails.getUsername()))
                && !isTokenExpired(token);
    }

}
