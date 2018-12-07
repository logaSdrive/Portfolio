package dao;

import java.util.Collection;

import beans.Coupon;
import beans.CouponType;
import exceptions.CouponDBDAOException;
import exceptions.CouponSystemException;

/**
 * Defines set of interactions between abstract database and standard actions on
 * coupons. It also responsible for implementation measures of the integrity for
 * all coupons data in the database.
 * 
 * @author AlexanderZhilokov
 *
 */
public interface CouponDAO {

	/**
	 * Records a new coupons to the database and marks as a creator the last
	 * company, who successfully logged through this instance. Notifies (through
	 * onlooker) if the coupon was not stored by any reasons.
	 * 
	 * @param coupon
	 *            - a new coupon with the values of parameters which should be
	 *            stored.
	 * @throws CouponSystemException
	 *             if there are serious problems with connection to the database.
	 */
	void createCoupon(Coupon coupon) throws CouponSystemException;

	/**
	 * Returns the data about a specific coupon.
	 * 
	 * @param id
	 *            - the unique value which corresponds to the ID of the coupon as it
	 *            stored in the database.
	 * @return the coupon with all it data as it stored in the database. Returns
	 *         null and notifies (through onlooker) if there is no coupon found with
	 *         ID equals to the given ID value by any reasons. Or if the given ID
	 *         values not belongs to the coupons which were created by the last
	 *         company, who successfully logged through this instance. Or if not any
	 *         company logged through this instance. Or if indeed a company logged
	 *         but even so was not found by any reason.
	 * @throws CouponSystemException
	 *             if there are serious problems with connection to the database.
	 */
	Coupon readCoupon(long id) throws CouponSystemException;

	/**
	 * Returns the data about a specific coupon.
	 * 
	 * @param title
	 *            - the unique value which corresponds to the title of the coupon as
	 *            it stored in the database.
	 * @return the coupon with all it data as it stored in the database. Returns
	 *         null and notifies (through onlooker) if there is no coupon found with
	 *         title equals to the given value by any reasons. Or if the given title
	 *         values not belongs to the coupons which were created by the last
	 *         company, who successfully logged through this instance. Or if not any
	 *         company logged through this instance. Or if indeed a company logged
	 *         but even so was not found by any reason.
	 * @throws CouponSystemException
	 *             if there are serious problems with connection to the database.
	 */
	Coupon readCoupon(String title) throws CouponSystemException;

	/**
	 * Returns the data about a specific coupon.
	 * 
	 * @param couponId
	 *            - the unique value which corresponds to the ID of the coupon as it
	 *            stored in the database.
	 * @return the coupon with all it data as it stored in the database. Returns
	 *         null and notifies (through onlooker) if there was no coupon found
	 *         with ID equals to the given ID value by any reasons.
	 * @throws CouponSystemException
	 *             if there are serious problems with connection to the database.
	 */
	Coupon readCouponAsAdmin(long couponId) throws CouponSystemException;

	/**
	 * Returns the data about a specific coupon.
	 * 
	 * @param title
	 *            - the unique value which corresponds to the title of the coupon as
	 *            it stored in the database.
	 * @return the coupon with all it data as it stored in the database. Returns
	 *         null and notifies (through onlooker) if there was no coupon found
	 *         with title equals to the given value by any reasons.
	 * @throws CouponSystemException
	 *             if there are serious problems with connection to the database.
	 */
	Coupon readCouponAsAdmin(String title) throws CouponSystemException;

	/**
	 * Updates the data about a specific coupon. Notifies (through onlooker) if
	 * there was no coupon found with ID equals to the given coupon ID by any
	 * reasons. Or if the given coupon was not created by the last company, who
	 * successfully logged through this instance. Or if not any company logged
	 * through this instance. Or if indeed a company logged but even so was not
	 * found by any reason.
	 * 
	 * @param coupon
	 *            - a coupon which parameters values should be replaced and stored
	 *            in the database as a new data about a specific coupon. The coupon
	 *            parameter ID should be equals to the ID of the corresponding
	 *            coupon as it stored in the database.
	 * @throws CouponSystemException
	 *             if there are serious problems with connection to the database.
	 */
	void updateCoupon(Coupon coupon) throws CouponSystemException;

