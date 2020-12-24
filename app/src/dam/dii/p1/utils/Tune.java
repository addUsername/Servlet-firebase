package dam.dii.p1.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Tune {

	public static HttpServletResponse response(HttpServletResponse response, String token, boolean header, boolean cook,
			boolean httpOnly) {

		if (header) {
			response.setHeader("auth", token);
		}

		if (cook) {
			Cookie cookie = new Cookie("myJwtCookie", token);
			if (httpOnly) {
				cookie.setPath("/; HttpOnly");
				cookie.setSecure(true);
			} else {
				cookie.setPath("/");
			}
			cookie.setMaxAge(60);
			response.addCookie(cookie);
		}
		return response;
	}

	public static HttpServletRequest request(HttpServletRequest request, String username, String token, boolean signed,
			boolean attribute) {

		request.setAttribute("name", username);
		request.setAttribute("signed", signed);
		if (attribute) {
			request.setAttribute("jwt", token);
		}
		return request;
	}

	public static HttpServletResponse error(HttpServletResponse response, String title, String error) {
		response.setHeader("CustomErrorTitle", title);
		response.setHeader("CustomErrorString", error);
		return response;
	}
}
