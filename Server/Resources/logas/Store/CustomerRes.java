package logas.Store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
import facades.CustomerFacade;
import system.CouponSystem;
import utilities.Utilities;

/**
 * Serves for the requests from Customer type of users.
 * 
 * @author AlexanderZhilokov
 *
 */
@Path("Customer")
public class CustomerRes {

	/**
	 * Stores a reference to the data of the current request.
	 */
	@Context
	private HttpServletRequest request;

	/**
	 * Marks this customer as the owner of the coupon which id corresponds to the
	 * given coupon id value.
	 * 
	 * @param coupon
	 *            - a coupon which id corresponds to the id value of the coupon (as
	 *            it stored in a database) which meant to be bought.
	 * @return a collection of object which consists from only one element - it is a
	 *         log about executed operation.
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("purchaseCoupon")
	public Collection<Object> purchaseCoupon(Coupon coupon) {
		Collection<Object> response = new ArrayList<>();
		Logger logger = Utilities.getLogger();
		try {
			customer().purchaseCoupon(coupon);
			Log log = customer().onlooker.getLog();
			logger.info("At at CustomerRes.java (purchaseCoupon) by: " + request.getSession().getId() + "\n"
					+ log.toString());
			response.add(log);
			return response;
		} catch (PossibleDBCorruptionException e) {
			// transaction failed and rollback failed as well.
			CouponSystem.getInstance().shutdownPatiently(10000);
			logger.log(Level.SEVERE,
					"Exception at CustomerRes.java (purchaseCoupon) by: " + request.getSession().getId(), e);
			response.add(customer().onlooker.getLog());
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE,
					"Exception at CustomerRes.java (purchaseCoupon) by: " + request.getSession().getId(), e);
			response.add(customer().onlooker.getLog());
			return response;
		}
	}

	/**
	 * Returns all the coupons from the database which were bought by this customer.
	 * 
	 * @return a collection of object: it first element is a log about executed
	 *         operation and rest are a coupons from the database. If by any reason
	 *         operation was not succeeded - it consists from the log only.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getAllPurchasedCoupons")
	public Collection<Object> getAllPurchasedCoupons() {
		Collection<Object> response = new ArrayList<>();
		Logger logger = Utilities.getLogger();
		try {
			Collection<Coupon> coupons = customer().getAllPurchasedCoupons();
			Log log = customer().onlooker.getLog();
			logger.info("At at CustomerRes.java (getAllPurchasedCoupons) by: " + request.getSession().getId() + "\n"
					+ log.toString());
			response.add(log);
			response.addAll(coupons);
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE,
					"Exception at CustomerRes.java (getAllPurchasedCoupons) by: " + request.getSession().getId(), e);
			response.add(customer().onlooker.getLog());
			return response;
		}
	}

	/**
	 * Returns all the coupons from the database.
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
			Collection<Coupon> coupons = customer().getAllCoupons();
			Log log = customer().onlooker.getLog();
			logger.info("At at CustomerRes.java (getAllCoupons) by: " + request.getSession().getId() + "\n"
					+ log.toString());
			response.add(log);
			response.addAll(coupons);
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE,
					"Exception at CustomerRes.java (getAllCoupons) by: " + request.getSession().getId(), e);
			response.add(customer().onlooker.getLog());
			return response;
		}
	}

	/**
	 * Returns all the coupons from the database which type is corresponds to the
	 * given value and which were bought by this customer early.
	 * 
	 * @param type
	 *            - a value which corresponds to one of the values of enum
	 *            CouponType.
	 * @return a collection of object: it first element is a log about executed
	 *         operation and rest are a coupons from the database which types are
	 *         equals to the given type value and which were bought by this customer
	 *         early. If by any reason operation was not succeeded - it consists
	 *         from the log only.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getAllPurchasedCouponsByType/{type}")
	public Collection<Object> getAllPurchasedCouponsByType(@PathParam("type") String type) {
		Collection<Object> response = new ArrayList<>();
		Logger logger = Utilities.getLogger();
		try {
			Collection<Coupon> coupons = customer().getAllPurchasedCouponsByType(CouponType.valueOf(type));
			Log log = customer().onlooker.getLog();
			logger.info("At at CustomerRes.java (getAllPurchasedCouponsByType) by: " + request.getSession().getId()
					+ "\n" + log.toString());
			response.add(log);
			response.addAll(coupons);
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE,
					"Exception at CustomerRes.java (getAllPurchasedCouponsByType) by: " + request.getSession().getId(),
					e);
			response.add(customer().onlooker.getLog());
			return response;
		}
	}

	/**
	 * Returns all the coupons from the database which prices are equals or lower to
	 * the given value and which were bought by this customer early.
	 * 
	 * @param price
	 *            - a price value which uses as a cutoff value for returning
	 *            coupons.
	 * @return a collection of object: it first element is a log about executed
	 *         operation and rest are a coupons from the database which prices are
	 *         equals or lower to the given value and which were bought by this
	 *         customer early. If by any reason operation was not succeeded - it
	 *         consists from the log only.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getAllPurchasedCouponsByPrice/{price}")
	public Collection<Object> getAllPurchasedCouponsByPrice(@PathParam("price") double price) {
		Collection<Object> response = new ArrayList<>();
		Logger logger = Utilities.getLogger();
		try {
			Collection<Coupon> coupons = customer().getAllPurchasedCouponsByPrice(price);
			Log log = customer().onlooker.getLog();
			logger.info("At at CustomerRes.java (getAllPurchasedCouponsByPrice) by: " + request.getSession().getId()
					+ "\n" + log.toString());
			response.add(log);
			response.addAll(coupons);
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE,
					"Exception at CustomerRes.java (getAllPurchasedCouponsByPrice) by: " + request.getSession().getId(),
					e);
			response.add(customer().onlooker.getLog());
			return response;
		}
	}

	/**
	 * Returns a customer facade from the current session.
	 * 
	 * @return an instance of the CustomerFacade class.
	 */
	private CustomerFacade customer() {
		return (CustomerFacade) request.getSession().getAttribute("Customer");
	}

}