package logas.Store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import beans.Log;
import utilities.Utilities;

/**
 * Serves for the common requests from all type of users.
 * 
 * @author AlexanderZhilokov
 *
 */
@Path("Common")
public class CommonRes {

	/**
	 * Stores a reference to the data of the current request.
	 */
	@Context
	private HttpServletRequest request;

	/**
	 * Stores a reference to the data of the current response.
	 */
	@Context
	private HttpServletResponse response;

	/**
	 * Returns a log which says to the users that requested operation is refused and
	 * he should login.
	 * 
	 * @return a collection of object which consists from only one element - it is a
	 *         log that says current operation was refused because of the missing
	 *         credentials.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("ofSession")
	public Collection<Object> ofSessionGET() {
		return callForLoggin();
	}

	/**
	 * Returns a log which says to the users that requested operation is refused and
	 * he should login.
	 * 
	 * @return a collection of object which consists from only one element - it is a
	 *         log that says current operation was refused because of the missing
	 *         credentials.
	 */
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("ofSession")
	public Collection<Object> ofSessionDELETE() {
		return callForLoggin();
	}

	/**
	 * Returns a log which says to the users that requested operation is refused and
	 * he should login.
	 * 
	 * @return a collection of object which consists from only one element - it is a
	 *         log that says current operation was refused because of the missing
	 *         credentials.
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("ofSession")
	public Collection<Object> ofSessionPOST() {
		return callForLoggin();
	}

	/**
	 * Returns a log which says to the users that requested operation is refused and
	 * he should login.
	 * 
	 * @return a collection of object which consists from only one element - it is a
	 *         log that says current operation was refused because of the missing
	 *         credentials.
	 */
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Path("ofSession")
	public Collection<Object> ofSessionPUT() {
		return callForLoggin();
	}

	/**
	 * Removes the cookies and session data of the user which associated with the
	 * given type of client (as described in the enum ClientType).
	 * 
	 * @param type
	 *            - specifies from which type of client data should be removed (it
	 *            should be a String which equals to the name of one of the values
	 *            of enum ClientType).
	 */
	@GET
	@Path("logout/{type}")
	public void logout(@PathParam("type") String type) {
		String name = null;
		Logger logger = Utilities.getLogger();
		try {
			if (type != null) {
				Cookie[] cookies = request.getCookies();
				if (cookies != null) {
					int shot = -1;
					for (Cookie cookie : cookies) {
						name = cookie.getName();
						shot = name.indexOf('.');
						if (shot != -1) {
							if (type.equals(name.substring(0, shot))) {
								cookie.setValue("");
								cookie.setPath("/Store");
								cookie.setMaxAge(0);
								response.addCookie(cookie);
							}
						}
					}
				}
			}
			request.getSession().removeAttribute(type);
			logger.info("At CommonRes.java (logout) by: " + request.getSession().getId() + "\nLogged out from " + type
					+ ".facade");
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "Exception at CommonRes.java (logout) by: " + request.getSession().getId()
					+ "\n(type: " + type + ".facade)", e);
		}
	}

	/**
	 * Returns a log which says to the users that requested operation is refused and
	 * he should login.
	 * 
	 * @return a collection of object which consists from only one element - it is a
	 *         log that says current operation was refused because of the missing
	 *         credentials.
	 */
	private Collection<Object> callForLoggin() {
		Logger logger = Utilities.getLogger();
		try {
			Log log = new Log();
			log.setStatus("FAILURE");
			log.setHeader("Please get logged!");
			log.setMessage(
					"Missing credentials. Means you should login (Chose logout option in the menu. Or if you want to keep this window, you can get logged in a new tab (browser tab)"
							+ " and continue with current tab after).");
			log.setSignature(
					"signature: the operation was not send to the execution (means it has not specified signature value).");
			Collection<Object> response = new ArrayList<>();
			response.add(log);
			logger.info(
					"At CommonRes.java (callForLoggin) by: " + request.getSession().getId() + "\n" + log.toString());
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "Exception at CommonRes.java (callForLoggin) by: " + request.getSession().getId(),
					e);
			return null;
		}
	}

}