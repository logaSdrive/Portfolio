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

import facades.AdminFacade;
import facades.ClientCouponFacade;
import utilities.Utilities;

/**
 * Filters all requests on the admin path (a directory with css, html, js of the
 * admin page).
 * 
 * @author AlexanderZhilokov
 *
 */
public class AdminFilter implements Filter {

	/**
	 * Default constructor.
	 */
	public AdminFilter() {
	}

	/**
	 * @see Filter#destroy()
	 */
	@Override
	public void destroy() {
	}

	/**
	 * Checks if there is an AdminFacade stored in a current session. If not it will
	 * try to restored it uses cookies data and if than yet not succeed it will
	 * redirect the user to the login page.
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		Logger logger = Utilities.getLogger();
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		try {
			Object facade = httpRequest.getSession().getAttribute("Admin");
			if (facade == null || !(facade instanceof AdminFacade)) {
				logger.fine("At AdminFilter.java by: " + httpRequest.getSession().getId()
						+ "\nTrying to restore AdminFacade.");
				ClientCouponFacade admin = Utilities.restoreFacade("Admin", httpRequest.getCookies());
				if (admin == null) {
					logger.fine("At AdminFilter.java by: " + httpRequest.getSession().getId()
							+ "\nFailed to restore AdminFacade.");
					httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.html");
					return;
				} else {
					logger.fine("At AdminFilter.java by: " + httpRequest.getSession().getId()
							+ "\nSucceed to restore AdminFacade.");
					httpRequest.getSession().setAttribute("Admin", admin);
				}
			}
			chain.doFilter(request, response);
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "Exception at AdminFilter.java by: " + httpRequest.getSession().getId(), e);
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	@Override
	public void init(FilterConfig fConfig) throws ServletException {
	}

}