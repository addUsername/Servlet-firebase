package dam.dii.p1;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.crypto.SecretKey;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dam.dii.p1.entities.Usuario;
import dam.dii.p1.security.Crypt;
import dam.dii.p1.security.JwtHandler;
import dam.dii.p1.services.Persistence;

/**
 * Servlet implementation class MyServlet
 */
@WebServlet("/")
public class MyServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Persistence per;
	private Crypt cry;
	private JwtHandler jwt;
	private final byte[] jwtSecret = "more_secrets".getBytes();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MyServlet() {
		super();
		per = new Persistence("mySecret", "https://my-awesome-test-1c310-default-rtdb.firebaseio.com/");

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 *
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		File file = new File(getServletContext().getRealPath("key.dat"));
		jwt = new JwtHandler(jwtSecret);
		if (!file.exists()) {
			SecretKey sc = Crypt.generateKey(getServletContext().getRealPath("key.dat"));
			cry = new Crypt(sc);
		} else {
			cry = new Crypt(getServletContext().getRealPath("key.dat"));
		}

		Usuario usuario = new Usuario();
		usuario.setName(request.getParameter("user"));
		usuario.setPass(cry.encode(request.getParameter("pass")));
		usuario.setPass2(cry.encode(request.getParameter("pass2")));
		// response.sendRedirect("index.jsp");

		if (request.getParameter("newUser") == null) {
			// user that already exists
			Usuario storedUser = per.findByUsuario(usuario);

			if (Arrays.equals(cry.decode(usuario.getPass()), cry.decode(storedUser.getPass()))) {
				// LOGGED
				request.setAttribute("name", usuario.getName());
				request.setAttribute("signed", false);
				request.setAttribute("jwt", jwt.generateToken(usuario));
				response.setHeader("auth", jwt.generateToken(usuario));
				getServletContext().getRequestDispatcher("/hello").forward(request, response);
			} else {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} else {
			if (Arrays.equals(cry.decode(usuario.getPass()), cry.decode(usuario.getPass2()))) {
				// Nuevo usuario
				boolean signed = per.save(usuario);
				request.setAttribute("signed", signed);
				// this header should invoque and alert saying what happened
				request.setAttribute("name", usuario.getName());
				request.setAttribute("jwt", jwt.generateToken(usuario));
				response.setHeader("auth", jwt.generateToken(usuario));
				getServletContext().getRequestDispatcher("/WEB-INF/hello.jsp").forward(request, response);
			}
		}
	}
}
