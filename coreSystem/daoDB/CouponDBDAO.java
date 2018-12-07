package daoDB;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import beans.Coupon;
import beans.CouponType;
import dao.CompanyDAO;
import dao.CouponDAO;
import dao.CustomerDAO;
import database.ChainLock;
import database.ConnectionPool;
import database.Jabberwocky;
import database.Jabberwocky.Add;
import database.Jabberwocky.GreateWork;
import database.Jabberwocky.Pop;
import static exceptions.CouponDBDAOException.*;
import exceptions.CouponSystemException;
import exceptions.JabberwockyException;
import exceptions.PossibleDBCorruptionException;
import system.Looker;

/**
 * Provides set of standard actions between Apache Derby database for coupons.
 * It also implements some basic logic because of the integrity reasons for all
 * coupons data in the database. It works only when coupons ID are
 * auto-incremented by the database and it assumes that all IDs are greater than
 * zero and all coupons titles that previously stored in the database are
 * unique. It also provides some basic synchronization at the level of the
 * tables in the database: all method which affects more than a one table at
 * once are synchronized by the tables they affects.
 * 
 * @author AlexanderZhilokov
 */
public class CouponDBDAO implements CouponDAO {

	/**
	 * Provides a connections to the database.
	 */
	private ConnectionPool pool;

	/**
	 * Stores the ID value the last company or customer which successfully logged.
	 */
	private long id;

	/**
	 * Uses for delivery warnings to the client facades.
	 */
	public Looker onlooker;

	/**
	 * Specifies behavior for the method 'clarify(coupon)': if true - than
	 * 'clarify(coupon)' should throws the exception, else only submit the hash code
	 * of the exception. (By default its true, only method 'validate(coupon)' use it
	 * in the 'silent' mode.)
	 */
	private boolean clarifySoundly;

	/**
	 * Stores the hash code of the exception from method 'clarify(coupon)' if
	 * needed.
	 */
	private int rept;

	/**
	 * Uses as integer representation for coupons parameters
	 */
	final static int ID = 0, TITLE = 1, START_DATE = 2, END_DATE = 3, AMOUNT = 4, TYPE = 5, MESSAGE = 6, PRICE = 7,
			IMAGE = 8, NUMBER_OF_PARAMETERS = 9;

	/**
	 * Creates a new instance which allows to work with coupons beans in a some
	 * abstraction level as interface GreatWork defines.
	 * 
	 * @return an instance which can be used as translator to the abstraction level
	 *         which @see Jabberwocky knows to used it.
	 */
	public static GreateWork adapter() {
		return new CouponAdapter();
	}

	/**
	 * Provides the necessary tools for Jabberwocky to work with coupon beans.
	 * 
	 * @author AlexanderZhilokov
	 */
	private static class CouponAdapter implements GreateWork {

		/**
		 * Stores the instance of coupon which is under the current workflow
		 */
		private Coupon coupon;

		/**
		 * Provides addition for an coupon bean to a current workflow in an abstract
		 * way.
		 */
		@Override
		public void setBean(Object bean) {
			this.coupon = (Coupon) bean;
		}

		/**
		 * Provides conversion to an coupon bean back from abstract to its original form
		 */
		@Override
		public Coupon getBean() {
			return coupon;
		}

		/**
		 * Provides initiations for a new coupon bean to a current workflow in an
		 * abstract way.
		 */
		@Override
		public void newBean() {
			this.coupon = new Coupon();
		}

		/**
		 * Provides setters to the coupon parameters which is under current workflow in
		 * an abstract way: they (setters) all must works with string as a parameter to
		 * set.
		 */
		@Override
		public Add[] drawSetters() {
			Add[] setters = new Add[NUMBER_OF_PARAMETERS];
			setters[ID] = (value) -> coupon.setId(Long.parseLong(value));
			setters[TITLE] = coupon::setTitle;
			setters[START_DATE] = (value) -> coupon
					.setStartDate(new java.util.Date(java.sql.Date.valueOf(value).getTime()));
			setters[END_DATE] = (value) -> coupon
					.setEndDate(new java.util.Date(java.sql.Date.valueOf(value).getTime()));
			setters[AMOUNT] = (value) -> coupon.setAmount(Integer.parseInt(value));
			setters[TYPE] = (value) -> coupon.setType(CouponType.valueOf(value));
			setters[MESSAGE] = coupon::setMessage;
			setters[PRICE] = (value) -> coupon.setPrice(Double.parseDouble(value));
			setters[IMAGE] = coupon::setImage;
			return setters;
		}

