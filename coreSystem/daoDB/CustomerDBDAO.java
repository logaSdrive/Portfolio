package daoDB;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import beans.Coupon;
import beans.CouponType;
import beans.Customer;
import dao.CustomerDAO;
import database.ChainLock;
import database.ConnectionPool;
import database.Jabberwocky;
import database.Jabberwocky.Add;
import database.Jabberwocky.GreateWork;
import database.Jabberwocky.Pop;
import exceptions.CouponSystemException;

import static exceptions.CustomerDBDAOException.*;
import static exceptions.CouponDBDAOException.HASH_AMOUNT;
import static exceptions.CouponDBDAOException.HASH_TITLE;
import exceptions.JabberwockyException;
import exceptions.PossibleDBCorruptionException;
import system.Looker;

/**
 * Provides set of standard actions between Apache Derby database for customers.
 * It also implements some basic logic because of the integrity reasons for all
 * customers data in the database. It works only when customers ID are
 * auto-incremented by the database and it assumes that all IDs are greater than
 * zero and all customers titles that previously stored in the database are
 * unique. It also provides some basic synchronization at the level of the
 * tables in the database: all method which affects more than a one table at
 * once are synchronized by the tables they affects.
 * 
 * @author AlexanderZhilokov
 */
public class CustomerDBDAO implements CustomerDAO {

	/**
	 * Provides a connections to the database.
	 */
	private ConnectionPool pool;

	/**
	 * Stores the ID value the last customer which successfully logged.
	 */
	private long id;

	/**
	 * Uses for delivery warnings to the client facades.
	 */
	public Looker onlooker;

	/**
	 * Uses as integer representation for customers parameters
	 */
	final static int ID = 0, CUST_NAME = 1, PASSWORD = 2;

	/**
	 * Creates a new instance which allows to work with customers beans in a some
	 * abstraction level as interface GreatWork defines.
	 * 
	 * @return an instance which can be used as translator to the abstraction level
	 *         which @see Jabberwocky knows to used it.
	 */
	public static GreateWork adapter() {
		return new CustomerAdapter();
	}

	/**
	 * Provides the necessary tools for Jabberwocky to work with customer beans.
	 * 
	 * @author AlexanderZhilokov
	 */
	private static class CustomerAdapter implements GreateWork {

		/**
		 * Stores the instance of customer which is under the current workflow
		 */
		private Customer customer;

		/**
		 * Provides setters to the customer parameters which is under current workflow
		 * in an abstract way: they (setters) all must works with string as a parameter
		 * to set.
		 */
		@Override
		public Add[] drawSetters() {
			Add[] setters = { (value) -> customer.setId(Long.parseLong(value)), customer::setCustName,
					customer::setPassword };
			return setters;
		}

		/**
		 * Provides getters to the customer parameters which is under current workflow
		 * in an abstract way: they (getters) must transform all the returns values to
		 * strings which also must corresponds to type of data in the database as it
		 * stored.
		 */
		@Override
		public Pop[] drawGetters() {
			Pop[] getters = { () -> Long.toString(customer.getId()), customer::getCustName, customer::getPassword };
			return getters;
		}

		/**
		 * Provides addition for an customer bean to a current workflow in an abstract
		 * way.
		 */
		@Override
		public void setBean(Object bean) {
			this.customer = (Customer) bean;
		}

		/**
		 * Provides conversion to an customer bean back from abstract to its original
		 * form
		 */
		@Override
		public Customer getBean() {
			return customer;
		}

		/**
		 * Provides initiations for a new customer bean to a current workflow in an
		 * abstract way.
		 */
		@Override
		public void newBean() {
			this.customer = new Customer();
		}

	}

