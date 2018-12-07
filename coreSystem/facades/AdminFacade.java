package facades;

import java.util.Collection;

import beans.Company;
import beans.Customer;
import dao.CompanyDAO;
import dao.CustomerDAO;
import daoDB.CompanyDBDAO;
import daoDB.CustomerDBDAO;
import database.ConnectionPool;
import exceptions.CouponSystemException;
import exceptions.PossibleDBCorruptionException;
import system.Looker;

/**
 * Implements sets of interactions between the Coupon System and standard
 * actions associated with an administrator.
 * 
 * @author AlexanderZhilokov
 *
 */
public class AdminFacade implements ClientCouponFacade {

	/**
	 * Stores a name for all the administrators known to the Coupon System.
	 */
	private final static String NAME = "admin";

	/**
	 * Stores a password for all the administrators known to the Coupon System.
	 */
	private final static String PASSWORD = "1234";

	/**
	 * Counts the number of the operations which were performed by this instance of
	 * the AdminFacade class.
	 */
	protected long operationCounter;

	/**
	 * Stores an driver to work with the companies data from the database.
	 */
	private CompanyDAO companyDAO;

	/**
	 * Stores an driver to work with the customers data from the database.
	 */
	private CustomerDAO customerDAO;

	/**
	 * Uses for receiving warnings from the drivers.
	 */
	public Looker onlooker;

	/**
	 * Creates a new instance of the class.
	 * 
	 * @param pool
	 *            - through this object the drivers will receive connections to the
	 *            database. Saves it by reference, not copies the whole object.
	 */
	public AdminFacade(ConnectionPool pool) {
		onlooker = new Looker();
		companyDAO = new CompanyDBDAO(pool, onlooker);
		customerDAO = new CustomerDBDAO(pool, onlooker);
	}

	/**
	 * Checks if the given name and password belongs to the administrator which is
	 * known to the Coupon System and valid. And if so, it provides the reference to
	 * a new instance of the class AdminFacade (sets of interactions between the
	 * Coupon System and standard actions associated with an administrator).
	 */
	@Override
	public ClientCouponFacade login(String name, String password) {
		if (name == null || password == null) {
			return null;
		} else {
			if (name.equals(NAME) && password.equals(PASSWORD)) {
				return this;
			} else {
				return null;
			}
		}
	}

	/**
	 * Prints on a console a name under which an administrator was logged into this
	 * instance and a current number of the operation to perform through this
	 * instance.
	 */
	protected String printSignature() {
		return "signature: it is operation number " + operationCounter + " and client name is " + NAME;
	}

	/**
	 * Stores a new company in the database. Notifies through onlooker the reasons,
	 * if the operation was not successfully completed.
	 * 
	 * @param company
	 *            - a new company with all it data which should be stored.
	 */
	public void createCompany(Company company) {
		operationCounter++;
		onlooker.newLog();
		onlooker.log.setSignature(printSignature());
		if (company == null) {
			onlooker.markFailure("This link is empty. Means your should initialize it first.");
			onlooker.log.setHeader("There is a problem with the creation of the company.");
		} else {
			String badHeader = "There is a problem with the creation of the company with name - "
					+ company.getCompName() + ".";
			String goodHeader = "The creation of the company with name - " + company.getCompName()
					+ " was successfully done.";
			try {
				companyDAO.createCompany(company);
				if (onlooker.hasFails()) {
					onlooker.log.setHeader(badHeader);
				} else {
					onlooker.log.setHeader(goodHeader);
				}
			} catch (CouponSystemException e) {
				onlooker.markFailure(e.msg);
				onlooker.log.setHeader(badHeader);
			}
		}
	}

