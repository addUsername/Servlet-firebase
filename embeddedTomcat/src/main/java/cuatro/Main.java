package cuatro;

import java.io.File;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

public class Main {

	public static void main(String[] args) {

		String contextPath = "/";
		String warFilePath = "app2.war";

		File file = new File(warFilePath);
		File path = new File(".");

		Tomcat tomcat = new Tomcat();
		tomcat.setPort(8080);
		tomcat.getHost().setAppBase(".");
		tomcat.addWebapp(contextPath, warFilePath);

		try {
			tomcat.start();
		} catch (LifecycleException e) {
			e.printStackTrace();
		}
		tomcat.getServer().await();
	}

}