package exceptions;

import beans.Customer;

/**
 * Stores a data about some common exception which can arise during the work
 * with objects of Customer class.
 * 
 * @author AlexanderZhilokov
 *
 */
public class CustomerDBDAOException extends CouponSystemException {

	/**
	 * Needed for compiler to stop yelling at me.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Uses as hash codes for integer representation to the customer parameters
	 * which were the reasons for an exception.
	 */
	public final static int HASH_ID = 1 << 0, HASH_NAME = 1 << 1, HASH_PASS = 1 << 2;

	/**
	 * Stores the object which was under a work than conditions for the exception
	 * arises.
	 */
	public Customer customer;

	/**
	 * Creates an instance of the class CustomerDBDAOException.
	 * 
	 * @param customer
	 *            - the object which was under a work than conditions for the
	 *            exception arises.
	 * @param msg
	 *            - some text description of the conditions where from exception
	 *            arises.
	 * @param rept
	 *            - some integer representation of the conditions where from
	 *            exception arises.
	 */
	public CustomerDBDAOException(Customer customer, String msg, int rept) {
		this.rept = rept;
		this.customer = customer;
		this.msg = msg;
	}

}