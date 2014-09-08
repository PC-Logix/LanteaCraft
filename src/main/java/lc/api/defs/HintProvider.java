package lc.api.defs;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface HintProvider {
	String serverClass();

	String clientClass();
}
