package lc.api.defs;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Hint provider class marker
 *
 * @author AfterLifeLochie
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface HintProvider {
	/** The server class name */
	String serverClass();

	/** The client class name */
	String clientClass();
}
