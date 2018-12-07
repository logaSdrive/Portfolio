package system;

import daoDB.CouponDBDAO;
import database.ConnectionPool;
import exceptions.CouponSystemException;
import facades.AdminFacade;
import facades.ClientCouponFacade;
import facades.ClientType;
import facades.CompanyFacade;
import facades.CustomerFacade;
import facades.SuperAdminFacade;

/**
 * Implements a Coupon System - a system which provides to a different types of
 * clients different sets of the operation on coupons and stores all the
 * necessary data in a specific database.
 * 
 * @author AlexanderZhilokov
 *
 */
public class CouponSystem {

	/**
	 * Stores the only instance of the class.
	 */
	private static volatile CouponSystem instance;

	/**
	 * Provides a connections to the database.
	 */
	private static ConnectionPool pool;

	/**
	 * Refers to the instance which cleanse the database from the expired coupons
	 * and meant to be launched in a separate thread (implements runnable).
	 */
	private DailyCouponExpirationTask expiredCouponsCleaner;

	/**
	 * Provides the reference to the single instance of the CouponSystem class or
	 * creates new if there is not any. It is thread safe.
	 * 
	 * @return a reference to the single instance of this class.
	 */
	public static CouponSystem getInstance() {
		if (instance == null) {
			synchronized (CouponSystem.class) {
				if (instance == null)
					instance = new CouponSystem();
			}
		}
		return instance;
	}

	/**
	 * Provides the different types of the facades (sets of interactions between the
	 * Coupon System and standard actions associated with the specified client type)
	 * to the different types of clients existing in the Coupon System.
	 * 
	 * @param name
	 *            - a client name as it is known to the Coupon System.
	 * @param password
	 *            - a client password as it is known to the Coupon System.
	 * @param type
	 *            - the type of the facades in which you can be logged through the
	 *            Coupon System and which corresponds with the given name and
	 *            password.
	 * @return a facade if a client with the given name, password and type is known
	 *         to the Coupon System. Otherwise returns null.
	 */
	public ClientCouponFacade login(String name, String password, ClientType type) {
		switch (type) {
		case Admin:
			return (new AdminFacade(pool)).login(name, password);
		case SuperAdmin:
			return (new SuperAdminFacade(pool)).login(name, password);
		case Company:
			return (new CompanyFacade(pool)).login(name, password);
		case Customer:
			return (new CustomerFacade(pool)).login(name, password);
		default:
			return null;
		}
	}

	/**
	 * Stops maintenance all requests from the clients immediately (equals to say:
	 * closes all connection to the database). It also stops all services which were
	 * launched by the Coupon System to work in a parallel threads (as a
	 * DailyCouponExpirationTask for example).
	 */
	public void shutdown() {
		expiredCouponsCleaner.stopTask();
		try {
			pool.closeAllConnections();
		} catch (CouponSystemException e) {
			System.out.println("The shutdown of the Coupon System was completed not in a normal way.");
			System.out.println(e.msg);
		}
	}

	/**
	 * Stops maintenance all the new requests from the clients immediately and waits
	 * for some time before halting the maintenance of the already taken requests
	 * from the clients. (All services which were launched by the Coupon System to
	 * work in a parallel threads (as a DailyCouponExpirationTask for example) will
	 * stops only when the given time is passed).
	 * 
	 * @param patience
	 *            - a time to wait in a milliseconds before a full shutdown of the
	 *            Coupon System will occur.
	 */
	public void shutdownPatiently(long patience) {
		expiredCouponsCleaner.stopTask();
		try {
			pool.closeAllPatiently(patience);
		} catch (CouponSystemException e) {
			System.out.println("The shutdown of the Coupon System was completed not in a normal way.");
			System.out.println(e.msg);
		}
	}

	/**
	 * Creates the new instance of the class and launches all required services
	 * which meant to work in a parallel threads (as a DailyCouponExpirationTask for
	 * example). 
	 */
	private CouponSystem() {
		try {
			pool = ConnectionPool.getInstance();
		} catch (CouponSystemException e) {
			System.out.println("The initialization of the Coupon System failed.");
			System.out.println(e.msg);
		}
		expiredCouponsCleaner = new DailyCouponExpirationTask(new CouponDBDAO(pool, new Looker()));
		Thread tr = new Thread(expiredCouponsCleaner);
		tr.start();
	}

}