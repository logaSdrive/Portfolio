package refs;

import main.Vocabulary.ValType;
import vals.Val;

/**
 * Is used to store a mark which was founded on component's HTML representation
 * and should be considered as "road map" to how exactly to transfer the
 * corresponding value from the component's JSON representation to HTML and
 * back.
 * 
 * @author AlexanderZhilokov
 *
 */
public abstract class Ref implements Comparable<Ref> {

	/**
	 * Stores a mark which was founded on component's html representation and should
	 * be considered as "an entry point" to specific component's attribute which
	 * should be transfered from the component's JSON representation to the
	 * component's html representation and back) or null if the current ref does not
	 * uses as road map for any val mark.
	 */
	public Val val;

	/**
	 * Stores the type of the val mark or null if there is no val mark to the
	 * current ref instance binded.
	 */
	public ValType type;

	/**
	 * Describes how long (short) is a current ref's instance as a road map for
	 * transferring the binded to him val's mark between component's HTML and JSON
	 * representations. (Lesser number represents shorter path).
	 */
	public int priority;

	/**
	 * The name for the ref's instance.
	 */
	public String name;

	/**
	 * Returns the way corresponding to the current ref's instance to transfer an
	 * component's attribute (corresponding to it val's attribute) from JSON to HTML
	 * component's representation.
	 * 
	 * @return a js code that should be added to a Simplex.js file by the Simplex
	 *         compiler in order to transfer an component's attribute (corresponding
	 *         to the instance's val's attribute) from JSON to HTML component's
	 *         representation.
	 */
	public abstract String pope();

	/**
	 * Returns the way corresponding to the current ref's instance to transfer an
	 * component's attribute (corresponding to it val's attribute) from HTML to JSON
	 * component's representation.
	 * 
	 * @return a js code that should be added to a Simplex.js file by the Simplex
	 *         compiler in order to transfer an component's attribute (corresponding
	 *         to the instance's val's attribute) from HTML to JSON component's
	 *         representation.
	 */
	public abstract String push();

	/**
	 * A compare method for the set usage (Refs are equals than their names are
	 * equals. One ref smaller than another if it has or shorter "composite tree"
	 * address or than addresses have same length it alphabetically smaller).
	 */
	@Override
	public int compareTo(Ref other) {
		String[] myLinks = name.split("[.]");
		String[] otherLinks = other.name.split("[.]");
		if ((myLinks.length == 1) || (otherLinks.length == 1)) {
			if (myLinks.length != 1) {
				return -1;
			} else if (otherLinks.length != 1) {
				return 1;
			} else {
				return name.compareTo(other.name);
			}
		}
		int result;
		final int minLength = (myLinks.length > otherLinks.length) ? otherLinks.length : myLinks.length;
		for (int i = 0; i < minLength; i++) {
			result = myLinks[i].compareTo(otherLinks[i]);
			if (result != 0) {
				return result;
			}
		}
		return 0;
	}

}