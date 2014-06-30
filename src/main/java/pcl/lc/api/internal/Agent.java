/**
 * This file is part of the official LanteaCraft API. Please see the usage guide and
 * restrictions on use in the package-info file.
 */
package pcl.lc.api.internal;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
/**
 * Annotation for flagging an Integration Agent. The Agent provides direct
 * hooks onto other mods.
 * @author AfterLifeLochie
 *
 */
public @interface Agent {
	/**
	 * The name of the mod which must be loaded in runtime for this agent to be
	 * loaded. If the mod is not loaded, the agent will not be run.
	 * 
	 * @return The host mod name.
	 */
	String modname();
}
