package database;

import java.sql.Connection;
import static database.ConnectionPool.DEFAULT_DRIVER;
import static database.ConnectionPool.DEFAULT_URL;
import static database.ConnectionPool.NAME;
import static database.ConnectionPool.PSWD;
import java.sql.DriverManager;

/**
 * Drops five tables from the database (uses the database name and path as it
 * stored in @see ConnectionPoll): table Company, table Customer, table Coupon,
 * Company_Coupon and Customer_Coupon
 * 
 * @author AlexanderZhilokov
 *
 */
public class DBCleaner {

	/**
	 * Note for using it: before you run it make sure that tables with same names
	 * (Company, Customer, Coupons, Company_Coupon, Customer_Coupon) at the same
	 * database at the same path ( @see ConnectionPool for the exact name and path)
	 * are exists.
	 * 
	 * @param args
	 *            - Irrelevant.
	 */
	public static void main(String[] args) {
		System.out.println("Hi! This program will drop all the tables of the database of the project 'Coupon system'.");
		System.out.println("Starting right know:");
		try {
			Class.forName(DEFAULT_DRIVER);
			try (Connection con = DriverManager.getConnection(DEFAULT_URL, NAME, PSWD)) {
				Jabberwocky.via(con).execute("drop table Coupon", "drop table Company", "drop table Customer",
						"drop table Customer_Coupon", "drop table Company_Coupon").pt();
				System.out.println("done.");
			}
		} catch (Exception e) {
			System.out.println("Something went wrong: " + e.getMessage());
			e.printStackTrace();
		}
		System.out.println("end!");
	}

}