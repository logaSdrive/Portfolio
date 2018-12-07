package dao;

import java.util.Collection;
import java.util.Date;

import beans.Company;
import beans.Coupon;
import beans.CouponType;
import exceptions.CouponSystemException;

/**
 * Defines set of interactions between abstract database and standard actions on
 * companies and their coupons. It also responsible for implementation measures
 * of the integrity for all companies data in the database.
 * 
 * @author AlexanderZhilokov
 *
 */
public interface CompanyDAO {

	/**
	 * Stores a new company in the database. Notifies (through onlooker) if by any
	 * reasons there is no company was created.
	 * 
	 * @param company
	 *            - a new company with the values of parameters which should be
	 *            stored.
	 * @throws CouponSystemException
	 *             if there are serious problems with connection to the database.
	 */
	void createCompany(Company company) throws CouponSystemException;

	/**
	 * Returns the data about a specific company.
	 * 
	 * @param id
	 *            - the unique value which corresponds to the ID of the company as
	 *            it stored in the database.
	 * @return the company with all it data as it stored in the database. Returns
	 *         null and notifies (through onlooker) if there is no company was found
	 *         with ID equals to the given ID value by any reasons or if there are
	 *         some connection problems.
	 * @throws CouponSystemException
	 *             if there are serious problems with connection to the database.
	 */
	Company readCompany(long id) throws CouponSystemException;

	/**
	 * Returns the data about a specific company.
	 * 
	 * @param name
	 *            - the unique value which corresponds to the name of the company as
	 *            it stored in the database.
	 * @return the company with all it data as it stored in the database. Returns
	 *         null and notifies (through onlooker) if there is no company was found
	 *         with name equals to the given value by any reasons or if there are
	 *         some connection problems.
	 * @throws CouponSystemException
	 *             if there are serious problems with connection to the database.
	 */
	Company readCompany(String name) throws CouponSystemException;

	/**
	 * Updates the data about a specific company. Notifies (through onlooker) if by
	 * any reasons there is no company was updated.
	 * 
	 * @param company
	 *            - a company which parameters values should be replaced and stored
	 *            in the database as a new data about a specific company. The
	 *            company parameter ID should be equals to the ID corresponding
	 *            company as it stored in the database.
	 * @throws CouponSystemException
	 *             if there are serious problems with connection to the database.
	 */
	void updateCompany(Company company) throws CouponSystemException;

	/**
	 * Removes the data about a specific company from the database. Notifies
	 * (through onlooker) if by any reasons there is no company was deleted.
	 * 
	 * @param company
	 *            - a company to remove with the parameter ID value equals to the ID
	 *            of the corresponding company as it stored in the database.
	 * @throws CouponSystemException
	 *             if there are serious problems with connection to the database.
	 */
	void deleteCompany(Company company) throws CouponSystemException;

	/**
	 * Returns all companies stored in the database.
	 * 
	 * @return all companies from the database. If not any - returns the collection
	 *         which size is zero. Returns null and notifies (through onlooker) if
	 *         there are some problem with connection to the database.
	 * @throws CouponSystemException
	 *             if there are serious problems with connection to the database.
	 */
	Collection<Company> getAllCompanies() throws CouponSystemException;

	/**
	 * Returns all coupons which were created by the specific company.
	 * 
	 * @return a collection of coupons which were created by the last company, who
	 *         successfully logged through this instance. If the company has not any
	 *         coupons - returns the collection which size is zero. Returns null and
	 *         notifies (through onlooker) if received any problems with connection
	 *         to the database or if not any company logged through this instance or
	 *         if indeed a company logged but even so was not found by any reason.
	 * @throws CouponSystemException
	 *             if there are serious problems with connection to the database.
	 */
	Collection<Coupon> getCoupons() throws CouponSystemException;

