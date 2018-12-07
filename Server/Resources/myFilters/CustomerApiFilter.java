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
 * Filters all requests to the CustomerRes.
 * 
 * @author AlexanderZhilokov
 *
 */
public class CustomerApiFilter implements Filter {

	/**
	 * Default constructor.
	 */
	public CustomerApiFilter() {
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
	}

	/**
	 * Checks if there is an CustomerFacade stored in a current session. If not it will
	 * try to restored it uses cookies data and if than yet not succeed it will
	 * redirect the request to the CommonRes. In this case full path will be:
	 * "/webapi/Common/ofSession".
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		Logger logger = Utilities.getLogger();
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		try {
			Object facade = httpRequest.getSession().getAttribute("Customer");
			if (facade == null || !(facade instanceof CustomerFacade)) {
				logger.fine("At CustomerApiFilter.java by: " + httpRequest.getSession().getId()
						+ "\nTrying to restore CustomerFacade.");
				ClientCouponFacade customer = Utilities.restoreFacade("Customer", httpRequest.getCookies());
				if (customer == null) {
					logger.fine("At CustomerApiFilter.java by: " + httpRequest.getSession().getId()
							+ "\nFailed to restore CustomerFacade.");
					httpResponse.sendRedirect(httpRequest.getContextPath() + "/webapi/Common/ofSession");
					return;
				} else {
					logger.fine("At CustomerApiFilter.java by: " + httpRequest.getSession().getId()
							+ "\nSucceed to restore CustomerFacade.");
					httpRequest.getSession().setAttribute("Customer", customer);
				}
			}
			chain.doFilter(request, response);
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "Exception at CustomerApiFilter.java by: " + httpRequest.getSession().getId(), e);
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
	}

}