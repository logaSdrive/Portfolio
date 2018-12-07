package facades;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import beans.Coupon;
import beans.CouponType;
import dao.CompanyDAO;
import dao.CouponDAO;
import daoDB.CompanyDBDAO;
import daoDB.CouponDBDAO;
import database.ConnectionPool;
import static exceptions.CouponDBDAOException.*;

import exceptions.CouponSystemException;
import exceptions.PossibleDBCorruptionException;
import system.Looker;

/**
 * Implements sets of interactions between the Coupon System and standard
 * actions associated with an company.
 * 
 * @author AlexanderZhilokov
 *
 */
public class CompanyFacade implements ClientCouponFacade {

	/**
	 * Stores a name of the last company which successfully logged through this
	 * instance.
	 */
	private String name = "company: ";

	/**
	 * Counts the number of the operations which were performed by this instance of
	 * the CompanyFacade class.
	 */
	private long operationCounter;

	/**
	 * Stores an driver to work with the coupons data from the database.
	 */
	private CouponDAO couponDAO;

	/**
	 * Stores an driver to work with the companies data from the database.
	 */
	private CompanyDAO companyDAO;

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
	public CompanyFacade(ConnectionPool pool) {
		onlooker = new Looker();
		companyDAO = new CompanyDBDAO(pool, onlooker);
		couponDAO = new CouponDBDAO(pool, onlooker);
	}

