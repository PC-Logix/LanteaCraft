package pcl.lc.api.security;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

public interface ISecurityTokenIssuer {

	/**
	 * Request an {@link ISecurityToken} token be created for a provided entity,
	 * which may or may not be a player.
	 * 
	 * @param entity
	 *            The entity.
	 * @return An {@link ISecurityToken} instance.
	 */
	public ISecurityToken requestToken(Entity entity);

	/**
	 * Request an {@link ISecurityToken} token be created for a provided
	 * tile-entity.
	 * 
	 * @param tile
	 *            The tile-entity.
	 * @return An {@link ISecurityToken} instance.
	 */
	public ISecurityToken requestToken(TileEntity tile);

	/**
	 * Request an {@link ISecurityToken} token be created for a provided mod
	 * name.
	 * 
	 * @param modName
	 *            The name of the mod requesting the token.
	 * @return An {@link ISecurityToken} instance.
	 */
	public ISecurityToken requestToken(String modName);

}
