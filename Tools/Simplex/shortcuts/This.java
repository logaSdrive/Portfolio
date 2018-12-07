package shortcuts;

import main.Vocabulary.Deployment;

/**
 * Defines than given inner mark value should be replaced by some js code that
 * should call to some Simplex object function (get(your id, your position)) to
 * retrieve a JSON representation of a current component. (Uses in cases you
 * need to send current component values by binded to it html representation
 * button).
 * 
 * @author AlexanderZhilokov
 *
 */
public class This implements Deployment {

	/**
	 * Stores a current comoponent's name.
	 */
	private String component = "";

	/**
	 * Sets a current component's name.
	 */
	@Override
	public void initiate(String component) {
		this.component = component;
	}

	/**
	 * Defines than the shortcut should be deployed.
	 */
	@Override
	public boolean matches(String sequence) {
		return sequence.trim().equals("this");
	}

	/**
	 * Deploys the shortcut.
	 */
	@Override
	public String deploy() {
		return "\"Simplex." + component + ".get(\"+val.id+\",\"+\"'\"+position+\"'\"+\")\"";
	}

}
