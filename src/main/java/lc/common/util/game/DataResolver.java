package lc.common.util.game;

import lc.api.defs.IContainerDefinition;
import lc.api.defs.IGameDef;
import lc.common.LCLog;
import lc.common.impl.registry.DefinitionReference;
import net.minecraft.item.ItemStack;

public class DataResolver {

	public static ItemStack resolve(Object val) {
		if (val instanceof DefinitionReference) {
			DefinitionReference reference = (DefinitionReference) val;
			IGameDef def = reference.reference();
			if (def == null) {
				LCLog.fatal("Invalid reference, cannot resolve recipe.");
				return null;
			}
			Object[] params = reference.parameters();
			Integer count = null, metadata = null;
			if (params != null) {
				if (params.length >= 1)
					count = (Integer) params[0];
				if (params.length == 2)
					metadata = (Integer) params[1];
			}
			if (def instanceof IContainerDefinition) {
				IContainerDefinition blockItemDef = (IContainerDefinition) def;
				if (blockItemDef.getBlock() != null)
					return new ItemStack(blockItemDef.getBlock(), count == null ? 1 : count, metadata == null ? 0
							: metadata);
				else if (blockItemDef.getItem() != null)
					return new ItemStack(blockItemDef.getItem(), count == null ? 1 : count, metadata == null ? 0
							: metadata);
			} else {
				LCLog.fatal("Unsupported definition type %s.", def.getClass().getName());
				return null;
			}
		}
		LCLog.fatal("Cannot resolve object of type %s into ItemStack.", val.getClass().getName());
		return null;
	}

}
