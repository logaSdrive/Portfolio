package shortcuts;

import main.Vocabulary.Deployment;

/**
 * Defines than given inner mark value should be replaced by some js code that
 * should call to some Simplex object function (getAncestor(your position)) to
 * retrieve a current component direct parent. (Uses in cases nested components,
 * one into another).
 * 
 * @author AlexanderZhilokov
 *
 */
public class Super implements Deployment {

	/**
	 * Simply here cauze of need to meet the demand the Deployment interface
	 * implementation.
	 */
	@Override
	public void initiate(String component) {
	}

	/**
	 * Defines than the shortcut should be deployed.
	 */
	@Override
	public boolean matches(String sequence) {
		return sequence.trim().equals("super");
	}

	/**
	 * Deploys the shortcut.
	 */
	@Override
	public String deploy() {
		return "\"Simplex.getAncestor(\"+\"'\"+position+\"'\"+\")\"";
	}

}