		/**
		 * Provides getters to the coupon parameters which is under current workflow in
		 * an abstract way: they (getters) must transform all the returns values to
		 * strings which also must corresponds to type of data in the database as it
		 * stored.
		 */
		@Override
		public Pop[] drawGetters() {
			Pop[] getters = new Pop[NUMBER_OF_PARAMETERS];
			getters[ID] = () -> Long.toString(coupon.getId());
			getters[TITLE] = coupon::getTitle;
			getters[START_DATE] = () -> (new java.sql.Date(coupon.getStartDate().getTime())).toString();
			getters[END_DATE] = () -> (new java.sql.Date(coupon.getEndDate().getTime())).toString();
			getters[AMOUNT] = () -> Integer.toString(coupon.getAmount());
			getters[TYPE] = () -> coupon.getType().name();
			getters[MESSAGE] = coupon::getMessage;
			getters[PRICE] = () -> Double.toString(coupon.getPrice());
			getters[IMAGE] = coupon::getImage;
			return getters;
		}

	}

	/**
	 * Checks if the coupon parameters are legal for the inserting into the
	 * database. if coupon parameters was found to be illegal for insertion into the
	 * database: blank strings values, oversized string values, null values,
	 * negative numerical values, dates which are already expired, start date which
	 * is after the end date - will notify through onlooker.
	 * 
	 * @param coupon
	 *            - the coupon with all it data which should be checked.
	 * 
	 */
	public void clarify(Coupon coupon) {
		String msg = "";
		int rept = 0;
		boolean somethingWrong = false;
		if (coupon.getTitle() == null || coupon.getTitle().trim().equals("")) {
			msg += "The coupon's title is absent. Means you should give a title to the coupon. ";
			somethingWrong = true;
			rept |= HASH_TITLE;
		} else if (coupon.getTitle().length() > 40) {
			msg += "The coupon's title is too long. It has to contain no more than 40 letters. ";
			somethingWrong = true;
			rept |= HASH_TITLE;
		}
		if (coupon.getStartDate() == null) {
			msg += "The coupon missing a start date. Means you should set a start date. ";
			somethingWrong = true;
			rept |= HASH_START_DATE;
		}
		if (coupon.getEndDate() == null) {
			msg += "The coupon has no a end date. Means you should set a end date. ";
			somethingWrong = true;
			rept |= HASH_END_DATE;
		} else if (coupon.getEndDate().before(new Date(System.currentTimeMillis()))) {
			msg += "The coupon end date already expired. Means you should set a end date at least one day forward from today. ";
			somethingWrong = true;
			rept |= HASH_END_DATE;
		} else if (coupon.getStartDate() != null && coupon.getEndDate().before(coupon.getStartDate())) {
			msg += "The coupon end date is before the start date. Means you should set a end date at least one day forward from the start date. ";
			somethingWrong = true;
			rept |= HASH_END_DATE;
		}
		if (coupon.getAmount() < 0) {
			msg += "The coupon amount must be bigger or equal to zero. ";
			somethingWrong = true;
			rept |= HASH_AMOUNT;
		}
		if (coupon.getType() == null) {
			msg += "The coupon has no type. Means you should set a type for the coupon. ";
			somethingWrong = true;
			rept |= HASH_TYPE;
		}
		if (coupon.getMessage() == null || coupon.getMessage().trim().equals("")) {
			msg += "The coupon's message is absent. Means you should give any message to the coupon. ";
			somethingWrong = true;
			rept |= HASH_MESSAGE;
		} else if (coupon.getMessage().length() > 1000) {
			msg += "The coupon's message is too large. It has to contain no more than 1000 letters. ";
			somethingWrong = true;
			rept |= HASH_MESSAGE;
		}
		if (coupon.getPrice() < 0) {
			msg += "The coupon price cant be a negative value. You should set it at least equal to zero. ";
			somethingWrong = true;
			rept |= HASH_PRICE;
		}
		if (coupon.getImage() == null || coupon.getImage().trim().equals("")) {
			msg += "The coupon's image is undefined. Means you should define an url link for the coupon image.";
			somethingWrong = true;
			rept |= HASH_IMAGE;
		} else if (coupon.getImage().length() > 256) {
			msg += "The coupon's image url is too long. It has to contain no more than 256 symbols.";
			somethingWrong = true;
			rept |= HASH_IMAGE;
		}
		if (somethingWrong) {
			if (clarifySoundly) {
				onlooker.markFailure(msg, rept);
			} else {
				this.rept = rept;
			}
		}
	}

