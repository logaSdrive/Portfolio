package facades;

/**
 * Provides an abstraction for a way to login into a different facades of the
 * Coupon System.
 * 
 * @author AlexanderZhilokov
 *
 */
public interface ClientCouponFacade {

	/**
	 * Checks if the given name and password belongs to the user which is known to
	 * the Coupon System and valid. And if so, it provides the reference to a new
	 * instance of the client facade (sets of interactions between the Coupon System
	 * and standard actions associated with the specified client type).
	 * 
	 * @param name
	 *            - the unique name which corresponds to the name of the user as it
	 *            known to the Coupon System.
	 * @param password
	 *            - the password which corresponds to the passwords of the user as
	 *            it known to the Coupon System.
	 * @return the reference to a new instance of the client facade if the given
	 *         name and password belongs to the user which is known to the Coupon
	 *         System and is valid. If not - returns null.
	 */
	ClientCouponFacade login(String name, String password);

}