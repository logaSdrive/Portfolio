package system;

import beans.Log;

/**
 * Implements a way of a delivery messages to the final layer (user or logger)
 * from a bottoms layers (from the database drivers, facades, etc).
 * 
 * @author AlexanderZhilokov
 *
 */
public class Looker {

	/**
	 * Stores the log of the current operation to observe
	 */
	public Log log;

	/**
	 * Indicates if there are fails were observed.
	 */
	private boolean hasFails;
	
	public Looker() {
		log = new Log();
	}

	/**
	 * Starts a writing to a new log and nullifies all previous data.
	 */
	public void newLog() {
		log = new Log();
		hasFails = false;
		log.setStatus("SUCCESS");
	}

	/**
	 * Checks if there is any failures to deliver.
	 * 
	 * @return - true if any fails was happens early. Otherwise returns false.
	 */
	public boolean hasFails() {
		return hasFails;
	}

	/**
	 * Documents operation as a success with a warning.
	 * 
	 * @param message
	 *            - a warning message to deliver.
	 */
	public void markWarning(String message) {
		log.setMessage(message);
		log.setStatus("WARNING");
	}

	/**
	 * Documents operation as a failure.
	 * 
	 * @param message
	 *            - a failure message to deliver.
	 * @param hashcode
	 *            - a hashcode of the failure message.
	 */
	public void markFailure(String message, int hashcode) {
		log.setMessage(message);
		log.setStatus("FAILURE");
		log.setHashcode(hashcode);
		hasFails = true;
	}
	
	/**
	 * Documents operation as a failure.
	 * 
	 * @param message
	 *            - a failure message to deliver.
	 */
	public void markFailure(String message) {
		log.setMessage(message);
		log.setStatus("FAILURE");
		hasFails = true;
	}

	/**
	 * Documents operation as a success.
	 * 
	 * @param header
	 *            - a header (general description of the operation).
	 * @param signature
	 *            - a signature (a unique signature of the operation).
	 */
	public void markSuccess(String header, String signature) {
		log.setStatus("SUCCESS");
		log.setHeader(header);
		log.setMessage("");
		log.setSignature(signature);
	}

	/**
	 * Returns stored log as a string.
	 * 
	 * @return a String representation of the stored log.
	 */
	public String printLog() {
		return log.toString();
	}

	/**
	 * Returns stored log as an object which is ready to be transmitted in a JSON
	 * format.
	 * 
	 * @return a log as an object which is ready to be transmitted in a JSON format.
	 */
	public Log getLog() {
		return log;
	}

}