	/**
	 * Stores given coupons values into the database and marks as a creator the last
	 * company, who successfully logged through this instance. Except for the coupon
	 * ID - it is auto-incremented. Note that title must be unique. Before using it
	 * make sure that onlooker is not containing a fails. If storing was not
	 * happened: illegal coupons parameters or title which already in use or there
	 * is no any stored company ID value (there are not any company which
	 * successfully logged through this instance) or if the stored company ID is not
	 * valid any more (the company was deleted) or some connection problems - all
	 * these will be notified through the onlooker by marking a failure. If there
	 * are a synchronization problems with the connection - throws exception
	 * (CouponSystemException). If there are some serious problems with a specific
	 * connection (can not be trusted because of unsuccessful roll back, means there
	 * is a danger to the integrity data in the database) - will also throws
	 * exception (PossibleDBCorruptionException).
	 */
	@Override
	public void createCoupon(Coupon coupon) throws CouponSystemException {
		clarify(coupon);
		if (onlooker.hasFails()) {
			return;
		}
		if (coupon.getId() != 0) {
			String warning = "About coupon - " + coupon.getTitle()
					+ ": a little warning about ID. It seems you change it, but generally for all coupons ID is auto generated by the database, means your shouldn't affect it at all. "
					+ "Your will get it (ID) from the database. Just a reminder.";
			onlooker.markWarning(warning);
		}
		Collection<Object> coupons = new ArrayList<>();
		coupons.add(coupon);
		String companyIdCheck = "update Company set COMP_NAME = COMP_NAME where ID = " + id;
		String updateCoupon = "insert into Coupon (TITLE, START_DATE, END_DATE, AMOUNT, TYPE, MESSAGE, PRICE, IMAGE) "
				+ "select ?, ?, ?, ?, ?, ?, ?, ? from Coupon where TITLE = ? having count(*) = 0";
		String updateCompAndCoupon = "insert into Company_Coupon values (" + id
				+ ", (select ID from Coupon where TITLE = \"" + coupon.getTitle() + "\"))";
		synchronized (ChainLock.TableCoupon) {
			synchronized (ChainLock.TableCompanyCoupon) {
				Connection con = pool.getConnection();
				try {
					Jabberwocky.via(con).toTheTick().execute(companyIdCheck).pt().from(coupons).onTheRun()
							.execute(updateCoupon)
							.using(adapter(), TITLE, START_DATE, END_DATE, AMOUNT, TYPE, MESSAGE, PRICE, IMAGE, TITLE)
							.asItIs().execute(updateCompAndCoupon).pt().fin();
				} catch (JabberwockyException e) {
					switch (e.rept) {
					case -1:
						onlooker.markFailure("Your ID: " + id + " is not valid anymore. Sorry about that.");
						break;
					default:
						onlooker.markFailure(
								"A coupon's title must be unique. Means in database already exists some coupon with the same title as "
										+ coupon.getTitle() + ". You should change it.",
								HASH_TITLE);
						break;
					}
				} catch (SQLException e) {
					onlooker.markFailure("There are probably some connection problems. Sorry about that.");
					try {
						con.rollback();
						con.setAutoCommit(true);
					} catch (SQLException anotherE) {
						throw new PossibleDBCorruptionException(
								"One of connection from connection pool became corrupted.");
					}
				} finally {
					pool.returnConnection(con);
				}
			}
		}
	}

	/**
	 * Returns the data about a specific coupon. Assumes that only CompanyFacade
	 * will used it, and returns the data about a specific coupon only if the coupon
	 * was created by the last company which successfully logged through this
	 * instance. Before using it make sure that onlooker is not containing a fails.
	 * If uploading was not happened (unsuccessful search or the coupon was not
	 * created by the last company which successfully logged through this instance
	 * or some connection problems) - returns null and will notify through onlooker
	 * about the failure. If there are a synchronization problems with the
	 * connection - throws exception (CouponSystemException).
	 */
	@Override
	public Coupon readCoupon(long id) throws CouponSystemException {
		if (id <= 0) {
			onlooker.markFailure("All coupons ID must be greater than zero. Means the coupon ID - " + id
					+ " is invalide and there is no coupon with same ID in the database.");
			return null;
		}
		isItMineCoupon(id);
		return (onlooker.hasFails()) ? null : readCouponIfSo(id);
	}

