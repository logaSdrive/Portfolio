package shortcuts;

import main.Vocabulary.Deployment;

/**
 * Defines than given inner mark value should be replaced by some js code that
 * sends some nested object from a Simplex object and it (nested object) defines
 * all set of CRUDE action which were binded to the current component. (Uses in
 * cases you need to work with a different components on an abstraction level
 * (like you want to work with purchased coupons and coupon as them will be just
 * a coupons)).
 * 
 * @author AlexanderZhilokov
 *
 */
public class Component implements Deployment {

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
		return sequence.trim().equals("component");
	}

	/**
	 * Deploys the shortcut.
	 */
	@Override
	public String deploy() {
		return "\"Simplex." + component + "\"";
	}

}
