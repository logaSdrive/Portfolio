package database;

/**
 * Uses for the synchronization actions with the database.
 * 
 * @author AlexanderZhilokov
 *
 */
public enum ChainLock {

	/**
	 * Uses as a unique key to the synchronization the actions with the table
	 * Company
	 */
	TableCompany,

	/**
	 * Uses as a unique key to the synchronization the actions with the table
	 * Customer
	 */
	TableCustomer,

	/**
	 * Uses as a unique key to the synchronization the actions with the table Coupon
	 */
	TableCoupon,

	/**
	 * Uses as a unique key to the synchronization the actions with the table
	 * Company_Coupon
	 */
	TableCompanyCoupon,

	/**
	 * Uses as a unique key to the synchronization the actions with the table
	 * Customer_Coupon
	 */
	TableCustomerCoupon

}