	/**
	 * Checks if the given name and password belongs to the company which is known
	 * to the Coupon System and valid. And if so, it provides the reference to a new
	 * instance of the class CompanyFacade (sets of interactions between the Coupon
	 * System and standard actions associated with an company).
	 */
	@Override
	public ClientCouponFacade login(String name, String password) {
		if ((name == null) || (password == null)) {
			return null;
		} else {
			try {
				if (couponDAO.login(name, password, companyDAO)) {
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
	 * Prints on a console a name under which an company was logged into this
	 * instance and a current number of the operation to perform through this
	 * instance.
	 */
	private String printSignature() {
		return "signature: it is operation number " + operationCounter + " and client name is " + name;
	}

	/**
	 * Records a new coupons to the database and marks as a creator the last
	 * company, who successfully logged through this instance. Notifies through
	 * onlooker the reasons, if the operation was not successfully completed.
	 * 
	 * @param coupon
	 *            - a new coupon with the values of parameters which should be
	 *            stored.
	 * @throws PossibleDBCorruptionException
	 *             if during the operation were some serious problems and there is a
	 *             danger to the integrity data in the database.
	 */
	public void createCoupon(Coupon coupon) throws PossibleDBCorruptionException {
		operationCounter++;
		onlooker.newLog();
		onlooker.log.setSignature(printSignature());
		if (coupon == null) {
			onlooker.markFailure("This link is empty. Means your should initialize it first.");
			onlooker.log.setHeader("There is a problem with the creation of the coupon.");
		} else {
			String badHeader = "There is a problem with the creation of the coupon with title - " + coupon.getTitle()
					+ ".";
			String goodHeader = "The creation of the coupon with title - " + coupon.getTitle()
					+ " - was successfully done.";
			try {
				couponDAO.createCoupon(coupon);
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
	 * Removes the data about a specific coupon. Notifies through onlooker the
	 * reasons, if the operation was not successfully completed.
	 * 
	 * @param coupon
	 *            - a coupon to remove with the parameter ID value equals to the ID
	 *            of the corresponding coupon as it stored in the database.
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
				couponDAO.deleteCoupon(coupon);
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
	 * reasons, if the operation was not successfully completed (allowed to update
	 * only the coupon's price or the end date).
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
			onlooker.markFailure("This link is empty. Means your should initialize it first.");
			onlooker.log.setHeader("There is a problem with the alternation of the coupon.");
		} else {
			String badHeader = "There is a problem with the alternation of the coupon with title - " + coupon.getTitle()
					+ ".";
			String goodHeader = "The alternation of the coupon with title - " + coupon.getTitle()
					+ " was successfully done.";
			String msg = "";
			final int ok = -1;
			final int rept = couponDAO.validate(coupon);
			if (rept == ok) {
				try {
					Coupon original = couponDAO.readCoupon(coupon.getId());
					if (onlooker.hasFails()) {
						onlooker.log.setHeader(badHeader);
						return;
					}
					boolean somethingWrong = false;
					if (!coupon.getTitle().equals(original.getTitle())) {
						msg += "Please set the coupon title to the original value. ";
						somethingWrong = true;
					}
					if (!coupon.getStartDate().equals(original.getStartDate())) {
						msg += "Please set the coupon start date to the original value. ";
						somethingWrong = true;
					}
					if (!coupon.getType().equals(original.getType())) {
						msg += "Please set the coupon type to the original value. ";
						somethingWrong = true;
					}
					if (!coupon.getMessage().equals(original.getMessage())) {
						msg += "Please set the coupon message to the original value. ";
						somethingWrong = true;
					}
					if (!coupon.getImage().equals(original.getImage())) {
						msg += "Please set the coupon image to the original value. ";
						somethingWrong = true;
					}
					if (somethingWrong) {
						onlooker.markFailure(msg + "You can only update the coupons end date or the price. "
								+ "Thats means other coupon's properties must be assigned to the original values.");
						onlooker.log.setHeader(badHeader);
					} else {
						couponDAO.updateCoupon(coupon);
						if (onlooker.hasFails()) {
							onlooker.log.setHeader(badHeader);
						} else {
							onlooker.log.setHeader(goodHeader);
						}
					}
				} catch (CouponSystemException e) {
					onlooker.markFailure(e.msg);
					onlooker.log.setHeader(badHeader);
				}
			} else {
				if ((rept & HASH_END_DATE) != 0) {
					if (coupon.getEndDate() == null) {
						msg += "You are trying to assign the end date of the coupon to null."
								+ " You can't do it - every coupon must have the end date. ";
					} else if (coupon.getEndDate().before(new Date(System.currentTimeMillis()))) {
						msg += "You are trying to assign the end date to the already expired date. "
								+ "Means the end date of the coupon must be at least one day forward from today. ";
					} else if (coupon.getEndDate().before(coupon.getStartDate())) {
						msg += "Not sure if you also changed the start date (which is also forbidden to change),"
								+ " but the end date cant be early than a start date. ";
					}
				}
				if ((rept & HASH_PRICE) != 0) {
					msg += "You are trying to assign the negative value to the coupon price."
							+ " Any price of any coupon must be a non negative value. ";
				}
				if ((rept & HASH_AMOUNT) != 0) {
					msg += "Please set any positive value to the amount (greater than zero). You can't alternate the current amount of coupons, "
							+ "but since its very fluid (the current amount of the avialable coupons) you are not requried to guess it's actual value by minute."
							+ "Just put any positive value but keep in mind - the value you are entering cant affect the original value by any means. ";
				}
				if ((rept & (~(HASH_END_DATE | HASH_PRICE | HASH_AMOUNT))) != 0) {
					msg += "You can only update the coupons end date or the price. "
							+ "Thats means other coupon's properties must be assigned to the original values. "
							+ "Please do another check to the coupon's: ";
					if ((rept & HASH_TITLE) != 0) {
						msg += "'title' ";
					}
					if ((rept & HASH_START_DATE) != 0) {
						msg += "'start date' ";
					}
					if ((rept & HASH_TYPE) != 0) {
						msg += "'type' ";
					}
					if ((rept & HASH_MESSAGE) != 0) {
						msg += "'message' ";
					}
					if ((rept & HASH_IMAGE) != 0) {
						msg += "'image' ";
					}
					msg += " - they are not the original values.";
				}
				onlooker.markFailure(msg);
				onlooker.log.setHeader(badHeader);
			}
		}
	}

	/**
	 * Returns the data about a specific coupon. Notifies through onlooker the
	 * reasons, if the operation was not successfully completed.
	 * 
	 * @param id
	 *            - the unique value which corresponds to the ID of the coupon as it
	 *            stored in the database.
	 * @return the coupon with all it data as it stored in the database. Or if there
	 *         are any problems returns null and notifies through onlooker the
	 *         reasons.
	 */
	public Coupon getCoupon(long id) {
		operationCounter++;
		onlooker.newLog();
		onlooker.log.setSignature(printSignature());
		String badHeader = "There is a problem with the uploading the coupon with ID - " + id + ".";
		String goodHeader = "The uploading the coupon with ID - " + id + " - was successfully done.";
		Coupon coupon = null;
		try {
			coupon = couponDAO.readCoupon(id);
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
	 * Returns the data about a specific coupon. Notifies through onlooker the
	 * reasons, if the operation was not successfully completed.
	 * 
	 * @param title
	 *            - the unique value which corresponds to the title of the coupon as
	 *            it stored in the database.
	 * @return the coupon with all it data as it stored in the database. Or if there
	 *         are any problems returns null and notifies through onlooker the
	 *         reasons.
	 */
	public Coupon getCoupon(String title) {
		operationCounter++;
		onlooker.newLog();
		onlooker.log.setSignature(printSignature());
		String badHeader = "There is a problem with the uploading the coupon with title - " + title + ".";
		String goodHeader = "The uploading the coupon with title - " + title + " - was successfully done.";
		Coupon coupon = null;
		try {
			coupon = couponDAO.readCoupon(title);
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
	 * Returns all coupons which were created by the specific company. Notifies
	 * through onlooker the reasons, if the operation was not successfully
	 * completed.
	 * 
	 * @return a collection of coupons which were created by the last company, who
	 *         successfully logged through this instance. If the company has not any
	 *         coupons - returns the collection which size is zero. Or if there are
	 *         any problems returns null and notifies through onlooker the reasons.
	 */
	public Collection<Coupon> getAllCoupons() {
		operationCounter++;
		onlooker.newLog();
		onlooker.log.setSignature(printSignature());
		String badHeader = "There is a problem with the uploading all the company coupons.";
		String goodHeader = "The uploading all the company coupons was successfully done.";
		Collection<Coupon> coupons = null;
		try {
			coupons = companyDAO.getCoupons();
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
	 * Returns all the coupons which were created by the specific company which
	 * types are equals to the given type value. Notifies through onlooker the
	 * reasons, if the operation was not successfully completed.
	 * 
	 * @param type
	 *            - a value which uses as a cutoff filter for the all company
	 *            coupons which types are not equals to it.
	 * @return a collection of coupons which type are equals to the given type value
	 *         and which were created by the last company, who successfully logged
	 *         through this instance. If the company has not any coupons which meets
	 *         the requirements- it returns the collection which size is zero. Or if
	 *         there are any problems returns null and notifies through onlooker the
	 *         reasons.
	 */
	public Collection<Coupon> getCouponsByType(CouponType type) {
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
				coupons = companyDAO.getCouponsByType(type);
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
	 * Returns all coupons which were created by the specific company under the
	 * prices are lower than the given value. Notifies through onlooker the reasons,
	 * if the operation was not successfully completed.
	 * 
	 * @param price
	 *            - a value which uses as a cutoff filter for the all company
	 *            coupons which prices are equals to it or higher.
	 * @return a collection of coupons which prices are lower than the given
	 *         parameter and which were created by the last company, who
	 *         successfully logged through this instance. If the company has not any
	 *         coupons which meets the requirements- it returns the collection which
	 *         size is zero. Or if there are any problems returns null and notifies
	 *         through onlooker the reasons.
	 */
	public Collection<Coupon> getCouponsByPrice(double price) {
		operationCounter++;
		onlooker.newLog();
		onlooker.log.setSignature(printSignature());
		String badHeader = "There is a problem with the uploading all the coupons with the price lower or equal "
				+ price + ".";
		String goodHeader = "The uploading all the coupons with the price lower or equal " + price
				+ " was successfully done.";
		Collection<Coupon> coupons;
		try {
			coupons = companyDAO.getCouponsByPrice(price);
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
	 * Returns all the coupons which were created by the specific company and its
	 * end dates are before or equals to the given date. Notifies through onlooker
	 * the reasons, if the operation was not successfully completed.
	 * 
	 * @param date
	 *            - a value which uses as a cutoff filter for the all company
	 *            coupons which dates are after it .
	 * @return a collection of coupons which date is before or equals to the given
	 *         date and which were created by the last company, who successfully
	 *         logged through this instance. If the company has not any coupons
	 *         which meets the requirements- it returns the collection which size is
	 *         zero. Or if there are any problems returns null and notifies through
	 *         onlooker the reasons.
	 */
	public Collection<Coupon> getCouponsByDate(Date date) {
		operationCounter++;
		onlooker.newLog();
		onlooker.log.setSignature(printSignature());
		if (date == null) {
			onlooker.markFailure("This date link is empty. Means you should initialize it.");
			onlooker.log.setHeader("There is a problem with the collecting all the coupon until specified end date.");
			return null;
		} else {
			SimpleDateFormat dateOnly =new SimpleDateFormat("dd MMM yyyy");
			String badHeader = "There is a problem with the uploading all the coupons with the end date until " + dateOnly.format(date)
					+ ".";
			String goodHeader = "The uploading all the coupons with the end date until " + dateOnly.format(date)
					+ " was successfully done.";
			Collection<Coupon> coupons;
			try {
				coupons = companyDAO.getCouponsByDate(date);
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

}