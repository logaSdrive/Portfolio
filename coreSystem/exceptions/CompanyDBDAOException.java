package exceptions;

import beans.Company;

/**
 * Stores a data about some common exception which can arise during the work
 * with objects of Company class.
 * 
 * @author AlexanderZhilokov
 *
 */
public class CompanyDBDAOException extends CouponSystemException {

	/**
	 * Needed for compiler to stop yelling at me.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Uses as hash codes for integer representation to the company parameters which
	 * were the reasons for an exception.
	 */
	public final static int HASH_ID = 1 << 0, HASH_NAME = 1 << 1, HASH_PASS = 1 << 2, HASH_MAIL = 1 << 3;

	/**
	 * Stores the object which was under a work than conditions for the exception
	 * arises.
	 */
	public Company company;

	/**
	 * Creates an instance of the class CompanyDBDAOException.
	 * 
	 * @param company
	 *            - the object which was under a work than conditions for the
	 *            exception arises.
	 * @param msg
	 *            - some text description of the conditions where from exception
	 *            arises.
	 * @param rept
	 *            - some integer representation of the conditions where from
	 *            exception arises.
	 */
	public CompanyDBDAOException(Company company, String msg, int rept) {
		this.rept = rept;
		this.company = company;
		this.msg = msg;
	}

}