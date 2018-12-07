package facades;

import java.util.Collection;

import beans.Coupon;
import beans.CouponType;
import dao.CouponDAO;
import dao.CustomerDAO;
import daoDB.CouponDBDAO;
import daoDB.CustomerDBDAO;
import database.ConnectionPool;
import exceptions.CouponSystemException;
import exceptions.PossibleDBCorruptionException;
import system.Looker;

/**
 * Implements sets of interactions between the Coupon System and standard
 * actions associated with an customer.
 * 
 * @author AlexanderZhilokov
 *
 */
public class CustomerFacade implements ClientCouponFacade {

	/**
	 * Stores a name of the last customer which successfully logged through this
	 * instance.
	 */
	private String name = "customer: ";

	/**
	 * Counts the number of the operations which were performed by this instance of
	 * the CustomerFacade class.
	 */
	private long operationCounter;

	/**
	 * Stores an driver to work with the coupons data from the database.
	 */
	private CouponDAO couponDAO;

	/**
	 * Stores an driver to work with the customers data from the database.
	 */
	private CustomerDAO customerDAO;

	/**
	 * Uses for receiving warnings from the drivers.
	 */
	public Looker onlooker;

	/**
	 * Creates a new instance of the class.
	 * 
	 * @param pool
	 *            - through this object the drivers will receive connections to the
	 *            database. Saves it by reference, not copies the whole object.
	 */
	public CustomerFacade(ConnectionPool pool) {
		onlooker = new Looker();
		customerDAO = new CustomerDBDAO(pool, onlooker);
		couponDAO = new CouponDBDAO(pool, onlooker);
	}

	/**
	 * Checks if the given name and password belongs to the customer which is known
	 * to the Coupon System and valid. And if so, it provides the reference to a new
	 * instance of the class CustomerFacade (sets of interactions between the Coupon
	 * System and standard actions associated with an customer).
	 */
	@Override
	public ClientCouponFacade login(String name, String password) {
		if ((name == null) || (password == null)) {
			return null;
		} else {
			try {
				if (couponDAO.login(name, password, customerDAO)) {
					this.name += name;
					return this;
				} else {
					return null;
				}
			} catch (CouponSystemException e) {
				System.out.println("There is a problem with the initialization of the client facade: ");
				System.out.println(e.msg);
				printSignature();
				return null;
			}
		}
	}

	/**
	 * Prints on a console a name under which an customer was logged into this
	 * instance and a current number of the operation to perform through this
	 * instance.
	 */
	private String printSignature() {
		return "signature: it is operation number " + operationCounter + " and client name is " + name;

	}

	/**
	 * Records a purchase of the coupon and marks as it owner the last customer ,
	 * who successfully logged through this instance. It also updates the amount of
	 * the coupon (decreases by one). Notifies through onlooker the reasons, if the
	 * operation was not successfully completed.
	 * 
	 * @param coupon
	 *            - the unique value which corresponds to the ID of the coupon
	 *            wanted to purchase as it stored in the database.
	 * @throws PossibleDBCorruptionException
	 *             if during the operation were some serious problems and there is a
	 *             danger to the integrity data in the database.
	 */
	public void purchaseCoupon(Coupon coupon) throws PossibleDBCorruptionException {
		operationCounter++;
		onlooker.newLog();
		onlooker.log.setSignature(printSignature());
		if (coupon == null) {
			onlooker.markFailure(
					"This link is empty. Means you must initialize it first and give it proper ID at least.");
			onlooker.log.setHeader("There is a problem with the purchasing the coupon.");
		} else {
			String badHeader = "There is a problem with the purchasing the coupon with title - " + coupon.getTitle()
					+ ".";
			String goodHeader = "The purchasing the coupon with title - " + coupon.getTitle()
					+ " - was successfully done.";
			try {
				customerDAO.purchaseCoupon(coupon);
				if (onlooker.hasFails()) {
					onlooker.log.setHeader(badHeader);
				} else {
					onlooker.log.setHeader(goodHeader);
				}
			} catch (PossibleDBCorruptionException e) {
				onlooker.log.setHeader(badHeader);
				throw e;
			} catch (CouponSystemException e) {
				onlooker.markFailure(e.msg);
				onlooker.log.setHeader(badHeader);
			}
		}
	}

	/**
	 * Returns all coupons which were bought by the specific customer. Prints on a
	 * console the reasons, if the operation was not successfully completed.
	 * Notifies through onlooker the reasons, if the operation was not successfully
	 * completed.
	 * 
	 * @return a collection of coupons which were bought by the last customer, who
	 *         successfully logged through this instance. If the customer has not
	 *         any coupons - returns the collection which size is zero. Or if there
	 *         are any problems returns null and notifies through onlooker the
	 *         reasons.
	 */
	public Collection<Coupon> getAllPurchasedCoupons() {
		operationCounter++;
		onlooker.newLog();
		onlooker.log.setSignature(printSignature());
		String badHeader = "There is a problem with the uploading all the customer coupons.";
		String goodHeader = "The uploading all the customer coupons was successfully done.";
		Collection<Coupon> coupons = null;
		try {
			coupons = customerDAO.getCoupons();
			if (onlooker.hasFails()) {
				onlooker.log.setHeader(badHeader);
			} else {
				onlooker.log.setHeader(goodHeader);
			}
			return coupons;
		} catch (CouponSystemException e) {
			onlooker.markFailure(e.msg);
			onlooker.log.setHeader(badHeader);
			return null;
		}
	}

