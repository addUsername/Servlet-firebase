package dam.dii.p1;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.client.RestTemplate;

import dam.dii.p1.entities.Usuario;

/**
 * Servlet implementation class MyServlet
 */
@WebServlet("/")
public class MyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MyServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Usuario usuario = new Usuario();
		usuario.setName(request.getParameter("user"));
		usuario.setPass(request.getParameter("pass"));
		usuario.setPass2(request.getParameter("pass2"));
		//response.sendRedirect("index.jsp");
		
		if(request.getParameter("newUser") == null) {
			// user that already exists
			if(usuario.getPass().equals(usuario.getPass2())) {
				request.setAttribute("name", usuario.getName());
				this.getServletContext().getRequestDispatcher("/WEB-INF/hello.jsp").forward(request, response);
			}else {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			}
		}else {
			// Nuevo usuario
		}
		
	}

}
