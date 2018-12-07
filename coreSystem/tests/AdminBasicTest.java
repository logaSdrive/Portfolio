package tests;

import beans.Company;
import beans.Customer;
import exceptions.PossibleDBCorruptionException;
import facades.AdminFacade;
import facades.ClientType;
import system.CouponSystem;

/**
 * Runs all methods of AdminFacade.class with proper parameters as a "good
 * user".
 * 
 * @author AlexanderZhilokov
 *
 */
public class AdminBasicTest {

	/**
	 * Note for using it: 1)Before you run it on its own, make sure you enabled the
	 * code on the lines 169-171 (its shutdowns the coupon system in the end of the
	 * test). 2)You should run it in a proper order: AdminBasicTest.main(),
	 * CompanyBasicTest.main(), CustomerBasicTest.main(),
	 * SuperAdminBasicTest.main(), InputTroubleTest.main(), LogicalIssuesTest.main()
	 * and SynchronizationIssuesTest.main(). Or you can just launch MainTest.main()
	 * which runs them all in series.
	 * 
	 * @param args
	 *            - Irrelevant.
	 */
	public static void main(String[] args) {
		System.out.println("Hi! This is basic tests for Admin Facade (without exception cases):");

		// PREPARATION:

		System.out.println("Preparing data for the tests (companies and customers):");
		Company restaurant = new Company(), travelling = new Company(), electricity = new Company();
		restaurant.setCompName("Machneyuda");
		restaurant.setEmail("machney@email.co.il");
		restaurant.setPassword("Lamahane");
		travelling.setCompName("El-Al");
		travelling.setEmail("el-al.email.co.il");
		travelling.setPassword("noToOSpls");
		electricity.setCompName("Ashalim");
		electricity.setEmail("ashalim.email.co.il");
		electricity.setPassword("eicIsBad");
		Customer avi = new Customer(), ben = new Customer(), gil = new Customer();
		avi.setCustName("Avi");
		avi.setPassword("111");
		ben.setCustName("Ben");
		ben.setPassword("222");
		gil.setCustName("Gil");
		gil.setPassword("333");
		System.out.println("done.\n");

		System.out.println("Uploading the system:");
		CouponSystem store = CouponSystem.getInstance();
		System.out.println("done.\n");

		System.out.println("Getting logged as Admin:");
		AdminFacade admin = (AdminFacade) store.login("admin", "1234", ClientType.Admin);
		System.out.println("done.\n");

		// CREATING COMPANIES AND CUSTOMERS:

		System.out.println("Creating 3 companies:");
		admin.createCompany(restaurant);
		System.out.println(admin.onlooker.printLog());
		admin.createCompany(travelling);
		System.out.println(admin.onlooker.printLog());
		admin.createCompany(electricity);
		System.out.println(admin.onlooker.printLog());
		System.out.println("done.\n");

		System.out.println("Creating 3 customers:");
		admin.createCustomer(avi);
		System.out.println(admin.onlooker.printLog());
		admin.createCustomer(ben);
		System.out.println(admin.onlooker.printLog());
		admin.createCustomer(gil);
		System.out.println(admin.onlooker.printLog());
		System.out.println("done.\n");

		// READING COMPANIES AND CUSTOMERS:

		System.out.println("Getting companies by id one by one:");
		System.out.println(admin.getCompany(1));
		System.out.println(admin.onlooker.printLog());
		System.out.println(admin.getCompany(2));
		System.out.println(admin.onlooker.printLog());
		System.out.println(admin.getCompany(3));
		System.out.println(admin.onlooker.printLog());
		System.out.println("done.\n");

		System.out.println("Know all at once");
		System.out.println(admin.getAllCompanies());
		System.out.println(admin.onlooker.printLog());
		System.out.println("done.\n");

		System.out.println("Getting customers by id one by one:");
		System.out.println(admin.getCustomer(1));
		System.out.println(admin.onlooker.printLog());
		System.out.println(admin.getCustomer(2));
		System.out.println(admin.onlooker.printLog());
		System.out.println(admin.getCustomer(3));
		System.out.println(admin.onlooker.printLog());
		System.out.println("done.\n");

		System.out.println("Know all customers at once:");
		System.out.println(admin.getAllCustomers());
		System.out.println(admin.onlooker.printLog());
		System.out.println("done.\n");

		// UPDATING COMPANIES AND CUSTOMERS::

		System.out.println("Updating the pass for third company: ");
		electricity = admin.getCompany(3);
		electricity.setPassword("ThuvaIsBad");
		admin.updateCompany(electricity);
		System.out.println(admin.onlooker.printLog());
		System.out.println(admin.getCompany(3));
		System.out.println("done.\n");

		System.out.println("Restoring the pass back:");
		electricity.setPassword("eicIsBad");
		admin.updateCompany(electricity);
		System.out.println(admin.onlooker.printLog());
		System.out.println(admin.getCompany(3));
		System.out.println("done.\n");

		System.out.println("Updating the pass for the third customer: ");
		gil = admin.getCustomer(3);
		gil.setPassword("neverKnow");
		admin.updateCustomer(gil);
		System.out.println(admin.onlooker.printLog());
		System.out.println(admin.getCustomer(3));
		System.out.println("done.\n");

		System.out.println("Changing the pass back for the third customer: ");
		gil.setPassword("333");
		admin.updateCustomer(gil);
		System.out.println(admin.onlooker.printLog());
		System.out.println(admin.getCustomer(3));
		System.out.println("done.\n");

		// DELETING COMPANIES AND CUSTOMERS::

		System.out.println("Deleting the company Ashalim (id==3) and the customer gil (id==3):");
		try {
			electricity.setId(3);
			admin.removeCompany(electricity);
			System.out.println(admin.onlooker.printLog());
			gil.setId(3);
			admin.removeCustomer(gil);
			System.out.println(admin.onlooker.printLog());
		} catch (PossibleDBCorruptionException e) {
			System.out.println(e.msg);
			e.printStackTrace();
			store.shutdown();
		}
		System.out.println(admin.getAllCompanies());
		System.out.println(admin.onlooker.printLog());
		System.out.println(admin.getAllCustomers());
		System.out.println(admin.onlooker.printLog());
		System.out.println("done.\n");

		// THE END

		// System.out.println("Making system shutdown:");
		// store.shutdown();
		// System.out.println("done.\n");

		System.out.println("End!");
	}

}