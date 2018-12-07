package daoDB;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import beans.Company;
import beans.Coupon;
import beans.CouponType;
import dao.CompanyDAO;
import database.ChainLock;
import database.ConnectionPool;
import database.Jabberwocky;
import database.Jabberwocky.Add;
import database.Jabberwocky.GreateWork;
import database.Jabberwocky.Pop;
import static exceptions.CompanyDBDAOException.*;
import exceptions.CouponSystemException;
import exceptions.JabberwockyException;
import exceptions.PossibleDBCorruptionException;
import system.Looker;

/**
 * Provides set of standard actions between Apache Derby database for companies
 * and their coupons. It also implements some basic logic because of the
 * integrity reasons for all companies data in the database. It works only when
 * companies ID are auto-incremented by the database and it assumes that all IDs
 * are greater than zero and all companies names that previously stored in the
 * database are unique. It also provides some basic synchronization at the level
 * of the tables in the database: all method which affects more than a one table
 * at once are synchronized by the tables they affects.
 * 
 * @author AlexanderZhilokov
 */
public class CompanyDBDAO implements CompanyDAO {

	/**
	 * Provides a connections to the database.
	 */
	private ConnectionPool pool;

	/**
	 * Stores the ID value the last company which successfully logged.
	 */
	private long id;

	/**
	 * Uses for delivery warnings to the client facades.
	 */
	public Looker onlooker;

	/**
	 * Uses as integer representation for company parameters
	 */
	static final int ID = 0, COMP_NAME = 1, PASSWORD = 2, EMAIL = 3;

	/**
	 * Creates a new instance which allows to work with company beans in a some
	 * abstraction level as interface GreatWork defines.
	 * 
	 * @return an instance which can be used as translator to the abstraction level
	 *         which @see Jabberwocky knows to used it.
	 */
	public static GreateWork adapter() {
		return new CompanyAdapter();
	}

	/**
	 * Provides the necessary tools for Jabberwocky to work with company beans.
	 * 
	 * @author AlexanderZhilokov
	 */
	private static class CompanyAdapter implements GreateWork {

		/**
		 * Stores the instance of company which is under the current workflow
		 */
		private Company company;

		/**
		 * Provides setters to the company parameters which is under current workflow in
		 * an abstract way: they (setters) all must works with string as a parameter to
		 * set.
		 */
		@Override
		public Add[] drawSetters() {
			Add[] setters = { (value) -> company.setId(Long.parseLong(value)), company::setCompName,
					company::setPassword, company::setEmail };
			return setters;
		}

		/**
		 * Provides getters to the all company parameters which is under current
		 * workflow in an abstract way: they (getters) must transform all the returns
		 * values to strings which also must corresponds to type of data in the database
		 * as it stored.
		 */
		@Override
		public Pop[] drawGetters() {
			Pop[] getters = { () -> Long.toString(company.getId()), company::getCompName, company::getPassword,
					company::getEmail };
			return getters;
		}

		/**
		 * Provides addition for an company bean to a current workflow in an abstract
		 * way.
		 */
		@Override
		public void setBean(Object bean) {
			this.company = (Company) bean;

		}

		/**
		 * Provides conversion to an company bean back from abstract to its original
		 * form
		 */
		@Override
		public Company getBean() {
			return company;
		}

		/**
		 * Provides initiations for a new company bean to a current workflow in an
		 * abstract way.
		 */
		@Override
		public void newBean() {
			this.company = new Company();
		}
	}

	/**
	 * Checks if the company parameters are legal for the inserting into the
	 * database.
	 * 
	 * @param company
	 *            - the company with all it data which should be checked.
	 */
	public void clarify(Company company) {
		String msg = "";
		boolean somethingWrong = false;
		int rept = 0;
		if (company.getCompName() == null || company.getCompName().trim().equals("")) {
			msg += "The company's name is absent. Means you should name the company.";
			somethingWrong = true;
			rept |= HASH_NAME;
		} else if (company.getCompName().length() > 40) {
			msg += "The company's name is too long. It has to contain no more than 40 letters. ";
			somethingWrong = true;
			rept |= HASH_NAME;
		}
		if (company.getPassword() == null || company.getPassword().trim().equals("")) {
			msg += "The company's password is absent. Means you should write down the password. ";
			somethingWrong = true;
			rept |= HASH_PASS;
		} else if (company.getPassword().length() > 10) {
			msg += "The company's password is too long. It has to contain no more than 10 symbols. ";
			somethingWrong = true;
			rept |= HASH_PASS;
		}
		if (company.getEmail() == null || company.getEmail().trim().equals("")) {
			msg += "The company's email is undefined. Means you should mention it.";
			somethingWrong = true;
			rept |= HASH_MAIL;
		} else if (company.getEmail().length() > 30) {
			msg += "The company's email is to long. It has to conatin no more than 30 letters.";
			somethingWrong = true;
			rept |= HASH_MAIL;
		}
		if (somethingWrong)
			onlooker.markFailure(msg, rept);
	}

