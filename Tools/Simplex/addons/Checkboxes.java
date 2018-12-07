package addons;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import main.Vocabulary.Extension;
import main.Vocabulary.ValType;
import refs.Ref;
import refs.RefOnName;
import refs.RefOnNull;
import vals.Val;

/**
 * Deals with need to bind to the some HTML input elements from a checkboxes
 * type(or a radio type) the values from a JSON attributes for an component.
 * (Uses for processing a refs from "name" type and also knows to recognize the
 * val's marks that should be binded to this ref marks).
 * 
 * @author AlexanderZhilokov
 *
 */
public class Checkboxes implements Extension {

	/**
	 * Stores a map there compiler will store all entry point marks which were found
	 * in a specific component's HTML representation for the corresponding values of
	 * this component's JSON representation.
	 */
	public Map<String, Val> vals;

	/**
	 * Stores a set there compiler will store all marks for a "road map" how to
	 * transfer values between component's JSON and HTML representations which were
	 * founded and defined as refs for a current component.
	 */
	public Set<Ref> refs;

	/**
	 * Will store an entry point mark for a component's attribute value.
	 */
	private Val val;

	/**
	 * Will store a mark which should be considered as "a road map" for transferring
	 * a specific component's value as binded value to the some HTML input element
	 * from a checkboxes type(or a radio type).
	 */
	private Ref refToDefine;

	/**
	 * Sets a references for a collections of marks which will be founded and used
	 * by compiler for the current component.
	 */
	@Override
	public void initiate(Map<String, Val> vals, Set<Ref> refs) {
		this.vals = vals;
		this.refs = refs;
		val = null;
		refToDefine = null;
	}

	/**
	 * Defines than a given sentence with a given context is considered the ref or
	 * the val mark that should be binded to the some HTML input element from a
	 * checkboxes type(or a radio type).
	 */
	@Override
	public void transcribe(String sequence, String context) {
		String regexRef = "\\s?(ref)([.][a-zA-Z]+[0-9]*)+\\s?";
		String regexVal = "\\s?\\(\\s?\\(\\s?(val)[.][a-zA-Z]+[0-9]*\\={2}\\" + '"' + "?[a-zA-z0-9]+\\" + '"'
				+ "?\\s?\\)\\s?\\?\\s?(" + '"' + "checked" + '"' + ")\\:\\" + '"' + "{2}\\)\\s?";
		if (Pattern.compile(regexVal).matcher(sequence).matches()) {
			String name = sequence.substring(sequence.indexOf('.') + 1, sequence.indexOf('='));
			val = vals.getOrDefault(name.trim(), new Val(name.trim()));
		} else if (context.length() >= 5 && context.substring(context.length() - 5, context.length() - 1).equals("name")
				&& Pattern.compile(regexRef).matcher(sequence).matches()) {
			Ref ref = new RefOnName(sequence.trim().substring(4));
			if (!refs.contains(ref)) {
				refToDefine = ref;
			}
		}
	}

	/**
	 * Applies previously retrieved results to a collections of marks which used by
	 * compiler for the current component and nullifies a temporal data.
	 */
	@Override
	public void nullify() {
		if (refToDefine != null) {
			if (val == null) {
				Ref nullRef = new RefOnNull(refToDefine.name);
				refs.add(nullRef);
			} else {
				val.refs.add(refToDefine);
				refToDefine.type = ValType.VALUE;
				refToDefine.val = val;
				refs.add(refToDefine);
				vals.putIfAbsent(val.name, val);
			}
		}
		refToDefine = null;
		val = null;
	}

}