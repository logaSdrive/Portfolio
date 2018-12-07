package refs;

/**
 * Is used to store a mark which was founded on component's HTML representation
 * and should be considered as "road map" to transfer the corresponding value
 * from the component's JSON representation to HTML and back in cases than such
 * attribute should be binded to an input elements from checkboxe type or radio
 * type.
 * 
 * @author AlexanderZhilokov
 *
 */
public class RefOnName extends Ref {

	/**
	 * Returns the way to transfer the corresponding value (corresponding to it
	 * val's attribute) from JSON to HTML component's representation in cases than
	 * such attribute should be binded to an input elements from checkboxe type or
	 * radio type.
	 */
	public String push() {
		return "document.querySelectorAll('input[name=" + '"' + "'+ref." + name + "+'" + '"'
				+ "]').forEach(function(element) { if (element.value==val." + val.name
				+ ") { element.checked = true; } });";
	}

	/**
	 * Returns the way to transfer the corresponding value (corresponding to it
	 * val's attribute) from HTML to JSON component's representation in cases than
	 * such attribute should be binded to an input elements from checkboxe type or
	 * radio type.
	 */
	public String pope() {
		return "val." + val.name + "=document.querySelectorAll('input[name=" + '"' + "ref." + name + '"'
				+ "]:checked')." + type.name + ";";
	}

	/**
	 * Creates a ref with it name. Usually RefOnName instances considered as a
	 * longest way to transfer the data (high value for priority will be acquired).
	 * 
	 * @param name
	 *            - a given value for a ref's name to create.
	 */
	public RefOnName(String name) {
		this.name = name;
		priority = 1;
	}

}