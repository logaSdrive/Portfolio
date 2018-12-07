package tests;

import beans.Coupon;
import beans.CouponType;
import exceptions.PossibleDBCorruptionException;
import facades.ClientType;
import facades.SuperAdminFacade;
import system.CouponSystem;

/**
 * Runs all methods of SuperAdminFacade.class with proper parameters as a "good
 * user".
 * 
 * @author AlexanderZhilokov
 *
 */
public class SuperAdminBasicTest {

	/**
	 * Note for using it: 1)Before you run it on its own, make sure you enabled the
	 * code on the lines 116-118 (its shutdowns the coupon system in the end of the
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
		System.out.println("Hi! This is basic tests for Super-Admin Facade (without exception cases):");

		// PREPARATION:

		System.out.println("Preparing data for the tests (coupons):");
		Coupon couponOne, couponTwo;
		System.out.println("done.\n");

		System.out.println("Uploading the system:");
		CouponSystem store = CouponSystem.getInstance();
		System.out.println("done.\n");

		System.out.println("Getting logged as Super-Admin:");
		SuperAdminFacade superAdmin = (SuperAdminFacade) store.login("super-admin", "11111", ClientType.SuperAdmin);
		System.out.println("done.\n");

		// READING COUPONS:

		System.out.println("Getting all coupons from company El-Al (id==2):");
		System.out.println(superAdmin.getCompanyCoupons(2));
		System.out.println(superAdmin.onlooker.printLog());
		System.out.println("done.\n");

		System.out.println("Getting all coupons from customer Ben (id==2):");
		System.out.println(superAdmin.getCustomerCoupons(2));
		System.out.println(superAdmin.onlooker.printLog());
		System.out.println("done.\n");

		System.out.println("Getting all coupons from the database:");
		System.out.println(superAdmin.getAllCoupons());
		System.out.println(superAdmin.onlooker.printLog());
		System.out.println("done.\n");

		System.out.println("Getting all coupons from which type is Travelling:");
		System.out.println(superAdmin.getCouponsByType(CouponType.Travelling));
		System.out.println(superAdmin.onlooker.printLog());
		System.out.println("done.\n");

		System.out.println("Getting all coupon by id:");
		System.out.println("coupon with id==1 (Holiday Package!):");
		couponOne = superAdmin.getCoupon(1);
		System.out.println(superAdmin.onlooker.printLog());
		System.out.println(couponOne);
		System.out.println("done.\n");
		System.out.println("coupon with id==2 (Bussines Package!):");
		couponTwo = superAdmin.getCoupon(2);
		System.out.println(superAdmin.onlooker.printLog());
		System.out.println(couponTwo);
		System.out.println("done.\n");

		// UPDATING COUPONS:

		System.out.println("Updating the coupon with id==2 (Bussines Package!):");
		System.out.println(
				"(Price==99 and Message~correspond with El-Al's troubles not being 'only' one company in aeroport)");
		couponTwo.setPrice(99);
		couponTwo.setMessage(
				"Landing in London at 07:30. Flight time: two hours. Luggage will be charged additionaly per kilo.");
		superAdmin.updateCoupon(couponTwo);
		System.out.println(superAdmin.onlooker.printLog());
		System.out.println(superAdmin.getCoupon(2));
		System.out.println("done.\n");

		// DELETING COUPONS:

		try {
			System.out.println("Removing the coupon with id==1 (Holiday Package!) from customer Ben (id==2):");
			superAdmin.removeCouponFromCustomer(2, 1);
			System.out.println(superAdmin.onlooker.printLog());
			System.out.println(superAdmin.getCustomerCoupons(2));
			System.out.println("done.\n");

			System.out.println("Deleting from the database coupon with id==2 (Bussines Package!):");
			superAdmin.removeCoupon(couponTwo);
			System.out.println(superAdmin.onlooker.printLog());
			System.out.println(superAdmin.getAllCoupons());
			System.out.println("done.\n");
		} catch (PossibleDBCorruptionException e) {
			System.out.println(e.msg);
			e.printStackTrace();
		}

		// THE END

		// System.out.println("Making system shutdown:");
		// store.shutdown();
		// System.out.println("done.\n");

		System.out.println("End!");

	}

}