	/**
	 * Returns the data about a specific coupon. Assumes that only CompanyFacade
	 * will used it, and returns the data about a specific coupon only if the coupon
	 * was created by the last company which successfully logged through this
	 * instance. Before using it make sure that onlooker is not containing a fails.
	 * If uploading was not happened (unsuccessful search or the coupon was not
	 * created by the last company which successfully logged through this instance
	 * or some connection problems) - returns null and will notify through onlooker
	 * about the failure. If there are a synchronization problems with the
	 * connection - throws exception (CouponSystemException).
	 */
	@Override
	public Coupon readCoupon(String title) throws CouponSystemException {
		if (title == null || title.trim().equals("")) {
			onlooker.markFailure("The coupon's title is absent. Means the coupon title - "
					+ " is invalide and there is no coupon with same title in the database.");
			return null;
		}
		if (title.length() > 40) {
			onlooker.markFailure(
					"The coupon's title is too long. It has to contain no more than 40 letters. Means the coupon title - "
							+ title + " is invalide and there is no coupon with same title in the database.");
			return null;
		}
		isItMineCoupon(title);
		return (onlooker.hasFails()) ? null : readCouponIfSo(title);
	}

	/**
	 * Returns the data about a specific coupon. If uploading was not happened
	 * (unsuccessful search or some connection problems) - returns null and will
	 * notify through onlooker. If there are a synchronization problems with the
	 * connection - throws exception (CouponSystemException).
	 */
	@Override
	public Coupon readCouponAsAdmin(long id) throws CouponSystemException {
		if (id <= 0) {
			onlooker.markFailure("All coupons ID must be greater than zero. Means the coupon ID - " + id
					+ " is invalide and there is no coupon with same ID in the database.");
			return null;
		}
		return readCouponIfSo(id);
	}

	/**
	 * Returns the data about a specific coupon. If uploading was not happened
	 * (unsuccessful search or some connection problems) - returns null and will
	 * notify through onlooker. If there is are synchronization problems with the
	 * connection - throws exception (CouponSystemException).
	 */
	@Override
	public Coupon readCouponAsAdmin(String title) throws CouponSystemException {
		if (title == null || title.trim().equals("")) {
			onlooker.markFailure("The coupon's title is absent. . Means the coupon title - "
					+ " is invalide and there is no coupon with same title in the database.");
			return null;
		}
		if (title.length() > 40) {
			onlooker.markFailure(
					"The coupon's title is too long. It has to contain no more than 40 letters. Means the coupon title - "
							+ title + " is invalide and there is no coupon with same title in the database.");
			return null;
		}
		return readCouponIfSo(title);
	}

	/**
	 * Updates the data about a specific coupons. Except for the coupons titles,
	 * amount and ID - remains unchanged. Assumes that only CompanyFacade will used
	 * it, and updates the data about of the specific coupon only if the specific
	 * coupon is created by the company which successfully logged last time through
	 * this instance. If the given coupon was not updated or coupon is not created
	 * by the last company which successfully logged through this instance or some
	 * connection problems or the given coupon ID value is illegal - will notify
	 * through onlooker. Before using it make sure that onlooker is not containing a
	 * fails. If there are a synchronization problems with the connection - throws
	 * exception (CouponSystemException).
	 */
	@Override
	public void updateCoupon(Coupon coupon) throws CouponSystemException {
		if (coupon.getId() <= 0) {
			onlooker.markFailure("All coupons ID must be greater than zero. Means the coupon ID - " + coupon.getId()
					+ " is invalide and there is no coupon with same ID in the database.", HASH_ID);
			return;
		}
		isItMineCoupon(coupon.getId());
		if (!onlooker.hasFails()) {
			updateCouponIfSo(coupon);
		}
	}

