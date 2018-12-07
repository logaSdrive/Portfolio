package myFilters;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import facades.ClientCouponFacade;
import facades.SuperAdminFacade;
import utilities.Utilities;

/**
 * Filters all requests on the superAdmin path (a directory with css, html, js
 * of the superAdmin page).
 * 
 * @author AlexanderZhilokov
 *
 */
public class SuperAdminFilter implements Filter {

	/**
	 * Default constructor.
	 */
	public SuperAdminFilter() {
	}

	/**
	 * @see Filter#destroy()
	 */
	@Override
	public void destroy() {
	}

	/**
	 * Checks if there is an SuperAdminFacade stored in a current session. If not it
	 * will try to restored it uses cookies data and if than yet not succeed it will
	 * redirect the user to the login page.
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		Logger logger = Utilities.getLogger();
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		try {
			Object facade = httpRequest.getSession().getAttribute("SuperAdmin");
			if (facade == null || !(facade instanceof SuperAdminFacade)) {
				logger.fine("At SuperAdminFilter.java by: " + httpRequest.getSession().getId()
						+ "\nTrying to restore SuperAdminFacade.");
				ClientCouponFacade superAdmin = Utilities.restoreFacade("SuperAdmin", httpRequest.getCookies());
				if (superAdmin == null) {
					logger.fine("At SuperAdminFilter.java by: " + httpRequest.getSession().getId()
							+ "\nFailed to restore SuperAdminFacade.");
					httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.html");
					return;
				} else {
					logger.fine("At SuperAdminFilter.java by: " + httpRequest.getSession().getId()
							+ "\nSucceed to restore SuperAdminFacade.");
					httpRequest.getSession().setAttribute("SuperAdmin", superAdmin);
				}
			}
			chain.doFilter(request, response);
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "Exception at SuperAdminFilter.java by: " + httpRequest.getSession().getId(), e);
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	@Override
	public void init(FilterConfig fConfig) throws ServletException {
	}

}