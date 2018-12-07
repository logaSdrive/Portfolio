package dao;

import java.util.Collection;

import beans.Coupon;
import beans.CouponType;
import beans.Customer;
import exceptions.CouponSystemException;

/**
 * Defines set of interactions between abstract database and standard actions on
 * customers and their coupons. It also responsible for implementation measures
 * of the integrity for all customers data in the database.
 * 
 * @author AlexanderZhilokov
 *
 */
public interface CustomerDAO {

	/**
	 * Stores a new customer in the database. Notifies (through onlooker) if the
	 * customer was not stored by any reasons.
	 * 
	 * @param customer
	 *            - a new customer with the values of parameters which should be
	 *            stored.
	 * @throws CouponSystemException
	 *             if there are serious problems with connection to the database.
	 */
	void createCustomer(Customer customer) throws CouponSystemException;

	/**
	 * Returns the data about a specific customer.
	 * 
	 * @param id
	 *            - the unique value which corresponds to the ID of the customer as
	 *            it stored in the database.
	 * @return the customer with all his data as it stored in the database. Returns
	 *         null and notifies (through onlooker) if there was no customer found
	 *         with ID equals to the given ID value by any reasons.
	 * @throws CouponSystemException
	 *             if there are serious problems with connection to the database.
	 */
	Customer readCustomer(long id) throws CouponSystemException;

	/**
	 * Returns the data about a specific customer.
	 * 
	 * @param name
	 *            - the unique value which corresponds to the name of the customer
	 *            as it stored in the database.
	 * @return the customer with all his data as it stored in the database. Returns
	 *         null and notifies (through onlooker) if there was no customer found
	 *         with name equals to the given value by any reasons.
	 * @throws CouponSystemException
	 *             if there are serious problems with connection to the database.
	 */
	Customer readCustomer(String name) throws CouponSystemException;

	/**
	 * Updates the data about a specific customer. Notifies (through onlooker) if by
	 * any reasons there was no customer which was updated.
	 * 
	 * @param customer
	 *            - a customer which parameters values should be replaced and stored
	 *            in the database as a new data about a specific customer. The
	 *            customer parameter ID should be equals to the ID of the
	 *            corresponding customer as it stored in the database.
	 * @throws CouponSystemException
	 *             if there are serious problems with connection to the database.
	 */
	void updateCustomer(Customer customer) throws CouponSystemException;

	/**
	 * Removes the data about a specific customer from the database. Notifies
	 * (through onlooker) if by any reasons there was no customer which was deleted.
	 * 
	 * @param customer
	 *            - a customer to remove with the parameter ID value equals to the
	 *            ID of the corresponding customer as it stored in the database.
	 * @throws CouponSystemException
	 *             if there are serious problems with connection to the database.
	 */
	void deleteCustomer(Customer customer) throws CouponSystemException;

	/**
	 * Returns all customers stored in the database.
	 * 
	 * @return all customers from the database. If not any - returns the collection
	 *         which size is zero. Returns null and notifies (through onlooker) if
	 *         received any problems with connection to the database.
	 * @throws CouponSystemException
	 *             if there are serious problems with connection to the database.
	 */
	Collection<Customer> getAllCustomers() throws CouponSystemException;

	/**
	 * Returns all coupons which were bought by the specific customer.
	 * 
	 * @return a collection of coupons which were bought by the last customer, who
	 *         successfully logged through this instance. If the customer has not
	 *         any coupons - returns the collection which size is zero. Returns null
	 *         and notifies (through onlooker) if received any problems with
	 *         connection to the database or if not any customer logged through this
	 *         instance or if indeed a customer logged but even so was not found by
	 *         any reason.
	 * @throws CouponSystemException
	 *             if there are serious problems with connection to the database.
	 */
	Collection<Coupon> getCoupons() throws CouponSystemException;