	/**
	 * Updates the data about a specific coupons. Except for the coupons titles,
	 * amount and ID - remains unchanged. If the given coupon was not updated
	 * (unsuccessful search or there is a problems with given coupons parameters or
	 * some connection problems - will notify through onlooker. Before using it make
	 * sure that onlooker is not containing a fails. If there are a synchronization
	 * problems with the connection - throws exception (CouponSystemException).
	 */
	@Override
	public void updateCouponAsAdmin(Coupon coupon) throws CouponSystemException {
		if (coupon.getId() <= 0) {
			onlooker.markFailure("All coupons ID must be greater than zero. Means the coupon ID - " + coupon.getId()
					+ " is invalide and there is no coupon with same ID in the database.", HASH_ID);
			return;
		}
		updateCouponIfSo(coupon);
	}

	/**
	 * Removes the data about a specific coupon from the database. Assumes that only
	 * CompanyFacade will used it, and deletes the specific coupon only if the given
	 * coupon is created by the company which successfully logged last time through
	 * this instance. If the given coupon was not deleted (unsuccessful search or
	 * some connection problems or the coupon was not created by the last company
	 * which successfully logged through this instance - will notify through
	 * onlooker. Before using it make sure that onlooker is not containing a fails.
	 * If there are some serious problems with a specific connection (can not be
	 * trusted because of unsuccessful roll back, means there is a danger to the
	 * integrity data in the database) - throws exception
	 * (PossibleDBCorruptionException). If there are a synchronization problems with
	 * the connection - throws exception (CouponSystemException).
	 */
	@Override
	public void deleteCoupon(Coupon coupon) throws CouponSystemException {
		if (coupon.getId() <= 0) {
			onlooker.markFailure("All coupons ID must be greater than zero. Means the coupon ID - " + coupon.getId()
					+ " is invalide and there is no coupon with same ID in the database.", HASH_ID);
			return;
		}
		isItMineCoupon(coupon.getId());
		if (!onlooker.hasFails()) {
			deleteCouponIfSo(coupon);
		}
	}

	/**
	 * Removes the data about a specific coupon from the database. If the given
	 * coupon was not deleted: unsuccessful search or some connection problems -
	 * will notify through onlooker. Before using it make sure that onlooker is not
	 * containing a fails. If there are some serious problems with a specific
	 * connection (can not be trusted because of unsuccessful roll back, means there
	 * is a danger to the integrity data in the database) - throws exception
	 * (PossibleDBCorruptionException). If there are a synchronization problems with
	 * the connection - throws exception (CouponSystemException).
	 */
	@Override
	public void deleteCouponAsAdmin(Coupon coupon) throws CouponSystemException {
		if (coupon.getId() <= 0) {
			onlooker.markFailure("All coupons ID must be greater than zero. Means the coupon ID - " + coupon.getId()
					+ " is invalide and there is no coupon with same ID in the database.", HASH_ID);
			return;
		}
		deleteCouponIfSo(coupon);
	}