	/**
	 * Stores given company values into the database. Except for the ID - it is
	 * auto-incremented. Note that name must be unique. Before using it make sure
	 * that onlooker is not containing a fails. If storing was not happened: illegal
	 * company parameters or name which already in use or some connection problems -
	 * will notify through onlooker about the failure. If there are a
	 * synchronization problems with the connection throws exception
	 * (CouponSystemException).
	 */
	@Override
	public void createCompany(Company company) throws CouponSystemException {
		clarify(company);
		if (onlooker.hasFails()) {
			return;
		}
		if (company.getId() != 0) {
			String warning = "About company - " + company.getCompName()
					+ ": a little warning about ID. It seems you change it, but generally for all companies ID is auto generated by the database, means your shouldn't affect it at all."
					+ "Your will get it (ID) from the database. Just a reminder.";
			onlooker.markWarning(warning);
		}
		Collection<Object> companies = new ArrayList<>();
		companies.add(company);
		String sql = "insert into Company (COMP_NAME, PASSWORD, EMAIL) select ?, ?, ? from Company where COMP_NAME = ? having count(*) = 0";
		Connection con = pool.getConnection();
		try {
			Jabberwocky.via(con).toTheTick().from(companies).execute(sql).using(adapter(), COMP_NAME, PASSWORD, EMAIL,
					COMP_NAME);
		} catch (JabberwockyException e) {
			onlooker.markFailure(
					"A company's name must be unique. Means in database already exists some company with the same name as "
							+ company.getCompName() + ". You should change it.",
					HASH_NAME);
		} catch (SQLException e) {
			onlooker.markFailure("There are probably some connection problems. Sorry about that.");
		} finally {
			pool.returnConnection(con);
		}
	}

	/**
	 * Returns the data about a specific company. If uploading was not happened
	 * (unsuccessful search or some connection problems) - returns null and will
	 * notify through onlooker about the failure. If there are a synchronization
	 * problems with the connection throws exception (CouponSystemException).
	 */
	@Override
	public Company readCompany(long id) throws CouponSystemException {
		if (id <= 0) {
			onlooker.markFailure(
					"All companies ID must be greater than zero. Means the company ID is invalide and there is no company with same ID - "
							+ id + " in the database.");
			return null;
		}
		Collection<Object> companies = new ArrayList<>();
		String sql = "select * from Company where ID = " + id;
		Connection con = pool.getConnection();
		try {
			Jabberwocky.via(con).toTheTick().into(companies).execute(sql).using(adapter());
		} catch (JabberwockyException e) {
			onlooker.markFailure("The company with this ID - " + id + " - has not found.");
			return null;
		} catch (SQLException e) {
			onlooker.markFailure("There are probably some connection problems. Sorry about that.");
			return null;
		} finally {
			pool.returnConnection(con);
		}
		return (Company) companies.iterator().next();
	}

	/**
	 * Returns the data about a specific company. If uploading was not happened
	 * (unsuccessful search or some connection problems) - returns null and will
	 * notify through onlooker about the failure. If there are a synchronization
	 * problems with the connection throws exception (CouponSystemException).
	 */
	@Override
	public Company readCompany(String name) throws CouponSystemException {
		if (name == null || name.trim().equals("")) {
			onlooker.markFailure("The company's name is absent. . Means the company name - "
					+ " is invalide and there is no company with same name in the database.");
			return null;
		}
		if (name.length() > 40) {
			onlooker.markFailure("The company's name is too long. It has to contain no more than 40 letters. "
					+ "Means the company name - " + name
					+ " is invalide and there is no company with same name in the database.");
			return null;
		}
		Collection<Object> companies = new ArrayList<>();
		String sql = "select * from Company " + "where COMP_NAME = \"" + name + "\"";
		Connection con = pool.getConnection();
		try {
			Jabberwocky.via(con).toTheTick().into(companies).execute(sql).using(adapter());
		} catch (JabberwockyException e) {
			onlooker.markFailure("The company with this name - " + name + " - has not found.");
			return null;
		} catch (SQLException e) {
			onlooker.markFailure("There are probably some connection problems. Sorry about that.");
			return null;
		} finally {
			pool.returnConnection(con);
		}
		return (Company) companies.iterator().next();
	}

