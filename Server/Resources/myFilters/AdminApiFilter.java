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
 * Filters all requests to the AdminRes.
 * 
 * @author AlexanderZhilokov
 *
 */
public class AdminApiFilter implements Filter {

	/**
	 * Default constructor.
	 */
	public AdminApiFilter() {
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
	 * redirect the request to the CommonRes. In this case full path will be:
	 * "/webapi/Common/ofSession".
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		Logger logger = Utilities.getLogger();
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		try {
			Object facade = httpRequest.getSession().getAttribute("Admin");
			if (facade == null || !(facade instanceof AdminFacade)) {
				logger.fine("At AdminApiFilter.java by: " + httpRequest.getSession().getId()
						+ "\nTrying to restore AdminFacade.");
				ClientCouponFacade admin = Utilities.restoreFacade("Admin", httpRequest.getCookies());
				if (admin == null) {
					logger.fine("At AdminApiFilter.java by: " + httpRequest.getSession().getId()
							+ "\nFailed to restore AdminFacade.");
					httpResponse.sendRedirect(httpRequest.getContextPath() + "/webapi/Common/ofSession");
					return;
				} else {
					logger.fine("At AdminApiFilter.java by: " + httpRequest.getSession().getId()
							+ "\nSucceed to restore AdminFacade.");
					httpRequest.getSession().setAttribute("Admin", admin);
				}
			}
			chain.doFilter(request, response);
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "Exception at AdminApiFilter.java by: " + httpRequest.getSession().getId(), e);
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	@Override
	public void init(FilterConfig fConfig) throws ServletException {
	}

}