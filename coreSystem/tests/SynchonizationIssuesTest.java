package tests;

import java.sql.Connection;
import java.sql.DriverManager;
import static database.ConnectionPool.DEFAULT_DRIVER;
import static database.ConnectionPool.DEFAULT_URL;
import static database.ConnectionPool.NAME;
import static database.ConnectionPool.PSWD;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import beans.Company;
import beans.Coupon;
import beans.CouponType;
import beans.Customer;
import database.DBBuilder;
import database.DBCleaner;
import exceptions.PossibleDBCorruptionException;
import facades.AdminFacade;
import facades.ClientType;
import facades.CompanyFacade;
import facades.CustomerFacade;
import facades.SuperAdminFacade;
import system.CouponSystem;

/**
 * Applies different methods through different facades at the same time. It is
 * focusing on methods which are writing to and are reading from different
 * tables in the database at the same time.
 * 
 * @author AlexanderZhilokov
 *
 */
public class SynchonizationIssuesTest {

	/**
	 * I downloaded it from the interned. It should wait for "enter" to continue
	 * than invoked, and it does, but only sometimes.
	 */
	private static void pressAnyKeyToContinue() {
		System.out.println("Press Enter key to continue...");
		try {
			System.in.read();
		} catch (Exception e) {
		}
	}

	/**
	 * Note for using it: You should run it in a proper order:
	 * AdminBasicTest.main(), CompanyBasicTest.main(), CustomerBasicTest.main(),
	 * SuperAdminBasicTest.main(), InputTroubleTest.main(), LogicalIssuesTest.main()
	 * and SynchronizationIssuesTest.main(). Or you can just launch MainTest.main()
	 * which runs them all in series.
	 * 
	 * @param args
	 *            - Irrelevant.
	 */
	public static void main(String[] args) {
		System.out.println("Hi! This is a test for issues with syncronization.");

		// PREPARATION:

		System.out.println("Preparing data for the tests: ");
		System.out.println("Uploading the system:");
		CouponSystem store = CouponSystem.getInstance();
		System.out.println("done.\n");

		System.out.println("Dropping all tables:");
		DBCleaner.main(null);
		System.out.println("done.\n");

		System.out.println("Creating a new, clean database:");
		DBBuilder.main(null);
		System.out.println("done.\n");

		System.out.println("Getting logged as Admin:");
		AdminFacade admin = (AdminFacade) store.login("admin", "1234", ClientType.Admin);
		System.out.println("done.\n");

		System.out.println("Creating 15 different customers:");
		Customer[] customers = new Customer[15];
		for (int i = 0; i < 15; i++) {
			customers[i] = new Customer();
			customers[i].setCustName("Name-" + i);
			customers[i].setPassword("Pass-" + i);
			admin.createCustomer(customers[i]);
		}
		System.out.println("done.\n");

		System.out.println("Creating 15 different companies:");
		Company[] companies = new Company[15];
		for (int i = 0; i < 15; i++) {
			companies[i] = new Company();
			companies[i].setCompName("Name-" + i);
			companies[i].setPassword("Pass-" + i);
			companies[i].setEmail("Email");
			admin.createCompany(companies[i]);
		}
		System.out.println("done.\n");

		System.out.println("Getting logged as Company with name Name-1 (pass=='Pass-1'):");
		CompanyFacade company = (CompanyFacade) store.login("Name-1", "Pass-1", ClientType.Company);
		System.out.println("done.\n");

		System.out.println("Preparing template for the coupons and references for threads:");
		Coupon coupon = new Coupon();
		Thread[] threads = new Thread[15];
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, 1);
		Date theBegining = cal.getTime();
		cal.add(Calendar.DAY_OF_MONTH, 1);
		Date theEnd = cal.getTime();
		coupon.setStartDate(theBegining);
		coupon.setEndDate(theEnd);
		coupon.setType(CouponType.Food);
		coupon.setMessage("Msg");
		coupon.setImage("Img");
		System.out.println("done.\n");

		System.out.println("Creating 5 coupons through Company with name Name-1:");
		coupon.setAmount(5);
		for (int i = 0; i < 5; i++) {
			coupon.setTitle("Name-" + i);
			try {
				company.createCoupon(coupon);
			} catch (PossibleDBCorruptionException e) {
				System.out.println(e.msg);
				e.printStackTrace();
				store.shutdown();
			}
		}
		System.out.println("done.\n");