	/**
	 * Checks if the customer parameters are legal for the inserting into the
	 * database. If customer parameters was found to be illegal for insertion into
	 * the database: blank strings values, oversized string values, null values.-
	 * will notify through onlooker.
	 * 
	 * @param customer
	 *            - the customer with all it data which should be checked.
	 */
	public void clarify(Customer customer) {
		String msg = "";
		int rept = 0;
		boolean somethingWrong = false;
		if (customer.getCustName() == null || customer.getCustName().trim().equals("")) {
			msg += "The customer's name is absent. Means you should name the customer. ";
			somethingWrong = true;
			rept |= HASH_NAME;
		} else if (customer.getCustName().length() > 40) {
			msg += "The customer's name is too long. It has to contain no more than 40 letters. ";
			somethingWrong = true;
			rept |= HASH_NAME;
		}
		if (customer.getPassword() == null || customer.getPassword().trim().equals("")) {
			msg += "The customer's password is absent. Means you should write down the password.";
			somethingWrong = true;
			rept |= HASH_PASS;
		} else if (customer.getPassword().length() > 10) {
			msg += "The customer's password is too long. It has to contain no more than 10 symbols.";
			somethingWrong = true;
			rept |= HASH_PASS;
		}
		if (somethingWrong)
			onlooker.markFailure(msg, rept);
	}

	/**
	 * Stores given customer values into the database. Except for the ID - it is
	 * auto-incremented. Note that name must be unique. If storing was not happened:
	 * illegal customer parameters or name which already in use or some connection
	 * problems - will notify through the onlooker by marking the failure. Before
	 * using it make sure that onlooker is not containing a fails. If there are a
	 * synchronization problems with the connection - throws exception
	 * (CouponSystemException).
	 */
	@Override
	public void createCustomer(Customer customer) throws CouponSystemException {
		clarify(customer);
		if (onlooker.hasFails()) {
			return;
		}
		if (customer.getId() != 0) {
			String warning = "About customer - " + customer.getCustName()
					+ ": a little warning about ID: it seems you change it, but generally for all customers ID is auto generated by the database, means your shouldn't affect it at all. "
					+ "Your will get it (ID) from the database. Just a reminder.";
			onlooker.markWarning(warning);
		}
		Collection<Object> customers = new ArrayList<>();
		customers.add(customer);
		String sql = "insert into Customer (CUST_NAME, PASSWORD) select ?, ? from Customer where CUST_NAME = ? having count(*) = 0";
		Connection con = pool.getConnection();
		try {
			Jabberwocky.via(con).toTheTick().from(customers).execute(sql).using(adapter(), CUST_NAME, PASSWORD,
					CUST_NAME);
		} catch (JabberwockyException e) {
			onlooker.markFailure(
					"A customer's name must be unique. Means in database already exists some customer with the same name as "
							+ customer.getCustName() + ". You should change it.",
					HASH_NAME);
		} catch (SQLException e) {
			onlooker.markFailure("There are probably some connection problems. Sorry about that.");
		} finally {
			pool.returnConnection(con);
		}
	}

	/**
	 * Returns the data about a specific customer. If uploading was not happened
	 * (unsuccessful search or some connection problems) - returns null and will
	 * notify through onlooker about the failure. If there are a synchronization
	 * problems with the connection - throws exception (CouponSystemException).
	 */
	@Override
	public Customer readCustomer(long id) throws CouponSystemException {
		if (id <= 0) {
			onlooker.markFailure(
					"All customers ID must be greater than zero. Means the customer ID is invalide and there is no customer with same ID - "
							+ id + " in the database.");
			return null;
		}
		Collection<Object> customers = new ArrayList<>();
		String sql = "select * from Customer where ID = " + id;
		Connection con = pool.getConnection();
		try {
			Jabberwocky.via(con).toTheTick().into(customers).execute(sql).using(adapter());
		} catch (JabberwockyException e) {
			onlooker.markFailure("The customer with this ID - " + id + " - has not found.");
			return null;
		} catch (SQLException e) {
			onlooker.markFailure("There are probably some connection problems. Sorry about that.");
			return null;
		} finally {
			pool.returnConnection(con);
		}
		return (Customer) customers.iterator().next();
	}