	/**
	 * Returns all coupons which were created by the specific company under the
	 * prices are lower than the given value.
	 * 
	 * @param price
	 *            - a value which uses as a cutoff filter for the all company
	 *            coupons which prices are equals to it or higher.
	 * @return a collection of coupons which prices are lower than the given
	 *         parameter and which were created by the last company, who
	 *         successfully logged through this instance. If the company has not any
	 *         coupons which meets the requirements- it returns the collection which
	 *         size is zero. Returns null and notifies (through onlooker) if
	 *         received any problems with connection to the database. Or if not any
	 *         company logged through this instance. Or if indeed a company logged
	 *         but even so was not found by any reason. Or if the price value is
	 *         illegal (may varied on a different databases).
	 * @throws CouponSystemException
	 *             if there are serious problems with connection to the database.
	 */
	Collection<Coupon> getCouponsByPrice(double price) throws CouponSystemException;

	/**
	 * Returns all the coupons which were created by the specific company which
	 * types are equals to the given type value.
	 * 
	 * @param type
	 *            - a value which uses as a cutoff filter for the all company
	 *            coupons which types are not equals to it.
	 * @return a collection of coupons which type are equals to the given type value
	 *         and which were created by the last company, who successfully logged
	 *         through this instance. If the company has not any coupons which meets
	 *         the requirements - it returns the collection which size is zero.
	 *         Returns null and notifies (through onlooker) if received any problems
	 *         with connection to the database. Or if not any company logged through
	 *         this instance. Or if indeed a company logged but even so was not
	 *         found by any reason.
	 * @throws CouponSystemException
	 *             if there are serious problems with connection to the database.
	 */
	Collection<Coupon> getCouponsByType(CouponType type) throws CouponSystemException;

	/**
	 * Returns all the coupons which were created by the specific company and its
	 * end dates are before or equals to the given date.
	 * 
	 * @param date
	 *            - a value which uses as a cutoff filter for the all company
	 *            coupons which dates are after it .
	 * @return a collection of coupons which date is before or equals to the given
	 *         date and which were created by the last company, who successfully
	 *         logged through this instance. If the company has not any coupons
	 *         which meets the requirements- it returns the collection which size is
	 *         zero. Returns null and notifies (through onlooker) if received any
	 *         problems with connection to the database. Or if not any company
	 *         logged through this instance. Or if indeed a company logged but even
	 *         so was not found by any reason. Or if the price value is illegal (may
	 *         varied on a different databases).
	 * @throws CouponSystemException
	 *             if there are serious problems with connection to the database.
	 */
	Collection<Coupon> getCouponsByDate(Date date) throws CouponSystemException;

	/**
	 * Returns all coupons which were created by the specific company.
	 * 
	 * @param compId
	 *            - the unique value which corresponds to the ID of the company as
	 *            it stored in the database.
	 * @return a collection of coupons which were created by the company. If the
	 *         company has not any coupons - returns the collection which size is
	 *         zero. If received any problems with connection to the database or if
	 *         by any reasons there is no company was found with ID equals to the
	 *         given parameter - returns null and notifies (through onlooker) .
	 * @throws CouponSystemException
	 *             if there are serious problems with connection to the database.
	 */
	Collection<Coupon> getCompanyCoupons(long compId) throws CouponSystemException;

	/**
	 * Checks if exists a company in the database with name and password equals to
	 * the given values.
	 * 
	 * @param compName
	 *            - a company name as it should appear in the database.
	 * @param password
	 *            - a company password as it should appear in the database.
	 * @return true if exists a company in the database with name and password
	 *         equals to the given values. Otherwise it returns false.
	 * @throws CouponSystemException
	 *             if received any problems with connection to the database.
	 */
	boolean login(String compName, String password) throws CouponSystemException;

	/**
	 * Returns the ID of the last company, who successfully logged through this
	 * instance.
	 * 
	 * @return the ID of the last company, who successfully logged through this
	 *         instance as it stored in the database.
	 */
	long getObtainedId();

}