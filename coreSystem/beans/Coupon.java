package beans;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Is used to store a coupon values and to transmit it in JSON format if needed.
 * 
 * @author AlexanderZhilokov
 * 
 */
@XmlRootElement
public class Coupon implements Serializable {

	/**
	 * Stores the serial number of the current version of the class.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * A unique number which is used as identity (PK) in the database.
	 */
	private long id;

	/**
	 * Stores a coupon title.
	 */
	private String title;

	/**
	 * Stores a coupon message.
	 */
	private String message;

	/**
	 * Stores a coupon image.
	 */
	private String image;

	/**
	 * Stores a coupon start date.
	 */
	private Date startDate;

	/**
	 * Stores a coupon end date.
	 */
	private Date endDate;

	/**
	 * Stores the amount of the coupons which yet to be sold.
	 */
	private int amount;

	/**
	 * Stores a coupon price.
	 */
	private double price;

	/**
	 * Stores a coupon type.
	 */
	private CouponType type;

	/**
	 * Creates a coupon with values of id, price, amount equals to zero and title,
	 * type, start date, end date, message, image equals to null.
	 */
	public Coupon() {

	}

	/**
	 * Convert the coupon with all parameters values into a single string.
	 */
	@Override
	public String toString() {
		return "Coupon [id=" + id + ", title=" + title + ", message=" + message + ", image=" + image + ", startDate="
				+ startDate + ", endDate=" + endDate + ", amount=" + amount + ", price=" + price + ", type=" + type
				+ "]";
	}

	/**
	 * Returns the coupon ID - a unique number which is used as identity (PK) in the
	 * database.
	 * 
	 * @return value of the parameter id of the coupon.
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets the coupon ID - a unique number which is used as identity (PK) in the
	 * database.
	 * 
	 * @param id
	 *            - new value for the coupon id.
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Returns the coupon title.
	 * 
	 * @return value of the parameter title of the coupon.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the coupon title.
	 * 
	 * @param title
	 *            - new value for the coupon title.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Returns the coupon message.
	 * 
	 * @return value of the parameter message of the coupon.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the coupon message.
	 * 
	 * @param message
	 *            - new value for the coupon message.
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Returns the coupon image.
	 * 
	 * @return value of the parameter image of the coupon.
	 */
	public String getImage() {
		return image;
	}

	/**
	 * Sets the coupon image.
	 * 
	 * @param image
	 *            - new value for the coupon image.
	 */
	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * Returns the coupon start date.
	 * 
	 * @return value of the parameter start date of the coupon.
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Sets the coupon start date.
	 * 
	 * @param startDate
	 *            - new value for the coupon start date.
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Returns the coupon end date.
	 * 
	 * @return value of the parameter end date of the coupon.
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Sets the coupon end date.
	 * 
	 * @param endDate
	 *            - new value for the coupon end date.
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * Returns the coupon amount which yet to be sold.
	 * 
	 * @return value of the parameter amount of the coupon.
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * Sets the coupon amount which yet to be sold.
	 * 
	 * @param amount
	 *            - new value for the coupon amount.
	 */
	public void setAmount(int amount) {
		this.amount = amount;
	}

	/**
	 * Returns the coupon price.
	 * 
	 * @return value of the parameter price of the coupon.
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * Sets the coupon price.
	 * 
	 * @param price
	 *            - new value for the coupon price.
	 */
	public void setPrice(double price) {
		this.price = price;
	}

	/**
	 * Returns the coupon type.
	 * 
	 * @return value of the parameter type of the coupon.
	 */
	public CouponType getType() {
		return type;
	}

	/**
	 * Sets the coupon type.
	 * 
	 * @param type
	 *            - new value for the coupon type.
	 */
	public void setType(CouponType type) {
		this.type = type;
	}

}