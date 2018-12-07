package tests;

import java.util.Calendar;
import java.util.Date;

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
 * Runs all facades with illegal values as parameters (which can be resolved on
 * the front end)
 * 
 * @author AlexanderZhilokov
 *
 */
public class InputTroublesTest {

	/**
	 * Note for using it: 1)Before you run it on its own, make sure you enabled the
	 * code on the lines 413-415 (its shutdowns the coupon system in the end of the
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
		System.out.println("Hi! This is a tests for the cases of the incorrect input:\n"
				+ "null parametres and objects, strings which contains only blank's symbols, date's wich is already passsed or in contradiction with basic logic:");

		// PREPARATION:

		System.out.println("Preparing data for the tests (companies, coupons and customers):");
		Company company = new Company();
		Customer customer = new Customer();
		Coupon coupon = new Coupon();
		String ten = "0123456789", hundred = "", thousand = "", validName = "Name", validPass = "Pass",
				validMsg = "Msg", validImg = "Image", validMail = "Mail";
		CouponType someType = CouponType.Food;
		for (int i = 0; i < 10; i++)
			hundred += ten;
		for (int i = 0; i < 10; i++)
			thousand += hundred;
		String largeName = ten + ten + ten + ten + "!", largeMessage = thousand + "!",
				largeImage = hundred + hundred + ten + ten + ten + ten + ten + "123456!", largePass = ten + "!",
				largeMail = ten + ten + ten + "!";
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -1);
		Date yesterday = cal.getTime();
		cal.add(Calendar.DAY_OF_MONTH, 2);
		Date before = cal.getTime();
		cal.add(Calendar.DAY_OF_MONTH, 1);
		Date after = cal.getTime();
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

		System.out.println("Getting logged as Customer with name Ben (pass=='222'):");
		CustomerFacade ben = (CustomerFacade) store.login("Ben", "222", ClientType.Customer);
		System.out.println("done.\n");

		System.out.println("Getting logged as Super-Admin:");
		SuperAdminFacade superAdmin = (SuperAdminFacade) store.login("super-admin", "11111", ClientType.SuperAdmin);
		System.out.println("done.\n");

		// CAUSING NULL-ARGUMENT EXCEPTION FOR BEANS AND PARAM'S:

		System.out.println(
				"Trying to create, pucrhase, read, delete and update a company, coupon and a customer with objects wich are not iniatilized"
						+ " through Admin, Super-Admin, Customer and Company facades:");
		System.out.println("--------Admin Create Company----------");
		admin.createCompany(null);
		System.out.println(admin.onlooker.printLog());
		System.out.println("--------Admin Create Customer----------");
		admin.createCustomer(null);
		System.out.println(admin.onlooker.printLog());
		System.out.println("--------Admin Update Company----------");
		admin.updateCompany(null);
		System.out.println(admin.onlooker.printLog());
		System.out.println("--------Admin Update Customer----------");
		admin.updateCustomer(null);
		System.out.println(admin.onlooker.printLog());
		System.out.println("--------Company Get Coupons By Type----------");
		elAl.getCouponsByType(null);
		System.out.println(elAl.onlooker.printLog());
		System.out.println("--------Company Get Coupons By Date----------");
		elAl.getCouponsByDate(null);
		System.out.println(elAl.onlooker.printLog());
		System.out.println("--------Company Update Coupon----------");
		elAl.updateCoupon(null);
		System.out.println(elAl.onlooker.printLog());
		System.out.println("--------Customer Get Coupons By Type----------");
		ben.getAllPurchasedCouponsByType(null);
		System.out.println(ben.onlooker.printLog());
		System.out.println("--------Super Admin Get Coupons By Type----------");
		superAdmin.getCouponsByType(null);
		System.out.println(superAdmin.onlooker.printLog());
		System.out.println("--------Super Admin Update Coupon----------");
		superAdmin.updateCoupon(null);
		System.out.println(superAdmin.onlooker.printLog());
		try {
			System.out.println("--------Admin Remove Company----------");
			admin.removeCompany(null);
			System.out.println(admin.onlooker.printLog());
			System.out.println("--------Admin Remove Customer----------");
			admin.removeCustomer(null);
			System.out.println(admin.onlooker.printLog());
			System.out.println("--------Company Create Coupon----------");
			elAl.createCoupon(null);
			System.out.println(elAl.onlooker.printLog());
			System.out.println("--------Company Remove Coupon----------");
			elAl.removeCoupon(null);
			System.out.println(elAl.onlooker.printLog());
			System.out.println("--------Customer Purchase Coupon----------");
			ben.purchaseCoupon(null);
			System.out.println(ben.onlooker.printLog());
			System.out.println("--------Super Admin Remove Coupon----------");
			superAdmin.removeCoupon(null);
			System.out.println(superAdmin.onlooker.printLog());
		} catch (PossibleDBCorruptionException e) {
			System.out.println(e.msg);
			e.printStackTrace();
			store.shutdown();
		}
		System.out.println("done.\n");

		System.out.println(
				"Trying to create and update a company, coupon and a customer with param's which are not iniatilized"
						+ " through Admin and Company facades:");
		company.setId(2);
		customer.setId(2);
		coupon.setId(1);
		System.out.println("--------Admin Create Company----------");
		admin.createCompany(company);
		System.out.println(admin.onlooker.printLog());
		System.out.println("--------Admin Create Customer----------");
		admin.createCustomer(customer);
		System.out.println(admin.onlooker.printLog());
		System.out.println("--------Admin Update Company----------");
		admin.updateCompany(company);
		System.out.println(admin.onlooker.printLog());
		System.out.println("--------Admin Update Customer----------");
		admin.updateCustomer(customer);
		System.out.println(admin.onlooker.printLog());
		System.out.println("--------Company Update Coupon----------");
		elAl.updateCoupon(coupon);
		System.out.println(elAl.onlooker.printLog());
		System.out.println("--------Super Admin Update Coupon----------");
		superAdmin.updateCoupon(coupon);
		System.out.println(superAdmin.onlooker.printLog());
		try {
			System.out.println("--------Company Create Coupon----------");
			elAl.createCoupon(coupon);
			System.out.println(elAl.onlooker.printLog());
		} catch (PossibleDBCorruptionException e) {
			System.out.println(e.msg);
			e.printStackTrace();
			store.shutdown();
		}
		System.out.println("done.\n");

		// BLANK VALUES FOR STRING PARAM'S:

		System.out.println(
				"Trying to create and update a company, coupon and a customer with objects wich are contains blank strings as name, titles, messages, etc"
						+ " through Admin, Super-Admin and Company facades:");
		company.setCompName("");
		company.setPassword(" ");
		company.setEmail("  ");
		customer.setCustName("    ");
		customer.setPassword("      ");
		coupon.setTitle("");
		coupon.setStartDate(before);
		coupon.setEndDate(after);
		coupon.setType(someType);
		coupon.setMessage(" ");
		coupon.setImage("   ");
		System.out.println("--------Admin Create Company----------");
		admin.createCompany(company);
		System.out.println(admin.onlooker.printLog());
		System.out.println("--------Admin Create Customer----------");
		admin.createCustomer(customer);
		System.out.println(admin.onlooker.printLog());
		System.out.println("--------Admin Update Company----------");
		admin.updateCompany(company);
		System.out.println(admin.onlooker.printLog());
		System.out.println("--------Admin Update Customer----------");
		admin.updateCustomer(customer);
		System.out.println(admin.onlooker.printLog());
		System.out.println("--------Company Update Coupon----------");
		elAl.updateCoupon(coupon);
		System.out.println(elAl.onlooker.printLog());
		System.out.println("--------Super Admin Update Coupon----------");
		superAdmin.updateCoupon(coupon);
		System.out.println(superAdmin.onlooker.printLog());
		try {
			System.out.println("--------Company Create Coupon----------");
			elAl.createCoupon(coupon);
			System.out.println(elAl.onlooker.printLog());
		} catch (PossibleDBCorruptionException e) {
			System.out.println(e.msg);
			e.printStackTrace();
			store.shutdown();
		}
		System.out.println("done.\n");

		// CAUSING EXCEPTION ABOUT OVERSIZED VALUES FOR STRING PARAMETRES:

		System.out.println(
				"Trying to create and update a company, coupon and a customer with objects wich are contains oversized strings as name, titles, messages, etc"
						+ " through Admin, Super-Admin and Company facades:");
		company.setCompName(largeName);
		company.setPassword(largePass);
		company.setEmail(largeMail);
		customer.setCustName(largeName);
		customer.setPassword(largePass);
		coupon.setTitle(largeName);
		coupon.setMessage(largeMessage);
		coupon.setImage(largeImage);
		System.out.println("--------Admin Create Company----------");
		admin.createCompany(company);
		System.out.println(admin.onlooker.printLog());
		System.out.println("--------Admin Create Customer----------");
		admin.createCustomer(customer);
		System.out.println(admin.onlooker.printLog());
		System.out.println("--------Admin Update Company----------");
		admin.updateCompany(company);
		System.out.println(admin.onlooker.printLog());
		System.out.println("--------Admin Update Customer----------");
		admin.updateCustomer(customer);
		System.out.println(admin.onlooker.printLog());
		System.out.println("--------Company Update Coupon----------");
		elAl.updateCoupon(coupon);
		System.out.println(elAl.onlooker.printLog());
		System.out.println("--------Super Admin Update Coupon----------");
		superAdmin.updateCoupon(coupon);
		System.out.println(superAdmin.onlooker.printLog());
		try {
			System.out.println("--------Company Create Coupon----------");
			elAl.createCoupon(coupon);
			System.out.println(elAl.onlooker.printLog());
		} catch (PossibleDBCorruptionException e) {
			System.out.println(e.msg);
			e.printStackTrace();
			store.shutdown();
		}
		System.out.println("done.\n");

		// DATES, PRICES AND AMOUNTS BASIC LOGIC:

		System.out.println(
				"Trying to create, read, purchase and update a company, coupon and a customer with objects wich are contains negative numeric values or ilogical's dates"
						+ "\nthrough Admin, Super-Admin, Customer and Company facades:");
		System.out.println("Trying to create and update a coupon with negative price and amount and passed end date:");
		coupon.setTitle(validName);
		coupon.setMessage(validMsg);
		coupon.setImage(validImg);
		coupon.setEndDate(yesterday);
		coupon.setAmount(-1);
		coupon.setPrice(-1);
		System.out.println("--------Company Update Coupon----------");
		elAl.updateCoupon(coupon);
		System.out.println(elAl.onlooker.printLog());
		System.out.println("--------Super Admin Update Coupon----------");
		superAdmin.updateCoupon(coupon);
		System.out.println(superAdmin.onlooker.printLog());
		try {
			System.out.println("--------Company Create Coupon----------");
			elAl.createCoupon(coupon);
			System.out.println(elAl.onlooker.printLog());
		} catch (PossibleDBCorruptionException e) {
			System.out.println(e.msg);
			e.printStackTrace();
			store.shutdown();
		}
		System.out.println("done.\n");

		System.out.println("Trying to create and update a coupon with end date before start date:");
		coupon.setAmount(0);
		coupon.setPrice(0);
		coupon.setStartDate(after);
		coupon.setEndDate(before);
		System.out.println("--------Company Update Coupon----------");
		elAl.updateCoupon(coupon);
		System.out.println(elAl.onlooker.printLog());
		System.out.println("--------Super Admin Update Coupon----------");
		superAdmin.updateCoupon(coupon);
		System.out.println(superAdmin.onlooker.printLog());
		try {
			System.out.println("--------Company Create Coupon----------");
			elAl.createCoupon(coupon);
			System.out.println(elAl.onlooker.printLog());
		} catch (PossibleDBCorruptionException e) {
			System.out.println(e.msg);
			e.printStackTrace();
			store.shutdown();
		}
		System.out.println("done.\n");

		System.out.println("Trying to update, read and remove companies and customers with non positive id:");
		company.setId(0);
		company.setCompName(validName);
		company.setEmail(validMail);
		company.setPassword(validPass);
		customer.setId(0);
		customer.setCustName(validName);
		customer.setPassword(validPass);
		System.out.println("--------Admin Update Company----------");
		admin.updateCompany(company);
		System.out.println(admin.onlooker.printLog());
		System.out.println("--------Admin Update Customer----------");
		admin.updateCustomer(customer);
		System.out.println(admin.onlooker.printLog());
		System.out.println("--------Admin Get Company----------");
		admin.getCompany(0);
		System.out.println(admin.onlooker.printLog());
		System.out.println("--------Admin Get Customer----------");
		admin.getCustomer(0);
		System.out.println(admin.onlooker.printLog());
		System.out.println("--------Super Admin Get Company Coupons----------");
		superAdmin.getCompanyCoupons(0);
		System.out.println(superAdmin.onlooker.printLog());
		System.out.println("--------Super Admin Get Customer Coupons----------");
		superAdmin.getCustomerCoupons(0);
		System.out.println(superAdmin.onlooker.printLog());
		try {
			System.out.println("--------Admin Remove Company----------");
			admin.removeCompany(company);
			System.out.println(admin.onlooker.printLog());
			System.out.println("--------Admin Remove Customer----------");
			admin.removeCustomer(customer);
			System.out.println(admin.onlooker.printLog());
			System.out.println("--------Super Admin Remove Coupon From Customer----------");
			superAdmin.removeCouponFromCustomer(0, 1);
			System.out.println(superAdmin.onlooker.printLog());
		} catch (PossibleDBCorruptionException e) {
			System.out.println(e.msg);
			e.printStackTrace();
			store.shutdown();
		}
		System.out.println("done.\n");

		System.out.println("Trying to update, read, purchase and remove from customer's coupon with non positive id"
				+ "\nand trying to read coupons using expired end date and negative price as filter:");
		coupon.setId(0);
		coupon.setStartDate(before);
		coupon.setEndDate(after);
		System.out.println("--------Company Get Coupon----------");
		elAl.getCoupon(0);
		System.out.println(elAl.onlooker.printLog());
		System.out.println("--------Company Get Coupon By Date----------");
		elAl.getCouponsByDate(yesterday);
		System.out.println(elAl.onlooker.printLog());
		System.out.println("--------Company Get Coupon By Price----------");
		elAl.getCouponsByPrice(-1);
		System.out.println(elAl.onlooker.printLog());
		System.out.println("--------Company Update Coupon----------");
		elAl.updateCoupon(coupon);
		System.out.println(elAl.onlooker.printLog());
		System.out.println("--------Customer Get Coupon By Price----------");
		ben.getAllPurchasedCouponsByPrice(-1);
		System.out.println(ben.onlooker.printLog());
		System.out.println("--------Super Admin Get Coupon----------");
		superAdmin.getCoupon(0);
		System.out.println(superAdmin.onlooker.printLog());
		System.out.println("--------Super Admin Update Coupon----------");
		superAdmin.updateCoupon(coupon);
		System.out.println(superAdmin.onlooker.printLog());
		try {
			System.out.println("--------Company Remove Coupon----------");
			elAl.removeCoupon(coupon);
			System.out.println(elAl.onlooker.printLog());
			System.out.println("--------Customer Purchase Coupon----------");
			ben.purchaseCoupon(coupon);
			System.out.println(ben.onlooker.printLog());
			System.out.println("--------Super Admin Remove Coupon----------");
			superAdmin.removeCoupon(coupon);
			System.out.println(superAdmin.onlooker.printLog());
			System.out.println("--------Super Admin Remove Coupon From Customer----------");
			superAdmin.removeCouponFromCustomer(2, 0);
			System.out.println(superAdmin.onlooker.printLog());
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