package beans;

import java.io.Serializable;
import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Is used to store a company values and to transmit it in JSON format if
 * needed.
 * 
 * @author AlexanderZhilokov
 * 
 */
@XmlRootElement
public class Company implements Serializable {

	/**
	 * Stores the serial number of the current version of the class.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * A unique number which is used as identity (PK) in the database.
	 */
	private long id;

	/**
	 * A company name and password should be used for acquiring access to the
	 * database.
	 */
	private String compName, password;

	/**
	 * Stores a company email
	 */
	private String email;

	/**
	 * Stores a coupons which were created early
	 */
	private Collection<Coupon> coupons;

	/**
	 * Creates a company with values of id equals to zero and name, password, email,
	 * coupons equals to null.
	 */
	public Company() {

	}

	/**
	 * Returns the company ID - a unique number which is used as identity (PK) in
	 * the database).
	 * 
	 * @return value of the parameter id of the company.
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets the company ID - a unique number which is used as identity (PK) in the
	 * database).
	 * 
	 * @param id
	 *            - new value for the company id.
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Returns the company name - should be used for acquiring access to the
	 * database as a company name.
	 * 
	 * @return value of the parameter name of the company.
	 */
	public String getCompName() {
		return compName;
	}

	/**
	 * Sets the company name - should be used for acquiring access to the database
	 * as a company name.
	 * 
	 * @param compName
	 *            - new value for the company name.
	 */
	public void setCompName(String compName) {
		this.compName = compName;
	}

	/**
	 * Returns the company password - should be used for acquiring access to the
	 * database as a password.
	 * 
	 * @return value of the parameter password of the company.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the company password - should be used for acquiring access to the
	 * database as a password.
	 * 
	 * @param password
	 *            - new value for the company password.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Returns the company email.
	 * 
	 * @return value of the parameter email of the company.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the company email.
	 * 
	 * @param email
	 *            - new value for the company email.
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Returns the company coupons which were created early. Note that this method
	 * returns a reference and not a copy of the objects of the collection.
	 * 
	 * @return reference to the collection of the company coupons.
	 */
	public Collection<Coupon> getCoupons() {
		return coupons;
	}

	/**
	 * Sets the new reference for the collection of the company coupons. Note that
	 * this method only saves reference and not actually copies the objects of the
	 * collection.
	 * 
	 * @param coupons
	 *            - new reference for the company coupons collection.
	 */
	public void setCoupons(Collection<Coupon> coupons) {
		this.coupons = coupons;
	}

	/**
	 * Convert the company with all parameters values into a single string.
	 */
	@Override
	public String toString() {
		return "Company [id=" + id + ", compName=" + compName + ", password=" + password + ", email=" + email
				+ ", coupons=" + coupons + "]";
	}

}