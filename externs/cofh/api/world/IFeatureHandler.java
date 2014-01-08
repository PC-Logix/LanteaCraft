
package cofh.api.world;

/**
 * Provides an interface to allow for the addition of Features to world generation.
 * 
 * See {@link IFeatureGenerator}.
 * 
 * @author King Lemming
 * 
 */
public interface IFeatureHandler {

    public boolean registerFeature(IFeatureGenerator feature);

}
