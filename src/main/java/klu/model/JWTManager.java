package klu.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTManager {
		
	public final String SEC_KEY = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890MKDJSHIEUFBEIC";
	public final SecretKey key = Keys.hmacShaKeyFor(SEC_KEY.getBytes());
	
	public String generateToken(String email, String role)
	{
		Map<String, String> data = new HashMap<String, String>();
		data.put("email", email);
		data.put("role", role);
		
		return Jwts.builder()
				.setClaims(data)
				.setIssuedAt(new Date())
				.setExpiration(new Date(new Date().getTime() + 3600000))
				.signWith(key)
				.compact();
	}

	public String validateToken(String token) {
		try {
			Claims claims = Jwts.parserBuilder()
							.setSigningKey(key)
							.build()
							.parseClaimsJws(token)
							.getBody();
			
			Date expiry = claims.getExpiration();
			if(expiry == null || expiry.before(new Date()))
				return "401";
			return claims.get("email", String.class);
		} catch (Exception e) {
			return "401";
		}
	}

	public String getRoleFromToken(String token) {
		try {
			Claims claims = Jwts.parserBuilder()
							.setSigningKey(key)
							.build()
							.parseClaimsJws(token)
							.getBody();
			return claims.get("role", String.class);
		} catch (Exception e) {
			return null;
		}
	}
}