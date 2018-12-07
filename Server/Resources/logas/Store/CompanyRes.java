package logas.Store;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import beans.Coupon;
import beans.CouponType;
import beans.Log;
import exceptions.PossibleDBCorruptionException;
import facades.CompanyFacade;
import system.CouponSystem;
import utilities.Utilities;

/**
 * Serves for the requests from Company type of users.
 * 
 * @author AlexanderZhilokov
 *
 */
@Path("Company")
public class CompanyRes {

	/**
	 * Stores a reference to the data of the current request.
	 */
	@Context
	private HttpServletRequest request;

	/**
	 * Stores a new coupon in a database.
	 * 
	 * @param coupon
	 *            - a coupon with all it values which meant to be stored in a
	 *            database.
	 * @return a collection of object which consists from only one element - it is a
	 *         log about executed operation.
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("createCoupon")
	public Collection<Object> createCoupon(Coupon coupon) {
		Collection<Object> response = new ArrayList<>();
		Logger logger = Utilities.getLogger();
		try {
			company().createCoupon(coupon);
			Log log = company().onlooker.getLog();
			logger.info(
					"At at CompanyRes.java (createCoupon) by: " + request.getSession().getId() + "\n" + log.toString());
			response.add(log);
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "Exception at CompanyRes.java (createCoupon) by: " + request.getSession().getId(),
					e);
			response.add(company().onlooker.getLog());
			return response;
		}
	}

	/**
	 * Returns a coupon which id is equals to the given value of id.
	 * 
	 * @param id
	 *            - value which corresponds to the coupon's id as it stored in a
	 *            database
	 * @return a collection of object: it first element is a log about executed
	 *         operation and second is a coupon which id corresponds to the given
	 *         parameter and with all it values from the database. If by any reason
	 *         operation was not succeeded - second element of returning collection
	 *         is null.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getCoupon/{id}")
	public Collection<Object> getCoupon(@PathParam("id") int id) {
		Collection<Object> response = new ArrayList<>();
		Logger logger = Utilities.getLogger();
		try {
			Coupon coupon = company().getCoupon(id);
			Log log = company().onlooker.getLog();
			logger.info(
					"At at CompanyRes.java (getCoupon) by: " + request.getSession().getId() + "\n" + log.toString());
			response.add(log);
			response.add(coupon);
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "Exception at CompanyRes.java (getCoupon) by: " + request.getSession().getId(), e);
			response.add(company().onlooker.getLog());
			return response;
		}
	}

	/**
	 * Returns a coupon which title is equals to the given value.
	 * 
	 * @param title
	 *            - value which corresponds to the coupon's title as it stored in a
	 *            database
	 * @return a collection of object: it first element is a log about executed
	 *         operation and second is a coupon which title corresponds to the given
	 *         parameter and with all it values from the database. If by any reason
	 *         operation was not succeeded - second element of returning collection
	 *         is null.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getCouponByTitle/{title}")
	public Collection<Object> getCouponByTitle(@PathParam("title") String title) {
		Collection<Object> response = new ArrayList<>();
		Logger logger = Utilities.getLogger();
		try {
			Coupon coupon = company().getCoupon(title);
			Log log = company().onlooker.getLog();
			logger.info("At at CompanyRes.java (getCouponByTitle) by: " + request.getSession().getId() + "\n"
					+ log.toString());
			response.add(log);
			response.add(coupon);
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE,
					"Exception at CompanyRes.java (getCouponByTitle) by: " + request.getSession().getId(), e);
			response.add(company().onlooker.getLog());
			return response;
		}
	}

	/**
	 * Updates a coupon in a database which id is equals to the value of id of the
	 * given coupon.
	 * 
	 * @param coupon
	 *            - a coupon with all it values which should be updated and which id
	 *            corresponds to the coupon's id as it stored in a database
	 * @return a collection of object which consists from only one element - it is a
	 *         log about executed operation.
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("updateCoupon")
	public Collection<Object> updateCoupon(Coupon coupon) {
		Collection<Object> response = new ArrayList<>();
		Logger logger = Utilities.getLogger();
		try {
			Date date = coupon.getStartDate();
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			coupon.setStartDate(cal.getTime());
			company().updateCoupon(coupon);
			Log log = company().onlooker.getLog();
			logger.info(
					"At at CompanyRes.java (updateCoupon) by: " + request.getSession().getId() + "\n" + log.toString());
			response.add(log);
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "Exception at CompanyRes.java (updateCoupon) by: " + request.getSession().getId(),
					e);
			response.add(company().onlooker.getLog());
			return response;
		}
	}

	/**
	 * Deletes a coupon in a database which id is equals to the given value of id.
	 * 
	 * @param coupon
	 *            - a coupon which should be deleted and which id corresponds to the
	 *            coupon's id as it stored in a database
	 * @return a collection of object which consists from only one element - it is a
	 *         log about executed operation.
	 */
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("removeCoupon")
	public Collection<Object> removeCoupon(Coupon coupon) {
		Collection<Object> response = new ArrayList<>();
		Logger logger = Utilities.getLogger();
		try {
			company().removeCoupon(coupon);
			Log log = company().onlooker.getLog();
			logger.info(
					"At at CompanyRes.java (removeCoupon) by: " + request.getSession().getId() + "\n" + log.toString());
			response.add(log);
			return response;
		} catch (PossibleDBCorruptionException e) {
			// transaction failed and rollback failed as well.
			CouponSystem.getInstance().shutdownPatiently(10000);
			logger.log(Level.SEVERE, "Exception at CompanyRes.java (removeCoupon) by: " + request.getSession().getId(),
					e);
			response.add(company().onlooker.getLog());
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "Exception at CompanyRes.java (removeCoupon) by: " + request.getSession().getId(),
					e);
			response.add(company().onlooker.getLog());
			return response;
		}
	}

	/**
	 * Returns all the coupons from the database which were created by this company.
	 * 
	 * @return a collection of object: it first element is a log about executed
	 *         operation and rest are a coupons from the database. If by any reason
	 *         operation was not succeeded - it consists from the log only.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getAllCoupons")
	public Collection<Object> getAllCoupons() {
		Collection<Object> response = new ArrayList<>();
		Logger logger = Utilities.getLogger();
		try {
			Collection<Coupon> coupons = company().getAllCoupons();
			Log log = company().onlooker.getLog();
			logger.info("At at CompanyRes.java (getAllCoupons) by: " + request.getSession().getId() + "\n"
					+ log.toString());
			response.add(log);
			response.addAll(coupons);
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "Exception at CompanyRes.java (getAllCoupons) by: " + request.getSession().getId(),
					e);
			response.add(company().onlooker.getLog());
			return response;
		}
	}

	/**
	 * Returns all the coupons from the database which type is corresponds to the
	 * given value and which were created by this company early.
	 * 
	 * @param type
	 *            - a value which corresponds to one of the values of enum
	 *            CouponType.
	 * @return a collection of object: it first element is a log about executed
	 *         operation and rest are a coupons from the database which types are
	 *         equals to the given type value and which were created by this company
	 *         early. If by any reason operation was not succeeded - it consists
	 *         from the log only.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getCouponsByType/{type}")
	public Collection<Object> getCouponsByType(@PathParam("type") String type) {
		Collection<Object> response = new ArrayList<>();
		Logger logger = Utilities.getLogger();
		try {
			Collection<Coupon> coupons = company().getCouponsByType(CouponType.valueOf(type));
			Log log = company().onlooker.getLog();
			logger.info("At at CompanyRes.java (getCouponsByType) by: " + request.getSession().getId() + "\n"
					+ log.toString());
			response.add(log);
			response.addAll(coupons);
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE,
					"Exception at CompanyRes.java (getCouponsByType) by: " + request.getSession().getId(), e);
			response.add(company().onlooker.getLog());
			return response;
		}
	}

	/**
	 * Returns all the coupons from the database which prices are equals or lower to
	 * the given value and which were created by this company early.
	 * 
	 * @param price
	 *            - a price value which uses as a cutoff value for returning
	 *            coupons.
	 * @return a collection of object: it first element is a log about executed
	 *         operation and rest are a coupons from the database which prices are
	 *         equals or lower to the given value and which were created by this
	 *         company early. If by any reason operation was not succeeded - it
	 *         consists from the log only.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getCouponsByPrice/{price}")
	public Collection<Object> getCouponsByPrice(@PathParam("price") double price) {
		Collection<Object> response = new ArrayList<>();
		Logger logger = Utilities.getLogger();
		try {
			Collection<Coupon> coupons = company().getCouponsByPrice(price);
			Log log = company().onlooker.getLog();
			logger.info("At at CompanyRes.java (getCouponsByPrice) by: " + request.getSession().getId() + "\n"
					+ log.toString());
			response.add(log);
			response.addAll(coupons);
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE,
					"Exception at CompanyRes.java (getCouponsByPrice) by: " + request.getSession().getId(), e);
			response.add(company().onlooker.getLog());
			return response;
		}
	}

	/**
	 * Returns all the coupons from the database which end dates are equals or lower
	 * to the given value and which were created by this company early.
	 * 
	 * @param timestamp
	 *            - a timestamp value for the date which uses as a cutoff value for
	 *            returning coupons.
	 * @return a collection of object: it first element is a log about executed
	 *         operation and rest are a coupons from the database which dates are
	 *         equals or lower to the given value and which were created by this
	 *         company early. If by any reason operation was not succeeded - it
	 *         consists from the log only.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getCouponsByDate/{timestamp}")
	public Collection<Object> getCouponsByDate(@PathParam("timestamp") long timestamp) {
		Collection<Object> response = new ArrayList<>();
		Logger logger = Utilities.getLogger();
		try {
			Collection<Coupon> coupons = company().getCouponsByDate(new Date(timestamp));
			Log log = company().onlooker.getLog();
			logger.info("At at CompanyRes.java (getCouponsByDate) by: " + request.getSession().getId() + "\n"
					+ log.toString());
			response.add(log);
			response.addAll(coupons);
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE,
					"Exception at CompanyRes.java (getCouponsByDate) by: " + request.getSession().getId(), e);
			response.add(company().onlooker.getLog());
			return response;
		}
	}

	/**
	 * Returns a company facade from the current session.
	 * 
	 * @return an instance of the CompanyFacade class.
	 */
	private CompanyFacade company() {
		return (CompanyFacade) request.getSession().getAttribute("Company");
	}

}