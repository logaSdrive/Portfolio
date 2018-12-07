package facades;

/**
 * Is used to define the types of the facades (sets of interactions between the
 * Coupon System and actions associated with the specified client type) in which
 * you can be logged through the Coupon System.
 * 
 * @author AlexanderZhilokov
 *
 */
public enum ClientType {

	/**
	 * Uses to define the instance of the AdminFacade class.
	 */
	Admin,

	/**
	 * Uses to define the instance of the CompanyFacade class.
	 */
	Company,

	/**
	 * Uses to define the instance of the CustomerFacade class.
	 */
	Customer,

	/**
	 * Uses to define the instance of the SuperAdminFacade class.
	 */
	SuperAdmin
}