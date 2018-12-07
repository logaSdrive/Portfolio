package database;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import exceptions.JabberwockyException;

/**
 * Able to execute the given sql through the given connection, and if necessary
 * able to write into the database from the given Collection or to write from
 * the database into the given Collection as the given sql says. (All objects in
 * the given collections should be the same and should be given an adapter which
 * implements the "GreatWork" interface as a tool to work with them). Also
 * Jabberwocky knows to combine these actions with some simple restrictions.
 * 
 * @author AlexanderZhilokov
 *
 */
public class Jabberwocky {

	/**
	 * Uses to define an abstraction level to all the setters methods for the
	 * classes for which you want to create an adapter - some class which implements
	 * the "GreatWork" interface.
	 * 
	 * @author AlexanderZhilokov
	 *
	 */
	public interface Add {

		/**
		 * Able to set the string value from ResultSet.getString(..) into the specific
		 * attribute of an object under the current workflow.
		 * 
		 * @param value
		 *            - a value from the ResultSet.getString(..) which meant to be set
		 *            as a new value for corresponding attribute of the specific object.
		 */
		void add(String value);
	}

	/**
	 * Uses to define an abstraction level to all the getters methods for the
	 * classes for which you want to create an adapter - some class which implements
	 * the "GreatWork" interface.
	 * 
	 * @author AlexanderZhilokov
	 *
	 */
	public interface Pop {

		/**
		 * Able to get the specific attribute from an object which is under the current
		 * workflow: should returns a string which contains the corresponding attribute
		 * value and which can be used by prepared statement:
		 * preparedStatement.setString(.., ...pop()).
		 * 
		 * @return the corresponding attribute value and which can be used by prepared
		 *         statement: preparedStatement.setString(.., ...pop()).
		 */
		String pop();
	}

	/**
	 * Concept for the abstraction is simple: if you want to implements the
	 * "GreatWork" (to become an adapter for the some class of objects) you should
	 * create a container which can store a objects from the desired class and you
	 * should provide access to the stored object attributes via arrays of setters
	 * and getters which elements implements interfaces Add and Pop.
	 * 
	 * @author AlexanderZhilokov
	 *
	 */
	public interface GreateWork {

		/**
		 * Defines an abstraction level for access to whole attributes of a specific
		 * object: each element of the returning array should be able to set the
		 * specific attribute into an object which is under the current workflow through
		 * a string value and this string value meant to be received lately from
		 * ResutlSet: ResultSet.getString(..).
		 * 
		 * @return an array of setters for all the attributes of the object which is
		 *         under the current workflow.
		 */
		Add[] drawSetters();

		/**
		 * Defines an abstraction level for access to whole attributes of a specific
		 * object: each element of the returning array should be able to get the
		 * specific attribute from an object which is under the current workflow as a
		 * string value and this string value meant to be used lately by prepared
		 * statement: preparedStatement.setString(.., ...pop()).
		 * 
		 * @return an array of getters for all the attributes of the object which is
		 *         under the current workflow.
		 */
		Pop[] drawGetters();

		/**
		 * Initiates a new object to a current workflow.
		 */
		void newBean();

		/**
		 * Adds the given object to a current workflow
		 * 
		 * @param bean
		 *            - the object to set to a current workflow.
		 */
		void setBean(Object bean);

		/**
		 * Brings back the object which is under a current workflow
		 * 
		 * @return the object which is under a current workflow.
		 */
		Object getBean();
	}

	/**
	 * Provides the frame for Jabberwocky to operate.
	 * 
	 * @author AlexanderZhilokov
	 *
	 */
	public static class Nonsense {

		/**
		 * Defines a condition which brings Jabberwocky to stop an actions immediately
		 * and when notify the caller.
		 * 
		 * @author AlexanderZhilokov
		 *
		 */
		private interface Notify {

			/**
			 * Checks if there is a condition to stop and when notify the caller.
			 * 
			 * @param rept
			 *            - value which is represents the current condition of the operation
			 *            flow.
			 * @throws JabberwockyException
			 *             - if it is time to break the current operation flow.
			 * @throws SQLException
			 *             - if there is any problems with a connection to the database.
			 */
			void lookAt(int rept) throws JabberwockyException, SQLException;
		}

		/**
		 * Defines a meaning to the operation which meant to be executed with the given
		 * Collection of objects.
		 * 
		 * @author AlexanderZhilokov
		 *
		 */
		private interface MakeSense {

