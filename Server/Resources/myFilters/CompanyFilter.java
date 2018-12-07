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
import facades.CompanyFacade;
import utilities.Utilities;

/**
 * Filters all requests on the company path (a directory with css, html, js of
 * the company page).
 * 
 * @author AlexanderZhilokov
 *
 */
public class CompanyFilter implements Filter {
	/**
	 * Default constructor.
	 */
	public CompanyFilter() {
	}

	/**
	 * @see Filter#destroy()
	 */
	@Override
	public void destroy() {
	}

	/**
	 * Checks if there is an CompanyFacade stored in a current session. If not it
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
			Object facade = httpRequest.getSession().getAttribute("Company");
			if (facade == null || !(facade instanceof CompanyFacade)) {
				logger.fine("At CompanyFilter.java by: " + httpRequest.getSession().getId()
						+ "\nTrying to restore CompanyFacade.");
				ClientCouponFacade company = Utilities.restoreFacade("Company", httpRequest.getCookies());
				if (company == null) {
					logger.fine("At CompanyFilter.java by: " + httpRequest.getSession().getId()
							+ "\nFailed to restore CompanyFacade.");
					httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.html");
					return;
				} else {
					logger.fine("At CompanyFilter.java by: " + httpRequest.getSession().getId()
							+ "\nSucceed to restore CompanyFacade.");
					httpRequest.getSession().setAttribute("Company", company);
				}
			}
			chain.doFilter(request, response);
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "Exception at CompanyFilter.java by: " + httpRequest.getSession().getId(), e);
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	@Override
	public void init(FilterConfig fConfig) throws ServletException {
	}

}