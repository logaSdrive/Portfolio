package system;

import java.util.Calendar;
import java.util.Date;

import dao.CouponDAO;
import exceptions.CouponSystemException;
import exceptions.DailyTaskException;

/**
 * Cleanse the database from the expired coupons. Meant to be launched in a
 * separate thread (implements runnable). Once in a day (one minute after
 * midnight) it wakes up and removes all the coupons from the database which end
 * dates is before the current system time.
 * 
 * @author AlexanderZhilokov
 *
 */
public class DailyCouponExpirationTask implements Runnable {

	/**
	 * Uses it to calculate the time until the midnight.
	 */
	final Calendar cal;

	/**
	 * Says if the current tread was interrupted.
	 */
	private boolean timeToQuite;

	/**
	 * Stores the reference to the current thread.
	 */
	private Thread task;

	/**
	 * Stores an driver to work with the coupons data from the database.
	 */
	private CouponDAO couponDAO;

	/**
	 * Provides the execution for a cleanup on the expired coupons and it meant to
	 * be used as method run() into an instance of the Thread class. Than invoked it
	 * sets the clock to sleep until one minute after midnight. Than times comes it
	 * cleans the database from all the coupons which end dates is before the
	 * current system time. If there is any connections problems to the database it
	 * throws the DailyTaskException.
	 */
	@Override
	public void run() throws DailyTaskException {
		task = Thread.currentThread();
		while (!timeToQuite) {
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 1);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			cal.add(Calendar.DAY_OF_MONTH, 1);
			Date midnight = cal.getTime();
			long untilMidnight = midnight.getTime() - System.currentTimeMillis();
			try {
				Thread.sleep(untilMidnight);
				try {
					couponDAO.removeExpiredCoupons();
				} catch (CouponSystemException e) {
					timeToQuite = true;
					throw new DailyTaskException("Failed to execute daily coupon expiration." + e.msg);
				}
			} catch (InterruptedException e) {
				break;
			}
		}
	}

	/**
	 * Creates an instance of the DailyCouponExpirationTask class.
	 * 
	 * @param couponDAO
	 *            - the driver for to work with the coupons data from the database.
	 */
	public DailyCouponExpirationTask(CouponDAO couponDAO) {
		timeToQuite = false;
		cal = Calendar.getInstance();
		this.couponDAO = couponDAO;
	}

	/**
	 * Stops the execution of the daily expiration coupon task (interrupts the
	 * current thread).
	 */
	public void stopTask() {
		timeToQuite = true;
		task.interrupt();
	}

}