			/**
			 * Executes set of the operations with the Collection of objects which was given
			 * early and with the given instance of the "GreatWork" interface and with
			 * others parameters if they are needed. (Must be given to Jabberwocky some
			 * Collection of an objects before you invoke it).
			 * 
			 * @param adapter
			 *            - some instance which is implements the 'GreatWork' for the
			 *            objects from the Collection which was given early.
			 * @param parametres
			 *            - integer values which will be used by prepareStatement if needed:
			 *            loop on each parameter by order they was given {
			 *            PreparedStatement.setString(the current parameter order + 1, Pop[]
			 *            adapter.drawGetters[ the value of the current parameter]}).
			 * @return the current frame in which Jabberwocky operates.
			 * @throws SQLException
			 *             if there are any problems with connection to the database.
			 * @throws JabberwockyException
			 *             if there is a condition to stop and notify the caller.
			 */
			Nonsense soundly(GreateWork adapter, int... parametres) throws SQLException, JabberwockyException;
		}

		/**
		 * Marks the sql's index from the array of 'sql's to execute' (see String[] sql)
		 * as index for operations with the given Collection.
		 */
		private final static int MAIN = 0;

		/**
		 * Uses as a hash code prefix for the report value than there is a condition to
		 * notify a caller. (Only method Nonsence.pt() uses it)
		 */
		private final static int PT = 1;

		/**
		 * Responsible for checks if there is a condition to stop and to notify.
		 */
		private Notify validator;

		/**
		 * Could be a mixture of the three verbs 'bleat', 'murmur', and 'warble'. In a
		 * simple words: it is meant to be defined by desirable operation with the given
		 * Collection of an objects.
		 */
		private MakeSense burble;

		/**
		 * Stores the connection to the database.
		 */
		private Connection con;

		/**
		 * Is a creation that has been given much critical praise. Should able to
		 * provides the tools to work with objects from the given Collection.
		 */
		private GreateWork magnumOpus;

		/**
		 * Marks if Jabberwocky should returns the database to the previous state if the
		 * operations were not fully executed.
		 */
		private boolean rollback;

		/**
		 * Stores the operations sql's which meant to be executed.
		 */
		private String[] sql;

		/**
		 * Stores the Collection of an objects to work with.
		 */
		private Collection<Object> beans;

		/**
		 * Creates a new frame in which Jabberwocky will operate.
		 * 
		 * @param con
		 *            - a connection to the database to work with.
		 */
		private Nonsense(Connection con) {
			validator = (rept) -> {
			};
			burble = (adapter, parametres) -> {
				System.out.println("And as in uffish thought he stood,\n" + "The Jabberwock, with eyes of flame,\n"
						+ "Came whiffling through the tulgey wood,\n" + "And burbled as it came!\n (c)'Lewis Carroll'");
				return this;
			};
			this.con = con;
		}

		/**
		 * Indicates to Jabberwocky that it should write the objects to the database
		 * (Create, Update) and gives him a Collection of an objects to work with.
		 * 
		 * @param listToUse
		 *            - Collection of the objects to write into the database.
		 * @return the current frame.
		 */
		public Nonsense from(Collection<Object> listToUse) {
			beans = listToUse;
			burble = (adapter, parametres) -> {
				magnumOpus = adapter;
				int iterationCounter = 0;
				try (PreparedStatement stmt = con.prepareStatement(sql[MAIN])) {
					Pop[] getters;
					for (Object bean : beans) {
						magnumOpus.setBean(bean);
						getters = magnumOpus.drawGetters();
						for (int i = 0; i < parametres.length; i++) {
							stmt.setString(i + 1, getters[parametres[i]].pop());
						}
						validator.lookAt(stmt.executeUpdate());
						iterationCounter++;
					}
				} catch (JabberwockyException e) {
					e.rept = iterationCounter;
					throw e;
				}
				return this;
			};
			return this;
		}

		/**
		 * Indicates to Jabberwocky that it should read the objects from the database
		 * (Read) and gives him a Collection of an objects to work with.
		 * 
		 * @param listToFill
		 *            - Collection of the objects to fill of.
		 * @return the current frame.
		 */
		public Nonsense into(Collection<Object> listToFill) {
			beans = listToFill;
			burble = (adapter, parametres) -> {
				magnumOpus = adapter;
				int iterationCounter = 0;
				try (Statement stmt = con.createStatement()) {
					ResultSet rs = stmt.executeQuery(sql[MAIN]);
					Add[] setters;
					while (rs.next()) {
						magnumOpus.newBean();
						setters = magnumOpus.drawSetters();
						for (int i = 0; i < setters.length; i++) {
							setters[i].add(rs.getString(i + 1));
						}
						beans.add(magnumOpus.getBean());
						iterationCounter++;
					}
					validator.lookAt(iterationCounter);
				}
				return this;
			};
			return this;
		}