		// ASSUMPTIONS:

		// I am assuming that Apache Derby is:
		// synchronized on writing to the same table
		// synchronized on reading to the same table
		// So it leaves only the issues with writing and reading from different tables:
		// delete beans, create coupons, purchase coupons, cash-back customers.

		// PURCHASING SAME COUPON BY MULTIPLE CUSTOMERS

		System.out.println(
				"In this test we will have 5 coupon with 5 amount each and 15 customers which wants to by each coupon.");
		pressAnyKeyToContinue();
		System.out.println("Initializing threads:");
		for (int i = 0; i < 15; i++) {
			CustomerFacade customer = (CustomerFacade) store.login(customers[i].getCustName(),
					customers[i].getPassword(), ClientType.Customer);
			threads[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					CustomerFacade me = customer;
					Coupon coupon = new Coupon();
					for (int i = 1; i <= 15; i++) {
						coupon.setId(i);
						coupon.setTitle("Name-" + i);
						try {
							me.purchaseCoupon(coupon);
							System.out.println(me.onlooker.printLog());
						} catch (PossibleDBCorruptionException e) {
							System.out.println(e.msg);
							e.printStackTrace();
							store.shutdown();
						}
					}
				}
			});
		}
		System.out.println("done.\n");
		System.out.println("The coupons right know:");
		Collection<Coupon> coupons = company.getAllCoupons();
		for (Coupon data : coupons) {
			System.out.println(data);
		}
		System.out.println("Applying the test:");
		for (Thread tr : threads) {
			tr.start();
		}
		for (Thread tr : threads) {
			if (tr.isAlive())
				try {
					tr.join();
				} catch (InterruptedException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					store.shutdown();
				}
		}
		System.out.println("The results (note that amount for each coupon should be zero):");
		coupons = company.getAllCoupons();
		for (Coupon data : coupons) {
			System.out.println(data);
		}
		System.out.println("done.\n");
		pressAnyKeyToContinue();

		// CASHING BACK SAME CUSTOMERS BY MULTIPLE ADMINS

		System.out.println("In this test we will have 15 customers with a total 25 coupons on stash"
				+ " and 15 admins which wants to cashback each customers.");
		pressAnyKeyToContinue();
		System.out.println("Initializing threads:");
		for (int i = 0; i < 15; i++) {
			SuperAdminFacade someAdmin = (SuperAdminFacade) store.login("super-admin", "11111", ClientType.SuperAdmin);
			threads[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					SuperAdminFacade me = someAdmin;
					for (int i = 1; i <= 15; i++) {
						for (int j = 1; j <= 5; j++) {
							try {
								me.removeCouponFromCustomer(i, j);
								System.out.println(me.onlooker.printLog());
							} catch (PossibleDBCorruptionException e) {
								System.out.println(e.msg);
								e.printStackTrace();
								store.shutdown();
							}
						}
					}
				}
			});
		}
		System.out.println("done.\n");
		System.out.println("Applying the test:");
		for (Thread tr : threads) {
			tr.start();
		}
		for (Thread tr : threads) {
			if (tr.isAlive())
				try {
					tr.join();
				} catch (InterruptedException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					store.shutdown();
				}
		}
		System.out.println("The results (note that amount for each coupon equal to 5):");
		coupons = company.getAllCoupons();
		for (Coupon data : coupons) {
			System.out.println(data);
		}
		System.out.println("done.\n");
		pressAnyKeyToContinue();

		// DELETING THE CUSTOMER WHICH IS PURCHASING A COUPONS

		System.out.println("In this test we will have 10 customers which wants each to purchase"
				+ " 15 different coupons and 5 admins which wants to delete the customers.");
		pressAnyKeyToContinue();
		System.out.println("Putting aditional coupons in database:");
		for (int i = 6; i <= 15; i++) {
			coupon.setTitle("Name-" + i);
			try {
				company.createCoupon(coupon);
			} catch (PossibleDBCorruptionException e) {
				System.out.println(e.msg);
				e.printStackTrace();
				store.shutdown();
			}
		}
		System.out.println("done.\n");
		System.out.println("Initializing threads:");
		for (int i = 0; i < 10; i++) {
			CustomerFacade customer = (CustomerFacade) store.login(customers[i].getCustName(),
					customers[i].getPassword(), ClientType.Customer);
			threads[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					CustomerFacade me = customer;
					Coupon coupon = new Coupon();
					for (int i = 1; i <= 15; i++) {
						coupon.setId(i);
						coupon.setTitle("Name-" + i);
						try {
							me.purchaseCoupon(coupon);
							System.out.println(me.onlooker.printLog());
						} catch (PossibleDBCorruptionException e) {
							System.out.println(e.msg);
							e.printStackTrace();
							store.shutdown();
						}
					}
				}
			});
		}
		for (int i = 10; i < 15; i++) {
			SuperAdminFacade someAdmin = (SuperAdminFacade) store.login("super-admin", "11111", ClientType.SuperAdmin);
			int prefix = i - 10;
			threads[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					SuperAdminFacade me = someAdmin;
					int myPrefix = prefix;
					Customer customer = new Customer();
					for (int i = 1; i <= 2; i++) {
						customer.setId(myPrefix * 2 + i);
						customer.setCustName("Name-" + myPrefix * 2 + i);
						try {
							me.removeCustomer(customer);
							System.out.println(me.onlooker.printLog());
						} catch (PossibleDBCorruptionException e) {
							System.out.println(e.msg);
							e.printStackTrace();
							store.shutdown();
						}
					}
				}
			});
		}
		System.out.println("done.\n");
		System.out.println("Applying the test:");
		for (Thread tr : threads) {
			tr.start();
		}
		for (Thread tr : threads) {
			if (tr.isAlive())
				try {
					tr.join();
				} catch (InterruptedException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					store.shutdown();
				}
		}
		System.out.println("The results (from table Customer_Coupon):"
				+ "result_set.hasNext() on sql 'select * from Customer_Coupon' should be false:");
		try {
			Class.forName(DEFAULT_DRIVER);
			try (Connection con = DriverManager.getConnection(DEFAULT_URL, NAME, PSWD);
					Statement stmt = con.createStatement()) {
				System.out.println("Applying next on result set wich sql is as mentioned above: rs.next() = "
						+ stmt.executeQuery("select * from Customer_Coupon").next());
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			store.shutdown();
		}
		System.out.println("done.\n");
		pressAnyKeyToContinue();

		// PURCHASING AND DELETING SAME COUPON

		System.out.println("In this test we will have 10 customers which wants each to purchase"
				+ " 15 different coupons and 5 companies which wants to delete their coupons.");
		pressAnyKeyToContinue();
		System.out.println("Cleaning up the results of previous tets from database and");
		for (int i = 1; i <= 15; i++) {
			coupon.setId(i);
			coupon.setTitle("Name-" + i);
			try {
				company.removeCoupon(coupon);
			} catch (PossibleDBCorruptionException e) {
				System.out.println(e.msg);
				e.printStackTrace();
				store.shutdown();
			}
		}
		for (int i = 11; i <= 15; i++) {
			Customer customer = new Customer();
			customer.setId(i);
			try {
				admin.removeCustomer(customer);
			} catch (PossibleDBCorruptionException e) {
				System.out.println(e.msg);
				e.printStackTrace();
				store.shutdown();
			}
		}
		System.out.println("done.\n");
		System.out.println("Creating 15 new coupons with amount 15 each:");
		coupon.setId(0);
		coupon.setAmount(15);
		for (int i = 0; i < 5; i++) {
			company = (CompanyFacade) store.login(companies[i].getCompName(), companies[i].getPassword(),
					ClientType.Company);
			try {
				for (int j = i * 3 + 1; j <= (i + 1) * 3; j++) {
					coupon.setTitle("Name-" + j);
					company.createCoupon(coupon);
				}
			} catch (PossibleDBCorruptionException e) {
				System.out.println(e.msg);
				e.printStackTrace();
				store.shutdown();
			}
		}
		System.out.println("done.\n");
		System.out.println("Creating 10 new customers:");
		for (int i = 0; i < 10; i++) {
			admin.createCustomer(customers[i]);
		}
		System.out.println("done.\n");
		System.out.println("Initializing threads:");
		for (int i = 0; i < 10; i++) {
			CustomerFacade customer = (CustomerFacade) store.login(customers[i].getCustName(),
					customers[i].getPassword(), ClientType.Customer);
			threads[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					CustomerFacade me = customer;
					Coupon coupon = new Coupon();
					for (int i = 1; i <= 15; i++) {
						coupon.setId(i + 15);
						coupon.setTitle("Name-" + i);
						try {
							me.purchaseCoupon(coupon);
							System.out.println(me.onlooker.printLog());
						} catch (PossibleDBCorruptionException e) {
							System.out.println(e.msg);
							e.printStackTrace();
							store.shutdown();
						}
					}
				}
			});
		}
		for (int i = 0; i < 5; i++) {
			CompanyFacade someCompany = (CompanyFacade) store.login(companies[i].getCompName(),
					companies[i].getPassword(), ClientType.Company);
			int prefix = i;
			threads[i + 10] = new Thread(new Runnable() {
				@Override
				public void run() {
					CompanyFacade me = someCompany;
					Coupon coupon = new Coupon();
					int myPrefix = prefix;
					for (int i = 1; i <= 3; i++) {
						coupon.setId(3 * myPrefix + i + 15);
						try {
							me.removeCoupon(coupon);
							System.out.println(me.onlooker.printLog());
						} catch (PossibleDBCorruptionException e) {
							System.out.println(e.msg);
							e.printStackTrace();
							store.shutdown();
						}
					}
				}
			});
		}
		System.out.println("done.\n");
		System.out.println("Applying the test:");
		for (Thread tr : threads) {
			tr.start();
		}
		for (Thread tr : threads) {
			if (tr.isAlive())
				try {
					tr.join();
				} catch (InterruptedException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					store.shutdown();
				}
		}
		System.out.println("The results (from table Customer_Coupon):"
				+ "result_set.hasNext() on sql 'select * from Customer_Coupon' should be false:");
		try {
			Class.forName(DEFAULT_DRIVER);
			try (Connection con = DriverManager.getConnection(DEFAULT_URL, NAME, PSWD);
					Statement stmt = con.createStatement()) {
				System.out.println("Applying next on result set wich sql is as mentioned above: rs.next() = "
						+ stmt.executeQuery("select * from Customer_Coupon").next());
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			store.shutdown();
		}
		System.out.println("done.\n");
		pressAnyKeyToContinue();

		// DELETING COMPANY WHILE CUSTOMERS BUYING IT COUPON'S

		System.out.println("In this test we will have 10 customers which wants to buy"
				+ " 15 different coupons and 5 admins which wants to delete the companies owners of coupons.");
		pressAnyKeyToContinue();
		System.out.println("Creating 15 new coupons with amount 15 each:");
		coupon.setId(0);
		coupon.setAmount(15);
		for (int i = 0; i < 5; i++) {
			company = (CompanyFacade) store.login(companies[i].getCompName(), companies[i].getPassword(),
					ClientType.Company);
			try {
				for (int j = i * 3 + 1; j <= (i + 1) * 3; j++) {
					coupon.setTitle("Name-" + j);
					company.createCoupon(coupon);
				}
			} catch (PossibleDBCorruptionException e) {
				System.out.println(e.msg);
				e.printStackTrace();
				store.shutdown();
			}
		}
		System.out.println("done.\n");
		System.out.println("Initializing threads:");
		for (int i = 0; i < 10; i++) {
			CustomerFacade customer = (CustomerFacade) store.login(customers[i].getCustName(),
					customers[i].getPassword(), ClientType.Customer);
			threads[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					CustomerFacade me = customer;
					Coupon coupon = new Coupon();
					for (int i = 1; i <= 15; i++) {
						coupon.setId(i + 30);
						coupon.setTitle("Name-" + i);
						try {
							me.purchaseCoupon(coupon);
							System.out.println(me.onlooker.printLog());
						} catch (PossibleDBCorruptionException e) {
							System.out.println(e.msg);
							e.printStackTrace();
							store.shutdown();
						}
					}
				}
			});
		}
		for (int i = 10; i < 15; i++) {
			AdminFacade someAdmin = (AdminFacade) store.login("admin", "1234", ClientType.Admin);
			int prefix = i - 9;
			threads[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					AdminFacade me = someAdmin;
					int myPrefix = prefix;
					Company company = new Company();
					company.setId(myPrefix);
					try {
						me.removeCompany(company);
						System.out.println(me.onlooker.printLog());
					} catch (PossibleDBCorruptionException e) {
						System.out.println(e.msg);
						e.printStackTrace();
						store.shutdown();
					}
				}
			});
		}
		System.out.println("done.\n");
		System.out.println("Applying the test:");
		for (Thread tr : threads) {
			tr.start();
		}
		for (Thread tr : threads) {
			if (tr.isAlive())
				try {
					tr.join();
				} catch (InterruptedException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					store.shutdown();
				}
		}
		System.out.println("done.\n");
		System.out.println("The results (from table Customer_Coupon):"
				+ "result_set.hasNext() on sql 'select * from Customer_Coupon' should be false:");
		try {
			Class.forName(DEFAULT_DRIVER);
			try (Connection con = DriverManager.getConnection(DEFAULT_URL, NAME, PSWD);
					Statement stmt = con.createStatement()) {
				System.out.println("Applying next on result set wich sql is as mentioned above: rs.next() = "
						+ stmt.executeQuery("select * from Customer_Coupon").next());
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			store.shutdown();
		}
		System.out.println("done.\n");
		pressAnyKeyToContinue();

		// DELETING COMPANY WHICH IS CREATING A NEW COUPON

		System.out.println("In this test we will have 10 companies which wants each to create"
				+ " 15 different coupons \nand 5 admins which wants to delete the companies.");
		pressAnyKeyToContinue();
		System.out.println("Initializing threads:");
		for (int i = 0; i < 10; i++) {
			CompanyFacade someCompany = (CompanyFacade) store.login(companies[i + 5].getCompName(),
					companies[i + 5].getPassword(), ClientType.Company);
			int prefix = i;
			threads[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					int myPrefix = prefix;
					CompanyFacade me = someCompany;
					Coupon myCoupon = new Coupon();
					myCoupon.setStartDate(theBegining);
					myCoupon.setEndDate(theEnd);
					myCoupon.setType(CouponType.Food);
					myCoupon.setMessage("Msg");
					myCoupon.setImage("Img");
					for (int i = 0; i < 15; i++) {
						try {
							myCoupon.setTitle("Name-" + Integer.toString(15 * myPrefix + i));
							me.createCoupon(myCoupon);
							System.out.println(me.onlooker.printLog());
						} catch (PossibleDBCorruptionException e) {
							System.out.println(e.msg);
							e.printStackTrace();
							store.shutdown();
						}
					}
				}
			});
		}
		for (int i = 10; i < 15; i++) {
			AdminFacade someAdmin = (AdminFacade) store.login("admin", "1234", ClientType.Admin);
			threads[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					AdminFacade me = someAdmin;
					Company company = new Company();
					try {
						for (int i = 6; i <= 15; i++) {
							company.setId(i);
							me.removeCompany(company);
							System.out.println(me.onlooker.printLog());
						}
					} catch (PossibleDBCorruptionException e) {
						System.out.println(e.msg);
						e.printStackTrace();
						store.shutdown();
					}
				}
			});
		}
		System.out.println("done.\n");
		System.out.println("Applying the test:");
		for (Thread tr : threads) {
			tr.start();
		}
		for (Thread tr : threads) {
			if (tr.isAlive())
				try {
					tr.join();
				} catch (InterruptedException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					store.shutdown();
				}
		}
		System.out.println("done.\n");
		System.out.println("The results (from table Company_Coupon):"
				+ "result_set.hasNext() on sql 'select * from Company_Coupon' should be false:");
		try {
			Class.forName(DEFAULT_DRIVER);
			try (Connection con = DriverManager.getConnection(DEFAULT_URL, NAME, PSWD);
					Statement stmt = con.createStatement()) {
				System.out.println("Applying next on result set wich sql is as mentioned above: rs.next() = "
						+ stmt.executeQuery("select * from Company_Coupon").next());
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			store.shutdown();
		}

		// THE END

		System.out.println("Making system shutdown:");
		store.shutdown();
		System.out.println("done.\n");

		System.out.println("End!");
	}

}