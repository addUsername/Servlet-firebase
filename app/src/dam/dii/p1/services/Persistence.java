package dam.dii.p1.services;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import dam.dii.p1.entities.Usuario;
import dam.dii.p1.utils.Mapper;

/**
 * We use {@link RestController} to call firebase api rest, spring-core,
 * spring-web, spring-beans and commons-logging are used..
 * 
 * @author SERGI
 *
 */
public class Persistence {

	private final String SECRET; // TODO implement
	private final String URI; // https://[...].firebaseio.com/
	private final String PRE = "rest/users";
	private final String EXT = ".json";

	private final RestTemplate rt;

	/**
	 * @param secret This will be the pass to firebase database
	 * @param uri    This is the uri to firebase
	 */
	public Persistence(String secret, String uri) {

		SECRET = secret;
		URI = uri;
		rt = new RestTemplate();
	}

	/**
	 * Just a Get to rest/users/{username} return an user initialized as null
	 * 
	 * @param user
	 * @return
	 */
	public Usuario findByUsuario(Usuario user) {

		String url = URI + PRE + "/" + user.getName() + EXT;
		Usuario toReturn = Mapper.jsonToUsuario(rt.getForObject(url, String.class));
		return toReturn;
	}

	/**
	 * A Put call bc firebase add and id y we call it with a Push verb, we use the
	 * username as key/id/node so no duplicates
	 * 
	 * @param user
	 * @return
	 */
	public boolean save(Usuario user) {

		// i know..
		String url = URI + PRE + "/" + user.getName() + EXT;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> entity = new HttpEntity<String>(Mapper.usuarioToJson(user), headers);
		rt.put(url, entity);
		return true;
	}

}
