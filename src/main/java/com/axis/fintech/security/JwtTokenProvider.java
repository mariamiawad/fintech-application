package com.axis.fintech.security;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

	@Value("${jwt.secret:YXNkZmFzZGZhc2RmYXNkZg==}")
	private String jwtSecret;

	@Value("${jwt.expiration:3600000}")
	private long jwtExpirationInMs;
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // ✅ Secure 256-bit key

	public String generateToken(UserDetails userDetails) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

		return Jwts.builder().setSubject(userDetails.getUsername()).setIssuedAt(now).setExpiration(expiryDate)
				.signWith(key, SignatureAlgorithm.HS256).compact();
	}

	public String getUsernameFromToken(String token) {
		Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();

		return claims.getSubject();
	}

	public boolean validateToken(String token, UserDetails userDetails) {
		final String username = getUsernameFromToken(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	private boolean isTokenExpired(String token) {
		final Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
				.getBody().getExpiration();
		return expiration.before(new Date());
	}
}