	/**
	 * Returns ArrayList with all the coupons stored in the database. If there are
	 * not any - return ArrayList which size is zero. If there are some connection
	 * problems - returns null and will notify through onlooker. If there are a
	 * synchronization problems with the connection - throws exception
	 * (CouponSystemException).
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Collection<Coupon> getAllCoupons() throws CouponSystemException {
		Collection coupons = new ArrayList<>();
		String sql = "select * from Coupon";
		Connection con = pool.getConnection();
		try {
			Jabberwocky.via(con).into(coupons).execute(sql).using(adapter());
		} catch (SQLException e) {
			onlooker.markFailure("There are probably some connection problems. Sorry about that.");
			return null;
		} finally {
			pool.returnConnection(con);
		}
		return coupons;
	}

	/**
	 * Returns ArrayList with all the coupons stored in the database which types are
	 * equals to the given type value. If there are not any - return ArrayList which
	 * size is zero. If there are some connection problems - returns null and will
	 * notify through onlooker. If there are a synchronization problems with the
	 * connection - throws exception (CouponSystemException).
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Collection<Coupon> getAllCouponsByType(CouponType type) throws CouponSystemException {
		String sql = "select  * from Coupon where TYPE = \"" + type.name() + "\"";
		Collection coupons = new ArrayList<>();
		Connection con = pool.getConnection();
		try {
			Jabberwocky.via(con).into(coupons).execute(sql).using(adapter());
		} catch (SQLException e) {
			onlooker.markFailure("There are probably some connection problems. Sorry about that.");
			return null;
		} finally {
			pool.returnConnection(con);
		}
		return coupons;
	}

	/**
	 * Removes all the coupons from the database which end dates are before the
	 * current date time. If there are some connection problems
	 * (CouponSystemException) or some serious problems with a specific connection
	 * (can not be trusted because of unsuccessful roll back, means there is a
	 * danger to the integrity data in the database (PossibleDBCorruptionException))
	 * - throws exception.
	 */
	@Override
	public void removeExpiredCoupons() throws CouponSystemException {
		java.sql.Date current = new java.sql.Date(System.currentTimeMillis());
		String cleanupCoupon = "delete from Coupon where END_DATE < \"" + current.toString() + "\"";
		String cleanupCompAndCoupon = "delete from Company_Coupon where COUPON_ID  in (select ID from Coupon  where END_DATE < \""
				+ current.toString() + "\")";
		String cleanupCustAndCoupon = "delete from Customer_Coupon where COUPON_ID  in (select ID from Coupon where END_DATE < \""
				+ current.toString() + "\")";
		synchronized (ChainLock.TableCoupon) {
			synchronized (ChainLock.TableCompanyCoupon) {
				synchronized (ChainLock.TableCustomerCoupon) {
					Connection con = pool.getConnection();
					try {
						Jabberwocky.via(con).onTheRun()
								.execute(cleanupCoupon, cleanupCustAndCoupon, cleanupCompAndCoupon).pt().fin();
					} catch (SQLException e) {
						try {
							con.rollback();
							con.setAutoCommit(true);
						} catch (SQLException anotherE) {
							throw new PossibleDBCorruptionException(
									"One of connection from connection pool became corrupted.");
						}
						throw new CouponSystemException(
								"There are probably some connection problems. Sorry about that.");
					} finally {
						pool.returnConnection(con);
					}
				}
			}
		}
	}

	/**
	 * Checks if exists a company in the database with name and password equals to
	 * the given values. And if exists stores it ID. There is only one company ID
	 * which stored by every separate instance, that means each successful login
	 * through same instance of this class will override this ID value. In a more
	 * simple way: only the ID of the last company which successfully logged through
	 * this instance will saved in this specific instance. If there is any
	 * connection problem - throws CouponSystemException.
	 */
	@Override
	public boolean login(String compName, String password, CompanyDAO companyDAO) throws CouponSystemException {
		if (companyDAO.login(compName, password)) {
			id = companyDAO.getObtainedId();
			return true;
		}
		return false;
	}

	/**
	 * Checks if exists a customer in the database with name and password equals to
	 * the given values. And if exists stores it ID. There is only one customer ID
	 * which stored by every separate instance, that means each successful login
	 * through same instance of this class will override this ID value. In a more
	 * simple way: only the ID of the last customer which successfully logged
	 * through this instance will saved in this specific instance. If there is any
	 * connection problem - throws CouponSystemException.
	 */
	@Override
	public boolean login(String custName, String password, CustomerDAO customerDAO) throws CouponSystemException {
		if (customerDAO.login(custName, password)) {
			id = customerDAO.getObtainedId();
			return true;
		}
		return false;
	}

	/**
	 * Checks if the coupon parameters are legal for the inserting into the
	 * database. Uses method 'clarify' in 'silent' mode - forbid him to use the
	 * onlooker and instead transmit the hash code of the exception as a return
	 * statement.
	 */
	@Override
	public int validate(Coupon coupon) {
		clarifySoundly = false;
		this.rept = -1;
		clarify(coupon);
		clarifySoundly = true;
		return this.rept;
	}

	/**
	 * Creates new instance of the class.
	 * 
	 * @param pool
	 *            - through this object new instance will receive connections to the
	 *            database. Saves it by reference, not copies the whole object.
	 * @param onlooker
	 *            - through this object new instance will delivery warnings or fails
	 *            notification if necessary. Means - it only saves the reference,
	 *            and not copies the whole object.
	 */
	public CouponDBDAO(ConnectionPool pool, Looker onlooker) {
		this.pool = pool;
		this.onlooker = onlooker;
		this.clarifySoundly = true;
		this.rept = -1;
	}

