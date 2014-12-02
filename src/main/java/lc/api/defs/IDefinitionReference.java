package lc.api.defs;

/**
 * Acts as a container for a reference to a formal game definition. Used
 * particularly when code is reliant on a definition being formalized earlier
 * than formalization actually occurs.
 * 
 * @author AfterLifeLochie
 *
 */
public interface IDefinitionReference {

	/**
	 * The game definition object referenced by this reference.
	 * 
	 * @return The referenced reference.
	 */
	public IGameDef reference();

	/**
	 * The reference parameters. The holder of the reference should choose how
	 * to interpret these parameters; there is no standard.
	 * 
	 * @return The reference parameters.
	 */
	public Object[] parameters();

	/**
	 * Push a particular parameter value onto the parameter stack at a
	 * particular index.
	 * 
	 * @param i
	 *            The index
	 * @param v
	 *            The parameter
	 * @return The self object.
	 */
	public IDefinitionReference push(int i, Object v);

	/**
	 * Push a list of parameters onto the end of the parameter stack.
	 * 
	 * @param paramList
	 *            The parameters
	 * @return The self object.
	 */
	public IDefinitionReference pushAll(Object... paramList);

	/**
	 * Copies the reference and it's parameters (shallow copy).
	 * 
	 * @return The copy of the reference.
	 */
	public IDefinitionReference copy();

}
