package dam.dii.p1;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import dam.dii.p1.services.Secrets;
import dam.dii.p1.utils.FileUtils;

@WebListener
public class Application implements ServletContextListener {

	private byte[] magicKey = "ohmygooooooorrod".getBytes();

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("DESTROYED");

	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		File encFile = FileUtils.loadFileAsResource("fileKeys.dat", false);
		File fileoutput = FileUtils.loadFileAsResource("fileoutput.dat", true);

		HashMap<String, Object> keys = null;
		if (encFile.exists()) {
			try {
				Secrets.decrypt(magicKey, encFile, fileoutput);
				keys = Secrets.readKeyDat(fileoutput);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("FILE NO EXIISST");
		}

		ServletContext context = arg0.getServletContext();
		context.addServlet("myInstanciatedServlet", new MyServlet(keys)).addMapping("/");
		// context.addServlet("myHelloInstanciated", new HelloServlet((byte[])
		// keys.get("jwtSecrety"))).addMapping("/hello");
		context.addServlet("myHelloInstanciated", new HelloServlet((byte[]) keys.get("jwtSecrety")))
				.addMapping("/auth");

	}

}
