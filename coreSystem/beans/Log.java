package beans;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Is used to log an operation values and to transmit it in JSON format if
 * needed.
 * 
 * @author AlexanderZhilokov
 * 
 */
@XmlRootElement
public class Log implements Serializable {

	/**
	 * Stores the serial number of the current version of the class.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Uses as integer representation for the message of the log.
	 */
	private int hashcode;

	/**
	 * Specify the completion status of operation where from current log was
	 * created. (Ets: Is operation succeed, or is it succeed, but where are some
	 * issues that should be addressed (warning) or is it fails totally.)
	 */
	private String status;

	/**
	 * Specify the type of the operation where from current log was created.
	 */
	private String header;

	/**
	 * Stores the content of a message for the current log.
	 */
	private String message;

	/**
	 * Specify the unique information about condition where from log was created.
	 * (Ets: date, client name and client type, operation ordinal number during the
	 * specific session and so on).
	 */
	private String signature;

	/**
	 * Returns the log hashcode.
	 * 
	 * @return value of the parameter hashcode of the log.
	 */
	public int getHashcode() {
		return hashcode;
	}

	/**
	 * Sets the log hashcode.
	 * 
	 * @param hashcode
	 *            - new value for the log hashcode.
	 */
	public void setHashcode(int hashcode) {
		this.hashcode = hashcode;
	}

	/**
	 * Returns the log status.
	 * 
	 * @return value of the parameter status of the log.
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets the log status.
	 * 
	 * @param status
	 *            - new value for the log status.
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Returns the log header.
	 * 
	 * @return value of the parameter header of the log.
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * Sets the log header.
	 * 
	 * @param header
	 *            - new value for the log header.
	 */
	public void setHeader(String header) {
		this.header = header;
	}

	/**
	 * Returns the log message.
	 * 
	 * @return value of the parameter message of the log.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the log message.
	 * 
	 * @param message
	 *            - new value for the log message.
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * Returns the log signature.
	 * 
	 * @return value of the parameter signature of the log.
	 */
	public String getSignature() {
		return signature;
	}

	/**
	 * Sets the log signature.
	 * 
	 * @param signature
	 *            - new value for the log signature.
	 */
	public void setSignature(String signature) {
		this.signature = signature;
	}

	/**
	 * Creates a log with values of hashcode equals to zero and header, message,
	 * signature equals to null.
	 */
	public Log() {
	}

	/**
	 * Convert the log with all parameters values into a single string.
	 */
	@Override
	public String toString() {
		return "Log: " + status + "\nheader: " + header + "\nmessage: " + message+"\n" + signature;
	}

}