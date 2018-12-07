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
import facades.CustomerFacade;
import utilities.Utilities;

/**
 * Filters all requests on the customer path (a directory with css, html, js of
 * the customer page).
 * 
 * @author AlexanderZhilokov
 *
 */
public class CustomerFilter implements Filter {

	/**
	 * Default constructor.
	 */
	public CustomerFilter() {
	}

	/**
	 * @see Filter#destroy()
	 */
	@Override
	public void destroy() {
	}

	/**
	 * Checks if there is an CustomerFacade stored in a current session. If not it
	 * will try to restored it uses cookies data and if than yet not succeed it will
	 * redirect the user to the login page.
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		Logger logger = Utilities.getLogger();
		try {
			Object facade = httpRequest.getSession().getAttribute("Customer");
			if (facade == null || !(facade instanceof CustomerFacade)) {
				logger.fine("At CustomerFilter.java by: " + httpRequest.getSession().getId()
						+ "\nTrying to restore CustomerFacade.");
				ClientCouponFacade customer = Utilities.restoreFacade("Customer", httpRequest.getCookies());
				if (customer == null) {
					logger.fine("At CustomerFilter.java by: " + httpRequest.getSession().getId()
							+ "\nFailed to restore CustomerFacade.");
					httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.html");
					return;
				} else {
					logger.fine("At CustomerFilter.java by: " + httpRequest.getSession().getId()
							+ "\nSucceed to restore CustomerFacade.");
					httpRequest.getSession().setAttribute("Customer", customer);
				}
			}
			chain.doFilter(request, response);
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "Exception at CustomerFilter.java by: " + httpRequest.getSession().getId(), e);
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	@Override
	public void init(FilterConfig fConfig) throws ServletException {
	}

}