package exceptions;

import beans.Coupon;

/**
 * Stores a data about some common exception which can arise during the work
 * with objects of Coupon class.
 * 
 * @author AlexanderZhilokov
 *
 */
public class CouponDBDAOException extends CouponSystemException {

	/**
	 * Needed for compiler to stop yelling at me.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Uses as hash codes for integer representation to the coupon parameters which
	 * were the reasons for an exception.
	 */
	public final static int HASH_ID = 1 << 0, HASH_TITLE = 1 << 1, HASH_START_DATE = 1 << 2, HASH_END_DATE = 1 << 3,
			HASH_AMOUNT = 1 << 4, HASH_TYPE = 1 << 5, HASH_MESSAGE = 1 << 6, HASH_PRICE = 1 << 7, HASH_IMAGE = 1 << 8;

	/**
	 * Stores the object which was under a work than conditions for the exception
	 * arises.
	 */
	public Coupon coupon;

	/**
	 * Creates an instance of the class CouponDBDAOException.
	 * 
	 * @param coupon
	 *            - the object which was under a work than conditions for the
	 *            exception arises.
	 * @param msg
	 *            - some text description of the conditions where from exception
	 *            arises.
	 * @param rept
	 *            - some integer representation of the conditions where from
	 *            exception arises.
	 */
	public CouponDBDAOException(Coupon coupon, String msg, int rept) {
		this.coupon = coupon;
		this.msg = msg;
		this.rept = rept;
	}

}