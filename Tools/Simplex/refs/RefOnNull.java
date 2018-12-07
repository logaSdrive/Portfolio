package refs;

/**
 * Is used to store a mark which was founded on component's HTML representation
 * and should not be considered as a "road map" to transfer any values but it
 * uses as unique id(name) to run some js functions on component's HTML
 * representation.
 * 
 * @author AlexanderZhilokov
 *
 */
public class RefOnNull extends Ref {

	/**
	 * Returns the blank string.
	 */
	@Override
	public String pope() {
		return "";
	}

	/**
	 * Returns the blank string.
	 */
	@Override
	public String push() {
		return "";
	}

	/**
	 * Creates a ref with it name.
	 * 
	 * @param name
	 *            - a given value for a ref's name to create.
	 */
	public RefOnNull(String name) {
		this.name = name;
	}

}