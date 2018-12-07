package database;

import static database.ConnectionPool.DEFAULT_DRIVER;
import static database.ConnectionPool.DEFAULT_URL;
import static database.ConnectionPool.NAME;
import static database.ConnectionPool.PSWD;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Creates five new tables for the database (uses the database name and path as
 * it stored in @see ConnectionPoll): table Company, table Customer, table
 * Coupon - with column ID set to be auto-incremented. And also tables
 * Company_Coupon and Customer_Coupon
 * 
 * @author AlexanderZhilokv
 *
 */
public class DBBuilder {

	/**
	 * Just for the comfort
	 */
	public static final String CREATE = "create table";

	/**
	 * Uses as main part of the sql for the creation table Company
	 */
	public static final String TABLE_COMPANY = " Company (ID BIGINT NOT NULL AUTO_INCREMENT, "
			+ "COMP_NAME VARCHAR(40), PASSWORD VARCHAR(10), EMAIL VARCHAR(30), PRIMARY KEY (ID))";

	/**
	 * Uses as main part of the sql for the creation table Coupon
	 */
	public static final String TABLE_COUPON = " Coupon (ID BIGINT NOT NULL AUTO_INCREMENT, "
			+ "TITLE VARCHAR(40), START_DATE DATE, END_DATE DATE, AMOUNT INTEGER, "
			+ "TYPE VARCHAR(30), MESSAGE VARCHAR(1000), PRICE FLOAT, IMAGE VARCHAR(256), PRIMARY KEY (ID))";

	/**
	 * Uses as main part of the sql for the creation table Customer
	 */
	public static final String TABLE_CUSTOMER = " Customer (ID BIGINT NOT NULL AUTO_INCREMENT, CUST_NAME VARCHAR(40), PASSWORD VARCHAR(10), PRIMARY KEY (ID))";

	/**
	 * Uses as main part of the sql for the creation table Customer_Coupon
	 */
	public static final String TABLE_CUSTOMER_COUPON = " Customer_Coupon (CUST_ID BIGINT, COUPON_ID BIGINT, PRIMARY KEY (CUST_ID, COUPON_ID))";

	/**
	 * Uses as main part of the sql for the creation table Company_Coupon
	 */
	public static final String TABLE_COMPANY_COUPON = " Company_Coupon (COMP_ID BIGINT, COUPON_ID BIGINT, PRIMARY KEY (COMP_ID, COUPON_ID))";

	/**
	 * Note for using it: before you run it make sure that tables with same names
	 * (Company, Customer, Coupons, Company_Coupon, Customer_Coupon) at the same
	 * database at the same path ( @see ConnectionPool for the exact name and path)
	 * are not already exists.
	 * 
	 * @param args
	 *            - Irrelevant.
	 */
	public static void main(String[] args) {
		System.out.println(
				"Hi! This program will create all the tables for the database for the project 'Coupon system'.");
		System.out.println("Starting right know:");
		try {
			Class.forName(DEFAULT_DRIVER);
			try (Connection con = DriverManager.getConnection(DEFAULT_URL, NAME, PSWD)) {
				System.out
						.println("Creating the tables: company, customer, coupon, customer_coupon, company_coupon...");
				Jabberwocky.via(con).execute(CREATE + TABLE_COMPANY, CREATE + TABLE_COUPON, CREATE + TABLE_CUSTOMER,
						CREATE + TABLE_CUSTOMER_COUPON, CREATE + TABLE_COMPANY_COUPON).pt();
				System.out.println("done.");
			}
		} catch (Exception e) {
			System.out.println("Something went wrong: " + e.getMessage());
			e.printStackTrace();
		}
		System.out.println("end!");
	}

}