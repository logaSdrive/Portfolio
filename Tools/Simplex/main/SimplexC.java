package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import main.Vocabulary.Addons;
import main.Vocabulary.Shortcuts;
import main.Vocabulary.ValType;
import refs.Ref;
import refs.RefOnId;
import refs.RefOnNull;
import vals.Val;

/**
 * It compiles in the directory "files" a java script file which will be named
 * as Simplex.js. This file will provide a set of CRUD operations which uses as
 * mediator between a JSON and it's (for all components) HTML representation on
 * the page. To compile such file you need to specify as arguments (you should
 * run it with arguments) all components (one HTML file per each component) for
 * all type of JSON objects which you need to present on the page. This HTML
 * files should be prepared with number of rules which defined in a
 * Simplex.doc.txt file.
 * 
 * @author AlexanderZhilokov
 */
public class SimplexC {

	/**
	 * Integer representation for a char symbol of a new line in ISO 8859-1 format.
	 */
	public static final int NEW_LINE = 10;

	/**
	 * Integer representation for a char symbol of a carriage return in ISO 8859-1
	 * format.
	 */
	public static final int CARRIAGE_RETURN = 13;

	/**
	 * Integer representation for a char symbol of a horizontal tab in ISO 8859-1
	 * format.
	 */
	public static final int HORIZONTAL_TAB = 9;

	/**
	 * Integer representation for a space symbol in ISO 8859-1 format.
	 */
	public static final int SPACE = 32;

	/**
	 * Integer representation for a apostrophe symbol in ISO 8859-1 format.
	 */
	public static final int APOSTROPHE = 39;

	/**
	 * Integer representation for a double quotation symbol in ISO 8859-1 format.
	 */
	public static final int DOUBLE_QUOTATION = 34;

	/**
	 * Stores all names of the components which will be compiled in a new Simplex
	 * and are defined by names of their HTML files which you send as arguments for
	 * main method (without the file extension value).
	 */
	static String[] components;

	/**
	 * Stores all entry point marks which were found in a specific component's HTML
	 * representation for the corresponding values of this component's JSON
	 * representation.
	 */
	static Map<String, Val> vals;

	/**
	 * Stores all marks which were founded on component's HTML representation and
	 * should be considered as "a road maps" to how exactly to transfer the
	 * corresponding values from the component's JSON representation to the
	 * component's HTML representation.
	 */
	static Set<Ref> refs;

	/**
	 * Will store a mark which should be considered as "a road map" for transferring
	 * a specific component's value which may be located in a current context (in a
	 * boundary of the current html element).
	 */
	static Ref refToDefine;

	/**
	 * Will store an entry point mark for a specific component's value which may be
	 * located in a current context (in a boundary of the current html element)..
	 */
	static Val valToDefine;

	/**
	 * Will store a type for an entry point mark for a specific component's html
	 * representation value which may be located in a current context.
	 */
	static ValType typeToBind;

	/**
	 * Defines the conditions than a single quote case should be processed in a
	 * different way. (After a mark statement for a SimplexC).
	 */
	static boolean processQuote = false;

	/**
	 * Defines the conditions than all page markup symbols (like space symbol or tab
	 * symbol) should be ignored. Uses for minifying a component's html
	 * representation.
	 */
	static boolean trim = false;

	/**
	 * Defines than a gap (space symbol) was added. Uses for minifying a component's
	 * html representation.
	 */
	static boolean gapAdded = true;

