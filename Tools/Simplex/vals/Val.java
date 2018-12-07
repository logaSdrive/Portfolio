package vals;

import java.util.ArrayList;
import java.util.List;

import refs.Ref;

/**
 * Is used to store a mark which was founded on component's html representation
 * and should be considered as entry point to store the corresponding value from
 * the component's JSON representation to HTML and back.
 * 
 * @author AlexanderZhilokov
 *
 */
public class Val {

	/**
	 * Hashcode method for the hash map usage (by val's name hashcode).
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/**
	 * Equals method for the hash map usage (vals are equals if their names are
	 * equals).
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Val other = (Val) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	/**
	 * The name that should correspond to the name of the component's JSON
	 * representation attribute.
	 */
	public String name;

	/**
	 * Stores all marks which was founded on component's html representation and
	 * should be considered as "a road map" how exactly to to transfer the
	 * corresponding value from the component's JSON representation to the
	 * component's html representation.
	 */
	public List<Ref> refs;

	/**
	 * Creates a val with it name.
	 * 
	 * @param name
	 *            - a given value for a val's name to create.
	 */
	public Val(String name) {
		this.name = name;
		refs = new ArrayList<>();
	}

	/**
	 * Prepares "a road maps" (a ways to transfer between JSON and HTML a
	 * corresponding to the current val's instance attribute of an component).
	 */
	public void initiate() {
		refs.sort(
				(one, another) -> (one.priority == another.priority) ? 0 : (one.priority > another.priority) ? 1 : -1);
	}

	/**
	 * Returns "the shortest" way to transfer an component's attribute corresponding
	 * to the current val's instance from JSON to HTML component's representation.
	 * 
	 * @return a js code that should be added to a Simplex.js file by the Simplex
	 *         compiler.
	 */
	public String push() {
		return refs.iterator().next().push();
	}

	/**
	 * Returns "the shortest" way to transfer an component's attribute corresponding
	 * to the current val's instance from HTML to JSON component's representation.
	 * 
	 * @return a js code that should be added to a Simplex.js file by the Simplex
	 *         compiler.
	 */
	public String pope() {
		return refs.iterator().next().pope();
	}

}