	/**
	 * Updates the data about a specific company. Except for the company name and ID
	 * - remains unchanged. If the given company was not updated: unsuccessful
	 * search or some connection problems - will notify through onlooker about the
	 * failure. Before using it make sure that onlooker is not containing a fails.
	 * If there are a synchronization problems with the connection throws exception
	 * (CouponSystemException).
	 */
	@Override
	public void updateCompany(Company company) throws CouponSystemException {
		clarify(company);
		if (onlooker.hasFails()) {
			return;
		}
		if (company.getId() <= 0) {
			onlooker.markFailure(
					"All companies ID must be greater than zero. Means the company ID is invalide and there is no company with this ID - "
							+ company.getId() + " in the database.", HASH_ID);
			return;
		}
		Collection<Object> companies = new ArrayList<>();
		companies.add(company);
		String sql = "update Company set PASSWORD = ?, EMAIL = ? where ID = ? and COMP_NAME = ?";
		Connection con = pool.getConnection();
		try {
			Jabberwocky.via(con).toTheTick().from(companies).execute(sql).using(adapter(), PASSWORD, EMAIL, ID,
					COMP_NAME);
		} catch (JabberwockyException e) {
			onlooker.markFailure(
					"There is no company with name - " + company.getCompName() + " and with ID - " + company.getId()
							+ " in the database. Means both uses as kind of primary keys and you can't update them.",
					HASH_ID | HASH_NAME);
		} catch (SQLException e) {
			onlooker.markFailure("There are probably some connection problems. Sorry about that.");
		} finally {
			pool.returnConnection(con);
		}
	}

