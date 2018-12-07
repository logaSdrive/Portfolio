package myServlets;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import facades.ClientCouponFacade;
import facades.ClientType;
import system.CouponSystem;
import utilities.Utilities;

/**
 * Uses as entry points for all unauthorized users and provides a paths to the
 * pages for the different type of users according their name, password and
 * types (which will be checked).
 * 
 * @author AlexanderZhilokov
 *
 */
public class Login extends HttpServlet {

	/**
	 * Stores the serial number of the current version of the class.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Stores the life period for cookies.
	 */
	private static final int MONTH = 60 * 60 * 24 * 30;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Login() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.sendRedirect(request.getContextPath() + "/login.html");
	}

	/**
	 * Checks the request parameters and if it's name, password and type parameter
	 * retrieves an instance of the ClientCouponFacade from the CouponSystem - it
	 * saves cookies (about this parameters) and redirects users to the
	 * corresponding page. If not - redirect to the loggin page again.
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Logger logger = Utilities.getLogger();
		String name = request.getParameter("name");
		String password = request.getParameter("password");
		ClientType type = ClientType.valueOf(request.getParameter("type"));
		if (type == ClientType.Admin && password.equals("11111")) {
			type = ClientType.SuperAdmin;
			name = "super-admin";
		}
		ClientCouponFacade facade;
		try {
			facade = CouponSystem.getInstance().login(name, password, type);
			if (facade != null) {
				switch (type) {
				case SuperAdmin:
					request.getSession().setAttribute("SuperAdmin", facade);
					bakeCookies(response, name, password, type);
					logger.info("At login.java by: " + request.getSession().getId() + "\n" + name
							+ " logged as SuperAdmin.");
					response.sendRedirect(request.getContextPath() + "/superAdmin/super-admin.html");
					break;
				case Admin:
					request.getSession().setAttribute("Admin", facade);
					bakeCookies(response, name, password, type);
					logger.info(
							"At login.java by: " + request.getSession().getId() + "\n" + name + " logged as Admin.");
					response.sendRedirect(request.getContextPath() + "/admin/admin.html");
					break;
				case Company:
					request.getSession().setAttribute("Company", facade);
					bakeCookies(response, name, password, type);
					logger.info(
							"At login.java by: " + request.getSession().getId() + "\n" + name + " logged as Company.");
					response.sendRedirect(request.getContextPath() + "/company/company.html");
					break;
				case Customer:
					request.getSession().setAttribute("Customer", facade);
					bakeCookies(response, name, password, type);
					logger.info(
							"At login.java by: " + request.getSession().getId() + "\n" + name + " logged as Customer.");
					response.sendRedirect(request.getContextPath() + "/customer/customer.html");
					break;
				default:
					break;
				}
			} else {
				response.sendRedirect(request.getContextPath() + "/login.html");
			}
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "Exception at Login.java by: " + request.getSession().getId(), e);
			response.getWriter().append(e.getMessage());
		}
	}

	/**
	 * Save the cookies to the users browser.
	 * 
	 * @param response
	 *            - reference to the current response data.
	 * @param name
	 *            - name of the user.
	 * @param password
	 *            - password of the user.
	 * @param type
	 *            - type of the user.
	 */
	private void bakeCookies(HttpServletResponse response, String name, String password, ClientType type) {
		Cookie nameCookie = new Cookie(type.name() + ".name", name);
		Cookie pswdCookie = new Cookie(type.name() + ".pswd", password);
		nameCookie.setMaxAge(MONTH);
		pswdCookie.setMaxAge(MONTH);
		response.addCookie(nameCookie);
		response.addCookie(pswdCookie);
	}

}