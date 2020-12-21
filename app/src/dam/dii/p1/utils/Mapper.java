package dam.dii.p1.utils;

import dam.dii.p1.entities.Usuario;

public class Mapper {

	public static String usuarioToJson(Usuario user) {
		return "{\"name\":\""+ user.getName() + "\",\"pass\":\"" + user.getPass() +"\"}";
	}
	public static Usuario jsonToUsuario(String json) {		

		String[] pair = json.split("\"");
		Usuario user = new Usuario();
		if(pair.length > 2) {
			user.setName(pair[0]); //mirar cual es
			user.setPass(pair[1]); //mirar que indice es
		}else {
			user.setName(null); //mirar cual es
			user.setPass(null); //mirar que indice es
		}		
		
		System.out.println(json);
		return user;
	}
}
