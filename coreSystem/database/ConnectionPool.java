package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import exceptions.CouponSystemException;

/**
 * Provides a connections to the database. Note that this class is a singleton
 * and it is threads safe to work with. Uses it attribute - DEFAULT_URL to
 * establish path for the connections to the database.
 * 
 * @author AlexanderZhilokov
 *
 */
public class ConnectionPool {

	/**
	 * Stores the only instance of the class.
	 */
	private static volatile ConnectionPool instance;

	/**
	 * Stores the class name for a driver which will be used for establishing a
	 * connection to the database. Note that DBBuilder and DBCleaner classes also
	 * uses this value for creation and dropping the database.
	 */
	public static final String DEFAULT_DRIVER = "com.mysql.jdbc.Driver";

	/**
	 * Stores the path and the name for the database. Note that DBBuilder and
	 * DBCleaner classes also uses this value for creation and dropping the
	 * database.
	 */
	public static final String DEFAULT_URL = "jdbc:MySQL://localhost:3306/ZhilokovAlexanderDB?autoReconnect=true&useSSL=false";

	/**
	 * Stores the user name for establishing a connection to the database. Note that
	 * DBBuilder and DBCleaner classes also uses this value for creation and
	 * dropping the database.
	 */
	public static final String NAME = "root";

	/**
	 * Stores the password for establishing a connection to the database. Note that
	 * DBBuilder and DBCleaner classes also uses this value for creation and
	 * dropping the database.
	 */
	public static final String PSWD = "root";

	/**
	 * Limits the number of the contemporary connections.
	 */
	private final int POOLS_CAP = 10;

	/**
	 * Defines the behavior of the method 'getConnection': if false -
	 * 'getConnection' denies any request for the connection. If true - it does as
	 * usual.
	 */
	private static boolean access;

	/**
	 * Uses as backup storage for the connections
	 */
	private static List<Connection> connectionsB;

	/**
	 * Uses as primary storage for the connections
	 */
	private static Set<Connection> connections;

	/**
	 * Creates the instance of the class and opens up the connections to the
	 * database.
	 * 
	 * @throws CouponSystemException
	 *             if there are any connection problems.
	 */
	private ConnectionPool() throws CouponSystemException {
		connectionsB = new ArrayList<>();
		connections = new HashSet<>();
		try {
			Class.forName(DEFAULT_DRIVER);
			for (int i = 0; i < POOLS_CAP; i++) {
				Connection con = DriverManager.getConnection(DEFAULT_URL, NAME, PSWD);
				connectionsB.add(con);
				connections.add(con);
			}
		} catch (Exception e) {
			throw new CouponSystemException("Sorry, there are some database connection's problems:\n" + e.getMessage());
		}
		access = true;
	}

	/**
	 * Provides a connection from the pool of connections. If there are any
	 * available at the moment, will wait when it's free. In a simple words - this
	 * method synchronized and limited by the connections pools size.
	 * 
	 * @return a connection to the database which name and path stored into
	 *         DEFAULT_URL attribute of the class.
	 * @throws CouponSystemException
	 *             if it was interrupted when it waits for a free connection. Or if
	 *             access denied due the invocation methods 'closeAllConnections()'
	 *             or 'closeAllPatiently(long)'
	 */
	public synchronized Connection getConnection() throws CouponSystemException {
		if (access) {
			while (connections.isEmpty()) {
				try {
					wait();
				} catch (InterruptedException e) {
					throw new CouponSystemException(
							"There is a problem with synchronization of access to the database");
				}
			}
			Iterator<Connection> it = connections.iterator();
			Connection con = it.next();
			it.remove();
			return con;
		} else {
			throw new CouponSystemException("The Coupon System is shuting down. Sorry about that.");
		}
	}

	/**
	 * Brings back the given connection to the connection pool and notifies about it
	 * if there is someone who waits for a free connection. Please note that this
	 * method does not performs any checks if a given connection is valid and
	 * belongs to the connection pool (means provides connection to the same
	 * database).
	 * 
	 * @param con
	 *            - the connection to add back to the connection pool.
	 */
	public synchronized void returnConnection(Connection con) {
		connections.add(con);
		notify();
	}

	/**
	 * Closes all the connections from the connection pool in an instant way. (Does
	 * not wait for a connection to be freed, than closes it).
	 * 
	 * @throws CouponSystemException
	 *             if fails to close any connections from the pool.
	 */
	public void closeAllConnections() throws CouponSystemException {
		access = false;
		for (Connection connection : connectionsB) {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new CouponSystemException(
						"Failed to close all connections, there are some database connection's problems:\n"
								+ e.getMessage());
			}
		}
	}

	/**
	 * Closes all the connections from the connection pool in an patient way. (Does
	 * wait some given values of milliseconds before it closes all the connections).
	 * During the waiting time the instance refuses to give any connections and
	 * checks every second if all connections from the pool are brought back and if
	 * they are - closes them immediately.
	 * 
	 * @param patience
	 *            - the given value for waiting time in milliseconds before it
	 *            closes all connections in an instant way.
	 * @throws CouponSystemException
	 *             if interrupted during the waiting time.
	 */
	public void closeAllPatiently(long patience) throws CouponSystemException {
		access = false;
		long untilThen = System.currentTimeMillis() + patience;
		while (System.currentTimeMillis() < untilThen) {
			if (connections.size() == POOLS_CAP) {
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				throw new CouponSystemException(
						"Failed to close patiently all connections. It seems there is a problem with syncronization.\n"
								+ e.getMessage());
			}
		}
		closeAllConnections();
	}

	/**
	 * Provides the reference to the single instance of ConnectionPool class or
	 * creates new if there is not any. It is thread safe.
	 * 
	 * @return a reference to the single instance of this class.
	 * @throws CouponSystemException
	 *             if there are any connection problems with opening up the
	 *             connections to the database. (Path and the database name defined
	 *             in the attribute DEFAULT_URL of this class.)
	 */
	public static ConnectionPool getInstance() throws CouponSystemException {
		if (instance == null) {
			synchronized (ConnectionPool.class) {
				if (instance == null)
					instance = new ConnectionPool();
			}
		}
		return instance;
	}

}