	/**
	 * Returns all coupons which were stored in the database. Prints on a console
	 * the reasons, if the operation was not successfully completed. Notifies
	 * through onlooker the reasons, if the operation was not successfully
	 * completed.
	 * 
	 * @return all coupons from the database. If not any - returns the collection
	 *         which size is zero. Or if there are any problems returns null and
	 *         notifies through onlooker the reasons.
	 */
	public Collection<Coupon> getAllCoupons() {
		operationCounter++;
		onlooker.newLog();
		onlooker.log.setSignature(printSignature());
		String badHeader = "There is a problem with the uploading the coupons.";
		String goodHeader = "The uploading the coupons was successfully done.";
		Collection<Coupon> coupons = null;
		try {
			coupons = couponDAO.getAllCoupons();
			if (onlooker.hasFails()) {
				onlooker.log.setHeader(badHeader);
			} else {
				onlooker.log.setHeader(goodHeader);
			}
			return coupons;
		} catch (CouponSystemException e) {
			onlooker.markFailure(e.msg);
			onlooker.log.setHeader(badHeader);
			return null;
		}
	}

	/**
	 * Returns all coupons which were bought by the specific customer which types
	 * are equals to the given type value. Notifies through onlooker the reasons, if
	 * the operation was not successfully completed.
	 * 
	 * @param type
	 *            - a value which uses as a cutoff filter for the all customer
	 *            coupons which types are not equals to it.
	 * @return a collection of coupons which type are equals to the given type value
	 *         and which were bought by the last customer, who successfully logged
	 *         through this instance. If the customer has not any coupons which
	 *         meets the requirements- it returns the collection which size is zero.
	 *         Or if there are any problems returns null and notifies through
	 *         onlooker the reasons.
	 */
	public Collection<Coupon> getAllPurchasedCouponsByType(CouponType type) {
		operationCounter++;
		onlooker.newLog();
		onlooker.log.setSignature(printSignature());
		if (type == null) {
			onlooker.markFailure("This type link is empty. Means you should initialize it.");
			onlooker.log.setHeader("There is a problem with the collecting all the coupon by specified type.");
			return null;
		} else {
			String badHeader = "There is a problem with the uploading all the coupons with the type - " + type.name()
					+ ".";
			String goodHeader = "The uploading all the coupons with the type - " + type.name()
					+ " - was successfully done.";
			Collection<Coupon> coupons = null;
			try {
				coupons = customerDAO.getCouponsByType(type);
				if (onlooker.hasFails()) {
					onlooker.log.setHeader(badHeader);
				} else {
					onlooker.log.setHeader(goodHeader);
				}
				return coupons;
			} catch (CouponSystemException e) {
				onlooker.markFailure(e.msg);
				onlooker.log.setHeader(badHeader);
				return null;
			}
		}
	}

	/**
	 * Returns all coupons which was bought by the specific customer under the
	 * prices are lower than the given value. Notifies through onlooker the reasons,
	 * if the operation was not successfully completed.
	 * 
	 * @param price
	 *            - a value which uses as a cutoff filter for the all customer
	 *            coupons which prices are equals to it or higher.
	 * @return a collection of coupons which prices are lower than the given
	 *         parameter and which were bought by the last customer, who
	 *         successfully logged through this instance. If the customer has not
	 *         any coupons which meets the requirements - it returns the collection
	 *         which size is zero. Or if there are any problems returns null and
	 *         notifies through onlooker the reasons.
	 */
	public Collection<Coupon> getAllPurchasedCouponsByPrice(double price) {
		operationCounter++;
		onlooker.newLog();
		onlooker.log.setSignature(printSignature());
		String badHeader = "There is a problem with the uploading all the coupons with the price lower or equal "
				+ price + ".";
		String goodHeader = "The uploading all the coupons with the price lower or equal " + price
				+ " was successfully done.";
		Collection<Coupon> coupons = null;
		try {
			coupons = customerDAO.getCouponsByPrice(price);
			if (onlooker.hasFails()) {
				onlooker.log.setHeader(badHeader);
			} else {
				onlooker.log.setHeader(goodHeader);
			}
			return coupons;
		} catch (CouponSystemException e) {
			onlooker.markFailure(e.msg);
			onlooker.log.setHeader(badHeader);
			return null;
		}
	}
}