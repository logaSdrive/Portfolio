package logas.Store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import beans.Company;
import beans.Customer;
import beans.Log;
import exceptions.PossibleDBCorruptionException;
import facades.AdminFacade;
import system.CouponSystem;
import utilities.Utilities;

/**
 * Serves for the requests from Admin type of users.
 * 
 * @author AlexanderZhilokov
 *
 */
@Path("Admin")
public class AdminRes {

	/**
	 * Stores a reference to the data of the current request.
	 */
	@Context
	private HttpServletRequest request;

	/**
	 * Stores a new company in a database.
	 * 
	 * @param company
	 *            - a company with all it values which meant to be stored in a
	 *            database.
	 * @return a collection of object which consists from only one element - it is a
	 *         log about executed operation.
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("createCompany")
	public Collection<Object> createCompany(Company company) {
		Collection<Object> response = new ArrayList<>();
		Logger logger = Utilities.getLogger();
		try {
			admin().createCompany(company);
			Log log = admin().onlooker.getLog();
			logger.info(
					"At at AdminRes.java (createCompany) by: " + request.getSession().getId() + "\n" + log.toString());
			response.add(log);
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "Exception at AdminRes.java (createCompany) by: " + request.getSession().getId(),
					e);
			response.add(admin().onlooker.getLog());
			return response;
		}
	}

	/**
	 * Returns a company which id is equals to the given value of id.
	 * 
	 * @param id
	 *            - value which corresponds to the company's id as it stored in a
	 *            database
	 * @return a collection of object: it first element is a log about executed
	 *         operation and second is a company which id corresponds to the given
	 *         parameter and with all it values from the database. If by any reason
	 *         operation was not succeeded - second element of returning collection
	 *         is null.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getCompany/{id}")
	public Collection<Object> getCompany(@PathParam("id") int id) {
		Collection<Object> response = new ArrayList<>();
		Logger logger = Utilities.getLogger();
		try {
			Company company = admin().getCompany(id);
			Log log = admin().onlooker.getLog();
			logger.info("At at AdminRes.java (getCompany) by: " + request.getSession().getId() + "\n" + log.toString());
			response.add(log);
			response.add(company);
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "Exception at AdminRes.java (getCompany) by: " + request.getSession().getId(), e);
			response.add(admin().onlooker.getLog());
			return response;
		}
	}

	/**
	 * Returns a company which name is equals to the given value.
	 * 
	 * @param name
	 *            - value which corresponds to the company's name as it stored in a
	 *            database
	 * @return a collection of object: it first element is a log about executed
	 *         operation and second is a company which name corresponds to the given
	 *         parameter and with all it values from the database. If by any reason
	 *         operation was not succeeded - second element of returning collection
	 *         is null.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getCompanyByName/{name}")
	public Collection<Object> getCompanyByName(@PathParam("name") String name) {
		Collection<Object> response = new ArrayList<>();
		Logger logger = Utilities.getLogger();
		try {
			Company company = admin().getCompany(name);
			Log log = admin().onlooker.getLog();
			logger.info("At at AdminRes.java (getCompanyByName) by: " + request.getSession().getId() + "\n"
					+ log.toString());
			response.add(log);
			response.add(company);
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE,
					"Exception at AdminRes.java (getCompanyByName) by: " + request.getSession().getId(), e);
			response.add(admin().onlooker.getLog());
			return response;
		}
	}

	/**
	 * Updates a company in a database which id is equals to the value of id of the
	 * given company.
	 * 
	 * @param company
	 *            - a company with all it values which should be updated and which
	 *            id corresponds to the company's id as it stored in a database
	 * @return a collection of object which consists from only one element - it is a
	 *         log about executed operation.
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("updateCompany")
	public Collection<Object> updateCompany(Company company) {
		Collection<Object> response = new ArrayList<>();
		Logger logger = Utilities.getLogger();
		try {
			admin().updateCompany(company);
			Log log = admin().onlooker.getLog();
			logger.info(
					"At at AdminRes.java (updateCompany) by: " + request.getSession().getId() + "\n" + log.toString());
			response.add(log);
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "Exception at AdminRes.java (updateCompany) by: " + request.getSession().getId(),
					e);
			response.add(admin().onlooker.getLog());
			return response;
		}
	}

	/**
	 * Deletes a company in a database which id is equals to the given value of id.
	 * 
	 * @param company
	 *            - a company which should be deleted and which id corresponds to
	 *            the company's id as it stored in a database
	 * @return a collection of object which consists from only one element - it is a
	 *         log about executed operation.
	 */
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("removeCompany")
	public Collection<Object> removeCompany(Company company) {
		Collection<Object> response = new ArrayList<>();
		Logger logger = Utilities.getLogger();
		try {
			admin().removeCompany(company);
			Log log = admin().onlooker.getLog();
			logger.info(
					"At at AdminRes.java (removeCompany) by: " + request.getSession().getId() + "\n" + log.toString());
			response.add(log);
			return response;
		} catch (PossibleDBCorruptionException e) {
			// transaction failed and rollback failed as well.
			CouponSystem.getInstance().shutdownPatiently(10000);
			logger.log(Level.SEVERE, "Exception at AdminRes.java (removeCompany) by: " + request.getSession().getId(),
					e);
			response.add(admin().onlooker.getLog());
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "Exception at AdminRes.java (removeCompany) by: " + request.getSession().getId(),
					e);
			response.add(admin().onlooker.getLog());
			return response;
		}
	}

	/**
	 * Returns all the companies from the database.
	 * 
	 * @return a collection of object: it first element is a log about executed
	 *         operation and rest are a companies from the database. If by any
	 *         reason operation was not succeeded - it consists from the log only.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getAllCompanies")
	public Collection<Object> getAllCompanies() {
		Collection<Object> response = new ArrayList<>();
		Logger logger = Utilities.getLogger();
		try {
			Collection<Company> companies = admin().getAllCompanies();
			Log log = admin().onlooker.getLog();
			logger.info("At at AdminRes.java (getAllCompanies) by: " + request.getSession().getId() + "\n"
					+ log.toString());
			response.add(log);
			response.addAll(companies);
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "Exception at AdminRes.java (getAllCompanies) by: " + request.getSession().getId(),
					e);
			response.add(admin().onlooker.getLog());
			return response;
		}
	}

	/**
	 * Stores a new customer in a database.
	 * 
	 * @param customer
	 *            - a customer with all it values which meant to be stored in a
	 *            database.
	 * @return a collection of object which consists from only one element - it is a
	 *         log about executed operation.
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("createCustomer")
	public Collection<Object> createCustomer(Customer customer) {
		Collection<Object> response = new ArrayList<>();
		Logger logger = Utilities.getLogger();
		try {
			admin().createCustomer(customer);
			Log log = admin().onlooker.getLog();
			logger.info(
					"At at AdminRes.java (createCustomer) by: " + request.getSession().getId() + "\n" + log.toString());
			response.add(log);
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "Exception at AdminRes.java (createCustomer) by: " + request.getSession().getId(),
					e);
			response.add(admin().onlooker.getLog());
			return response;
		}
	}

	/**
	 * Returns a customer which id is equals to the given value of id.
	 * 
	 * @param id
	 *            - value which corresponds to the customer's id as it stored in a
	 *            database
	 * @return a collection of object: it first element is a log about executed
	 *         operation and second is a customer which id corresponds to the given
	 *         parameter and with all it values from the database. If by any reason
	 *         operation was not succeeded - second element of returning collection
	 *         is null.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getCustomer/{id}")
	public Collection<Object> getCustomer(@PathParam("id") int id) {
		Collection<Object> response = new ArrayList<>();
		Logger logger = Utilities.getLogger();
		try {
			Customer customer = admin().getCustomer(id);
			Log log = admin().onlooker.getLog();
			logger.info(
					"At at AdminRes.java (getCustomer) by: " + request.getSession().getId() + "\n" + log.toString());
			response.add(log);
			response.add(customer);
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "Exception at AdminRes.java (getCustomer) by: " + request.getSession().getId(), e);
			response.add(admin().onlooker.getLog());
			return response;
		}
	}

	/**
	 * Returns a customer which name is equals to the given value.
	 * 
	 * @param name
	 *            - value which corresponds to the customer's name as it stored in a
	 *            database
	 * @return a collection of object: it first element is a log about executed
	 *         operation and second is a customer which name corresponds to the
	 *         given parameter and with all it values from the database. If by any
	 *         reason operation was not succeeded - second element of returning
	 *         collection is null.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getCustomerByName/{name}")
	public Collection<Object> getCustomeByNamer(@PathParam("name") String name) {
		Collection<Object> response = new ArrayList<>();
		Logger logger = Utilities.getLogger();
		try {
			Customer customer = admin().getCustomer(name);
			Log log = admin().onlooker.getLog();
			logger.info("At at AdminRes.java (getCustomerByName) by: " + request.getSession().getId() + "\n"
					+ log.toString());
			response.add(log);
			response.add(customer);
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE,
					"Exception at AdminRes.java (getCustomerByName) by: " + request.getSession().getId(), e);
			response.add(admin().onlooker.getLog());
			return response;
		}
	}

	/**
	 * Updates a customer in a database which id is equals to the value of id of the
	 * given customer.
	 * 
	 * @param customer
	 *            - a customer with all it values which should be updated and which
	 *            id corresponds to the customer's id as it stored in a database
	 * @return a collection of object which consists from only one element - it is a
	 *         log about executed operation.
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("updateCustomer")
	public Collection<Object> updateCustomer(Customer customer) {
		Collection<Object> response = new ArrayList<>();
		Logger logger = Utilities.getLogger();
		try {
			admin().updateCustomer(customer);
			Log log = admin().onlooker.getLog();
			logger.info(
					"At at AdminRes.java (updateCustomer) by: " + request.getSession().getId() + "\n" + log.toString());
			response.add(log);
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "Exception at AdminRes.java (updateCustomer) by: " + request.getSession().getId(),
					e);
			response.add(admin().onlooker.getLog());
			return response;
		}
	}

	/**
	 * Deletes a customer in a database which id is equals to the given value of id.
	 * 
	 * @param customer
	 *            - a customer which should be deleted and which id corresponds to
	 *            the customer's id as it stored in a database
	 * @return a collection of object which consists from only one element - it is a
	 *         log about executed operation.
	 */
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("removeCustomer")
	public Collection<Object> removeCustomer(Customer customer) {
		Collection<Object> response = new ArrayList<>();
		Logger logger = Utilities.getLogger();
		try {
			admin().removeCustomer(customer);
			Log log = admin().onlooker.getLog();
			logger.info(
					"At at AdminRes.java (removeCustomer) by: " + request.getSession().getId() + "\n" + log.toString());
			response.add(log);
			return response;
		} catch (PossibleDBCorruptionException e) {
			// transaction failed and rollback failed as well.
			CouponSystem.getInstance().shutdownPatiently(10000);
			logger.log(Level.SEVERE, "Exception at AdminRes.java (removeCustomer) by: " + request.getSession().getId(),
					e);
			response.add(admin().onlooker.getLog());
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "Exception at AdminRes.java (removeCustomer) by: " + request.getSession().getId(),
					e);
			response.add(admin().onlooker.getLog());
			return response;
		}
	}

	/**
	 * Returns all the customers from the database.
	 * 
	 * @return a collection of object: it first element is a log about executed
	 *         operation and rest are a customers from the database. If by any
	 *         reason operation was not succeeded - it consists from the log only.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getAllCustomers")
	public Collection<Object> getAllCustomers() {
		Collection<Object> response = new ArrayList<>();
		Logger logger = Utilities.getLogger();
		try {
			Collection<Customer> customers = admin().getAllCustomers();
			Log log = admin().onlooker.getLog();
			logger.info("At at AdminRes.java (getAllCustomers) by: " + request.getSession().getId() + "\n"
					+ log.toString());
			response.add(log);
			response.addAll(customers);
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "Exception at AdminRes.java (getAllCustomers) by: " + request.getSession().getId(),
					e);
			response.add(admin().onlooker.getLog());
			return response;
		}
	}

	/**
	 * Returns a admin facade from the current session.
	 * 
	 * @return an instance of the AdminFacade class.
	 */
	private AdminFacade admin() {
		return (AdminFacade) request.getSession().getAttribute("Admin");
	}

}