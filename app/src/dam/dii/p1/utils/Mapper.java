package dam.dii.p1.utils;

import dam.dii.p1.entities.Usuario;

public class Mapper {

	public static String usuarioToJson(Usuario user) {
		return "{\"name\":\"" + user.getName() + "\",\"pass\":\"" + user.getPass() + "\"}";
	}

	public static Usuario jsonToUsuario(String json) {

		String[] pair = json.split("\"");
		Usuario user = new Usuario();
		if (pair.length > 6) {
			user.setName(pair[3]);
			user.setPass(pair[7]);
		} else {
			user.setName(null);
			user.setPass(null);
		}
		return user;
	}
}