	/**
	 * Returns the data about a specific customer. If uploading was not happened
	 * (unsuccessful search or some connection problems) - returns null and will
	 * notify through onlooker about the failure. If there are a synchronization
	 * problems with the connection - throws exception (CouponSystemException).
	 */
	@Override
	public Customer readCustomer(String name) throws CouponSystemException {
		if (name == null || name.trim().equals("")) {
			onlooker.markFailure("The customer's name is absent. . Means the customer name - "
					+ " is invalide and there is no customer with same name in the database.");
			return null;
		}
		if (name.length() > 40) {
			onlooker.markFailure(
					"The customer's name is too long. It has to contain no more than 40 letters. Means the customer name - "
							+ name + " is invalide and there is no customer with same name in the database.");
			return null;
		}
		Collection<Object> customers = new ArrayList<>();
		String sql = "select * from Customer where CUST_NAME = \"" + name + "\"";
		Connection con = pool.getConnection();
		try {
			Jabberwocky.via(con).toTheTick().into(customers).execute(sql).using(adapter());
		} catch (JabberwockyException e) {
			onlooker.markFailure("The customer with this name - " + name + " - has not found.");
			return null;
		} catch (SQLException e) {
			onlooker.markFailure("There are probably some connection problems. Sorry about that.");
			return null;
		} finally {
			pool.returnConnection(con);
		}
		return (Customer) customers.iterator().next();
	}

	/**
	 * Updates the data about a specific customer. Except for the customer name and
	 * ID - remains unchanged. If the given customer was not updated (unsuccessful
	 * search or some connection problems - will notify through onlooker. Before
	 * using it make sure that onlooker is not containing a fails. If there are a
	 * synchronization problems with the connection - throws exception
	 * (CouponSystemException).
	 */
	@Override
	public void updateCustomer(Customer customer) throws CouponSystemException {
		if (customer.getId() <= 0) {
			onlooker.markFailure(
					"All customers ID must be greater than zero. Means the customer ID is invalide and there is no customer with same ID - "
							+ customer.getId() + " in the database.",
					HASH_ID);
			return;
		}
		clarify(customer);
		if (onlooker.hasFails()) {
			return;
		}
		Collection<Object> customers = new ArrayList<>();
		customers.add(customer);
		String sql = "update Customer set PASSWORD=? where ID = ? and CUST_NAME = ?";
		Connection con = pool.getConnection();
		try {
			Jabberwocky.via(con).toTheTick().from(customers).execute(sql).using(adapter(), PASSWORD, ID, CUST_NAME);
		} catch (JabberwockyException e) {
			onlooker.markFailure(
					"There is no customer with name - " + customer.getCustName() + " and with ID - " + customer.getId()
							+ " in the database. Means both uses as kind of primary keys and you can't update them.",
					HASH_ID | HASH_NAME);
		} catch (SQLException e) {
			onlooker.markFailure("There are probably some connection problems. Sorry about that.");
		} finally {
			pool.returnConnection(con);
		}
	}