	/**
	 * Uses for construction refs composites in a Simplex.js file. (Like you want
	 * that all coupons will have it model (minified representation on a page), and
	 * you want for this model refs to same entry points be named as for a full
	 * representation (coupon refs for values: id (ref for id - for full
	 * representation) - model.id (ref for minified), title (ref for title for full
	 * representation) - model.title (ref for minified), etc) - so in this case -
	 * model{} is a refs composite.
	 * 
	 * I know now that there is a pattern for this case (composite pattern), but (1)
	 * i now it know, not than i wrote this methods, (2) it all static methods
	 * (brute force without much thinking (shit code if y prefer to call it this
	 * way, but ill disagree)) cauze it like hardcoded - you can modify another
	 * parts (not this) of the Simplex through Vocabulary with a classic paradigm of
	 * object oriented programming.
	 * 
	 * @param links
	 *            - an array which every element represents leaf or composite and
	 *            whole array is a full "address" of current component on a "tree".
	 * @param from
	 *            - defines an index to the links array from there it should be
	 *            processed.
	 * @param newObj
	 *            - defines if a current opponents is a new "tree" (new composite).
	 * @return part of the js code which should be added to Simplex.js in order to
	 *         continue a building a current refs composite.
	 */
	public static String openRefsComposite(String[] links, int from, boolean newObj) {
		String result = "";
		int depth = links.length - 1;
		for (int i = from; i < depth; i++) {
			if (newObj) {
				result += "this." + links[i] + "={";
			} else {
				result += (links[i] + ":{");
			}
		}
		return result;
	}

	/**
	 * Closes a branches in a construction refs composites in a Simplex.js file.
	 * 
	 * @param depth
	 *            - how many branches should be closed.
	 * @return part of the js code which should be added to Simplex.js in order to
	 *         end a building a current refs composite.
	 */
	public static String closeRefsComposite(int depth) {
		String result = "";
		for (int i = 0; i < depth; i++) {
			result += "}";
		}
		return result;
	}

	/**
	 * Manages whole construction for refs composites in a Simplex.js file.
	 * 
	 * @param refs
	 *            - all refs which were founded in a current html component.
	 * @return a complete js code which should be added to Simplex.js in order to
	 *         construct all refs composites which are needed.
	 */
	public static String buildRefsComposite(Set<Ref> refs) {
		String[] prevLinks = null;
		String result = "";
		int minLength = 0;
		int differences = 0;
		boolean isNew = true;
		for (Ref ref : refs) {
			String[] currLinks = ref.name.split("[.]");
			if (currLinks.length > 1) {
				if (prevLinks == null) {
					result += openRefsComposite(currLinks, 0, isNew);
				} else {
					minLength = (currLinks.length < prevLinks.length) ? currLinks.length : prevLinks.length;
					differences = 0;
					for (int i = 0; i < minLength - 1; i++) {
						if (!currLinks[i].equals(prevLinks[i])) {
							differences++;
						}
					}
					result += closeRefsComposite(differences + (prevLinks.length - minLength));
					if (differences == currLinks.length - 1) {
						result += ";";
						isNew = true;
					} else if ((differences > 0) || prevLinks.length < currLinks.length)
						result += ",";
					result += openRefsComposite(currLinks, minLength - differences - 1, isNew);
				}
				prevLinks = currLinks;
			} else
				break;
			isNew = false;
		}
		if (prevLinks != null)
			result += closeRefsComposite(prevLinks.length - 1) + ";";
		return result;
	}

	/**
	 * Uses for transcribe "a mark" which was founded (is it known ref type? or is
	 * it known val type? or may be it a known shortcut?).
	 * 
	 * @param sequence
	 *            - a mark inner value.
	 * @param context
	 *            - a context for a mark value - is an html code for the specific
	 *            html element there current mark was founded.
	 */
	public static void transcribe(String sequence, String context) {
		String regexRef = "\\s?(ref)([.][a-zA-Z]+[0-9]*)+\\s?";
		String regexVal = "\\s?(val)[.][a-zA-Z]+[0-9]*\\s?";
		String shot = "";
		Val currVal;
		ValType currValType = null;
		if (Pattern.compile(regexVal).matcher(sequence).matches()) {
			currVal = vals.getOrDefault(sequence.trim().substring(4), new Val(sequence.trim().substring(4)));
			for (ValType type : ValType.values()) {
				if (type.name.equals("innerHTML") && context.equals("")) {
					currValType = type;
				} else if (type.name.length() + 1 <= context.length()) {
					shot = context.substring(context.length() - type.name.length() - 1, context.length() - 1);
					if (type.name.equals(shot)) {
						currValType = type;
					}
				}
			}
			if (currValType != null) {
				if (refToDefine != null) {
					refToDefine.type = currValType;
					refToDefine.val = currVal;
					currVal.refs.add(refToDefine);
					vals.putIfAbsent(currVal.name, currVal);
					refs.add(refToDefine);
				} else {
					valToDefine = currVal;
					typeToBind = currValType;
				}
			}
		} else if (Pattern.compile(regexRef).matcher(sequence).matches()) {
			Ref ref = new RefOnId(sequence.trim().substring(4));
			if (!refs.contains(ref)) {
				if (context.length() >= 3
						&& context.substring(context.length() - 3, context.length() - 1).equals("id")) {
					if (valToDefine != null && typeToBind != null) {
						ref.type = typeToBind;
						ref.val = valToDefine;
						valToDefine.refs.add(ref);
						refs.add(ref);
						vals.putIfAbsent(valToDefine.name, valToDefine);
					} else {
						refToDefine = ref;
					}
				}
			}
		}
	}