		/**
		 * Sets the operations sql's which meant to be executed.
		 * 
		 * @param sql
		 *            - bunch of the sql's operations which meant to be executed. Note
		 *            that if you want to Jabberwocky to execute operations with a
		 *            Collection of an objects - you should deliver only one parameter
		 *            as sql. Contrary, if you want to Jabberwocky execute bunch of
		 *            operations without using any data from the Collection of an
		 *            objects - you can set as any sql's parameters as you wish.
		 * @return the current frame.
		 */
		public Nonsense execute(String... sql) {
			this.sql = sql;
			return this;
		}

		/**
		 * Actually executes desirable operation with the Collection of objects which
		 * was given to Jabberwocky early in the current frame. (Must be given to
		 * Jabberwocky some Collection of an objects before you invoke it).
		 * 
		 * @param adapter
		 *            - should able to provides the tools to work with objects from the
		 *            given Collection.
		 * @param parametres
		 *            - integer values which will be used by prepareStatement if needed:
		 *            will used in a loop on each parameter by order they was given {
		 *            PreparedStatement.setString(the current parameter order + 1, Pop[]
		 *            adapter.drawGetters[ the value of the current parameter]}).
		 * @return the current frame.
		 * @throws SQLException
		 *             if there are any connection problems.
		 */
		public Nonsense using(GreateWork adapter, int... parametres) throws SQLException {
			burble.soundly(adapter, parametres);
			return this;
		}

		/**
		 * Actually executes desirable operations if they meant to be executed without
		 * the use of any Collection of an objects.
		 * 
		 * @return the current frame.
		 * @throws SQLException
		 *             if there are any connection problems.
		 * @throws JabberwockyException
		 *             if there is a condition to stop and notify the caller.
		 */
		public Nonsense pt() throws SQLException, JabberwockyException {
			int sign = 0;
			try (Statement stmt = con.createStatement()) {
				while (sign < sql.length) {
					validator.lookAt(stmt.executeUpdate(sql[sign]));
					sign++;
				}
			} catch (JabberwockyException e) {
				sign += PT;
				e.rept = -sign;
				throw e;
			}
			return this;
		}

		/**
		 * Sets the simple condition to stop and notify the caller (for the current
		 * frame): if there are not any rows in the database which were affected or if
		 * there is any object from the given Collection which was not delivered into
		 * the database or if there is not any object from the database was delivered
		 * into the given Collection - it will make Jabberwocky to throw the
		 * JabberockyException with corresponding value of a rept attribute (for details
		 * see JabberwockyException).
		 * 
		 * @return the current frame.
		 */
		public Nonsense toTheTick() {
			validator = (rept) -> {
				if (rept == 0) {
					if (rollback) {
						con.rollback();
						con.setAutoCommit(true);
						rollback = false;
					}
					throw new JabberwockyException();
				}
			};
			return this;
		}

		/**
		 * Nullifies all the previously setted conditions to stop and and notify the
		 * caller.
		 * 
		 * @return the current frame.
		 */
		public Nonsense asItIs() {
			validator = (rept) -> {
			};
			return this;
		}

		/**
		 * Indicates to Jabberwocky that it should returns the database to the previous
		 * state if operations were not fully executed. Note that in a case SQLException
		 * (in all current frame) it is on your side to reset the status of the
		 * connection (which was given to Jabberwocky to work with) if needed.
		 * 
		 * @return the current frame.
		 * @throws SQLException
		 *             if there is a problem to disable auto-commit for the connection
		 *             which was given early.
		 */
		public Nonsense onTheRun() throws SQLException {
			con.setAutoCommit(false);
			rollback = true;
			return this;
		}

		/**
		 * Indicates to Jabberwocky that it should commit all the changes from the
		 * operations which was previously completed.
		 * 
		 * @return the current frame.
		 * @throws SQLException
		 *             if there is any problem to commit the changes to the database.
		 */
		public Nonsense fin() throws SQLException {
			con.commit();
			con.setAutoCommit(true);
			rollback = false;
			return this;
		}
	}

	/**
	 * Invokes a Jabberwocky and provides a reference to the frame which it
	 * operates.
	 * 
	 * @param con
	 *            - the connection to the database to work with.
	 * @return the current frame.
	 */
	public static Nonsense via(Connection con) {
		return new Nonsense(con);
	}

}