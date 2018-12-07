package facades;

import java.util.Collection;

import beans.Coupon;
import beans.CouponType;
import dao.CompanyDAO;
import dao.CouponDAO;
import dao.CustomerDAO;
import daoDB.CompanyDBDAO;
import daoDB.CouponDBDAO;
import daoDB.CustomerDBDAO;
import database.ConnectionPool;
import exceptions.CouponSystemException;
import exceptions.PossibleDBCorruptionException;

/**
 * Implements sets of interactions between the Coupon System and standard
 * actions associated with the main administrator.
 * 
 * @author AlexanderZhilokov
 *
 */
public class SuperAdminFacade extends AdminFacade {

	/**
	 * Stores the name value for the main administrator.
	 */
	private final static String NAME = "super-admin";

	/**
	 * Stores a password for the main administrator.
	 */
	private final static String PASSWORD = "11111";

	/**
	 * Stores an driver to work with the coupons data from the database.
	 */
	private CouponDAO couponDAO;

	/**
	 * Stores an driver to work with the companies data from the database.
	 */
	private CompanyDAO companyDAO;

	/**
	 * Stores an driver to work with the customers data from the database.
	 */
	private CustomerDAO customerDAO;

	/**
	 * Creates a new instance of the class.
	 * 
	 * @param pool
	 *            - through this object the drivers will receive connections to the
	 *            database. Saves it by reference, not copies the whole object.
	 */
	public SuperAdminFacade(ConnectionPool pool) {
		super(pool);
		companyDAO = new CompanyDBDAO(pool, onlooker);
		customerDAO = new CustomerDBDAO(pool, onlooker);
		couponDAO = new CouponDBDAO(pool, onlooker);
	}

	/**
	 * Checks if the given name and password belongs to the main administrator which
	 * is known to the Coupon System and valid. And if so, it provides the reference
	 * to a new instance of the class SuperAdminFacade (sets of interactions between
	 * the Coupon System and standard actions associated with the main
	 * administrator).
	 */
	@Override
	public ClientCouponFacade login(String name, String password) {
		if (name == null || password == null) {
			return null;
		} else {
			if (name.equals(NAME) && password.equals(PASSWORD)) {
				return this;
			} else {
				return null;
			}
		}
	}

	/**
	 * Prints on a console a name under which the main administrator was logged into
	 * this instance and a current number of the operation to perform through this
	 * instance.
	 */
	@Override
	protected String printSignature() {
		return "signature: it is operation number " + operationCounter + " from " + NAME + " client";
	}