	/**
	 * Removes the data about a specific customer from the database. If the given
	 * customer was not deleted: unsuccessful search or some connection problems -
	 * will notify through onlooker. If there are a synchronization problems with
	 * the connection (CouponSystemException) or if there are some serious problems
	 * with a specific connection (can not be trusted because of unsuccessful roll
	 * back, means there is a danger to the integrity data in the database
	 * (PossibleDBCorruptionException) - throws exception.
	 */
	@Override
	public void deleteCustomer(Customer customer) throws CouponSystemException {
		if (customer.getId() <= 0) {
			onlooker.markFailure(
					"All customers ID must be greater than zero. Means the customer ID is invalide and there is no customer with same ID - "
							+ customer.getId() + " in the database.", HASH_ID);
			return;
		}
		String cleanupCustAndCoupon = "delete from Customer_Coupon where CUST_ID = " + customer.getId();
		String cleanupCustomer = "delete from Customer where ID = " + customer.getId();
		synchronized (ChainLock.TableCustomer) {
			synchronized (ChainLock.TableCustomerCoupon) {
				Connection con = pool.getConnection();
				try {
					Jabberwocky.via(con).onTheRun().toTheTick().execute(cleanupCustomer).pt().asItIs()
							.execute(cleanupCustAndCoupon).pt().fin();
				} catch (JabberwockyException e) {
					onlooker.markFailure("There is no customer with ID - " + customer.getId() + " in the database.",
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

	/**
	 * Returns ArrayList with all the customers stored in the database. If there are
	 * not any customers - return ArrayList which size is zero. If there are some
	 * connection problems - returns null and will notify through onlooker. If there
	 * are a synchronization problems with the connection - throws exception
	 * (CouponSystemException).
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Collection<Customer> getAllCustomers() throws CouponSystemException {
		String sql = "select * from Customer";
		Collection customers = new ArrayList<>();
		Connection con = pool.getConnection();
		try {
			Jabberwocky.via(con).into(customers).execute(sql).using(adapter());
		} catch (SQLException e) {
			onlooker.markFailure("There are probably some connection problems. Sorry about that.");
			return null;
		} finally {
			pool.returnConnection(con);
		}
		return customers;
	}

	/**
	 * Returns ArrayList with all the coupons which were purchased by the specific
	 * customer (last customer which successfully logged through this instance). If
	 * there are not any coupons - return ArrayList which size is zero. If there are
	 * some connection problems - returns null and will notify through onlooker. If
	 * there are a synchronization problems with the connection - throws exception
	 * (CouponSystemException).
	 */
	@Override
	public Collection<Coupon> getCoupons() throws CouponSystemException {
		return getCoupons(id);
	}

	/**
	 * Returns ArrayList with all the coupons under the given price which were
	 * purchased by the specific customer (last customer which successfully logged
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
		Connection con = pool.getConnection();
		String sql = "select * from Coupon where ID in (select COUPON_ID from Customer_Coupon where CUST_ID = " + id
				+ " and Coupon.PRICE <= " + price + ")";
		Collection coupons = new ArrayList<>();
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
	 * Returns ArrayList with all the coupons with given type which were bought by
	 * the specific customer (last customer which successfully logged through this
	 * instance). If there are not any coupons which types are equals to the given -
	 * return ArrayList which size is zero. If there are some connection problems -
	 * returns null and will notify through onlooker. If there are a synchronization
	 * problems with the connection - throws exception (CouponSystemException).
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Collection<Coupon> getCouponsByType(CouponType type) throws CouponSystemException {
		Connection con = pool.getConnection();
		String sql = "select * from Coupon where ID in (select COUPON_ID from Customer_Coupon where CUST_ID = " + id
				+ " and Coupon.TYPE = \"" + type.name() + "\")";
		Collection coupons = new ArrayList<>();
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
	 * Returns ArrayList with all the coupons which were bought by the specific
	 * customer with the given ID. If there are not any coupons for the customer
	 * which ID corresponds to the given ID - return ArrayList which size is zero.
	 * If there is no any customer with the given ID in the database or some
	 * connection problems - returns null and will notify through onlooker. If there
	 * are a synchronization problems with the connection - throws exception
	 * (CouponSystemException).
	 */
	@Override
	public Collection<Coupon> getCustomerCoupons(long custId) throws CouponSystemException {
		if (custId <= 0) {
			onlooker.markFailure(
					"All customers ID must be greater than zero. Means the customer ID is invalide and there is no customer with same ID - "
							+ custId + " in the database.");
			return null;
		}
		String customerCheck = "update Customer set CUST_NAME = CUST_NAME where ID = " + custId;
		Connection con = pool.getConnection();
		try {
			Jabberwocky.via(con).toTheTick().execute(customerCheck).pt();
		} catch (JabberwockyException e) {
			onlooker.markFailure("The customer with ID: " + custId + " - has not found.", HASH_ID);
			return null;
		} catch (SQLException e) {
			onlooker.markFailure("There are probably some connection problems. Sorry about that.");
			return null;
		} finally {
			pool.returnConnection(con);
		}
		Collection<Coupon> coupons = getCoupons(custId);
		if (coupons.isEmpty()) {
			String warning = "About the customer with ID - " + custId
					+ ": it seems that the customer with this ID does not have any coupons. Just to aware you. "
					+ "(It also possible that there are not any customer in the whole database wich ID matches the above.)";
			onlooker.markWarning(warning);
		}
		return coupons;
	}

	/**
	 * Records a purchase of the coupon and marks as owner the last customer, who
	 * successfully logged through this instance. It also updates the amount of the
	 * coupon (decreases by one). If there are not any coupons which ID corresponds
	 * to the given coupons ID or there is no stored customer ID value (there are
	 * not any customer which successfully logged through this instance) or the
	 * stored customer ID is no valid any more (the customer was deleted) - will
	 * notify through onlooker about the failure. If some serious problems with a
	 * specific connection: can not be trusted because of unsuccessful roll back,
	 * means there is a danger to the integrity data in the database - throws
	 * exception (PossibleDBCorruptionException). If there are a synchronization
	 * problems with the connection - throws exception (CouponSystemException).
	 */
	@Override
	public void purchaseCoupon(Coupon coupon) throws CouponSystemException {
		if (coupon.getId() <= 0) {
			onlooker.markFailure(
					"All coupons ID must be greater than zero. Means the coupon ID is invalide and there is no coupon with same ID - "
							+ coupon.getId() + " in the database.", HASH_ID);
			return;
		}
		String customerIdCheck = "update Customer set CUST_NAME = CUST_NAME where ID = " + id;
		String updateCouponAmount = "update Coupon set AMOUNT = AMOUNT - 1 where ID = " + coupon.getId()
				+ " and AMOUNT > 0";
		String updateCouponAndCust = "insert into Customer_Coupon (CUST_ID, COUPON_ID) select " + id + ", "
				+ coupon.getId() + " from Customer_Coupon where CUST_ID = " + id + " and COUPON_ID = " + coupon.getId()
				+ " having count(*) = 0";
		synchronized (ChainLock.TableCoupon) {
			synchronized (ChainLock.TableCustomerCoupon) {
				Connection con = pool.getConnection();
				try {
					Jabberwocky.via(con).toTheTick().onTheRun()
							.execute(customerIdCheck, updateCouponAndCust, updateCouponAmount).pt().fin();
				} catch (JabberwockyException e) {
					switch (e.rept) {
					case -1:
						onlooker.markFailure("Your ID: " + id + " is not valid anymore. Sorry about that.");
						break;
					case -2:
						onlooker.markFailure("The coupon with title: " + coupon.getTitle()
								+ " was purchased early. All customers can only purchase a coupon if they did not have it (a specified coupon) early.", HASH_TITLE);
						break;
					case -3:
						onlooker.markFailure("There is not any available amount of the coupon with title: "
								+ coupon.getTitle()
								+ " for the sale. (It also possible that there are not any coupon in the whole database which ID matches the above.)", HASH_AMOUNT);
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
	 * Removes the coupon which ID corresponds to the given ID value (custID) from
	 * the customer, its owner, which ID corresponds to the given ID value
	 * (couponID). It also updates the amount of the coupon (increases by one). If
	 * there is not any customer or any coupon which IDs corresponds to the givens
	 * IDs or if the customer does not owns the coupon or if there is a connection
	 * problems - will notify through onlooker about the failure. In the case of
	 * some serious problems with a specific connection (the connection can not be
	 * trusted any more because of unsuccessful roll back, means there is a danger
	 * to the integrity the data in the database - throws
	 * PossibleDBCorruptionException exception. If there are a synchronization
	 * problems with the connection - throws exception (CouponSystemException).
	 */
	@Override
	public void removeCouponFromTheCustomer(long custId, long couponId) throws CouponSystemException {
		String msg = "";
		boolean somethingWrong = false;
		if (couponId <= 0) {
			msg += "All coupons ID must be greater than zero. Means the coupon ID is invalide and there is no coupon with same ID - "
					+ couponId + " in the database.";
			somethingWrong = true;
		}
		if (custId <= 0) {
			msg += "All customers ID must be greater than zero. Means the customer ID is invalide and there is no customer with same ID - "
					+ custId + " in the database.";
			somethingWrong = true;
		}
		if (somethingWrong) {
			onlooker.markFailure(msg);
			return;
		}
		String customerIdCheck = "update Customer set CUST_NAME = CUST_NAME where ID = " + custId;
		String updateCouponAmount = "update Coupon set AMOUNT = AMOUNT + 1 where ID = " + couponId;
		String cleanupCustAndCoupon = "delete from Customer_Coupon where COUPON_ID = " + couponId + " and CUST_ID = "
				+ custId;
		synchronized (ChainLock.TableCoupon) {
			synchronized (ChainLock.TableCustomerCoupon) {
				Connection con = pool.getConnection();
				try {
					Jabberwocky.via(con).toTheTick().onTheRun()
							.execute(customerIdCheck, updateCouponAmount, cleanupCustAndCoupon).pt().fin();
				} catch (JabberwockyException e) {
					switch (e.rept) {
					case -1:
						onlooker.markFailure("There is no customer with ID: " + custId + " in the database.");
						break;
					case -2:
						onlooker.markFailure("There is no coupon with ID: " + couponId + " is the database.", HASH_ID);
						break;
					case -3:
						onlooker.markFailure("The customer with ID - " + custId + " does not have any coupon with ID: "
								+ couponId + ".");
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
	 * Checks if exists a customer in the database with name and password equals to
	 * the given values. And if exists stores it ID. There is only one customer ID
	 * which stored by every separate instance, that means each successful login
	 * through same instance of this class will override this ID value. In a more
	 * simple way: only the ID of the last customer which successfully logged
	 * through this instance will saved in this specific instance. If there is any
	 * connection problem - throws CouponSystemException.
	 */
	@Override
	public boolean login(String custName, String password) throws CouponSystemException {
		if (custName.length() > 40 || custName.trim().equals(""))
			return false;
		if (password.length() > 10 || password.trim().equals(""))
			return false;
		Customer customer = readCustomer(custName, password);
		if (customer == null) {
			return false;
		} else {
			this.id = customer.getId();
			return true;
		}
	}

	/**
	 * Returns the data about a specific customer.
	 * 
	 * @param custName
	 *            - the unique name which corresponds to the name of the customer as
	 *            it stored in the database.
	 * @param password
	 *            - the password which corresponds to the passwords of the customer
	 *            with the given name as it stored in the database.
	 * @return instance of Customer with all it data as it stored in the database.
	 *         Or null if there is not customer with given name and password at the
	 *         same time in the database.
	 * @throws CouponSystemException
	 *             if there are any problems with connection to the database.
	 */
	private Customer readCustomer(String custName, String password) throws CouponSystemException {
		Collection<Object> customers = new ArrayList<>();
		String sql = "select * from Customer where CUST_NAME = \"" + custName + "\" and PASSWORD = \"" + password
				+ "\"";
		Connection con = pool.getConnection();
		try {
			Jabberwocky.via(con).into(customers).execute(sql).using(adapter());
		} catch (SQLException e) {
			throw new CouponSystemException("There are probably some connection problems. Sorry about that.");
		} finally {
			pool.returnConnection(con);
		}
		return (customers.isEmpty()) ? null : (Customer) customers.iterator().next();
	}

	/**
	 * Returns all coupons which were purchased by the specific customer. If there
	 * is some connection problems returns null and will notify through onlooker
	 * about the failure.
	 * 
	 * @param id
	 *            - the ID of the customer which coupons should be returned.
	 * @return ArrayList of coupons which were bought by the specific customer with
	 *         the given id value. If not any coupon or there is not any customer
	 *         which ID corresponds to the given value of the id - returns ArrayList
	 *         with size zero.
	 * @throws CouponSystemException
	 *             if there are problems with synchronization of the connections
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Collection<Coupon> getCoupons(long id) throws CouponSystemException {
		Connection con = pool.getConnection();
		String sql = "select  * from Coupon where ID in (select COUPON_ID from Customer_Coupon where CUST_ID = " + id
				+ ")";
		Collection coupons = new ArrayList<>();
		try {
			Jabberwocky.via(con).into(coupons).execute(sql).using(CouponDBDAO.adapter());
		} catch (SQLException e) {
			onlooker.markFailure("There are probably some connection problems. Sorry about that.");
		} finally {
			pool.returnConnection(con);
		}
		return coupons;
	}

	/**
	 * Return the value of the ID of the last customer which successfully logged
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
	public CustomerDBDAO(ConnectionPool pool, Looker onlooker) {
		this.pool = pool;
		this.onlooker = onlooker;
	}

}