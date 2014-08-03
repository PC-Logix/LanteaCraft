package pcl.lc.api.security;

public interface ISecurityProvider {

	/**
	 * <p>
	 * Evaluates a request to perform an action of a specified type on a target.
	 * You must provide a security token which you have been granted, the target
	 * object (for example, a block, tile-entity, entity or player) and the
	 * action to be performed on that target.
	 * </p>
	 * 
	 * @param token
	 *            The security token instance for the actor.
	 * @param target
	 *            The target block, tile entity, entity or player which is the
	 *            subject of the action.
	 * @param action
	 *            The action being performed on the target.
	 * @return An {@link EnumSecurityResult} result indicating if the operation
	 *         is allowed or prohibited. The resulting EnumSecurityResult value
	 *         is <b>final</b>, and should be observed by the callee.
	 */
	public EnumSecurityResult evaluate(ISecurityToken token, Object target, EnumSecurityAction action);

}
