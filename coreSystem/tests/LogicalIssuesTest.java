package tests;

import java.util.Calendar;

import beans.Company;
import beans.Coupon;
import beans.CouponType;
import beans.Customer;
import exceptions.PossibleDBCorruptionException;
import facades.AdminFacade;
import facades.ClientType;
import facades.CompanyFacade;
import facades.CustomerFacade;
import facades.SuperAdminFacade;
import system.CouponSystem;

/**
 * Runs all facades with illegal values as parameters (which can not be resolved
 * on the front end)
 * 
 * @author AlexanderZhilokov
 *
 */
public class LogicalIssuesTest {

	/**
	 * Note for using it: 1)Before you run it on its own, make sure you enabled the
	 * code on the lines 273-275 (its shutdowns the coupon system in the end of the
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
		System.out.println("This is a test for logical issues:\n"
				+ "wrong id's, names which is already used, changing the things which you shouldnt permitted to change according to bussines logic and etc.");

		// PREPARATION:

		System.out.println("Preparing data for the tests (companies, coupons and customers):");
		Company company = new Company();
		company.setCompName("Some Company");
		company.setEmail("Some Mail");
		company.setPassword("Some Pass");
		Customer customer = new Customer();
		customer.setCustName("Some Customer");
		customer.setPassword("Some Pass");
		Coupon coupon = new Coupon();
		coupon.setTitle("Some Coupon");
		coupon.setMessage("Some Message");
		coupon.setImage("Some Image");
		coupon.setType(CouponType.Food);
		coupon.setPrice(99);
		coupon.setAmount(10);
		Calendar cal = Calendar.getInstance();
		cal.set(2017, 11, 1);
		coupon.setStartDate(cal.getTime());
		cal.set(2018, 1, 1);
		coupon.setEndDate(cal.getTime());
		System.out.println("done.\n");

		System.out.println("Uploading the system:");
		CouponSystem store = CouponSystem.getInstance();
		System.out.println("done.\n");

		System.out.println("Getting logged as Admin:");
		AdminFacade admin = (AdminFacade) store.login("admin", "1234", ClientType.Admin);
		System.out.println("done.\n");

		System.out.println("Getting logged as Company with name El-Al (pass=='noToOSpls'):");
		CompanyFacade elAl = (CompanyFacade) store.login("El-Al", "noToOSpls", ClientType.Company);
		System.out.println("done.\n");

		System.out.println("Getting logged as Company with name Machneyuda (pass=='Lamahane'):");
		CompanyFacade machneyada = (CompanyFacade) store.login("Machneyuda", "Lamahane", ClientType.Company);
		System.out.println("done.\n");

		System.out.println("Getting logged as Customer with name Avi (pass=='111'):");
		CustomerFacade avi = (CustomerFacade) store.login("Avi", "111", ClientType.Customer);
		System.out.println("done.\n");

		System.out.println("Getting logged as Customer with name Ben (pass=='222'):");
		CustomerFacade ben = (CustomerFacade) store.login("Ben", "222", ClientType.Customer);
		System.out.println("done.\n");

		System.out.println("Getting logged as Super-Admin:");
		SuperAdminFacade superAdmin = (SuperAdminFacade) store.login("super-admin", "11111", ClientType.SuperAdmin);
		System.out.println("done.\n");

		// WRONG ID'S:

		System.out.println(
				"Trying to pucrhase, read, delete and update a company, coupon and a customer wich ID's is wrongs"
						+ " through Admin, Super-Admin, Customer and Company facades:");
		company.setId(3);
		customer.setId(3);
		coupon.setId(3);
		System.out.println("--------Admin Update Company----------");
		admin.updateCompany(company);
		System.out.println(admin.onlooker.printLog());
		System.out.println("--------Admin Update Customer----------");
		admin.updateCustomer(customer);
		System.out.println(admin.onlooker.printLog());
		System.out.println("--------Admin Get Company----------");
		admin.getCompany(3);
		System.out.println(admin.onlooker.printLog());
		System.out.println("--------Admin Get Customer----------");
		admin.getCustomer(3);
		System.out.println(admin.onlooker.printLog());
		System.out.println("--------Company Update Coupon----------");
		elAl.updateCoupon(coupon);
		System.out.println(elAl.onlooker.printLog());
		System.out.println("--------Company Get Coupon----------");
		elAl.getCoupon(3);
		System.out.println(elAl.onlooker.printLog());
		System.out.println("--------Super Admin Update Coupon----------");
		superAdmin.updateCoupon(coupon);
		System.out.println(superAdmin.onlooker.printLog());
		System.out.println("--------Super Admin Get Company Coupons----------");
		superAdmin.getCompanyCoupons(3);
		System.out.println(superAdmin.onlooker.printLog());
		System.out.println("--------Super Admin Get Customer Coupons----------");
		superAdmin.getCustomerCoupons(3);
		System.out.println(superAdmin.onlooker.printLog());
		System.out.println("--------Super Admin Get Coupon----------");
		superAdmin.getCoupon(3);
		System.out.println(superAdmin.onlooker.printLog());
		try {
			System.out.println("--------Admin Remove Company----------");
			admin.removeCompany(company);
			System.out.println(admin.onlooker.printLog());
			System.out.println("--------Admin Remove Customer----------");
			admin.removeCustomer(customer);
			System.out.println(admin.onlooker.printLog());
			System.out.println("--------Company Remove Coupon----------");
			elAl.removeCoupon(coupon);
			System.out.println(elAl.onlooker.printLog());
			System.out.println("--------Customer Purchase Coupon----------");
			ben.purchaseCoupon(coupon);
			System.out.println(ben.onlooker.printLog());
			System.out.println("--------Super Admin Remove Coupon----------");
			superAdmin.removeCoupon(coupon);
			System.out.println(superAdmin.onlooker.printLog());
			System.out.println("--------Super Admin Remove None-Existing Coupon From Customer----------");
			superAdmin.removeCouponFromCustomer(2, 3);
			System.out.println(superAdmin.onlooker.printLog());
			System.out.println("--------Super Admin Remove Coupon From None-Existing Customer----------");
			superAdmin.removeCouponFromCustomer(3, 1);
			System.out.println(superAdmin.onlooker.printLog());
		} catch (PossibleDBCorruptionException e) {
			System.out.println(e.msg);
			e.printStackTrace();
			store.shutdown();
		}
		System.out.println("done.\n");

		// WRONG NAMES AND TITLES:

		System.out.println("Trying to create and update a company, coupon and a customer with wrong or already"
				+ " used names and titles" + "\nthrough Admin, Super-Admin and Company facades:");
		company.setId(1);
		customer.setId(1);
		coupon.setId(1);
		try {
			System.out.println("--------Company Create Coupon----------");
			coupon.setTitle("Holiday Package!");
			elAl.createCoupon(coupon);
			System.out.println(elAl.onlooker.printLog());
		} catch (PossibleDBCorruptionException e) {
			System.out.println(e.msg);
			e.printStackTrace();
			store.shutdown();
		}
		System.out.println("--------Company Update Coupon----------");
		coupon.setTitle("Not Holiday Package!");
		elAl.updateCoupon(coupon);
		System.out.println(elAl.onlooker.printLog());
		System.out.println("--------Admin Create Company----------");
		company.setCompName("Machneyuda");
		admin.createCompany(company);
		System.out.println(admin.onlooker.printLog());
		System.out.println("--------Admin Update Company----------");
		company.setCompName("Not Machneyuda");
		admin.updateCompany(company);
		System.out.println(admin.onlooker.printLog());
		System.out.println("--------Admin Create Customer----------");
		customer.setCustName("Avi");
		admin.createCustomer(customer);
		System.out.println(admin.onlooker.printLog());
		System.out.println("--------Admin Update Customer----------");
		customer.setCustName("Not Avi");
		admin.updateCustomer(customer);
		System.out.println(admin.onlooker.printLog());
		System.out.println("--------Super-Admin Update Coupon----------");
		superAdmin.updateCoupon(coupon);
		System.out.println(superAdmin.onlooker.printLog());
		System.out.println("done.\n");

		// ACCESS DENIED

		System.out.println(
				"Trying to read, update, purchase and delete a coupons whithout access due bussines logic reasons"
						+ "\nthrough Customer, Super-Admin and Company facades:");
		company.setCompName("Machneyuda");
		customer.setCustName("Avi");
		coupon.setTitle("Holiday Package!");
		try {
			avi.purchaseCoupon(coupon);
		} catch (PossibleDBCorruptionException e) {
			System.out.println(e.msg);
			e.printStackTrace();
			store.shutdown();
		}
		System.out.println("Getting all Avi's coupons: ");
		System.out.println(avi.getAllPurchasedCoupons());
		System.out.println("Trying to purchase coupon through customer (Avi) facade which already has a same coupon:");
		try {
			avi.purchaseCoupon(coupon);
			System.out.println(avi.onlooker.printLog());
		} catch (PossibleDBCorruptionException e) {
			System.out.println(e.msg);
			e.printStackTrace();
			store.shutdown();
		}
		System.out.println("done.\n");
		System.out.println(
				"Trying to update coupon start date, amount, type, message, image through company (elAl) facade:");
		coupon.setTitle("Holiday Package!");
		coupon.setAmount(10);
		System.out.println("The coupon parametres which we are trying to apply:");
		System.out.println(coupon);
		elAl.updateCoupon(coupon);
		System.out.println(elAl.onlooker.printLog());
		System.out.println("The coupon parametres as it know (must be all original):");
		System.out.println(elAl.getCoupon(1));
		System.out.println(
				"Know trying trying to purchase the coupon with amount zero through another customer (Ben) facade:");
		try {
			ben.purchaseCoupon(coupon);
			System.out.println(ben.onlooker.printLog());
		} catch (PossibleDBCorruptionException e) {
			System.out.println(e.msg);
			e.printStackTrace();
			store.shutdown();
		}
		System.out.println("done.\n");
		System.out.println(
				"Trying to read, update and delete the same coupon through a company facade which are not the creater of the coupon:");
		System.out.println("--------Company Get Coupon----------");
		machneyada.getCoupon(1);
		System.out.println(machneyada.onlooker.printLog());
		coupon.setPrice(150);
		System.out.println("--------Company Update Coupon----------");
		machneyada.updateCoupon(coupon);
		System.out.println(machneyada.onlooker.printLog());
		try {
			System.out.println("--------Company Remove Coupon----------");
			machneyada.removeCoupon(coupon);
			machneyada.updateCoupon(coupon);
		} catch (PossibleDBCorruptionException e) {
			System.out.println(e.msg);
			e.printStackTrace();
			store.shutdown();
		}
		System.out.println("done.\n");

		// THE END

		// System.out.println("Making system shutdown:");
		// store.shutdown();
		// System.out.println("done.\n");

		System.out.println("End!");
	}
}