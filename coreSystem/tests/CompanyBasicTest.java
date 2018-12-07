package tests;

import java.util.Calendar;

import beans.Coupon;
import beans.CouponType;
import exceptions.PossibleDBCorruptionException;
import facades.ClientType;
import facades.CompanyFacade;
import system.CouponSystem;

/**
 * Runs all methods of CompanyFacade.class with proper parameters as a "good
 * user".
 * 
 * @author AlexanderZhilokov
 *
 */
public class CompanyBasicTest {

	/**
	 * Note for using it: 1)Before you run it on its own, make sure you enabled the
	 * code on the lines 168-170 (its shutdowns the coupon system in the end of the
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
		System.out.println("Hi! This is basic tests for Company Facade (without exception cases):");

		// PREPARATION:

		System.out.println("Preparing data for the tests (coupons):");
		Coupon ticketOne = new Coupon(), ticketTwo = new Coupon(), ticketThree = new Coupon();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		ticketOne.setTitle("Holiday Package!");
		cal.set(2018, 0, 1);
		ticketOne.setStartDate(cal.getTime());
		cal.set(2018, 1, 1);
		ticketOne.setEndDate(cal.getTime());
		ticketOne.setAmount(1);
		ticketOne.setType(CouponType.Travelling);
		ticketOne.setPrice(99.99);
		ticketOne.setMessage("The best beaches of Somalia are at your service! Landing on the fly!"
				+ "(We have a wonderful instructor and our parachutes have a quality certificate.)");
		ticketOne.setImage("https://turism.boltai.com/wp-content/uploads/sites/34/2017/06/1108.jpg");

		ticketTwo.setTitle("Business Package!");
		cal.set(2018, 0, 1);
		ticketTwo.setStartDate(cal.getTime());
		cal.set(2019, 1, 1);
		ticketTwo.setEndDate(cal.getTime());
		ticketTwo.setAmount(10);
		ticketTwo.setType(CouponType.Travelling);
		ticketTwo.setPrice(100_500.0);
		ticketTwo.setMessage(
				"Landing in London at 3 am. Flight time 24 hours (additional passengers will taken on the way). "
						+ "The cost of the package does not include food, place for luggage, toilet, sitting places.");
		ticketTwo.setImage("http://www.beautynet.ru/images/stories/menfashion/mansuit_thumbs.jpg");

		ticketThree.setTitle("Special deal!");
		cal.set(2018, 0, 1);
		ticketThree.setStartDate(cal.getTime());
		cal.set(2019, 1, 1);
		ticketThree.setEndDate(cal.getTime());
		ticketThree.setAmount(10);
		ticketThree.setType(CouponType.Camping);
		ticketThree.setPrice(99.99);
		ticketThree.setMessage("A great opportunity to spend time in the waiting room of the best airport in Israel!");
		ticketThree.setImage("http://www.kant.ru/upload/iblock/221/22121292950f3403a9db1bb5552dee57.jpg");
		System.out.println("done.\n");

		System.out.println("Uploading the system:");
		CouponSystem store = CouponSystem.getInstance();
		System.out.println("done.\n");

		System.out.println("Getting logged as Company with name El-Al (pass=='noToOSpls'):");
		CompanyFacade ElAl = (CompanyFacade) store.login("El-Al", "noToOSpls", ClientType.Company);
		System.out.println("done.\n");

		// CREATING COUPONS:

		System.out.println("Creating 3 coupons:");
		try {
			ElAl.createCoupon(ticketOne);
			System.out.println(ElAl.onlooker.printLog());
			ElAl.createCoupon(ticketTwo);
			System.out.println(ElAl.onlooker.printLog());
			ElAl.createCoupon(ticketThree);
			System.out.println(ElAl.onlooker.printLog());
		} catch (PossibleDBCorruptionException e) {
			System.out.println(e.msg);
			e.printStackTrace();
			store.shutdown();
		}
		System.out.println("done.\n");

		// READING COUPONS:

		System.out.println("Getting coupons by id one by one:");
		System.out.println(ElAl.getCoupon(1));
		System.out.println(ElAl.onlooker.printLog());
		System.out.println(ElAl.getCoupon(2));
		System.out.println(ElAl.onlooker.printLog());
		System.out.println(ElAl.getCoupon(3));
		System.out.println(ElAl.onlooker.printLog());
		System.out.println("done.\n");

		System.out.println("Getting coupons all at once: ");
		System.out.println(ElAl.getAllCoupons());
		System.out.println(ElAl.onlooker.printLog());
		System.out.println("done.\n");

		System.out.println("Getting coupons by type (only travelling):");
		System.out.println(ElAl.getCouponsByType(CouponType.Travelling));
		System.out.println(ElAl.onlooker.printLog());
		System.out.println("done.\n");

		System.out.println("Getting coupons by price (lower than 100):");
		System.out.println(ElAl.getCouponsByPrice(100));
		System.out.println(ElAl.onlooker.printLog());
		System.out.println("done.\n");

		System.out.println("Getting coupons by end date (before 2019):");
		cal.set(2018, 11, 31);
		System.out.println(ElAl.getCouponsByDate(cal.getTime()));
		System.out.println(ElAl.onlooker.printLog());
		System.out.println("done.\n");

		// UPDATING COUPONS:

		System.out.println("Updating the third coupon: (setting price to 9.99 and end date to  begining of 2020):");
		ticketThree.setId(3);
		ticketThree.setPrice(9.99);
		cal.set(2020, 0, 1);
		ticketThree.setEndDate(cal.getTime());
		ElAl.updateCoupon(ticketThree);
		System.out.println(ElAl.onlooker.printLog());
		System.out.println(ElAl.getCoupon(3));
		System.out.println("done.\n");

		// DELETING COUPONS:

		System.out.println("Deleting the third coupon (id==3): ");
		try {
			ElAl.removeCoupon(ticketThree);
			System.out.println(ElAl.onlooker.printLog());
		} catch (PossibleDBCorruptionException e) {
			System.out.println(e.msg);
			e.printStackTrace();
			store.shutdown();
		}
		System.out.println(ElAl.getAllCoupons());
		System.out.println(ElAl.onlooker.printLog());
		System.out.println("done.\n");

		// THE END

		// System.out.println("Making system shutdown:");
		// store.shutdown();
		// System.out.println("done.\n");

		System.out.println("End!");

	}

}