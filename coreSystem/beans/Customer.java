package beans;

import java.io.Serializable;
import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Is used to store a customer values and to transmit it in JSON format if
 * needed.
 * 
 * @author AlexanderZhilokov
 * 
 */
@XmlRootElement
public class Customer implements Serializable {

	/**
	 * Stores the serial number of the current version of the class.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * A unique number which is used as identity (PK) in the database.
	 */
	private long id;

	/**
	 * A customer name and password should be used for acquiring access to the
	 * database.
	 */
	private String custName, password;

	/**
	 * Stores a coupons which were bought early.
	 */
	private Collection<Coupon> coupons;

	/**
	 * Creates a customer with values of id equals to zero and name, password,
	 * coupons equals to null.
	 */
	public Customer() {
	}

	/**
	 * Returns the customer ID - a unique number which is used as identity (PK) in
	 * the database.
	 * 
	 * @return a value of the parameter id of the customer.
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets the customer ID - a unique number which is used as identity (PK) in the
	 * database.
	 * 
	 * @param id
	 *            - new value for the customer id.
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Returns the customer name - should be used for acquiring access to the
	 * database as a customer name.
	 * 
	 * @return a value of the parameter name of the customer.
	 */
	public String getCustName() {
		return custName;
	}

	/**
	 * Sets the customer name - should be used for acquiring access to the database
	 * as a customer name.
	 * 
	 * @param custName
	 *            - new value for the customer name.
	 */
	public void setCustName(String custName) {
		this.custName = custName;
	}

	/**
	 * Returns the customer password - should be used for acquiring access to the
	 * database as a password.
	 * 
	 * @return a value of the parameter password of the customer.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the customer password - should be used for acquiring access to the
	 * database as a password.
	 * 
	 * @param password
	 *            - new value for the customer password.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Returns the customer coupons which he bought early. Note that this method
	 * returns a reference and not a copy of the objects of the collection
	 * 
	 * @return the reference to the collection of the customer coupons.
	 */
	public Collection<Coupon> getCoupons() {
		return coupons;
	}

	/**
	 * Sets the new reference for the collection of the customer coupons. Note that
	 * this method only saves reference and not actually copies the objects of the
	 * collection.
	 * 
	 * @param coupons
	 *            - new reference for the customer coupons collection.
	 */
	public void setCoupons(Collection<Coupon> coupons) {
		this.coupons = coupons;
	}

	/**
	 * Convert the customer with all parameters values into a single string.
	 */
	@Override
	public String toString() {
		return "Customer [id=" + id + ", custName=" + custName + ", password=" + password + ", coupons=" + coupons
				+ "]";
	}

}