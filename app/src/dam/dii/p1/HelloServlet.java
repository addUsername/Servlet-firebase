package dam.dii.p1;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dam.dii.p1.security.JwtHandler;
import dam.dii.p1.utils.Tune;

/**
 * Servlet implementation class HelloServlet
 */
@WebServlet("/hello")
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
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("DO GET HELLOSERVLET");

		Cookie myJwtCookie = getJwtCookie(request.getCookies());

		if (myJwtCookie == null || request.getAttribute("name") == null) {
			response = Tune.error(response, "There isn't any cookie ",
					"pls allow cookies, it's just one with your personal jwt token");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		if (jwt.isValid(myJwtCookie.getValue(), (String) request.getAttribute("name"))) {
			String color = jwt.getClaimsFromToken(myJwtCookie.getValue(), (String) request.getAttribute("name"));
			System.out.println("COLOOOORRRRR");
			System.out.println(color);
			request.setAttribute("colorFromJwt", color);
			getServletContext().getRequestDispatcher("/WEB-INF/hello/hello.jsp").forward(request, response);
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("DO post HELLOSERVLET");
		System.out.println(request);
		doGet(request, response);
	}
}