	/**
	 * Removes the data about a specific company from the database. Notifies through
	 * onlooker the reasons, if the operation was not successfully completed.
	 * 
	 * @param company
	 *            - a company to remove with the parameter ID value equals to the ID
	 *            of the corresponding company as it stored in the database.
	 * @throws PossibleDBCorruptionException
	 *             if during the operation were some serious problems and there is a
	 *             danger to the integrity data in the database.
	 */
	public void removeCompany(Company company) throws PossibleDBCorruptionException {
		operationCounter++;
		onlooker.newLog();
		onlooker.log.setSignature(printSignature());
		if (company == null) {
			onlooker.markFailure(
					"This link is empty. Means you must initialize it first and give it proper ID at least.");
			onlooker.log.setHeader("There is a problem with the removal of the company.");
		} else {
			String badHeader = "There is a problem with the removal of the company with name - " + company.getCompName()
					+ ".";
			String goodHeader = "The removal of the company with name - " + company.getCompName()
					+ " - was successfully done.";
			try {
				companyDAO.deleteCompany(company);
				if (onlooker.hasFails()) {
					onlooker.log.setHeader(badHeader);
				} else {
					onlooker.log.setHeader(goodHeader);
				}
			} catch (PossibleDBCorruptionException e) {
				onlooker.log.setHeader(badHeader);
				throw e;
			} catch (CouponSystemException e) {
				onlooker.markFailure(e.msg);
				onlooker.log.setHeader(badHeader);
			}
		}
	}

	/**
	 * Updates the data about a specific company. Notifies through onlooker the
	 * reasons, if the operation was not successfully completed.
	 * 
	 * @param company
	 *            - a company which parameters values should be replaced and stored
	 *            in the database as a new data about a specific company. The
	 *            company parameter ID should be equals to the ID corresponding
	 *            company as it stored in the database.
	 */
	public void updateCompany(Company company) {
		operationCounter++;
		onlooker.newLog();
		onlooker.log.setSignature(printSignature());
		if (company == null) {
			onlooker.markFailure(
					"This link is empty. Means you must initialize it first and give it proper ID at least.");
			onlooker.log.setHeader("There is a problem with the alternation of the company.");
		} else {
			String badHeader = "There is a problem with the alternation of the company with name - "
					+ company.getCompName() + ".";
			String goodHeader = "The alternation of the company with name - " + company.getCompName()
					+ " - was successfully done.";
			try {
				companyDAO.updateCompany(company);
				if (onlooker.hasFails()) {
					onlooker.log.setHeader(badHeader);
				} else {
					onlooker.log.setHeader(goodHeader);
				}
			} catch (CouponSystemException e) {
				onlooker.markFailure(e.msg);
				onlooker.log.setHeader(badHeader);
			}
		}
	}

	/**
	 * Returns the data about a specific company. Notifies through onlooker the
	 * reasons, if the operation was not successfully completed.
	 * 
	 * @param id
	 *            - the unique value which corresponds to the ID of the company as
	 *            it stored in the database.
	 * @return the company with all it data as it stored in the database. Or if
	 *         there are any problems returns null and notifies through onlooker the
	 *         reasons.
	 */
	public Company getCompany(long id) {
		operationCounter++;
		onlooker.newLog();
		onlooker.log.setSignature(printSignature());
		String badHeader = "There is a problem with the uploading the company with ID -  " + id + ".";
		String goodHeader = "The uploading the company with ID -  " + id + " - was successfully done.";
		Company company = null;
		try {
			company = companyDAO.readCompany(id);
			if (onlooker.hasFails()) {
				onlooker.log.setHeader(badHeader);
			} else {
				onlooker.log.setHeader(goodHeader);
			}
			return company;
		} catch (CouponSystemException e) {
			onlooker.markFailure(e.msg);
			onlooker.log.setHeader(badHeader);
			return null;
		}
	}

	/**
	 * Returns the data about a specific company. Notifies through onlooker the
	 * reasons, if the operation was not successfully completed.
	 * 
	 * @param name
	 *            - the unique value which corresponds to the name of the company as
	 *            it stored in the database.
	 * @return the company with all it data as it stored in the database. Or if
	 *         there are any problems returns null and notifies through onlooker the
	 *         reasons.
	 */
	public Company getCompany(String name) {
		operationCounter++;
		onlooker.newLog();
		onlooker.log.setSignature(printSignature());
		String badHeader = "There is a problem with the uploading the company with name - " + name + ".";
		String goodHeader = "The uploading the company with name - " + name + " - was successfully done.";
		Company company = null;
		try {
			company = companyDAO.readCompany(name);
			if (onlooker.hasFails()) {
				onlooker.log.setHeader(badHeader);
			} else {
				onlooker.log.setHeader(goodHeader);
			}
			return company;
		} catch (CouponSystemException e) {
			onlooker.markFailure(e.msg);
			onlooker.log.setHeader(badHeader);
			return null;
		}
	}

