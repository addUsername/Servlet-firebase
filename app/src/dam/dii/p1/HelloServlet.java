package dam.dii.p1;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dam.dii.p1.security.JwtHandler;
import dam.dii.p1.utils.Tune;

/**
 * Servlet implementation class HelloServlet
 */
//@WebServlet("/auth")
public class HelloServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private JwtHandler jwt;
	// private final byte[] jwtSecret = "more_secrets".getBytes();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public HelloServlet(byte[] jwtSecret) {
		super();
		jwt = new JwtHandler(jwtSecret);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("DO post HELLOSERVLET");

		Cookie cook = getJwtCookie(request.getCookies());
		String token = null;
		Boolean headers = false, cookie = false, httpOnly = false;
		if (cook != null) {
			cookie = true;
			token = cook.getValue();
			httpOnly = false; // do this
		} else if (request.getHeader("auth") != null) {
			headers = true;
			token = request.getHeader("auth").toString();
		} else if (request.getAttribute("auth") != null) {

			token = request.getAttribute("auth").toString();
		}
		if (token == null) {
			response = Tune.error(response, "There isn't any token ",
					"pls allow cookies, it's just one with your personal jwt token");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		if (request.getAttribute("name") == null) {
			response = Tune.error(response, "There isn't any token ",
					"pls allow cookies, it's just one with your personal jwt token");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		if (jwt.isValid(token, (String) request.getAttribute("name"))) {
			String color = jwt.getClaimsFromToken(token, (String) request.getAttribute("name"));

			response = Tune.response(response, token, headers, cookie, httpOnly);
			request.setAttribute("colorFromJwt", color);
			if (request.getAttribute("toDocs") != null) {
				System.out.println("WEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
				getServletContext().getRequestDispatcher("/WEB-INF/doc/index.html");
			}

			getServletContext().getRequestDispatcher("/WEB-INF/hello.jsp").include(request, response);
			return;
		} else {
			response = Tune.error(response, "No cabron ",
					"something went bad validating your token.. could be you (expired token, not your token.. or we lost same keys. Just sign in again");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
	}

	private Cookie getJwtCookie(Cookie[] cookies) {
		for (Cookie co : cookies) {
			if (co.getName().equals("myJwtCookie"))
				return co;
		}
		return null;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("DO get HELLOSERVLET");
		doPost(request, response);
	}
}
