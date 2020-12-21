package dam.dii.p1.entities;

public class Usuario {

	private String name;
	private String pass;
	private String pass2;
	
	public Usuario() {}
	public Usuario(String name, String pass, String pass2) {
		super();
		this.name = name;
		this.pass = pass;
		this.pass2 = pass2;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
	public String getPass2() {
		return pass2;
	}
	public void setPass2(String pass2) {
		this.pass2 = pass2;
	}	
	
}
