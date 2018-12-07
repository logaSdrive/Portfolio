package utilities;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.Cookie;

import facades.ClientCouponFacade;
import facades.ClientType;
import system.CouponSystem;

/**
 * Some useful utilities.
 * 
 * @author AlexanderZhilokov
 *
 */
public class Utilities {

	/**
	 * Stores the only instance of the logger.
	 */
	private static volatile Logger logger;

	/**
	 * Provides the reference to the single instance of the logger or creates new if
	 * there is not any. It is thread safe.
	 * 
	 * @return a reference to the single instance of java.util.logging.logger class.
	 *         It configures it to store data in a two files which located at the
	 *         root directory (c:\ or d:\): logs.troubles and logs.general. First
	 *         one - logs.troubles - consists with severe levels logs only (an
	 *         exception which arises). Second - logs.general - includes all the
	 *         logs (fine levels logs).
	 */
	public static Logger getLogger() {
		if (logger == null) {
			synchronized (Utilities.class) {
				if (logger == null) {
					logger = Logger.getLogger("logs");
					try {
						logger.setUseParentHandlers(false);
						FileHandler general = new FileHandler("/logs.general");
						FileHandler severe = new FileHandler("/logs.troubles");
						general.setLevel(Level.FINE);
						severe.setLevel(Level.SEVERE);
						logger.addHandler(general);
						logger.addHandler(severe);
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				}
			}
		}
		return logger;
	}

	/**
	 * Restores a facade from the cookies.
	 * 
	 * @param type
	 *            - a type of the facade which meant to be restored (must be equals
	 *            to one of the name values of the enum ClientType).
	 * @param cookies
	 *            - an array of cookies which will be used to retrieve name and
	 *            password for the facade restoration.
	 * @return a ClientCouponFacade instance if succeed or null if not.
	 */
	public static ClientCouponFacade restoreFacade(String type, Cookie[] cookies) {
		Logger logger = getLogger();
		if (cookies != null) {
			String name = null;
			String pswd = null;
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(type + ".name")) {
					name = cookie.getValue();
				} else if (cookie.getName().equals(type + ".pswd")) {
					pswd = cookie.getValue();
				}
			}
			if (name != null && pswd != null) {
				switch (type) {
				case "SuperAdmin":
					logger.fine(
							"At Utilities.java (restoreFacade).\nSuperAdminFacade for  was restored (by using cookies).");
					return CouponSystem.getInstance().login(name, pswd, ClientType.SuperAdmin);
				case "Admin":
					logger.fine(
							"At Utilities.java (restoreFacade).\nAdminFacade for  was restored (by using cookies).");
					return CouponSystem.getInstance().login(name, pswd, ClientType.Admin);
				case "Company":
					logger.fine(
							"At Utilities.java (restoreFacade).\nCompanyFacade for  was restored (by using cookies).");
					return CouponSystem.getInstance().login(name, pswd, ClientType.Company);
				case "Customer":
					logger.fine(
							"At Utilities.java (restoreFacade).\nCustomerFacade for  was restored (by using cookies).");
					return CouponSystem.getInstance().login(name, pswd, ClientType.Customer);
				default:
					logger.warning(
							"At Utilities.java (restoreFacade).\nUnexpecting null value as ClientType was used to restore facade (by using cookies).");
					return null;
				}
			}
		}
		return null;
	}

}