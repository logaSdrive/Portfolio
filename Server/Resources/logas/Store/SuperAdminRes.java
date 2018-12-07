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
import beans.Coupon;
import beans.CouponType;
import beans.Customer;
import beans.Log;
import exceptions.PossibleDBCorruptionException;
import facades.SuperAdminFacade;
import system.CouponSystem;
import utilities.Utilities;

/**
 * Serves for the requests from SuperAdmin type of users.
 * 
 * @author AlexanderZhilokov
 *
 */
@Path("SuperAdmin")
public class SuperAdminRes {

	/**
	 * Stores a reference to the data of the current request.
	 */
	@Context
	private HttpServletRequest request;

	/**
	 * Returns a coupon which id is equals to the given value of id.
	 * 
	 * @param id
	 *            - value which corresponds to the coupon's id as it stored in a
	 *            database
	 * @return a collection of object: it first element is a log about executed
	 *         operation and second is a coupon which id corresponds to the given
	 *         parameter and with all it values from the database. If by any reason
	 *         operation was not succeeded - second element of returning collection
	 *         is null.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getCoupon/{id}")
	public Collection<Object> getCoupon(@PathParam("id") long id) {
		Collection<Object> response = new ArrayList<>();
		Logger logger = Utilities.getLogger();
		try {
			Coupon coupon = superAdmin().getCoupon(id);
			Log log = superAdmin().onlooker.getLog();
			logger.info(
					"At at SuperAdminRes.java (getCoupon) by: " + request.getSession().getId() + "\n" + log.toString());
			response.add(log);
			response.add(coupon);
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "Exception at SuperAdminRes.java (getCoupon) by: " + request.getSession().getId(),
					e);
			response.add(superAdmin().onlooker.getLog());
			return response;
		}
	}

	/**
	 * Updates a coupon in a database which id is equals to the value of id of the
	 * given coupon.
	 * 
	 * @param coupon
	 *            - a coupon with all it values which should be updated and which id
	 *            corresponds to the coupon's id as it stored in a database
	 * @return a collection of object which consists from only one element - it is a
	 *         log about executed operation.
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("updateCoupon")
	public Collection<Object> updateCoupon(Coupon coupon) {
		Collection<Object> response = new ArrayList<>();
		Logger logger = Utilities.getLogger();
		try {
			superAdmin().updateCoupon(coupon);
			Log log = superAdmin().onlooker.getLog();
			logger.info("At at SuperAdminRes.java (updateCoupon) by: " + request.getSession().getId() + "\n"
					+ log.toString());
			response.add(log);
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE,
					"Exception at SuperAdminRes.java (updateCoupon) by: " + request.getSession().getId(), e);
			response.add(superAdmin().onlooker.getLog());
			return response;
		}
	}

	/**
	 * Deletes a coupon in a database which id is equals to the given value of id.
	 * 
	 * @param coupon
	 *            - a coupon which should be deleted and which id corresponds to the
	 *            coupon's id as it stored in a database
	 * @return a collection of object which consists from only one element - it is a
	 *         log about executed operation.
	 */
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("removeCoupon")
	public Collection<Object> removeCoupon(Coupon coupon) {
		Collection<Object> response = new ArrayList<>();
		Logger logger = Utilities.getLogger();
		try {
			superAdmin().removeCoupon(coupon);
			Log log = superAdmin().onlooker.getLog();
			logger.info("At at SuperAdminRes.java (removeCoupon) by: " + request.getSession().getId() + "\n"
					+ log.toString());
			response.add(log);
			return response;
		} catch (PossibleDBCorruptionException e) {
			// transaction failed and rollback failed as well.
			CouponSystem.getInstance().shutdownPatiently(10000);
			logger.log(Level.SEVERE,
					"Exception at SuperAdminRes.java (removeCoupon) by: " + request.getSession().getId(), e);
			response.add(superAdmin().onlooker.getLog());
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE,
					"Exception at SuperAdminRes.java (removeCoupon) by: " + request.getSession().getId(), e);
			response.add(superAdmin().onlooker.getLog());
			return response;
		}
	}

	/**
	 * Returns all the coupons from the database.
	 * 
	 * @return a collection of object: it first element is a log about executed
	 *         operation and rest are a coupons from the database. If by any reason
	 *         operation was not succeeded - it consists from the log only.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getAllCoupons")
	public Collection<Object> getAllCoupons() {
		Collection<Object> response = new ArrayList<>();
		Logger logger = Utilities.getLogger();
		try {
			Collection<Coupon> coupons = superAdmin().getAllCoupons();
			Log log = superAdmin().onlooker.getLog();
			logger.info("At at SuperAdminRes.java (getAllCoupons) by: " + request.getSession().getId() + "\n"
					+ log.toString());
			response.add(log);
			response.addAll(coupons);
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE,
					"Exception at SuperAdminRes.java (getAllCoupons) by: " + request.getSession().getId(), e);
			response.add(superAdmin().onlooker.getLog());
			return response;
		}
	}

	/**
	 * Returns all the coupons from the database which type is corresponds to the
	 * given value.
	 * 
	 * @param type
	 *            - a value which corresponds to one of the values of enum
	 *            CouponType.
	 * @return a collection of object: it first element is a log about executed
	 *         operation and rest are a coupons from the database which types are
	 *         equals to the given type value. If by any reason operation was not
	 *         succeeded - it consists from the log only.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getCouponsByType/{type}")
	public Collection<Object> getCouponsByType(@PathParam("type") String type) {
		Collection<Object> response = new ArrayList<>();
		Logger logger = Utilities.getLogger();
		try {
			Collection<Coupon> coupons = superAdmin().getCouponsByType(CouponType.valueOf(type));
			Log log = superAdmin().onlooker.getLog();
			logger.info("At at SuperAdminRes.java (getCouponsByType) by: " + request.getSession().getId() + "\n"
					+ log.toString());
			response.add(log);
			response.addAll(coupons);
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE,
					"Exception at SuperAdminRes.java (getCouponsByType) by: " + request.getSession().getId(), e);
			response.add(superAdmin().onlooker.getLog());
			return response;
		}
	}

	/**
	 * Returns all the company's coupons from the database which id is corresponds
	 * to the given value.
	 * 
	 * @param id
	 *            - a value which corresponds to the company's id value as it stored
	 *            in a database.
	 * @return collection of object: it first element is a log about executed
	 *         operation and rest are the coupons from the database which were
	 *         created by the company which id is corresponds to the given id value.
	 *         If by any reason operation was not succeeded - it consists from the
	 *         log only.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getCompanyCoupons/{id}")
	public Collection<Object> getCompanyCoupons(@PathParam("id") long id) {
		Collection<Object> response = new ArrayList<>();
		Logger logger = Utilities.getLogger();
		try {
			Collection<Coupon> coupons = superAdmin().getCompanyCoupons(id);
			Log log = superAdmin().onlooker.getLog();
			logger.info("At at SuperAdminRes.java (getCompanyCoupons) by: " + request.getSession().getId() + "\n"
					+ log.toString());
			response.add(log);
			response.addAll(coupons);
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE,
					"Exception at SuperAdminRes.java (getCompanyCoupons) by: " + request.getSession().getId(), e);
			response.add(superAdmin().onlooker.getLog());
			return response;
		}
	}

	/**
	 * Returns all the customer's coupons from the database which id is corresponds
	 * to the given value.
	 * 
	 * @param id
	 *            - a value which corresponds to the customer's id value as it
	 *            stored in a database.
	 * @return collection of object: it first element is a log about executed
	 *         operation and rest are the coupons from the database which were
	 *         bought by the customer which id is corresponds to the given id value.
	 *         If by any reason operation was not succeeded - it consists from the
	 *         log only.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getCustomerCoupons/{id}")
	public Collection<Object> getCustomerCoupons(@PathParam("id") long id) {
		Collection<Object> response = new ArrayList<>();
		Logger logger = Utilities.getLogger();
		try {
			Collection<Coupon> coupons = superAdmin().getCustomerCoupons(id);
			Log log = superAdmin().onlooker.getLog();
			logger.info("At at SuperAdminRes.java (getCustomerCoupons) by: " + request.getSession().getId() + "\n"
					+ log.toString());
			response.add(log);
			response.addAll(coupons);
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE,
					"Exception at SuperAdminRes.java (getCustomerCoupons) by: " + request.getSession().getId(), e);
			response.add(superAdmin().onlooker.getLog());
			return response;
		}
	}

	/**
	 * Cashbacks a customer for a coupon which ids are corresponds to the given
	 * values.
	 * 
	 * @param custId
	 *            - a value which corresponds to the customer's id value as it
	 *            stored in a database.
	 * @param couponId
	 *            - a value which corresponds to the coupon's id value as it stored
	 *            in a database.
	 * @return a collection of object which consists from only one element - it is a
	 *         log about executed operation.
	 */
	@PUT
	@Path("removeCouponFromCustomer/{custId}/{couponId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Object> removeCouponFromCustomer(@PathParam("custId") long custId,
			@PathParam("couponId") long couponId) {
		Collection<Object> response = new ArrayList<>();
		Logger logger = Utilities.getLogger();
		try {
			superAdmin().removeCouponFromCustomer(custId, couponId);
			Log log = superAdmin().onlooker.getLog();
			logger.info("At at SuperAdminRes.java (removeCouponFromCustomer) by: " + request.getSession().getId() + "\n"
					+ log.toString());
			response.add(log);
			return response;
		} catch (PossibleDBCorruptionException e) {
			// transaction failed and rollback failed as well.
			CouponSystem.getInstance().shutdownPatiently(10000);
			logger.log(Level.SEVERE,
					"Exception at SuperAdminRes.java (removeCouponFromCustomer) by: " + request.getSession().getId(),
					e);
			response.add(superAdmin().onlooker.getLog());
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE,
					"Exception at SuperAdminRes.java (removeCouponFromCustomer) by: " + request.getSession().getId(),
					e);
			response.add(superAdmin().onlooker.getLog());
			return response;
		}
	}

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
			superAdmin().createCompany(company);
			Log log = superAdmin().onlooker.getLog();
			logger.info("At at SuperAdminRes.java (createCompany) by: " + request.getSession().getId() + "\n"
					+ log.toString());
			response.add(log);
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE,
					"Exception at SuperAdminRes.java (createCompany) by: " + request.getSession().getId(), e);
			response.add(superAdmin().onlooker.getLog());
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
			Company company = superAdmin().getCompany(id);
			Log log = superAdmin().onlooker.getLog();
			logger.info("At at SuperAdminRes.java (getCompany) by: " + request.getSession().getId() + "\n"
					+ log.toString());
			response.add(log);
			response.add(company);
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "Exception at SuperAdminRes.java (getCompany) by: " + request.getSession().getId(),
					e);
			response.add(superAdmin().onlooker.getLog());
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
			Company company = superAdmin().getCompany(name);
			Log log = superAdmin().onlooker.getLog();
			logger.info("At at SuperAdminRes.java (getCompanyByName) by: " + request.getSession().getId() + "\n"
					+ log.toString());
			response.add(log);
			response.add(company);
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE,
					"Exception at SuperAdminRes.java (getCompanyByName) by: " + request.getSession().getId(), e);
			response.add(superAdmin().onlooker.getLog());
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
			superAdmin().updateCompany(company);
			Log log = superAdmin().onlooker.getLog();
			logger.info("At at SuperAdminRes.java (updateCompany) by: " + request.getSession().getId() + "\n"
					+ log.toString());
			response.add(log);
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE,
					"Exception at SuperAdminRes.java (updateCompany) by: " + request.getSession().getId(), e);
			response.add(superAdmin().onlooker.getLog());
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
			superAdmin().removeCompany(company);
			Log log = superAdmin().onlooker.getLog();
			logger.info("At at SuperAdminRes.java (removeCompany) by: " + request.getSession().getId() + "\n"
					+ log.toString());
			response.add(log);
			return response;
		} catch (PossibleDBCorruptionException e) {
			// transaction failed and rollback failed as well.
			CouponSystem.getInstance().shutdownPatiently(10000);
			logger.log(Level.SEVERE,
					"Exception at SuperAdminRes.java (removeCompany) by: " + request.getSession().getId(), e);
			response.add(superAdmin().onlooker.getLog());
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE,
					"Exception at SuperAdminRes.java (removeCompany) by: " + request.getSession().getId(), e);
			response.add(superAdmin().onlooker.getLog());
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
			Collection<Company> companies = superAdmin().getAllCompanies();
			Log log = superAdmin().onlooker.getLog();
			logger.info("At at SuperAdminRes.java (getAllCompanies) by: " + request.getSession().getId() + "\n"
					+ log.toString());
			response.add(log);
			response.addAll(companies);
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE,
					"Exception at SuperAdminRes.java (getAllCompanies) by: " + request.getSession().getId(), e);
			response.add(superAdmin().onlooker.getLog());
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
			superAdmin().createCustomer(customer);
			Log log = superAdmin().onlooker.getLog();
			logger.info("At at SuperAdminRes.java (createCustomer) by: " + request.getSession().getId() + "\n"
					+ log.toString());
			response.add(log);
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE,
					"Exception at SuperAdminRes.java (createCustomer) by: " + request.getSession().getId(), e);
			response.add(superAdmin().onlooker.getLog());
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
			Customer customer = superAdmin().getCustomer(id);
			Log log = superAdmin().onlooker.getLog();
			logger.info("At at SuperAdminRes.java (getCustomer) by: " + request.getSession().getId() + "\n"
					+ log.toString());
			response.add(log);
			response.add(customer);
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE,
					"Exception at SuperAdminRes.java (getCustomer) by: " + request.getSession().getId(), e);
			response.add(superAdmin().onlooker.getLog());
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
			Customer customer = superAdmin().getCustomer(name);
			Log log = superAdmin().onlooker.getLog();
			logger.info("At at SuperAdminRes.java (getCustomerByName) by: " + request.getSession().getId() + "\n"
					+ log.toString());
			response.add(log);
			response.add(customer);
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE,
					"Exception at SuperAdminRes.java (getCustomerByName) by: " + request.getSession().getId(), e);
			response.add(superAdmin().onlooker.getLog());
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
			superAdmin().updateCustomer(customer);
			Log log = superAdmin().onlooker.getLog();
			logger.info("At at SuperAdminRes.java (updateCustomer) by: " + request.getSession().getId() + "\n"
					+ log.toString());
			response.add(log);
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE,
					"Exception at SuperAdminRes.java (updateCustomer) by: " + request.getSession().getId(), e);
			response.add(superAdmin().onlooker.getLog());
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
			superAdmin().removeCustomer(customer);
			Log log = superAdmin().onlooker.getLog();
			logger.info("At at SuperAdminRes.java (removeCustomer) by: " + request.getSession().getId() + "\n"
					+ log.toString());
			response.add(log);
			return response;
		} catch (PossibleDBCorruptionException e) {
			// transaction failed and rollback failed as well.
			CouponSystem.getInstance().shutdownPatiently(10000);
			logger.log(Level.SEVERE,
					"Exception at SuperAdminRes.java (removeCustomer) by: " + request.getSession().getId(), e);
			response.add(superAdmin().onlooker.getLog());
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE,
					"Exception at SuperAdminRes.java (removeCustomer) by: " + request.getSession().getId(), e);
			response.add(superAdmin().onlooker.getLog());
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
			Collection<Customer> customers = superAdmin().getAllCustomers();
			Log log = superAdmin().onlooker.getLog();
			logger.info("At at SuperAdminRes.java (getAllCustomers) by: " + request.getSession().getId() + "\n"
					+ log.toString());
			response.add(log);
			response.addAll(customers);
			return response;
		} catch (Throwable e) {
			logger.log(Level.SEVERE,
					"Exception at SuperAdminRes.java (getAllCustomers) by: " + request.getSession().getId(), e);
			response.add(superAdmin().onlooker.getLog());
			return response;
		}
	}

	/**
	 * Returns a superAdmin facade from the current session.
	 * 
	 * @return an instance of the SuperAdminFacade class.
	 */
	private SuperAdminFacade superAdmin() {
		return (SuperAdminFacade) request.getSession().getAttribute("SuperAdmin");
	}

}