package dam.dii.p1.security;

import java.util.Date;
import java.util.HashMap;

import dam.dii.p1.entities.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtHandler {

	private byte[] secret;
	private int EXPIRATION = 3 * 60 * 60;
	private SignatureAlgorithm ALGORITHM = SignatureAlgorithm.HS512;

	public JwtHandler(byte[] secret) {
		super();
		this.secret = secret;
	}

	public String generateToken(Usuario user) {
		return Jwts.builder().setSubject(user.getName()).setIssuedAt(new Date())
				.setExpiration(new Date(new Date().getTime() * EXPIRATION)).signWith(ALGORITHM, secret).compact();
	}

	public String generateTokenWithClaims(Usuario user, HashMap<String, Object> map) {

		Claims cl = Jwts.claims().setSubject(user.getName());
		cl.putAll(map);
		return Jwts.builder().setClaims(cl).setSubject(user.getName()).setIssuedAt(new Date())
				.setExpiration(new Date(new Date().getTime() * EXPIRATION)).signWith(ALGORITHM, secret).compact();
	}

	public Boolean isValid(String token, String username) {

		if (username.equals(getSubject(token)))
			return true;
		return false;
	}

	private String getSubject(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
	}

	public HashMap<String, Object> getClaimsFromToken(String token, String username) {
		if (!isValid(token, username))
			return null;
		return (HashMap<String, Object>) Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}
}