	/**
	 * Removes the data about a specific coupon. Notifies through onlooker the
	 * reasons, if the operation was not successfully completed.
	 * 
	 * @param coupon
	 *            - a coupon to remove with the parameter ID value equals to the ID
	 *            of the corresponding coupon as it stored in the database. Prints
	 *            on a console the reasons, if the operation was not successfully
	 *            completed.
	 * @throws PossibleDBCorruptionException
	 *             if during the operation were some serious problems and there is a
	 *             danger to the integrity data in the database.
	 */
	public void removeCoupon(Coupon coupon) throws PossibleDBCorruptionException {
		operationCounter++;
		onlooker.newLog();
		onlooker.log.setSignature(printSignature());
		if (coupon == null) {
			onlooker.markFailure(
					"This link is empty. Means you must initialize it first and give it proper ID at least.");
			onlooker.log.setHeader("There is a problem with the removal of the coupon.");
		} else {
			String badHeader = "There is a problem with the removal of the coupon with title - " + coupon.getTitle()
					+ ".";
			String goodHeader = "The removal of the coupon with title - " + coupon.getTitle()
					+ " - was successfully done.";
			try {
				couponDAO.deleteCouponAsAdmin(coupon);
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
	 * Updates the data about a specific coupon. Notifies through onlooker the
	 * reasons, if the operation was not successfully completed.
	 * 
	 * @param coupon
	 *            - a coupon which parameters values should be replaced and stored
	 *            in the database as a new data about a specific coupon. The coupon
	 *            parameter ID should be equals to the ID of the corresponding
	 *            coupon as it stored in the database.
	 */
	public void updateCoupon(Coupon coupon) {
		operationCounter++;
		onlooker.newLog();
		onlooker.log.setSignature(printSignature());
		if (coupon == null) {
			onlooker.markFailure(
					"This link is empty. Means you must initialize it first and give it proper ID at least.");
			onlooker.log.setHeader("There is a problem with the alternation of the coupon.");
		} else {
			String badHeader = "There is a problem with the alternation of the coupon with title - " + coupon.getTitle()
					+ ".";
			String goodHeader = "The alternation of the coupon with title - " + coupon.getTitle()
					+ " was successfully done.";
			try {
				couponDAO.updateCouponAsAdmin(coupon);
				if (onlooker.hasFails()) {
					onlooker.log.setHeader(badHeader);
				} else {
					onlooker.log.setHeader(goodHeader);
				}
			} catch (CouponSystemException e) {
				onlooker.markFailure(e.msg);
				onlooker.log.setHeader(badHeader);
			}
		}
	}

	/**
	 * Returns the data about a specific coupon. Notifies through onlooker the
	 * reasons, if the operation was not successfully completed.
	 * 
	 * @param couponId
	 *            - the unique value which corresponds to the ID of the coupon as it
	 *            stored in the database.
	 * @return the coupon with all it data as it stored in the database. Or if there
	 *         are any problems returns null and notifies through onlooker the
	 *         reasons.
	 */
	public Coupon getCoupon(long couponId) {
		operationCounter++;
		onlooker.newLog();
		onlooker.log.setSignature(printSignature());
		String badHeader = "There is a problem with uploading the coupon with ID - " + couponId + ".";
		String goodHeader = "The uploading the coupon with ID - " + couponId + " - was successfully done.";
		Coupon coupon = null;
		try {
			coupon = couponDAO.readCouponAsAdmin(couponId);
			if (onlooker.hasFails()) {
				onlooker.log.setHeader(badHeader);
			} else {
				onlooker.log.setHeader(goodHeader);
			}
			return coupon;
		} catch (CouponSystemException e) {
			onlooker.markFailure(e.msg);
			onlooker.log.setHeader(badHeader);
			return null;
		}
	}

	/**
	 * Returns all coupons stored in the database. Notifies through onlooker the
	 * reasons, if the operation was not successfully completed.
	 * 
	 * @return all coupons from the database. If not any - returns the collection
	 *         which size is zero. Or if there are any problems returns null and
	 *         notifies through onlooker the reasons.
	 */
	public Collection<Coupon> getAllCoupons() {
		operationCounter++;
		onlooker.newLog();
		onlooker.log.setSignature(printSignature());
		String badHeader = "There is a problem with collecting all the coupons from database.";
		String goodHeader = "The collection all the coupons from database was successfully done.";
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
	 * Returns all coupons stored in the database which types are equals to the
	 * given type value. Notifies through onlooker the reasons, if the operation was
	 * not successfully completed.
	 * 
	 * @param type-
	 *            a value which uses as a cutoff filter for the all coupons which
	 *            types are not equals to it.
	 * @return all coupons from the database. If not any - returns the collection
	 *         which size is zero. Or if there are any problems returns null and
	 *         notifies through onlooker the reasons.
	 */
	public Collection<Coupon> getCouponsByType(CouponType type) {
		operationCounter++;
		onlooker.newLog();
		onlooker.log.setSignature(printSignature());
		Collection<Coupon> coupons = null;
		if (type == null) {
			onlooker.markFailure("This type link is empty. Means you should initialize it.");
			onlooker.log.setHeader("There is a problem with the collecting all the coupon by specified type.");
		} else {
			String badHeader = "There is a problem with collecting all the coupons wich type is " + type
					+ " from database.";
			String goodHeader = "The collection all the coupons wich type is " + type
					+ " from database was successfully done.";
			try {
				coupons = couponDAO.getAllCouponsByType(type);
				if (onlooker.hasFails()) {
					onlooker.log.setHeader(badHeader);
				} else {
					onlooker.log.setHeader(goodHeader);
				}
			} catch (CouponSystemException e) {
				onlooker.markFailure(e.msg);
				onlooker.log.setHeader(badHeader);
				return null;
			}
		}
		return coupons;
	}

	/**
	 * Returns all coupons which were created by the specific company. Notifies
	 * through onlooker the reasons, if the operation was not successfully
	 * completed.
	 * 
	 * @param id
	 *            - the unique value which corresponds to the ID of the company as
	 *            it stored in the database.
	 * @return a collection of coupons which were created by the company. If the
	 *         company has not any coupons - returns the collection which size is
	 *         zero. Or if there are any problems returns null and notifies through
	 *         onlooker the reasons.
	 */
	public Collection<Coupon> getCompanyCoupons(long id) {
		operationCounter++;
		onlooker.newLog();
		onlooker.log.setSignature(printSignature());
		String badHeader = "There is a problem with collecting all the coupons from the company with ID - " + id
				+ " from database.";
		String goodHeader = "The collection all the coupons from the company with ID - " + id
				+ " from database was successfully done.";
		Collection<Coupon> coupons = null;
		try {
			coupons = companyDAO.getCompanyCoupons(id);
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
	 * Returns all coupons which were bought by the specific customer. Notifies
	 * through onlooker the reasons, if the operation was not successfully
	 * completed.
	 * 
	 * @param id
	 *            - the unique value which corresponds to the ID of the customer as
	 *            it stored in the database.
	 * @return a collection of coupons which were bought by the customer. If the
	 *         customer has not any coupons - returns the collection which size is
	 *         zero. Or if there are any problems returns null and notifies through
	 *         onlooker the reasons.
	 */
	public Collection<Coupon> getCustomerCoupons(long id) {
		operationCounter++;
		onlooker.newLog();
		onlooker.log.setSignature(printSignature());
		String badHeader = "There is a problem with collecting all the coupons from the customer with ID: " + id
				+ " from database.";
		String goodHeader = "The collection all the coupons from the customer with ID - " + id
				+ " from database was successfully done.";
		Collection<Coupon> coupons = null;
		try {
			coupons = customerDAO.getCustomerCoupons(id);
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
	 * Removes the coupon which was purchased early from the customer, it owner. It
	 * also updates the amount of the coupon (increases by one). Notifies through
	 * onlooker the reasons, if the operation was not successfully completed.
	 * 
	 * @param custId
	 *            - the unique value which corresponds to the ID of the customer who
	 *            owns a coupon which should removed.
	 * @param couponId
	 *            - the unique value which corresponds to the ID of the coupon which
	 *            should be removed from its owner.
	 * @throws PossibleDBCorruptionException
	 *             if during the operation were some serious problems and there is a
	 *             danger to the integrity data in the database.
	 */
	public void removeCouponFromCustomer(long custId, long couponId) throws PossibleDBCorruptionException {
		operationCounter++;
		onlooker.newLog();
		onlooker.log.setSignature(printSignature());
		String badHeader = "There is a problem with the removal of the coupon with ID - " + couponId
				+ " from the customer with ID - " + custId + ".";
		String goodHeader = "The removal of the coupon with ID - " + couponId + " from the customer with ID - " + custId
				+ " - was successfully done.";
		try {
			customerDAO.removeCouponFromTheCustomer(custId, couponId);
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