	/**
	 * Checks if the given ID value correspond to ID value of any coupons which
	 * company with stored id are created (last company which successfully logged
	 * through this instance). If the given ID value not equals to ID value of any
	 * company coupons (the last company which successfully logged through this
	 * instance) or if there are any connection problems - will notify through
	 * onlooker.
	 * 
	 * @param couponId
	 *            - the unique ID value which corresponds to the ID of the coupon as
	 *            it stored in the database.
	 * @throws CouponSystemException
	 *             if there are problems with synchronization of the connections.
	 */
	private void isItMineCoupon(long couponId) throws CouponSystemException {
		String companyIdCheck = "update Company set COMP_NAME = COMP_NAME where ID in (select COMP_ID from Company_Coupon where COUPON_ID = "
				+ couponId + " and COMP_ID = " + this.id + ")";
		Connection con = pool.getConnection();
		try {
			Jabberwocky.via(con).toTheTick().execute(companyIdCheck).pt();
		} catch (JabberwockyException e) {
			onlooker.markFailure("The coupon with ID - " + couponId
					+ " is not yours. (It also possible that there are not any coupon in the whole database which ID matches the above.)", HASH_ID);
		} catch (SQLException e) {
			onlooker.markFailure("There are probably some connection problems. Sorry about that.");
		} finally {
			pool.returnConnection(con);
		}
	}

	/**
	 * Checks if the given title value correspond to title value of any coupons
	 * which company with stored id are created (last company which successfully
	 * logged through this instance) or if there are any connection problems - will
	 * notify through onlooker.
	 * 
	 * @param title
	 *            - the unique title value which corresponds to the title of the
	 *            coupon as it stored in the database.
	 * @throws CouponSystemException
	 *             if there are problems with synchronization of the connections.
	 */
	private void isItMineCoupon(String title) throws CouponSystemException {
		String companyIdCheck = "update Coupon set TITLE = TITLE where TITLE = \"" + title + "\" and ID in "
				+ "(select COUPON_ID from Company_Coupon where COMP_ID = " + this.id + ")";
		Connection con = pool.getConnection();
		try {
			Jabberwocky.via(con).toTheTick().execute(companyIdCheck).pt();
		} catch (JabberwockyException e) {
			onlooker.markFailure("The coupon with title - " + title
					+ " is not yours. (It also possible that there are not any coupon in the whole database which title matches the above.)");
		} catch (SQLException e) {
			onlooker.markFailure("There are probably some connection problems. Sorry about that.");
		} finally {
			pool.returnConnection(con);
		}
	}

	/**
	 * Returns the data about a specific coupon. if there was no coupon found with
	 * ID equals to the given ID value or if there are any connection problems -
	 * returns null and will notify through onlooker about the failure.
	 * 
	 * @param couponId
	 *            - the unique value which corresponds to the ID of the coupon as it
	 *            stored in the database.
	 * @return the coupon with all it data as it stored in the database.
	 * @throws CouponSystemException
	 *             if there are problems with synchronization of the connections.
	 */
	private Coupon readCouponIfSo(long couponId) throws CouponSystemException {
		Collection<Object> coupons = new ArrayList<>();
		String couponUpload = "select * from Coupon where ID = " + couponId;
		Connection con = pool.getConnection();
		try {
			Jabberwocky.via(con).toTheTick().into(coupons).execute(couponUpload).using(adapter());
		} catch (JabberwockyException e) {
			onlooker.markFailure("The coupon with this ID - " + couponId + " - has not found.");
			return null;
		} catch (SQLException e) {
			onlooker.markFailure("There are probably some connection problems. Sorry about that.");
			return null;
		} finally {
			pool.returnConnection(con);
		}
		return (Coupon) coupons.iterator().next();
	}

	/**
	 * Returns the data about a specific coupon. if there was no coupon found with
	 * title equals to the given value or if there are any connection problems -
	 * returns null and will notify through onlooker about the failure.
	 * 
	 * @param title
	 *            - the unique value which corresponds to the title of the coupon as
	 *            it stored in the database.
	 * @return the coupon with all it data as it stored in the database.
	 * @throws CouponSystemException
	 *             if there are problems with synchronization of the connections.
	 */
	private Coupon readCouponIfSo(String title) throws CouponSystemException {
		Collection<Object> coupons = new ArrayList<>();
		String couponUpload = "select * from Coupon where TITLE = \"" + title + "\"";
		Connection con = pool.getConnection();
		try {
			Jabberwocky.via(con).toTheTick().into(coupons).execute(couponUpload).using(adapter());
		} catch (JabberwockyException e) {
			onlooker.markFailure("The coupon with this title - " + title + " - has not found.");
			return null;
		} catch (SQLException e) {
			onlooker.markFailure("There are probably some connection problems. Sorry about that.");
			return null;
		} finally {
			pool.returnConnection(con);
		}
		return (Coupon) coupons.iterator().next();
	}

