package tests;

import database.DBBuilder;
import database.DBCleaner;

/**
 * Runs all tests which i wrote in a proper order.
 * 
 * @author AlexanderZhilokov
 *
 */
public class MainTest {

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
	 * Note for using it: 1)It assumes that the database (ZhilokovAlexanderDB) yet
	 * not created. Please drop the database if it exists. 2)If you enabled
	 * previously shutting downs the coupon system in the end of the another tests,
	 * please disabled it. Cauze singleton pool connection and java garbage
	 * collector does not works well together.
	 * 
	 * @param args
	 *            - Irrelevant.
	 */
	public static void main(String[] args) {

		System.out.println(
				"Hi! This is main tests for all project Coupon System. It will launch in series all tests which i wroted.");
		System.out.println("It will create the database and will drop it by the end of the test.");
		
		System.out.println("Creating a new, clean database:");
		DBBuilder.main(null);
		System.out.println("done.\n");

		System.out.println("Launching basic tests for all facades:");
		pressAnyKeyToContinue();
		AdminBasicTest.main(null);
		pressAnyKeyToContinue();
		CompanyBasicTest.main(null);
		pressAnyKeyToContinue();
		CustomerBasicTest.main(null);
		pressAnyKeyToContinue();
		SuperAdminBasicTest.main(null);
		pressAnyKeyToContinue();

		System.out.println("Launching exception-based tests:");
		pressAnyKeyToContinue();
		InputTroublesTest.main(null);
		pressAnyKeyToContinue();
		LogicalIssuesTest.main(null);
		pressAnyKeyToContinue();
		SynchonizationIssuesTest.main(null);
		pressAnyKeyToContinue();

		System.out.println("Dropping all tables:");
		DBCleaner.main(null);
		System.out.println("done.\n");
		System.out.println("Thats all tests!");
	}

}