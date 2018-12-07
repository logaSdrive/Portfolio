package exceptions;

/**
 * Stores a data about some common exception which can arise during the
 * Jabberwocky work.
 * 
 * @author AlexanderZhilokov
 *
 */
public class JabberwockyException extends RuntimeException {

	/**
	 * Needed for compiler to stop yelling at me.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Stores some integer representation of the conditions where from exception
	 * arises. If equals to zero: means there are not any objects from or into the
	 * database were delivered into or from the Collection which was given to
	 * Jabberwocky to work with. If rept greater than zero: means there is an object
	 * from the given Collection which was not delivered into the database and the
	 * value of the rept corresponds with the ordinal number of the object in the
	 * Collection. (More precisely there are all objects which ordinal numbers are
	 * greater or equals than this number were not delivered as well). If less than
	 * zero: means there are not any rows in the database which were affected by the
	 * others operations (which are not meant to use any objects from the given
	 * Collection). And rept actual value than will be equals to the orders of the
	 * specific operation which caused the exception multiplied by -1. (As it
	 * (operation) was delivered into the method Jabberwocky.execute(...String).)
	 */
	public int rept;

	/**
	 * Creates an instance of the class JabberwockyException.
	 */
	public JabberwockyException() {
	}

}