	/**
	 * Reads from given reader at HTML file of an component and returns a js code
	 * which should be added to Simplex.js in order to build a CRUD set of action
	 * for a current component.(To create from JSON object on page, read from page
	 * to JSON, update from JSON to page and delete from page using JSON). Works
	 * with ISO 8859-1 HTML files.
	 * 
	 * @param in
	 *            - a given reader to read the current HTML file.
	 * @return returns a js code which should be added to Simplex.js in order to
	 *         build a CRUD set of action for a current component.
	 * @throws IOException
	 *             if there are a problems with reading the file.
	 */
	static String read(BufferedReader in, String component) throws IOException {
		String result = "";
		String sequence = "";
		String context = "";
		boolean keyword = false;
		boolean deployed = false;
		int shot;
		int currentPos = 0;
		int contextStart = 0;
		int sequenceStart = 0;
		vals = new HashMap<>();
		refs = new TreeSet<>();
		Addons[] addons = Addons.values();
		Shortcuts[] shortcuts = Shortcuts.values();
		for (Addons addon : addons) {
			addon.extension.initiate(vals, refs);
		}
		for (Shortcuts shortcut : shortcuts) {
			shortcut.core.initiate(component);
		}
		while ((shot = in.read()) != -1) {
			currentPos = result.length() - 1;
			if (shot != NEW_LINE && shot != HORIZONTAL_TAB && shot != CARRIAGE_RETURN) {
				if (shot != SPACE) {
					if (shot == '{' && result.charAt(currentPos) == '{') {
						keyword = true;
						sequenceStart = currentPos;
						if (result.charAt(currentPos - 1) == APOSTROPHE) {
							if (keyword) {
								sequenceStart = sequenceStart - 3;
							}
							result = result.substring(0, currentPos - 1);
							processQuote = true;
						} else {
							result = result.substring(0, currentPos) + "'+";
						}
						continue;
					}
					if (shot == '}' && result.charAt(currentPos) == '}') {
						sequence = result.substring(sequenceStart + 2, currentPos);
						context = result.substring(contextStart + 1, result.length() - sequence.length() - 4);
						deployed = false;
						for (Shortcuts shortcut : shortcuts) {
							if (shortcut.core.matches(sequence)) {
								result = result.substring(0, sequenceStart + 2) + shortcut.core.deploy();
								currentPos = result.length();
								deployed = true;
								break;
							}
						}
						if (!deployed) {
							transcribe(sequence, context);
							for (Addons addon : addons) {
								addon.extension.transcribe(sequence, context);
							}
						}
						if (processQuote) {
							result = result.substring(0, currentPos);
						} else {
							result = result.substring(0, currentPos) + "+'";
						}
						continue;
					}
					if ((shot == '>' || shot == '<' || shot == DOUBLE_QUOTATION || shot == APOSTROPHE || shot == '=')) {
						trim = true;
						if (shot == '>' || shot == '<') {
							contextStart = currentPos;
							if (shot == '<') {
								if (refToDefine != null) {
									Ref nullRef = new RefOnNull(refToDefine.name);
									refs.add(nullRef);
								}
								refToDefine = null;
								valToDefine = null;
								typeToBind = null;
								for (Addons addon : addons) {
									addon.extension.nullify();
								}
							}
						}
						if (shot == APOSTROPHE) {
							if (processQuote) {
								result += "+" + '"' + "'" + '"' + "+'";
								processQuote = false;
							} else {
								result += (char) shot + "+" + '"' + "'" + '"' + "+'";
							}
						} else if (result.length() > 0) {
							if (result.charAt(currentPos) == SPACE) {
								result = result.substring(0, currentPos) + (char) shot;
							} else {
								result += (char) shot;
							}
						}
					} else {
						result += (char) shot;
						gapAdded = false;
						trim = false;
					}
				} else if (!gapAdded && !trim) {
					result += (char) shot;
					gapAdded = true;
				}
			} else if (currentPos >= 0 && result.charAt(currentPos) != SPACE && !trim) {
				result += " ";
			}
		}
		return "'<" + result + "';";
	}

