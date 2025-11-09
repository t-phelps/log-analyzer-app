package jwt;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;


/**
 * JWT generator class
 */
public class JwtTokenGenerator {

    private static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    /**
     * Generate a JSON web string for a user
     * @param username - the user to generate a string for
     * @return a signed JWS
     */
    public String getJwt(String username){


        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600_000))
                .signWith(KEY)
                .compact();
    }

    /**
     * Verify a JWT token
     * @param token - the jwt token to verify
     * @return
     */
    public String validateJwt(String token){
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

}