	/**
	 * Returns all companies stored in the database. Notifies through onlooker the
	 * reasons, if the operation was not successfully completed.
	 * 
	 * @return all companies from the database. If not any - returns the collection
	 *         which size is zero. Or if there are any problems returns null and
	 *         notifies through onlooker the reasons.
	 */
	public Collection<Company> getAllCompanies() {
		operationCounter++;
		onlooker.newLog();
		onlooker.log.setSignature(printSignature());
		String badHeader = "There is a problem with collecting all the companies from database.";
		String goodHeader = "The collection all the companies from database was succesfully done.";
		Collection<Company> companies = null;
		try {
			companies = companyDAO.getAllCompanies();
			if (onlooker.hasFails()) {
				onlooker.log.setHeader(badHeader);
			} else {
				onlooker.log.setHeader(goodHeader);
			}
			return companies;
		} catch (CouponSystemException e) {
			onlooker.markFailure(e.msg);
			onlooker.log.setHeader(badHeader);
			return null;
		}
	}

	/**
	 * Stores a new customer in the database. Notifies through onlooker the reasons,
	 * if the operation was not successfully completed.
	 * 
	 * @param customer
	 *            - a new customer with all it data which should be stored.
	 */
	public void createCustomer(Customer customer) {
		operationCounter++;
		onlooker.newLog();
		onlooker.log.setSignature(printSignature());
		if (customer == null) {
			onlooker.markFailure("This link is empty. Means your should initialize it first.");
			onlooker.log.setHeader("There is a problem with the creation of the customer.");
		} else {
			String badHeader = "There is a problem with the creation of the customer with name - "
					+ customer.getCustName() + ".";
			String goodHeader = "The creation of the customer with name - " + customer.getCustName()
					+ " - was successfully done.";
			try {
				customerDAO.createCustomer(customer);
				if (onlooker.hasFails()) {
					onlooker.log.setHeader(badHeader);
				} else {
					onlooker.log.setHeader(goodHeader);
				}
			} catch (CouponSystemException e) {
				onlooker.markFailure(e.msg);
				onlooker.log.setHeader(badHeader);
			}
		}
	}

	/**
	 * Removes the data about a specific customer from the database. Notifies
	 * through onlooker the reasons, if the operation was not successfully
	 * completed.
	 * 
	 * @param customer
	 *            - a customer to remove with the parameter ID value equals to the
	 *            ID of the corresponding customer as it stored in the database.
	 * @throws PossibleDBCorruptionException
	 *             if during the operation were some serious problems and there is a
	 *             danger to the integrity data in the database.
	 */
	public void removeCustomer(Customer customer) throws PossibleDBCorruptionException {
		operationCounter++;
		onlooker.newLog();
		onlooker.log.setSignature(printSignature());
		if (customer == null) {
			onlooker.markFailure("This link is empty. Means your should initialize it first.");
			onlooker.log.setHeader("There is a problem with the removal of the customer.");
		} else {
			String badHeader = "There is a problem with the removal of the customer with name - "
					+ customer.getCustName() + ".";
			String goodHeader = "The removal of the customer with name - " + customer.getCustName()
					+ " - was successfully done.";
			try {
				customerDAO.deleteCustomer(customer);
				if (onlooker.hasFails()) {
					onlooker.log.setHeader(badHeader);
				} else {
					onlooker.log.setHeader(goodHeader);
				}
			} catch (PossibleDBCorruptionException e) {
				onlooker.log.setHeader(badHeader);
				throw e;
			} catch (CouponSystemException e) {
				onlooker.markFailure(e.msg);
				onlooker.log.setHeader(badHeader);
			}
		}
	}