	/**
	 * Writes to a Simplex.js a js code which should be added in order to build a
	 * CRUD set of action for a current component. (To create from JSON object on
	 * page, read from page to JSON, update from JSON to page and delete from page
	 * using JSON).
	 * 
	 * @param out
	 *            - a given writer to write in a Simplex.js file.
	 * @param cookie
	 *            - a string which is compiled with Simplex rules which uses to
	 *            describe an html code for a current component.
	 * @param component
	 *            - a name for a current component.
	 * @throws IOException
	 *             if there are any problems with writing to Simplex.js.
	 */
	public static void write(BufferedWriter out, String cookie, String component) throws IOException {
		out.write(",\n\t" + component + " : {\n\t\tname : \"" + component
				+ "\",\n\t\tspots : [],\n\t\tref : function(prefix, sufix) {\n\t\t\tprefix = prefix + \":" + component
				+ "\";\n");
		out.write("\t\t\t" + buildRefsComposite(refs) + "\n");
		for (Ref ref : refs) {
			if (ref.name.equals("root")) {
				out.write("\t\t\tthis.root = prefix + \":\" + sufix;\n");
			} else {
				out.write("\t\t\tthis." + ref.name + " = prefix + \":" + ref.name + ":\" + sufix;\n");
			}
		}
		out.write("\t\t},\n");
		out.write(
				"\t\tcreate : function(obj, position) {\n\t\t\tvar val = obj;\n\t\t\tvar ref = new this.ref(position, val.id);\n"
						+ "\t\t\tvar container = document.createElement('div');\n" + "\t\t\tcontainer.innerHTML =");
		out.write(cookie);
		out.write("\n\t\t\tdocument.getElementById(position).appendChild(container.firstChild);\n"
				+ "\t\t\tSimplex.setSpot(position, Simplex." + component + ");\n\t\t},\n");
		out.write("\t\tget : function(id, position) {\n\t\t\tif (document.getElementById(position + \":" + component
				+ ":\" + id) == null)\n"
				+ "\t\t\t\treturn;\n\t\t\tvar ref = new this.ref(position, id);\n\t\t\tvar val = {};\n");
		for (Val val : vals.values()) {
			out.write("\t\t\t" + val.pope() + "\n");
		}
		out.write("\t\t\treturn val;\n\t\t},\n");
		out.write("\t\tupdate : function(val, position) {\n\t\t\tif (document.getElementById(position + \":" + component
				+ ":\" + val.id) == null)\n\t\t\t\treturn;\n\t\t\tvar ref = new this.ref(position, val.id);\n");
		for (Ref ref : refs) {
			if (!ref.push().equals("")) {
				out.write("\t\t\t" + ref.push() + "\n");
			}
		}
		out.write("\t\t},\n");
		out.write("\t\tremove : function(id, position) {\n"
				+ "\t\t\tvar element = document.getElementById(position + \":" + component + ":\" + id);\n"
				+ "\t\t\tif (element != null)\n\t\t\t\tdocument.getElementById(position).removeChild(element);\n"
				+ "\t\t\t\tSimplex.freeSpot(position, Simplex." + component + ", false);\n\t\t}\n\t}");
	}

