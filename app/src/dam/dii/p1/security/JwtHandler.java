package dam.dii.p1.security;

import java.util.Date;
import java.util.HashMap;

import dam.dii.p1.entities.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Jjwt here, too much easy compared to {@link Crypt} lib needed: jackson-core,
 * jackson-annotations and jackson-databind
 *
 * @author SERGI
 *
 */
public class JwtHandler {

	private byte[] secret;
	private int EXPIRATION = 3 * 60 * 60;
	private SignatureAlgorithm ALGORITHM = SignatureAlgorithm.HS512;

	public JwtHandler(byte[] secret) {
		super();
		this.secret = secret;
	}

	/**
	 * This jwt has a subject (it is a "special claim"), issue and expiration
	 * days, @link {@link JwtHandler#ALGORITHM} and the @link
	 * {@link JwtHandler#secret} key
	 *
	 * @param user
	 * @return
	 */
	public String generateToken(Usuario user) {
		return Jwts.builder().setSubject(user.getName()).setIssuedAt(new Date())
				.setExpiration(new Date(new Date().getTime() * EXPIRATION)).signWith(ALGORITHM, secret).compact();
	}

	/**
	 * This jwt has a subject (it is a "special claim"), issue and expiration days,
	 * algortihm and the secret key AND "normal" claims, which we can decode in
	 * {@link JwtHandler#getClaimsFromToken(String, String)}, nothing is too safe
	 * around here so no passwords.
	 *
	 * @param user
	 * @return
	 */
	public String generateTokenWithClaims(Usuario user, HashMap<String, Object> map) {

		Claims cl = Jwts.claims().setSubject(user.getName());
		cl.putAll(map);
		cl.setSubject(user.getName());
		return Jwts.builder().setClaims(cl).setIssuedAt(new Date())
				.setExpiration(new Date(new Date().getTime() * EXPIRATION)).signWith(ALGORITHM, secret).compact();
	}

	/**
	 * A lot of things could be wrong, finally we just care about if the token
	 * subject equals to username, that means that the user had signed up not long
	 * ({@link JwtHandler#EXPIRATION} before.
	 *
	 * @param token
	 * @param username
	 * @return
	 */
	public Boolean isValid(String token, String username) {

		if (username.equals(getSubject(token)))
			return true;
		return false;
	}

	private String getSubject(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
	}

	/**
	 * Parse claims added in
	 * {@link JwtHandler#generateTokenWithClaims(Usuario, HashMap)}
	 *
	 * @param token
	 * @param username
	 * @return
	 */
	public String getClaimsFromToken(String token, String username) {
		if (!isValid(token, username))
			return null;
		return new HashMap<>(Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody()).get("color")
				.toString();
	}
}