	/**
	 * Removes the data about a specific company from the database. If the given
	 * company was not deleted: unsuccessful search or some connection problems -
	 * will notify through onlooker about the failure. If there are some serious
	 * problems with a specific connection (can not be trusted because of
	 * unsuccessful roll back, means there is a danger to the integrity data in the
	 * database - throws exception (PossibleDBCorruptionException). If there are a
	 * synchronization problems with the connection throws exception
	 * (CouponSystemException).
	 */
	@Override
	public void deleteCompany(Company company) throws CouponSystemException {
		if (company.getId() <= 0) {
			onlooker.markFailure(
					"All companies ID must be greater than zero. Means the company ID is invalide and there is no company with this ID - "
							+ company.getId() + " in the database.", HASH_ID);
			return;
		}
		String cleanupCompany = "delete from Company where ID = " + company.getId();
		String cleanupCoupons = "delete from Coupon where ID in (select COUPON_ID from Company_Coupon  where COMP_ID = "
				+ company.getId() + ")";
		String cleanupCompAndCoupon = "delete from Company_Coupon where COMP_ID = " + company.getId();
		String cleanupCustAndCoupon = "delete from Customer_Coupon where COUPON_ID in (select COUPON_ID from Company_Coupon where COMP_ID = "
				+ company.getId() + ")";
		synchronized (ChainLock.TableCompany) {
			synchronized (ChainLock.TableCompanyCoupon) {
				synchronized (ChainLock.TableCustomerCoupon) {
					Connection con = pool.getConnection();
					try {
						Jabberwocky.via(con).onTheRun().toTheTick().execute(cleanupCompany).pt().asItIs()
								.execute(cleanupCoupons, cleanupCustAndCoupon, cleanupCompAndCoupon).pt().fin();
					} catch (JabberwockyException e) {
						onlooker.markFailure("There is no company with ID - " + company.getId() + " in the database.",
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

	/**
	 * Returns ArrayList with all the companies stored in the database. If there are
	 * not any companies - return ArrayList which size is zero. If there are some
	 * connection problems - returns null and will notify through onlooker. If there
	 * are a synchronization problems with the connection - throws exception
	 * (CouponSystemException).
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Collection<Company> getAllCompanies() throws CouponSystemException {
		String sql = "select * from Company";
		Collection companies = new ArrayList<>();
		Connection con = pool.getConnection();
		try {
			Jabberwocky.via(con).execute(sql).into(companies).using(adapter());
		} catch (SQLException e) {
			onlooker.markFailure("There are probably some connection problems. Sorry about that.");
			return null;
		} finally {
			pool.returnConnection(con);
		}
		return companies;
	}

	/**
	 * Returns ArrayList with all the coupons which were created by the specific
	 * company (last company which successfully logged through this instance). If
	 * there are some connection problems - returns null and will notify through
	 * onlooker. If there are a synchronization problems with the connection -
	 * throws exception (CouponSystemException).
	 */
	@Override
	public Collection<Coupon> getCoupons() throws CouponSystemException {
		return getCoupons(id);
	}

	/**
	 * Returns ArrayList with all the coupons with given type which were created by
	 * the specific company (last company which successfully logged through this
	 * instance). If there are not any coupons which types are equals to the given -
	 * return ArrayList which size is zero. If there are some connection problems -
	 * returns null and will notify through onlooker. If there are a synchronization
	 * problems with the connection - throws exception (CouponSystemException).
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Collection<Coupon> getCouponsByType(CouponType type) throws CouponSystemException {
		String sql = "select * from Coupon where ID in (select COUPON_ID from Company_Coupon where COMP_ID = " + id
				+ " and Coupon.TYPE = \"" + type.name() + "\")";
		Collection coupons = new ArrayList<>();
		Connection con = pool.getConnection();
		try {
			Jabberwocky.via(con).into(coupons).execute(sql).using(CouponDBDAO.adapter());
		} catch (SQLException e) {
			onlooker.markFailure("There are probably some connection problems. Sorry about that.");
			return null;
		} finally {
			pool.returnConnection(con);
		}
		return coupons;
	}

	/**
	 * Returns ArrayList with all the coupons under the given price which were
	 * created by the specific company (last company which successfully logged
	 * through this instance). If there are not any coupons which price is lower or
	 * equals to the given price - return ArrayList which size is zero. If given
	 * price value is not valid or if there are some connection problems - returns
	 * null and will notify through onlooker. If there are a synchronization
	 * problems with the connection - throws exception (CouponSystemException).
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Collection<Coupon> getCouponsByPrice(double price) throws CouponSystemException {
		if (price < 0) {
			onlooker.markFailure("The price value is less than zero. "
					+ "Means there are not any coupon in the database with the price lower or equal " + price + ".");
			return null;
		}
		String sql = "select * from Coupon where ID in (select COUPON_ID from Company_Coupon where COMP_ID = " + id
				+ " and Coupon.PRICE <= " + price + ")";
		Collection coupons = new ArrayList<>();
		Connection con = pool.getConnection();
		try {
			Jabberwocky.via(con).into(coupons).execute(sql).using(CouponDBDAO.adapter());
		} catch (SQLException e) {
			onlooker.markFailure("There are probably some connection problems. Sorry about that.");
			return null;
		} finally {
			pool.returnConnection(con);
		}
		return coupons;
	}

	/**
	 * Returns ArrayList with all the coupons under the given date and which were
	 * created by the specific company (last company which successfully logged by
	 * this instance). If there are not any coupons which date is before or equals
	 * to the given date - return ArrayList which size is zero. If given date value
	 * is not valid or if there are some connection problems - returns null and will
	 * notify through onlooker. If there are a synchronization problems with the
	 * connection - throws exception (CouponSystemException).
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Collection<Coupon> getCouponsByDate(Date date) throws CouponSystemException {
		java.sql.Date sqlDate = new java.sql.Date(date.getTime());
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		java.sql.Date yesterday = new java.sql.Date((cal.getTime()).getTime());
		if (sqlDate.before(yesterday)) {
			onlooker.markFailure("The date - " + date
					+ " is already expired. Means there are not any coupon in the database with the end date early than specified date.");
			return null;
		}
		String sql = "select * from Coupon where ID in (select COUPON_ID from Company_Coupon where COMP_ID = " + id
				+ " and Coupon.END_DATE <= \"" + sqlDate.toString() + "\")";
		Collection coupons = new ArrayList<>();
		Connection con = pool.getConnection();
		try {
			Jabberwocky.via(con).into(coupons).execute(sql).using(CouponDBDAO.adapter());
		} catch (SQLException e) {
			onlooker.markFailure("There are probably some connection problems. Sorry about that.");
			return null;
		} finally {
			pool.returnConnection(con);
		}
		return coupons;
	}

	/**
	 * Returns ArrayList with all the coupons which were created by the specific
	 * company with the given ID. If there are not any coupons for the company which
	 * ID correspond to the given ID - return ArrayList which size is zero. If there
	 * is no any company with the given ID in the database or some connection
	 * problems - returns null and will notify through onlooker. If there are a
	 * synchronization problems with the connection - throws exception
	 * (CouponSystemException).
	 */
	@Override
	public Collection<Coupon> getCompanyCoupons(long compId) throws CouponSystemException {
		if (compId <= 0) {
			onlooker.markFailure(
					"All companies ID must be greater than zero. Means the company ID is invalide and there is no company with this ID - "
							+ compId + " in the database.");
			return null;
		}
		String companyIdCheck = "update Company set COMP_NAME = COMP_NAME where ID = " + compId;
		Connection con = pool.getConnection();
		try {
			Jabberwocky.via(con).toTheTick().execute(companyIdCheck).pt();
		} catch (JabberwockyException e) {
			onlooker.markFailure("The company with ID - " + compId + " - has not found.", HASH_ID);
			return null;
		} catch (SQLException e) {
			onlooker.markFailure("There are probably some connection problems. Sorry about that.");
			return null;
		} finally {
			pool.returnConnection(con);
		}
		Collection<Coupon> coupons = getCoupons(compId);
		if (coupons.isEmpty()) {
			String warning = "About the company with ID - " + compId + ": "
					+ "it seems that company with this ID does not have any coupons. Just to aware you."
					+ "(It also possible that there are not any company in the whole database wich ID matches the above.)";
			onlooker.markWarning(warning);
		}
		return coupons;
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
	public boolean login(String compName, String password) throws CouponSystemException {
		if (compName.length() > 40 || compName.trim().equals(""))
			return false;
		if (password.length() > 10 || password.trim().equals(""))
			return false;
		Company company = readCompany(compName, password);
		if (company == null) {
			return false;
		} else {
			id = company.getId();
			return true;
		}
	}

	/**
	 * Return the value of the ID of the last company which successfully logged
	 * through this instance. If not any - returns zero.
	 */
	@Override
	public long getObtainedId() {
		return id;
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
	public CompanyDBDAO(ConnectionPool pool, Looker onlooker) {
		this.pool = pool;
		this.onlooker = onlooker;
	}

	/**
	 * Returns the data about a specific company.
	 * 
	 * @param compName
	 *            - the unique name which corresponds to the name of the company as
	 *            it stored in the database.
	 * @param password
	 *            - the password which corresponds to the passwords of the company
	 *            with the given name as it stored in the database.
	 * @return instance of Company with all it data as it stored in the database. Or
	 *         null if there is not company with given name and password at the same
	 *         time in the database.
	 * @throws CouponSystemException
	 *             if there are any problems with connection to the database.
	 */
	private Company readCompany(String compName, String password) throws CouponSystemException {
		Collection<Object> companies = new ArrayList<>();
		String sql = "select * from Company where COMP_NAME = \"" + compName + "\" and PASSWORD = \"" + password + "\"";
		Connection con = pool.getConnection();
		try {
			Jabberwocky.via(con).into(companies).execute(sql).using(adapter());
		} catch (SQLException e) {
			throw new CouponSystemException("There are probably some connection problems. Sorry about that.");
		} finally {
			pool.returnConnection(con);
		}
		return (companies.isEmpty()) ? null : (Company) companies.iterator().next();
	}

	/**
	 * Returns all coupons which were created by the specific company. If there are
	 * any connection problems returns - null and will notify through onlooker about
	 * the failure.
	 * 
	 * @param id
	 *            - the ID of the company which coupons should be returned.
	 * @return ArrayList of coupons which were created by the specific company with
	 *         the given id value. If not any coupon or there is not any company
	 *         which ID corresponds to the given value of the id - returns ArrayList
	 *         with size zero.
	 * @throws CouponSystemException
	 *             if there are problems with synchronization of the connections.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Collection<Coupon> getCoupons(long id) throws CouponSystemException {
		String sql = "select * from Coupon where ID in (select COUPON_ID from Company_Coupon where COMP_ID = " + id
				+ ")";
		Collection coupons = new ArrayList<>();
		Connection con = pool.getConnection();
		try {
			Jabberwocky.via(con).into(coupons).execute(sql).using(CouponDBDAO.adapter());
		} catch (SQLException e) {
			onlooker.markFailure("There are probably some connection problems. Sorry about that.");
			return null;
		} finally {
			pool.returnConnection(con);
		}
		return coupons;
	}

}