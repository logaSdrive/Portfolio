package exceptions;

/**
 * Stores a data about some common exception which can arise during the work of
 * DailyCouponExpirationTask of this project.
 * 
 * @author AlexanderZhilokov
 *
 */
public class DailyTaskException extends RuntimeException {

	/**
	 * Needed for compiler to stop yelling at me.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Stores some text description of the conditions where from exception arises.
	 */
	String msg;

	/**
	 * Creates an instance of the class DailyTaskException.
	 * 
	 * @param msg
	 *            - some text description of the conditions where from exception
	 *            arises.
	 */
	public DailyTaskException(String msg) {
		this.msg = msg;
	}

}