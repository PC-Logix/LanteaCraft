package lc.api.components;

import lc.api.jit.AnyPredicate;

public interface IInteroperabilityRegistry {

	public void registerTypePredicate(InteroperabilityType type, AnyPredicate predicate);

	public boolean isInstanceOfType(InteroperabilityType type, Object zz);

}
