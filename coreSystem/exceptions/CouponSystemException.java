package exceptions;

/**
 * Stores a data about some common exception which can arise during the work of
 * different classes of this project.
 * 
 * @author AlexanderZhilokov
 *
 */
public class CouponSystemException extends Exception {

	/**
	 * Needed for compiler to stop yelling at me.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Stores some text description of the conditions where from exception arises.
	 */
	public String msg;

	/**
	 * Stores some integer representation of the conditions where from exception
	 * arises.
	 */
	public int rept;

	/**
	 * Creates an instance of the class CouponSystemException.
	 */
	public CouponSystemException() {
	}

	/**
	 * Creates an instance of the class CouponSystemException.
	 * 
	 * @param msg
	 *            - some text description of the conditions where from exception
	 *            arises.
	 */
	public CouponSystemException(String msg) {
		this.msg = msg;
	}

	/**
	 * Creates an instance of the class CouponSystemException.
	 * 
	 * @param id
	 *            - some integer representation of the conditions where from
	 *            exception arises.
	 * @param msg
	 *            - some text description of the conditions where from exception
	 *            arises.
	 */
	public CouponSystemException(int id, String msg) {
		this(msg);
		this.rept = id;
	}

}