	/**
	 * Updates the data about a specific customer. Notifies through onlooker the
	 * reasons, if the operation was not successfully completed.
	 * 
	 * @param customer
	 *            - a customer which parameters values should be replaced and stored
	 *            in the database as a new data about a specific customer. The
	 *            customer parameter ID should be equals to the ID of the
	 *            corresponding customer as it stored in the database.
	 */
	public void updateCustomer(Customer customer) {
		operationCounter++;
		onlooker.newLog();
		onlooker.log.setSignature(printSignature());
		if (customer == null) {
			onlooker.markFailure("This link is empty. Means your should initialize it first.");
			onlooker.log.setHeader("There is a problem with the alternation of the customer.");
		} else {
			String badHeader = "There is a problem with the alternation of the customer with name - "
					+ customer.getCustName() + ".";
			String goodHeader = "The alternation of the customer with name - " + customer.getCustName()
					+ " - was succesfully done.";
			try {
				customerDAO.updateCustomer(customer);
				if (onlooker.hasFails()) {
					onlooker.log.setHeader(badHeader);
				} else {
					onlooker.log.setHeader(goodHeader);
				}
			} catch (CouponSystemException e) {
				onlooker.markFailure(e.msg);
				onlooker.log.setHeader(badHeader);
			}
		}
	}

	/**
	 * Returns the data about a specific customer. Notifies through onlooker the
	 * reasons, if the operation was not successfully completed.
	 * 
	 * @param id
	 *            - the unique value which corresponds to the ID of the customer as
	 *            it stored in the database.
	 * @return the customer with all his data as it stored in the database. Or if
	 *         there are any problems returns null and notifies through onlooker the
	 *         reasons.
	 */
	public Customer getCustomer(long id) {
		operationCounter++;
		onlooker.newLog();
		onlooker.log.setSignature(printSignature());
		String badHeader = "There is a problem with uploading the customer with ID -" + id + ".";
		String goodHeader = "The uploading the customer with ID - " + id + " - was succesfully done.";
		Customer customer = null;
		try {
			customer = customerDAO.readCustomer(id);
			if (onlooker.hasFails()) {
				onlooker.log.setHeader(badHeader);
			} else {
				onlooker.log.setHeader(goodHeader);
			}
			return customer;
		} catch (CouponSystemException e) {
			onlooker.markFailure(e.msg);
			onlooker.log.setHeader(badHeader);
			return null;
		}
	}

	/**
	 * Returns the data about a specific customer. Notifies through onlooker the
	 * reasons, if the operation was not successfully completed.
	 * 
	 * @param name
	 *            - the unique value which corresponds to the name of the customer
	 *            as it stored in the database.
	 * @return the customer with all his data as it stored in the database. Or if
	 *         there are any problems returns null and notifies through onlooker the
	 *         reasons.
	 */
	public Customer getCustomer(String name) {
		operationCounter++;
		onlooker.newLog();
		onlooker.log.setSignature(printSignature());
		String badHeader = "There is a problem with uploading the customer with name - " + name + ".";
		String goodHeader = "The uploading the customer with name - " + name + " - was successfully done.";
		Customer customer = null;
		try {
			customer = customerDAO.readCustomer(name);
			if (onlooker.hasFails()) {
				onlooker.log.setHeader(badHeader);
			} else {
				onlooker.log.setHeader(goodHeader);
			}
			return customer;
		} catch (CouponSystemException e) {
			onlooker.markFailure(e.msg);
			onlooker.log.setHeader(badHeader);
			return null;
		}
	}

	/**
	 * Returns all customers stored in the database. Notifies through onlooker the
	 * reasons, if the operation was not successfully completed.
	 * 
	 * @return all customers from the database. If not any - returns the collection
	 *         which size is zero. Or if there are any problems returns null and
	 *         notifies through onlooker the reasons.
	 */
	public Collection<Customer> getAllCustomers() {
		operationCounter++;
		onlooker.newLog();
		onlooker.log.setSignature(printSignature());
		String badHeader = "There is a problem with collecting all the customers from database.";
		String goodHeader = "The collection all the customers from database was successfully done.";
		Collection<Customer> customers = null;
		try {
			customers = customerDAO.getAllCustomers();
			if (onlooker.hasFails()) {
				onlooker.log.setHeader(badHeader);
			} else {
				onlooker.log.setHeader(goodHeader);
			}
			return customers;
		} catch (CouponSystemException e) {
			onlooker.markFailure(e.msg);
			onlooker.log.setHeader(badHeader);
			return null;
		}
	}

}