	/**
	 * Updates the data about a specific coupon. Notifies (through onlooker) if by
	 * any reasons there was no coupon updated.
	 * 
	 * @param coupon
	 *            - a coupon which parameters values should be replaced and stored
	 *            in the database as a new data about a specific coupon. The coupon
	 *            parameter ID should be equals to the ID of the corresponding
	 *            coupon as it stored in the database.
	 * @throws CouponSystemException
	 *             if there are serious problems with connection to the database.
	 */
	void updateCouponAsAdmin(Coupon coupon) throws CouponSystemException;

	/**
	 * Removes the data about a specific coupon. Notifies (through onlooker) if
	 * there was no coupon found with ID equals to the given coupon ID by any
	 * reasons. Or if the given coupon was not created by the last company, who
	 * successfully logged through this instance. Or if not any company logged
	 * through this instance. Or if indeed a company logged but even so was not
	 * found by any reason.
	 * 
	 * @param coupon
	 *            - a coupon to remove with the parameter ID value equals to the ID
	 *            of the corresponding coupon as it stored in the database.
	 * @throws CouponSystemException
	 *             if there are serious problems with connection to the database.
	 */
	void deleteCoupon(Coupon coupon) throws CouponSystemException;

	/**
	 * Removes the data about a specific coupon. Notifies (through onlooker) if by
	 * any reasons there was no coupon deleted.
	 * 
	 * @param coupon
	 *            - a coupon to remove with the parameter ID value equals to the ID
	 *            of the corresponding coupon as it stored in the database.
	 * @throws CouponSystemException
	 *             if there are serious problems with connection to the database.
	 */
	void deleteCouponAsAdmin(Coupon coupon) throws CouponSystemException;

	/**
	 * Returns all coupons stored in the database.
	 * 
	 * @return all coupons from the database. If not any - returns the collection
	 *         which size is zero. Returns null and notifies (through onlooker) if
	 *         received any problems with connection to the database.
	 * @throws CouponSystemException
	 *             if there are serious problems with connection to the database.
	 */
	Collection<Coupon> getAllCoupons() throws CouponSystemException;

	/**
	 * Returns all coupons stored in the database which types are equals to the
	 * given type value.
	 * 
	 * @param type-
	 *            a value which uses as a cutoff filter for the all coupons which
	 *            types are not equals to it.
	 * @return all coupons from the database. If not any - returns the collection
	 *         which size is zero. Returns null and notifies (through onlooker) if
	 *         received any problems with connection to the database.
	 * @throws CouponSystemException
	 *             if there are serious problems with connection to the database.
	 */
	Collection<Coupon> getAllCouponsByType(CouponType type) throws CouponSystemException;

	/**
	 * Removes from the database all the coupons which ends dates are expired.
	 * 
	 * @throws CouponSystemException
	 *             if received any problems with connection to the database.
	 */
	void removeExpiredCoupons() throws CouponSystemException;

	/**
	 * Checks if exists a company in the database with name and password equals to
	 * the given values.
	 * 
	 * @param compName
	 *            - a company name as it should appear in the database.
	 * @param password
	 *            - a company password as it should appear in the database.
	 * @param companyDAO
	 *            - some instance for the standard actions on the companies which
	 *            will be used to perform the check.
	 * @return true if exists a company in the database with name and password
	 *         equals to the given values. Otherwise it returns false.
	 * @throws CouponSystemException
	 *             if received any problems with connection to the database.
	 */
	boolean login(String compName, String password, CompanyDAO companyDAO) throws CouponSystemException;

	/**
	 * Checks if exists a customer in the database with name and password equals to
	 * the given values.
	 * 
	 * @param custName
	 *            - a customer name as it should appear in the database.
	 * @param password
	 *            - a customer password as it should appear in the database.
	 * @param customerDAO
	 *            - some instance for the standard actions on the customers which
	 *            will be used to perform the check.
	 * @return true if exists a customer in the database with name and password
	 *         equals to the given values. Otherwise it returns false.
	 * @throws CouponSystemException
	 *             if received any problems with connection to the database.
	 */
	boolean login(String custName, String password, CustomerDAO customerDAO) throws CouponSystemException;

	/**
	 * Checks if the coupon parameters are legal for the inserting into the
	 * database.
	 * 
	 * @param coupon
	 *            - the coupon with all it data which should be checked.
	 * @return hash code value which contains all the problems which was found. Or
	 *         -1 if the given coupons parameters are legal.
	 * @see CouponDBDAOException - CouponDBDAOException provides all hash codes for
	 *      the each coupon parameter.
	 */
	int validate(Coupon coupon);

}