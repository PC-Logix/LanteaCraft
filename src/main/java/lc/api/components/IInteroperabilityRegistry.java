package lc.api.components;

import lc.api.jit.AnyPredicate;

/**
 * Interoperability registry interface. Stores all declared interoperability
 * provider at runtime.
 * 
 * @author AfterLifeLochie
 *
 */
public interface IInteroperabilityRegistry {

	/**
	 * Register a deciding predicate for a type.
	 * 
	 * @param type
	 *            The type of interoperability to offer
	 * @param predicate
	 *            The predicate decider
	 */
	public void registerTypePredicate(InteroperabilityType type, AnyPredicate predicate);

	/**
	 * Determine if an object is a predicated interoperable component of the
	 * interoperability type specified.
	 * 
	 * @param type
	 *            The interoperability type
	 * @param zz
	 *            The object
	 * @return If the object is an interoperable component of the type
	 *         specified.
	 */
	public boolean isInstanceOfType(InteroperabilityType type, Object zz);

}
