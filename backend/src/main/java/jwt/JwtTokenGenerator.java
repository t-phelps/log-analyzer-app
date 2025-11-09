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

    /**
     * Generate a JSON web string for a user
     * @param username - the user to generate a string for
     * @return a signed JWS
     */
    public String getJwt(String username){
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() * 3600_000))
                .signWith(key)
                .compact();
    }


}
