package auth2.auth2.Jwt;

import auth2.auth2.users.Users;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class jwtService {
    @Value("${auth2.jwt.SecretKey}")
      String key;


    public SecretKey getSecretKey(){
      return  Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    }


    public String JwtToken(Users users){
        return Jwts.builder()
                .signWith(getSecretKey())
                .subject(users.getUserEmail())
                .claim("Username",users.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+1000*60*10))
                .compact();
    }


    public String  extractUsername(String token){
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }


}
