package refs;

/**
 * Is used to store a mark which was founded on component's HTML representation
 * and should be considered as "road map" to transfer the corresponding value
 * from the component's JSON representation to HTML and back using "DOM
 * getElementById()" method.
 * 
 * @author AlexanderZhilokov
 *
 */
public class RefOnId extends Ref {

	/**
	 * Returns the way to transfer the corresponding value (corresponding to it
	 * val's attribute) from JSON to HTML component's representation using "DOM
	 * getElementById()" method.
	 */
	public String push() {
		return "document.getElementById(ref." + name + ")." + type.name + "=val." + val.name + ";";
	}

	/**
	 * Returns the way to transfer the corresponding value (corresponding to it
	 * val's attribute) from HTML to JSON component's representation using "DOM
	 * getElementById()" method.
	 */
	public String pope() {
		return "val." + val.name + "=document.getElementById(ref." + name + ")." + type.name + ";";
	}

	/**
	 * Creates a ref with it name. Usually RefOnId instances considered as a
	 * shortest way to transfer the data (low value for priority will be acquired).
	 * 
	 * @param name
	 *            - a given value for a ref's name to create.
	 */
	public RefOnId(String name) {
		this.name = name;
		priority = 0;
	}

}