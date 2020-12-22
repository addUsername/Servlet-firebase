package dam.dii.p1;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.client.RestTemplate;

import dam.dii.p1.entities.Usuario;
import dam.dii.p1.security.Crypt;
import dam.dii.p1.services.Persistence;

/**
 * Servlet implementation class MyServlet
 */
@WebServlet("/")
public class MyServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
    private Persistence per;
    private Crypt cry;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MyServlet() {
        super();        
        per = new Persistence("mySecret", "https://my-awesome-test-1c310-default-rtdb.firebaseio.com/");
            
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
		
		File file = new File(getServletContext().getRealPath("key.dat"));
		System.out.println(getServletContext().getRealPath("key.dat"));
		if(!file.exists()) {
			SecretKey sc = Crypt.generateKey(getServletContext().getRealPath("key.dat"));
			cry = new Crypt(sc);
		}else {
	        cry = new Crypt(getServletContext().getRealPath("key.dat")); 
		}
        
		Usuario usuario = new Usuario();
		usuario.setName(request.getParameter("user"));
		usuario.setPass(cry.encode(request.getParameter("pass")));
		//usuario.setPass2(cry.encode(request.getParameter("pass2")));
		//usuario.setPass(request.getParameter("pass"));
		//usuario.setPass2(request.getParameter("pass2"));
		//response.sendRedirect("index.jsp");
		
		System.out.println("PASS ENCODED check");
		System.out.println(usuario.getPass());
		
		byte[] bi = cry.decode(usuario.getPass());
		System.out.println("PASS DECODED check");
		System.out.println(new String(bi, StandardCharsets.UTF_8));
		if(true) return;
		if(request.getParameter("newUser") == null) {
			// user that already exists
			Usuario storedUser = per.findByUsuario(usuario);
			
			System.out.println("PASS from firebase");
			System.out.println(storedUser.getPass());
			System.out.println("PASS just encoded");
			System.out.println(usuario.getPass());
			
			if(usuario.getPass().equals(storedUser.getPass())) {
				//LOGGED
				
				request.setAttribute("name", usuario.getName());
				//request.setAtribute("jwt",jwtToken.generateToken(user));
				//response.setHeader("auth", jwtToken.generateToken(user));
				this.getServletContext().getRequestDispatcher("/WEB-INF/hello.jsp").forward(request, response);
			}else {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			}
		}else {
			//comparar contraseñas 1º!!
			// Nuevo usuario
			System.out.println("NEW USER");
			boolean signed = per.save(usuario);
			request.setAttribute("SIGNED",signed); //this header should invoque and alert saying that login was correct or wat
			this.getServletContext().getRequestDispatcher("/WEB-INF/hello.jsp").forward(request, response);;
		}
		
	}

}