	/**
	 * Updates the data about a specific coupon. If there was no coupon found with
	 * ID equals to the given coupon ID or if there are some connection problems -
	 * will notify through onlooker. Before using it make sure that onlooker is not
	 * containing a fails.
	 * 
	 * @param coupon
	 *            - the values of the parameters which should be replaced and stored
	 *            in the database as a new data about a specific coupon. The coupon
	 *            parameter ID should be equals to the ID corresponding coupon as it
	 *            stored in the database.
	 * @throws CouponSystemException
	 *             if there are problems with synchronization of the connections.
	 */
	private void updateCouponIfSo(Coupon coupon) throws CouponSystemException {
		clarify(coupon);
		if (onlooker.hasFails()) {
			return;
		}
		Collection<Object> coupons = new ArrayList<>();
		coupons.add(coupon);
		String couponUpdate = "update Coupon set START_DATE = ?, END_DATE = ?, TYPE = ?, MESSAGE = ?, PRICE = ?, IMAGE = ? "
				+ "where ID = ? and TITLE = ?";
		Connection con = pool.getConnection();
		try {
			Jabberwocky.via(con).toTheTick().from(coupons).execute(couponUpdate).using(adapter(), START_DATE, END_DATE,
					TYPE, MESSAGE, PRICE, IMAGE, ID, TITLE);
		} catch (JabberwockyException e) {
			onlooker.markFailure(
					"There is no coupon with title - " + coupon.getTitle() + " and with ID - " + coupon.getId()
							+ " in the database. Means both uses as a kind of primary keys and you can't update them.",
					HASH_ID | HASH_TITLE);
		} catch (SQLException e) {
			onlooker.markFailure("There are probably some connection problems. Sorry about that.");
		} finally {
			pool.returnConnection(con);
		}
	}

	/**
	 * Removes the data about a specific coupon. If there was no coupon found with
	 * ID equals to the given coupon ID or if there are some connection problems or
	 * if there are a serious problems with a specific connection (connection can
	 * not be trusted because of unsuccessful roll back) and there is a danger to
	 * the integrity data in the database - will notify through onlooker (in the
	 * last case will also throw an exception (PossibleDBCorruptionException).
	 * 
	 * @param coupon
	 *            - the coupon parameter ID should be equals to the ID the
	 *            corresponding coupon as it stored in the database.
	 * @throws CouponSystemException
	 *             if there are problems with synchronization of the connections or
	 *             if there are a serious problems with a specific connection
	 *             (connection can not be trusted because of unsuccessful roll back)
	 *             and there is a danger to the integrity data in the database
	 *             (PossibleDBCorruptionException).
	 */
	private void deleteCouponIfSo(Coupon coupon) throws CouponSystemException {
		String cleanupCoupon = "delete from Coupon where ID = " + coupon.getId();
		String cleanupCompAndCoupon = "delete from Company_Coupon where COUPON_ID = " + coupon.getId();
		String cleanupCustAndCoupon = "delete from Customer_Coupon where COUPON_ID = " + coupon.getId();
		synchronized (ChainLock.TableCoupon) {
			synchronized (ChainLock.TableCompanyCoupon) {
				synchronized (ChainLock.TableCustomerCoupon) {
					Connection con = pool.getConnection();
					try {
						Jabberwocky.via(con).onTheRun().toTheTick().execute(cleanupCoupon).pt().asItIs()
								.execute(cleanupCustAndCoupon, cleanupCompAndCoupon).pt().fin();
					} catch (JabberwockyException e) {
						onlooker.markFailure("There is no coupon with ID - " + coupon.getId() + " in the database.",
								HASH_ID);
					} catch (SQLException e) {
						onlooker.markFailure("There are probably some connection problems. Sorry about that.");
						try {
							con.rollback();
							con.setAutoCommit(true);
						} catch (SQLException anotherE) {
							throw new PossibleDBCorruptionException(
									"One of connection from connection pool became corrupted.");
						}
					} finally {
						pool.returnConnection(con);
					}
				}
			}
		}
	}

}