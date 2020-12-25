package dam.dii.p1;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import javax.crypto.SecretKey;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dam.dii.p1.entities.Usuario;
import dam.dii.p1.security.Crypt;
import dam.dii.p1.security.JwtHandler;
import dam.dii.p1.services.Persistence;
import dam.dii.p1.utils.Tune;

/**
 * Servlet implementation class MyServlet
 */
//@WebServlet("/login")
public class MyServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Persistence per;
	private Crypt cry;
	private JwtHandler jwt;

	private byte[] jwtSecret;
	private String firebaseSecret;
	private String firebaseUrl;
	private byte[] salt;
	private char[] cryptoKey;
	private byte[] magicKey;
	private SecretKey sc;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MyServlet(HashMap<String, Object> objs) {
		super();
		parseKeys(objs);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("DO GET!!");
		System.out.println(request.getParameter("color"));

		/*
		 * response = Tune.error(response, " wrong direction",
		 * "There isn't any data, if you come redirected from login or sign in.. this is bad."
		 * ); response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		 */
		doPost(request, response);
		return;
	}

	/**
	 * Too much steps.. but they are easy - Check for key.dat or generate a new
	 * one @see {@link Crypt} - Get from inputs from request ¿not safe maybe?, map
	 * to {@link Usuario} while encoding passwords - FLOW 1) check if is a register
	 * form or login 2) do the sign up/in 2.1) redirect to error page if necessary
	 * 3) apply parameters to the request 4) redirect to to {@link HelloServlet}
	 *
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("DO POST!!");
		if (request.getParameter("pass") == "") {

			response = Tune.error(response, "Write pass pls",
					"This is a cool way to see this error page bc nothing has instantiated yet");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);

			return;
		}
		per = new Persistence(firebaseSecret, firebaseUrl);
		jwt = new JwtHandler(jwtSecret);
		cry = new Crypt(sc);
		
		System.out.println(request.getParameter("color"));
		
		Usuario usuario = new Usuario();
		usuario.setName(request.getParameter("user"));
		usuario.setPass(cry.encode(request.getParameter("pass")));
		usuario.setPass2(cry.encode(request.getParameter("pass2")));
		// claims to add to jwt
		HashMap<String, Object> claims = new HashMap<String, Object>();
		claims.put("color", request.getParameter("color"));

		if (request.getParameter("newUser") == null) {
			// Login
			Usuario storedUser = per.findByUsuario(usuario);
			if (storedUser.getName() == "") {
				// User doesn't exist in firebase
				response = Tune.error(response, "Try Signing! mark checkbox!", "Username doesn't exists :(");
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}

			if (Arrays.equals(cry.decode(usuario.getPass()), cry.decode(storedUser.getPass()))) {
				// LOGGED
				String token = jwt.generateTokenWithClaims(usuario, claims);
				getServletContext().getRequestDispatcher("/auth").include(
						Tune.request(request, usuario.getName(), token, false, is(request.getParameter("body"))),
						Tune.response(response, token, is(request.getParameter("header")),
								is(request.getParameter("cookie")), is(request.getParameter("httponly"))));
				return;
			} else {
				response = Tune.error(response, "Worng pass bruh :( ", "bruh.. ");
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				return;

			}
		} else {
			// Should encode after compare ¿?
			if (Arrays.equals(cry.decode(usuario.getPass()), cry.decode(usuario.getPass2()))) {
				// Sign in
				boolean signed = per.save(usuario);
				String token = jwt.generateToken(usuario);

				getServletContext().getRequestDispatcher("/auth").forward(
						Tune.request(request, usuario.getName(), token, signed, is(request.getParameter("body"))),
						Tune.response(response, token, is(request.getParameter("header")),
								is(request.getParameter("cookie")), is(request.getParameter("httponly"))));
				return;
			} else {
				// Pass not match
				response = Tune.error(response, "Oops, pass didn't match ",
						"Remember set the checkBox AND fill all 3 inputs.. both pass should match, ez");
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
		}
	}

	private static boolean is(String a) {
		return (a == null) ? false : true;
	}

	private void parseKeys(HashMap<String, Object> keys) {

		sc = (SecretKey) keys.get("secretkey");
		jwtSecret = (byte[]) keys.get("jwtSecrety");
		firebaseSecret = (String) keys.get("firebaseSecret");
		firebaseUrl = (String) keys.get("firebaseUrl");
		cryptoKey = (char[]) keys.get("cryptoKey");
		salt = (byte[]) keys.get("salt");
	}
}
