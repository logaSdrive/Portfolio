package exceptions;

/**
 * Stores a data about serious exception which can arise during the work of
 * different classes of this project and may cause to a corruption of the
 * integrity of the data in the database if the coupon system continues to run.
 * 
 * @author AlexanderZhilokov
 *
 */
public class PossibleDBCorruptionException extends CouponSystemException {

	/**
	 * Needed for compiler to stop yelling at me.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Stores some text description of the conditions where from exception arises.
	 */
	public String msg;

	/**
	 * Creates an instance of the class PossibleDBCorruptionException.
	 * 
	 * @param msg
	 *            - some text description of the conditions where from exception
	 *            arises.
	 */
	public PossibleDBCorruptionException(String msg) {
		this.msg = msg;
	}

}