package main;

import java.util.Map;
import java.util.Set;

import addons.Checkboxes;
import refs.Ref;
import shortcuts.*;
import vals.Val;

/**
 * Defines a vocabulary for the simplex compiler (SimplexC). If you want to
 * modify or extents compiler capabilities you should declare the changes here.
 * 
 * @author AlexanderZhilokov
 *
 */
public class Vocabulary {

	/**
	 * Provides an interface which you should implement in order to write you own
	 * mark's processing for a simplex compiler.
	 * 
	 * @author AlexanderZhilokov
	 *
	 */
	public interface Extension {

		/**
		 * Will be called each time than the simplex compilers starts reads a new HTML
		 * files which represents a new components. It's a time to initiate you
		 * extension and to adapt it for a current HTML file.
		 * 
		 * @param vals
		 *            - a map there compiler will store all marks for entry points for
		 *            the component's attributes which will be founded and defined as
		 *            vals for a current component.
		 * @param refs
		 *            - a set there compiler will store all marks for "road map" how to
		 *            transfer values between component's JSON and HTML representations
		 *            which will be founded and defined as refs for a current component.
		 */
		void initiate(Map<String, Val> vals, Set<Ref> refs);

		/**
		 * Will be called by compiler than an mark founded and it is not known shortcut
		 * (all known shortcuts you can see below in a Vocabulary class). (Called after
		 * compiler tried to process the mark in a hardcoded way, regardless of the
		 * results). You should use the given previously (on initiate call) references
		 * to vals map and refs set to change and\or store the results.
		 * 
		 * @param sequence
		 *            - a mark inner value.
		 * @param context
		 *            - a context for a mark value - is an html code for the specific
		 *            html element there current mark was founded.
		 */
		void transcribe(String sequence, String context);

		/**
		 * Will be called than compiler decides it is a time to change a context
		 * (encounters the end of the current html element (element, like button, p, h
		 * or input)).
		 */
		void nullify();
	}

	/**
	 * Provides an interface which you should implement in order to write you own
	 * shortcut mark definitions and it properties for a simplex compiler.
	 * 
	 * @author AlexanderZhilokov
	 *
	 */
	public interface Deployment {

		/**
		 * Will be called each time than the simplex compilers starts reads a new HTML
		 * files which represents a new component. It's a time to initiate you shortcut
		 * and to adapt it for a current HTML file.
		 * 
		 * @param component
		 *            - name of the current component (name of the file without file
		 *            extension).
		 */
		void initiate(String component);

		/**
		 * Will be called each time compiler encounters a new mark.
		 * 
		 * @param sequence
		 *            - a mark inner value.
		 * @return true if you recognize a mark as a known shortcut, and that indicates
		 *         to the compiler, that it (compiler) should stop additional checks and
		 *         call your deploy method. Else should returns false.
		 */
		boolean matches(String sequence);

		/**
		 * Will be called directly after you matches method returns true.
		 * 
		 * @return js code that should be added instead of the shortcut(mark) inner
		 *         value. (Note, that it must be wrapped in a double quotation symbols).
		 */
		String deploy();

	}

	/**
	 * Declares to the compiler all known shortcuts.
	 * 
	 * @author AlexanderZhilokov
	 *
	 */
	public enum Shortcuts {

		/**
		 * Shortcut that specifies to complier: "instead word "super" it should call to
		 * some Simplex object function (getAncestor(your position)) to retrieve a
		 * current component direct parent". (Uses in cases nested components, one into
		 * another).
		 */
		SUPER(new Super()),

		/**
		 * Shortcut that specifies to complier "instead word "this" it should call to
		 * some Simplex object function (get(your id, your position)) to retrieve a JSON
		 * representation of a current component. (Uses in cases you need to send
		 * current component values by binded to it html representation button).
		 */
		THIS(new This()),

		/**
		 * Shortcut that specifies to complier "instead word "component" it should send
		 * some nested object in a Simplex object that defines all set of CRUDE action
		 * which were binded to this component. (Uses in cases you need to work with a
		 * different components on an abstraction level (like you want to work with
		 * purchased coupons and coupon as them will be just a coupons)).
		 */
		COMPONENT(new Component());

		/**
		 * Stores the interface that defines current shortcut properties (like how
		 * exactly shortcut would be recognized from a mark inner value and how it
		 * should be translated in a final Simplex.js code). See above the Deployment
		 * interface.
		 */
		public final Deployment core;

		/**
		 * Creates a shortcut with it properties described by the given core parameter.
		 * 
		 * @param core
		 *            - a properties for a shortcut to create.
		 */
		Shortcuts(Deployment core) {
			this.core = core;
		}
	}

	/**
	 * Declares to the compiler all known mark's ref's types.
	 * 
	 * @author AlexanderZhilokov
	 *
	 */
	public enum RefType {

		/**
		 * Defines that a mark from a known ref type may be in context "id" or "name" or
		 * without any context (null==""). Note that null type is used also to describe
		 * the unkown to compiler ref type marks. Add here a new ref type if you need.
		 */
		ID("id"), NAME("name"), NULL("");

		/**
		 * Exact string value as it defined by html syntax for defining a ref type.
		 */
		public final String type;

		/**
		 * Creates a mark's ref's type definition with a given parameter which uses in a
		 * checks for a context there is a ref's mark was founded.
		 * 
		 * @param type
		 *            - a context which defines a mark's ref's type.
		 */
		RefType(String type) {
			this.type = type;
		}
	}

	/**
	 * Declares to the compiler all known mark's val's types.
	 * 
	 * @author AlexanderZhilokov
	 *
	 */
	public enum ValType {

		/**
		 * Defines that a mark from a val's type may be in context or "name", or
		 * "innerHTML", or "src", or "value" only. The unknown to compiler val's marks
		 * types will be ignored. Add here a new val's type if you need.
		 */
		NAME("name"), INNER_HTML("innerHTML"), SOURCE("src"), VALUE("value");

		/**
		 * Exact string value as it defined by html syntax for defining a val type
		 * (except of a case for innerHTML - hardcoded and works only than a mark from a
		 * val type innerHTML is located directly to the opening innerHTML brackets.
		 */
		public final String name;

		/**
		 * Creates a mark's val's type definition with a given parameter which uses in a
		 * checks for a context there is val's mark was founded.
		 * 
		 * @param name
		 *            - a context which defines a mark's val's type.
		 */
		ValType(String name) {
			this.name = name;
		}
	}

	/**
	 * Declares to the compiler all known extension to treat a marks which are not
	 * shortcuts (refs and vals).
	 * 
	 * @author AlexanderZhilokov
	 *
	 */
	public enum Addons {

		/**
		 * Extension which i wrote to deal with need to bind to the html input elements
		 * from a checkboxes type(a or radio type) the values from a JSON attributes for
		 * an component. (Uses for processing a refs from "name" type and also knows to
		 * recognize the val's marks that should be binded to this ref's marks).
		 */
		CHECKBOXES(new Checkboxes());

		/**
		 * Stores the interface that defines current extension properties (like how
		 * exactly the case of the extension would be recognized from a mark inner value
		 * and it context and how it should be processed). See above the Extension
		 * interface.
		 */
		public final Extension extension;

		/**
		 * Creates an addon with it properties described by the given extension
		 * parameter.
		 * 
		 * @param extension
		 *            - a properties for an addon to create.
		 */
		Addons(Extension extension) {
			this.extension = extension;
		}
	}

}