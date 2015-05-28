package lc.common.configuration;

import lc.common.configuration.xml.ComponentConfig;

/**
 * Functional interface for objects which require at-runtime configuration,
 * usually passed down by the registering controller. The functional interface
 * is of type <code>configure(ComponentConfig c)</code>.
 * 
 * @author AfterLifeLochie
 *
 */
public interface IConfigure {

	/**
	 * <p>
	 * Called by the controlling object to pass configuration to the object
	 * represented under this interface. The controlling object delegates which
	 * configuration classifier is passed to this underlying object.
	 * </p>
	 * <p>
	 * The underlying object on which this method is invoked may read, write or
	 * modify as needed the configuration which is provided to it.
	 * </p>
	 * 
	 * 
	 * @param c
	 *            The configuration container to be utilized by the underlying
	 *            object.
	 */
	public void configure(ComponentConfig c);

}
