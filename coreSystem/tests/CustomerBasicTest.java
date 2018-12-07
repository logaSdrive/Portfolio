package tests;

import beans.Coupon;
import beans.CouponType;
import exceptions.PossibleDBCorruptionException;
import facades.ClientType;
import facades.CustomerFacade;
import system.CouponSystem;

/**
 * Runs all methods of CustomerFacade.class with proper parameters as a "good
 * user".
 * 
 * @author AlexanderZhilokov
 *
 */
public class CustomerBasicTest {

	/**
	 * Note for using it: 1)Before you run it on its own, make sure you enabled the
	 * code on the lines 86-88 (its shutdowns the coupon system in the end of the
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
		System.out.println("Hi! This is basic tests for Customer Facade (without exception cases):");

		// PREPARATION:

		System.out.println("Preparing data for the tests (coupons):");
		Coupon couponOne = new Coupon(), couponTwo = new Coupon();
		couponOne.setId(1);
		couponOne.setTitle("Holiday Package!");
		couponTwo.setId(2);
		couponTwo.setTitle("Business Package!");
		System.out.println("done.\n");

		System.out.println("Uploading the system:");
		CouponSystem store = CouponSystem.getInstance();
		System.out.println("done.\n");

		System.out.println("Getting logged as Customer name Ben (pass=='222'):");
		CustomerFacade ben = (CustomerFacade) store.login("Ben", "222", ClientType.Customer);
		System.out.println("done.\n");

		// PURCHASING COUPONS:

		System.out.println("Buing the coupons with id==1 (Holiday Package!) and id==2 (Business Package!):");
		try {
			ben.purchaseCoupon(couponOne);
			System.out.println(ben.onlooker.printLog());
			ben.purchaseCoupon(couponTwo);
			System.out.println(ben.onlooker.printLog());
		} catch (PossibleDBCorruptionException e) {
			System.out.println(e.msg);
			e.printStackTrace();
			store.shutdown();
		}
		System.out.println("done.\n");

		// READING COUPONS:

		System.out.println("Getting all customer's coupons at once:");
		System.out.println(ben.getAllPurchasedCoupons());
		System.out.println(ben.onlooker.printLog());
		System.out.println("done.\n");

		System.out.println("Getting all customer's coupons wich type is travelling:");
		System.out.println(ben.getAllPurchasedCouponsByType(CouponType.Travelling));
		System.out.println(ben.onlooker.printLog());
		System.out.println("done.\n");

		System.out.println("Getting all customer's coupons wich price is lower than 100:");
		System.out.println(ben.getAllPurchasedCouponsByPrice(100));
		System.out.println(ben.onlooker.printLog());
		System.out.println("done.\n");

		// THE END

		// System.out.println("Making system shutdown:");
		// store.shutdown();
		// System.out.println("done.\n");

		System.out.println("End!");

	}

}