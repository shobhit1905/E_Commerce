package com.ecommerce.project.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;


@Component
public class JwtUtils {
    private static final Logger logger =  LoggerFactory.getLogger(JwtUtils.class);

    // the below values are fetched in from application.properties file
    @Value("${spring.app.jwtSecret}")
    private String jwtSecret ;  // used to sign the tokens

    @Value("${spring.app.jwtExpirationMs}")
    private int jwtExpirationMs ;

    // getting the jwt token from http header
    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        logger.debug("Authorization token: {}", bearerToken);
        if(bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7) ; // remove bearer prefix
        }
        return null ;
    }

    // generate token from username
    public String generateTokenFromUsername(UserDetails userDetails) {
        String userName = userDetails.getUsername();
        return Jwts.builder()
                .setSubject(userName)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key())
                .compact() ;
    }

    public String getUserNameFromJwtToken(String token){
        return Jwts.parser().setSigningKey((SecretKey) key()).parseClaimsJws(token).getBody().getSubject();
    }

    private Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public boolean validateJwtToken(String authToken) {
        try{
            System.out.println("Validate");
            Jwts.parser().setSigningKey((SecretKey) key()).parseClaimsJws(authToken);
            return true ;
        }
        catch(MalformedJwtException e){
            logger.error("Invalid JWT token : {}", e.getMessage());
        }
        catch(ExpiredJwtException e){
            logger.error("JWT token is expired : {}", e.getMessage());
        }
        catch(UnsupportedJwtException e){
            logger.error("Unsupported JWT token is not supported : {}", e.getMessage());
        }
        catch (IllegalArgumentException e){
            logger.error("JWT claims string is empty : {}", e.getMessage());
        }

        return false ;
    }
}


// UserDetail is an interface that is used to represent a single user in the application that we want to authenticate
// We will implement our own custom UserDetail class as to meet the project requirement
// UserDetailService is a core interface that allows us to load user specific data
// We will also customize this to our requirements