	/**
	 * Returns all coupons which were bought by the specific customer under the
	 * prices are lower than the given value.
	 * 
	 * @param price
	 *            - a value which uses as a cutoff filter for the all customer
	 *            coupons which prices are equals to it or higher.
	 * @return a collection of coupons which prices are lower than the given
	 *         parameter and which were bought by the last customer, who
	 *         successfully logged through this instance. If the customer has not
	 *         any coupons which meets the requirements - it returns the collection
	 *         which size is zero. Returns null and notifies (through onlooker) if
	 *         received any problems with connection to the database. Or if not any
	 *         customer logged through this instance. Or if indeed a customer logged
	 *         but even so was not found by any reason. Or if the price value is
	 *         illegal (may varied on a different databases).
	 * @throws CouponSystemException
	 *             if there are serious problems with connection to the database.
	 */
	Collection<Coupon> getCouponsByPrice(double price) throws CouponSystemException;

	/**
	 * Returns all coupons which were bought by the specific customer which types
	 * are equals to the given type value.
	 * 
	 * @param type
	 *            - a value which uses as a cutoff filter for the all customer
	 *            coupons which types are not equals to it.
	 * @return a collection of coupons which type are equals to the given type value
	 *         and which were bought by the last customer, who successfully logged
	 *         through this instance. If the customer has not any coupons which
	 *         meets the requirements- it returns the collection which size is zero.
	 *         Returns null and notifies (through onlooker) if received any problems
	 *         with connection to the database. Or if not any customer logged
	 *         through this instance. Or if indeed a customer logged but even so was
	 *         not found by any reason.
	 * @throws CouponSystemException
	 *             if there are serious problems with connection to the database.
	 */
	Collection<Coupon> getCouponsByType(CouponType type) throws CouponSystemException;

	/**
	 * Returns all coupons which were bought by the specific customer.
	 * 
	 * @param custId
	 *            - the unique value which corresponds to the ID of the customer as
	 *            it stored in the database.
	 * @return a collection of coupons which were bought by the customer. If the
	 *         customer has not any coupons - returns the collection which size is
	 *         zero. Returns null and notifies (through onlooker) if received any
	 *         problems with connection to the database. Or if by any reasons there
	 *         is no customer was found with ID equals to the given parameter.
	 * @throws CouponSystemException
	 *             if there are serious problems with connection to the database.
	 */
	Collection<Coupon> getCustomerCoupons(long custId) throws CouponSystemException;

	/**
	 * Records a purchase of the coupon and marks as it owner the last customer ,
	 * who successfully logged through this instance. It also updates the amount of
	 * the coupon (decreases by one). Notifies (through onlooker) if by any reasons
	 * purchase was not happens.
	 * 
	 * @param coupon
	 *            - the unique value which corresponds to the ID of the coupon
	 *            wanted to purchase as it stored in the database.
	 * @throws CouponSystemException
	 *             if there are serious problems with connection to the database.
	 */
	void purchaseCoupon(Coupon coupon) throws CouponSystemException;

	/**
	 * Removes the coupon which was purchased early from the customer, it owner. It
	 * also updates the amount of the coupon (increases by one). Notifies (through
	 * onlooker) if by any reasons remove was not happens.
	 * 
	 * @param custId
	 *            - the unique value which corresponds to the ID of the customer who
	 *            owns a coupon which should removed.
	 * @param couponId
	 *            - the unique value which corresponds to the ID of the coupon which
	 *            should be removed from its owner.
	 * @throws CouponSystemException
	 *             if there are serious problems with connection to the database.
	 */
	void removeCouponFromTheCustomer(long custId, long couponId) throws CouponSystemException;

	/**
	 * Checks if exists a customer in the database with name and password equals to
	 * the given values.
	 * 
	 * @param custName
	 *            - a customer name as it should appear in the database.
	 * @param password
	 *            - a customer password as it should appear in the database.
	 * @return true if exists a customer in the database with name and password
	 *         equals to the given values. Otherwise it returns false.
	 * @throws CouponSystemException
	 *             if received any problems with connection to the database.
	 */
	boolean login(String custName, String password) throws CouponSystemException;

	/**
	 * Returns the ID of the last customer , who successfully logged through this
	 * instance.
	 * 
	 * @return the ID of the last customer , who successfully logged through this
	 *         instance as it stored in the database.
	 */
	long getObtainedId();

}