	/**
	 * Compiles a Simplex.js file located in a "files" directory and which provides
	 * an js object which is named Simplex. It consists with CRUD set of action for
	 * each given components. (To create from JSON object a specific component on
	 * page, to read from page a specific component to its JSON format, to update
	 * from JSON to specific component on page and to delete from page using JSON).
	 * Works with ISO 8859-1 HTML files.
	 * 
	 * @param args
	 *            - full name of the components (html files with specific marks for
	 *            a SimplexC) which should be provided with CRUDE set of actions.
	 * @throws IOException
	 *             if anything goes wrong.
	 */
	public static void main(String[] args) throws IOException {
		components = new String[args.length];
		for (int i = 0; i < args.length; i++) {
			int pt = args[i].lastIndexOf('.');
			if (pt != -1) {
				components[i] = args[i].substring(0, pt);
			} else {
				components[i] = args[i];
			}
		}
		File directory = new File("files");
		File output = new File(directory, "Simplex.js");
		BufferedReader in = null;
		try (BufferedWriter out = new BufferedWriter(new FileWriter(output))) {
			output.createNewFile();
			String roof = "";
			for (String component : components) {
				roof += "\t\tcase \"" + component + "\":\n\t\t\tancestor.component = Simplex." + component + ";\n"
						+ "\t\t\tancestor.obj = ancestor.component.get(id, ancestor.position);\n\t\t\treturn ancestor;\n";
			}
			out.write(
					"var Simplex = {\n\tgetAncestor : function(path) {\n\t\tvar id = path.slice(path.lastIndexOf(\":\") + 1);\n"
							+ "\t\tvar ancestor ={};\n\t\tpath = path.slice(0, path.lastIndexOf(\":\"));\n\t\tpath = path.slice(0, path.lastIndexOf(\":\"));\n"
							+ "\t\tancestor.position = path.slice(0, path.lastIndexOf(\":\"));\n\t\tswitch (path.slice(path.lastIndexOf(\":\") + 1)) {\n"
							+ roof + "\t\tdefault:\n\t\t\treturn null;\n\t\t}\n\t},\n"
							+ "\tsetSpot : function(position, component) {\n\t\tvar spot = Simplex.getSpot(position, component);\n"
							+ "\t\tif (spot == null) {\n\t\t\tspot = {\n\t\t\t\tcounter : 1,\n\t\t\t\tid : component.spots.length,\n"
							+ "\t\t\t\tposition : position,\n\t\t\t};\n\t\t\tcomponent.spots.push(spot);\n\t\t} else {\n\t\t\tspot.counter++;\n\t\t}\n\t},\n"
							+ "\tfreeSpot : function(position, component, power) {\n\t\tvar spot = Simplex.getSpot(position, component);\n"
							+ "\t\tif (spot != null && (spot.counter-- == 1 || power)) {\n\t\t\tcomponent.spots.splice(spot.id, 1);\n"
							+ "\t\t\tfor (i = spot.id; i < component.spots.length; i++) {\n\t\t\t\tcomponent.spots[i].id -=1;\n\t\t\t}\n\t\t}\n\t},\n"
							+ "\tgetSpot : function(position, component) {\n\t\tvar i = component.spots.length;\n\t\twhile (i-- > 0) {\n"
							+ "\t\t\tif (component.spots[i].position == position) {\n\t\t\t\treturn component.spots[i];\n\t\t\t}\n\t\t}\n\t\treturn null;\n\t}");
			for (int i = 0; i < args.length; i++) {
				File file = new File(directory, args[i]);
				in = new BufferedReader(new FileReader(file));
				String cookie = read(in, components[i]);
				in.close();
				write(out, cookie, components[i]);
			}
			out.write("\n}");
			System.out.println("A beautiful Simplex was successfully compiled!");
		} catch (IOException e) {
			System.out.println("Sorry about that...");
			e.printStackTrace();
			in.close();
		}
	}

}