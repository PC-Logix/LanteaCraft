package lc.common.impl.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import lc.api.components.IInteroperabilityRegistry;
import lc.api.components.InteroperabilityType;
import lc.api.jit.AnyPredicate;

/**
 * Interoperability registry implementation
 * 
 * @author AfterLifeLochie
 *
 */
public class InteroperabilityRegistry implements IInteroperabilityRegistry {

	private HashMap<InteroperabilityType, ArrayList<AnyPredicate>> map = new HashMap<InteroperabilityType, ArrayList<AnyPredicate>>();

	@Override
	public void registerTypePredicate(InteroperabilityType type, AnyPredicate predicate) {
		if (!map.containsKey(type))
			map.put(type, new ArrayList<AnyPredicate>());
		if (!map.get(type).contains(predicate))
			map.get(type).add(predicate);
	}

	@Override
	public boolean isInstanceOfType(InteroperabilityType type, Object zz) {
		if (!map.containsKey(type))
			return false;
		Iterator<AnyPredicate> ix = map.get(type).iterator();
		while (ix.hasNext())
			if (ix.next().test(new Object[] { zz }))
				return true;
		return false;
	}

}
