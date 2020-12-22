package dam.dii.p1.services;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import dam.dii.p1.entities.Usuario;
import dam.dii.p1.utils.Mapper;

public class Persistence {

	private final String SECRET; // env var
	private final String URI; //https://[...].firebaseio.com/
	private final String PRE = "rest/users";
	private final String EXT = ".json";
	
	private RestTemplate rt;
	
	public Persistence(String secret, String uri) {
		
		this.SECRET = secret;
		this.URI = uri;
		this.rt = new RestTemplate();
	}
	
	public Usuario findByUsuario(Usuario user) {
		
		String url = this.URI + this.PRE + "/" +user.getName() + this.EXT;
		Usuario toReturn = Mapper.jsonToUsuario(rt.getForObject(url, String.class));		
		return toReturn;
	}
	
	public boolean save(Usuario user) {
		
		// i know..		
		String url = this.URI + this.PRE + "/" +user.getName() + this.EXT;
		System.out.println(url);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		System.out.println("USER TO JSON");
		System.out.println(Mapper.usuarioToJson(user));
		
		HttpEntity<String> entity = new HttpEntity<String>(Mapper.usuarioToJson(user),headers);
		
		//String answer = rt.postForObject(url, entity, String.class);
		rt.put(url, entity);
		return true;
	}
	
}
