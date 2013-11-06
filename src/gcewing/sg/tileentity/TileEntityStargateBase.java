//------------------------------------------------------------------------------------------------
//
//   SG Craft - Stargate base tile entity
//
//------------------------------------------------------------------------------------------------

package gcewing.sg.tileentity;

import gcewing.sg.SGCraft;
import gcewing.sg.SGCraft.Items;
import gcewing.sg.base.BaseChunkLoadingTE;
import gcewing.sg.base.BaseConfiguration;
import gcewing.sg.base.BaseTEChunkManager;
import gcewing.sg.blocks.BlockStargateBase;
import gcewing.sg.core.GateAddressHelper;
import gcewing.sg.core.EnumIrisState;
import gcewing.sg.core.WorldLocation;
import gcewing.sg.core.EnumStargateState;
import gcewing.sg.render.tileentity.TileEntityStargateBaseRenderer;
import gcewing.sg.util.Trans3;
import gcewing.sg.util.Utils;
import gcewing.sg.util.Vector3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.network.packet.Packet41EntityEffect;
import net.minecraft.network.packet.Packet43Experience;
import net.minecraft.network.packet.Packet9Respawn;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import cpw.mods.fml.common.Optional.Interface;
import cpw.mods.fml.common.Optional.InterfaceList;
import cpw.mods.fml.common.Optional.Method;
import cpw.mods.fml.common.registry.GameRegistry;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.ILuaContext;
import dan200.computer.api.IPeripheral;

@InterfaceList({ @Interface(iface = "dan200.computer.api.IPeripheral", modid = "ComputerCraft") })
public class TileEntityStargateBase extends BaseChunkLoadingTE implements IInventory, IPeripheral {

	public final static String symbolChars = GateAddressHelper.symbolChars;
	public final static int numRingSymbols = GateAddressHelper.numSymbols;
	public final static double ringSymbolAngle = 360.0 / numRingSymbols;

	final static int diallingTime = 40; // ticks
	final static int interDiallingTime = 10; // ticks
	final static int transientDuration = 20; // ticks
	final static int disconnectTime = 30; // ticks

	final static double openingTransientIntensity = 1.3;
	final static double openingTransientRandomness = 0.25;
	final static double closingTransientRandomness = 0.25;
	final static double transientDamageRate = 50;

	public static int powerLevel = 0;

	static int gateOpeningsPerFuelItem = 24;
	static int minutesOpenPerFuelItem = 80;
	static int secondsToStayOpen = 5 * 60;
	static boolean oneWayTravel = false;
	static boolean closeFromEitherEnd = true;

	static int fuelPerItem;
	public static int maxFuelBuffer;
	static int fuelToOpen;
	static int ticksToStayOpen;
	final static int irisTimerVal = 2;

	static Random random = new Random();
	static DamageSource transientDamage = new TransientDamageSource();
	static DamageSource irisDamage = new irisDamageSource();
	static DamageSource recieveDamage = new recieveDamageSource();

	public EnumIrisState irisVarState;
	public String irisType = "iris";
	public int irisSlide;
	private int irisTimer;

	boolean hasSetChunkZone = false;

	public boolean isMerged;
	public EnumStargateState state = EnumStargateState.Idle;
	public double ringAngle, lastRingAngle, targetRingAngle; // degrees
	public int numEngagedChevrons;
	public String dialledAddress = "";
	// public String dialledAddress = "MYNCRFT"; // "AAAAAAA";
	public boolean isLinkedToController;
	public int linkedX, linkedY, linkedZ;
	WorldLocation connectedLocation;
	boolean isInitiator;
	int timeout;
	public int fuelBuffer;

	public IComputerAccess m_computer;

	IInventory inventory = new InventoryBasic("Stargate", false, 4);
	final static int fuelSlot = 0;

	double ehGrid[][][];

	public static void configure(BaseConfiguration cfg) {
		gateOpeningsPerFuelItem = cfg.getInteger("stargate", "gateOpeningsPerFuelItem", gateOpeningsPerFuelItem);
		minutesOpenPerFuelItem = cfg.getInteger("stargate", "minutesOpenPerFuelItem", minutesOpenPerFuelItem);
		secondsToStayOpen = cfg.getInteger("stargate", "secondsToStayOpen", secondsToStayOpen);
		oneWayTravel = cfg.getBoolean("stargate", "oneWayTravel", oneWayTravel);
		closeFromEitherEnd = cfg.getBoolean("stargate", "closeFromEitherEnd", closeFromEitherEnd);
		fuelPerItem = minutesOpenPerFuelItem * 60 * 20;
		maxFuelBuffer = 2 * fuelPerItem;
		fuelToOpen = fuelPerItem / gateOpeningsPerFuelItem;
		ticksToStayOpen = 20 * secondsToStayOpen;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getAABBPool().getAABB(xCoord - 2, yCoord, zCoord - 2, xCoord + 3, yCoord + 5, zCoord + 3);
	}

	@Override
	public boolean isInvNameLocalized() {
		return false;
	}

	@Override
	public IInventory getInventory() {
		return inventory;
	}

	// @Override
	// public boolean isStackValidForSlot(int slot, ItemStack stack) {
	// return true;
	// }

	/*
	 * public String getIrisType() { ItemStack is = getStackInSlot(4); if(is !=
	 * null) { if(is.getItem() instanceof SGDarkMultiItem) { SGDarkMultiItem mI
	 * = (SGDarkMultiItem)is.getItem();
	 * if(mI.isUpgradeType("Stargate Upgrade - Iris",is)) { return "Iris"; }
	 * else if(mI.isUpgradeType("Stargate Upgrade - Shield",is)) { return
	 * "Shield"; } } } IrisStateFromNum(0); return null; }
	 * 
	 * public int IrisStateToNum() { switch(irisVarState) { case Open:return 0;
	 * case Closing:return 1; case Closed:return 2; case Opening:return 3;
	 * default: return 0; } }
	 * 
	 * public void IrisStateFromNum(int num) { if(num ==
	 * 0)irisVarState=SGIrisState.Open; if(num ==
	 * 1)irisVarState=SGIrisState.Closing; if(num ==
	 * 2)irisVarState=SGIrisState.Closed; if(num ==
	 * 3)irisVarState=SGIrisState.Opening; markBlockForUpdate(); }
	 * 
	 * public String irisState() {
	 * //System.out.printf("SGBaseTE Iris State - %d\n", irisVarState);
	 * if(getIrisType() == null) { return "Error - No Iris"; } else {
	 * if(IrisStateToNum() == 0) return "Iris - Open"; else if(IrisStateToNum()
	 * == 1) return "Iris - Closing"; else if(IrisStateToNum() == 2) return
	 * "Iris - Closed"; else if(IrisStateToNum() == 3) return "Iris - Opening";
	 * } return "Error - Unknown state"; }
	 * 
	 * public String openIris() { String IT = getIrisType(); if(IT != null) {
	 * if(IrisStateToNum() == 2) { if(IT == "Iris") { IrisStateFromNum(3);
	 * irisSlide = 0; irisTimer = irisTimerVal; } else if(IT == "Shield") {
	 * IrisStateFromNum(0); } return "Iris opened"; } else if(IrisStateToNum()
	 * == 1 || IrisStateToNum() == 3) { return "Error - Iris in motion"; } }
	 * return "Error - No iris"; }
	 * 
	 * public String closeIris() { String IT = getIrisType(); if(IT != null) {
	 * if(IrisStateToNum() == 0) { if(IT == "Iris") { IrisStateFromNum(1);
	 * irisSlide = SGExtensions.irisFrames - 1; irisTimer = irisTimerVal; } else
	 * if(IT == "Shield") { IrisStateFromNum(2); } return "Iris closed"; } else
	 * if(IrisStateToNum() == 1 || IrisStateToNum() == 3) { return
	 * "Error - Iris in motion"; }
	 * 
	 * } return "Error - No iris"; }
	 * 
	 * public String toggleIris() { String IT = getIrisType(); if(IT != null) {
	 * if(irisState() == "Iris - Open") { return closeIris(); } else
	 * if(irisState() == "Iris - Closed") { return openIris(); } else { return
	 * "Error - Iris moving"; } } return "Error - No iris"; }
	 */

	public static TileEntityStargateBase at(IBlockAccess world, int x, int y, int z) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te instanceof TileEntityStargateBase)
			return (TileEntityStargateBase) te;
		else
			return null;
	}

	public static TileEntityStargateBase at(WorldLocation loc) {
		if (loc != null) {
			World world = /* DimensionManager. */GateAddressHelper.getWorld(loc.dimension);
			if (world != null)
				return TileEntityStargateBase.at(world, loc.x, loc.y, loc.z);
		}
		return null;
	}

	public static TileEntityStargateBase at(IBlockAccess world, NBTTagCompound nbt) {
		return TileEntityStargateBase.at(world, nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z"));
	}

	public int dimension() {
		if (worldObj != null)
			return worldObj.provider.dimensionId;
		else
			return -999;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		isMerged = nbt.getBoolean("isMerged");
		state = EnumStargateState.valueOf(nbt.getInteger("state"));
		targetRingAngle = nbt.getDouble("targetRingAngle");
		numEngagedChevrons = nbt.getInteger("numEngagedChevrons");
		// homeAddress = nbt.getString("homeAddress");
		dialledAddress = nbt.getString("dialledAddress");
		isLinkedToController = nbt.getBoolean("isLinkedToController");
		linkedX = nbt.getInteger("linkedX");
		linkedY = nbt.getInteger("linkedY");
		linkedZ = nbt.getInteger("linkedZ");
		// irisVarState = SGIrisState.valueOf(nbt.getInteger("irisState"));
		// irisSlide = nbt.getInteger("irisSlide");
		if (nbt.hasKey("connectedLocation"))
			connectedLocation = new WorldLocation(nbt.getCompoundTag("connectedLocation"));
		isInitiator = nbt.getBoolean("isInitiator");
		timeout = nbt.getInteger("timeout");
		fuelBuffer = nbt.getInteger("fuelBuffer");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setBoolean("isMerged", isMerged);
		nbt.setInteger("state", state.ordinal());
		nbt.setDouble("targetRingAngle", targetRingAngle);
		// nbt.setInteger("irisState", irisVarState.ordinal());
		// nbt.setInteger("irisSlide", irisSlide);
		nbt.setInteger("numEngagedChevrons", numEngagedChevrons);
		// nbt.setString("homeAddress", homeAddress);
		nbt.setString("dialledAddress", dialledAddress);
		nbt.setBoolean("isLinkedToController", isLinkedToController);
		nbt.setInteger("linkedX", linkedX);
		nbt.setInteger("linkedY", linkedY);
		nbt.setInteger("linkedZ", linkedZ);
		if (connectedLocation != null)
			nbt.setCompoundTag("connectedLocation", connectedLocation.toNBT());
		nbt.setBoolean("isInitiator", isInitiator);
		nbt.setInteger("timeout", timeout);
		nbt.setInteger("fuelBuffer", fuelBuffer);
	}

	public NBTTagCompound nbtWithCoords() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("x", xCoord);
		nbt.setInteger("y", yCoord);
		nbt.setInteger("z", zCoord);
		return nbt;
	}

	public static boolean isValidSymbolChar(String c) {
		return GateAddressHelper.isValidSymbolChar(c);
	}

	public static char symbolToChar(int i) {
		return GateAddressHelper.symbolToChar(i);
	}

	public static int charToSymbol(char c) {
		return GateAddressHelper.charToSymbol(c);
	}

	public static int charToSymbol(String c) {
		return GateAddressHelper.charToSymbol(c);
	}

	public String getHomeAddress() throws GateAddressHelper.AddressingError {
		return GateAddressHelper.addressForLocation(new WorldLocation(this));
	}

	public BlockStargateBase getBlock() {
		return (BlockStargateBase) getBlockType();
	}

	public int getRotation() {
		// return getBlockMetadata() & SGBaseBlock.rotationMask;
		return getBlock().rotationInWorld(getBlockMetadata(), this);
	}

	public double interpolatedRingAngle(double t) {
		return Utils.interpolateAngle(lastRingAngle, ringAngle, t);
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void updateEntity() {
		if (worldObj.isRemote) {
			clientUpdate();
			if (!hasSetChunkZone) {
				setForcedChunkRange(-1, -1, 1, 1);
				hasSetChunkZone = true;
			}
		} else {
			serverUpdate();
			checkForEntitiesInPortal();
			if (!hasSetChunkZone) {
				setForcedChunkRange(-1, -1, 1, 1);
				hasSetChunkZone = true;
			}
			// performPendingRemounts();
		}
	}

	String side() {
		return worldObj.isRemote ? "Client" : "Server";
	}

	void enterState(EnumStargateState newState, int newTimeout) {
		// System.out.printf("SGBaseTE: %s entering state %s with timeout %s\n",
		// side(), newState, newTimeout);
		state = newState;
		timeout = newTimeout;
		// System.out.println("enterState " + isInitiator + " " + newState);
		if (state == EnumStargateState.Dialling || state == EnumStargateState.Connected
				|| state == EnumStargateState.InterDialling || state == EnumStargateState.Transient) {
			if (!isInitiator)
				powerLevel = 15;
			else
				powerLevel = 0;
		} else if (state == EnumStargateState.Disconnecting || state == EnumStargateState.Idle)
			powerLevel = 0;
		worldObj.notifyBlockChange(xCoord, yCoord, zCoord, blockType.blockID);
		onInventoryChanged();
		markBlockForUpdate();

	}

	public boolean isConnected() {
		return state == EnumStargateState.Transient || state == EnumStargateState.Connected
				|| state == EnumStargateState.Disconnecting;
	}

	TileEntityStargateController getLinkedControllerTE() {
		if (isLinkedToController) {
			TileEntity cte = worldObj.getBlockTileEntity(linkedX, linkedY, linkedZ);
			if (cte instanceof TileEntityStargateController)
				return (TileEntityStargateController) cte;
		}
		return null;
	}

	public void checkForLink() {
		int range = TileEntityStargateController.linkRangeZ;
		for (int i = -range; i <= range; i++)
			for (int j = -range; j <= range; j++)
				for (int k = -range; k <= range; k++) {
					TileEntity te = worldObj.getBlockTileEntity(xCoord + i, yCoord + j, zCoord + k);
					if (te instanceof TileEntityStargateController)
						((TileEntityStargateController) te).checkForLink();
				}
	}

	public void unlinkFromController() {
		if (isLinkedToController) {
			TileEntityStargateController cte = getLinkedControllerTE();
			if (cte != null)
				cte.clearLinkToStargate();
			clearLinkToController();
		}
	}

	public void clearLinkToController() {
		// System.out.printf("SGBaseTE: Unlinking stargate at (%d, %d, %d) from controller\n",
		// xCoord, yCoord, zCoord);
		isLinkedToController = false;
		// markBlockForUpdate();
		onInventoryChanged();
	}

	// ------------------------------------ Server
	// --------------------------------------------

	public void connectOrDisconnect(String address, EntityPlayer player) {
		// System.out.printf("SGBaseTE: %s: connectOrDisconnect('%s') in state %s by %s\n",
		// side(), address, state, player);
		if (state == EnumStargateState.Idle) {
			if (address.length() == GateAddressHelper.addressLength)
				connect(address, player);
		} else {
			boolean canDisconnect = isInitiator || closeFromEitherEnd;
			TileEntityStargateBase dte = getConnectedStargateTE();
			boolean validConnection = dte != null && dte.getConnectedStargateTE() == this;
			if (canDisconnect || !validConnection) {
				if (state != EnumStargateState.Disconnecting)
					disconnect();
			} else if (!canDisconnect) {
			}
			// System.out.printf("SGBaseTE.connectOrDisconnect: Not initiator\n");
		}
	}

	void connect(String address, EntityPlayer player) {
		String homeAddress = findHomeAddress();
		TileEntityStargateBase dte = GateAddressHelper.findAddressedStargate(address);
		// System.out.printf("SGBaseTE.connect: addressed TE = %s\n", dte);
		if (dte == null) {
			diallingFailure(player, "No stargate at address " + address);
			return;
		}
		if (dte == this) {
			diallingFailure(player, "Stargate cannot connect to itself\n");
			return;
		}
		// System.out.printf("SGBaseTE.connect: addressed TE state = %s\n",
		// dte.state);
		if (dte.state != EnumStargateState.Idle) {
			diallingFailure(player, "Stargate at address " + address + " is busy");
			return;
		}
		if (!reloadFuel(fuelToOpen)) {
			diallingFailure(player, "Stargate has insufficient fuel");
			return;
		}
		startDiallingStargate(address, dte, true);
		dte.startDiallingStargate(homeAddress, this, false);
	}

	void diallingFailure(EntityPlayer player, String mess) {
		player.addChatMessage(mess);
		playSoundEffect("gcewing_sg:sg_abort", 1.0F, 1.0F);
	}

	String findHomeAddress() {
		String homeAddress;
		try {
			return getHomeAddress();
		} catch (GateAddressHelper.AddressingError e) {
			// System.out.printf("SGBaseTE.findHomeAddress: %s\n", e);
			return "";
		}
	}

	public void disconnect() {
		// System.out.printf("SGBaseTE: %s: disconnect()\n", side());
		TileEntityStargateBase dte = TileEntityStargateBase.at(connectedLocation);
		if (dte != null)
			dte.clearConnection();
		clearConnection();
	}

	public void clearConnection() {
		if (state != EnumStargateState.Idle || connectedLocation != null) {
			// System.out.printf("SGBaseTE.clearConnection: Resetting state\n");
			dialledAddress = "";
			connectedLocation = null;
			isInitiator = false;
			numEngagedChevrons = 0;
			onInventoryChanged();
			markBlockForUpdate();
			if (state == EnumStargateState.Connected) {
				enterState(EnumStargateState.Disconnecting, disconnectTime);
				// sendClientEvent(SGEvent.StartDisconnecting, 0);
				playSoundEffect("gcewing_sg:sg_close", 1.0F, 1.0F);
			} else {
				if (state != EnumStargateState.Idle && state != EnumStargateState.Disconnecting)
					playSoundEffect("gcewing_sg:sg_abort", 1.0F, 1.0F);
				enterState(EnumStargateState.Idle, 0);
				// sendClientEvent(SGEvent.FinishDisconnecting, 0);
			}
		}
	}

	void startDiallingStargate(String address, TileEntityStargateBase dte, boolean initiator) {
		// System.out.printf("SGBaseTE.startDiallingStargate %s, initiator = %s\n",
		// dte, initiator);
		dialledAddress = address;
		connectedLocation = new WorldLocation(dte);
		isInitiator = initiator;
		if (m_computer != null)
			if (!isInitiator)
				m_computer.queueEvent("sgIncoming", new Object[] { address });
		// markBlockForUpdate();
		onInventoryChanged();
		startDiallingNextSymbol();
	}

	void serverUpdate() {
		if (isMerged) {
			// performPendingTeleportations();
			fuelUsage();
			if (timeout > 0) {
				// int dimension = worldObj.provider.dimensionId;
				// System.out.printf(
				// "SGBaseTE.serverUpdate: (%d, %d, %d, %d) state %s, timeout %s\n",
				// dimension, xCoord, yCoord, zCoord, state, timeout);
				if (state == EnumStargateState.Transient)
					performTransientDamage();
				--timeout;
			} else
				switch (state) {
				case Idle:
					if (undialledDigitsRemaining())
						startDiallingNextSymbol();
					break;
				case Dialling:
					finishDiallingSymbol();
					break;
				case InterDialling:
					startDiallingNextSymbol();
					break;
				case Transient:
					enterState(EnumStargateState.Connected, isInitiator ? ticksToStayOpen : 0);
					break;
				case Connected:
					if (isInitiator)
						disconnect();
					break;
				case Disconnecting:
					enterState(EnumStargateState.Idle, 0);
					break;
				}
		}
	}

	void fuelUsage() {
		if (state == EnumStargateState.Connected && isInitiator)
			if (!useFuel(1))
				disconnect();
	}

	boolean useFuel(int amount) {
		// System.out.printf("SGBaseTE.useFuel: %d\n", amount);
		if (reloadFuel(amount)) {
			setFuelBuffer(fuelBuffer - amount);
			return true;
		} else
			return false;
	}

	boolean reloadFuel(int amount) {
		while (fuelBuffer < amount && fuelBuffer + fuelPerItem <= maxFuelBuffer)
			if (useFuelItem())
				setFuelBuffer(fuelBuffer + fuelPerItem);
			else
				break;
		return fuelBuffer >= amount;
	}

	boolean useFuelItem() {
		int n = getSizeInventory();
		for (int i = n - 1; i >= 0; i--) {
			ItemStack stack = getStackInSlot(i);
			if (stack != null && stack.getItem() == Items.naquadah && stack.stackSize > 0) {
				decrStackSize(i, 1);
				return true;
			}
		}
		return false;
	}

	void setFuelBuffer(int amount) {
		if (fuelBuffer != amount) {
			fuelBuffer = amount;
			onInventoryChanged();
			// System.out.printf("SGBaseTE: Fuel level now %d\n", fuelBuffer);
		}
	}

	public Trans3 localToGlobalTransformation() {
		return getBlock().localToGlobalTransformation(xCoord, yCoord, zCoord, getBlockMetadata(), this);
	}

	void performTransientDamage() {
		Trans3 t = localToGlobalTransformation();
		Vector3 p0 = t.p(-1.5, 0.5, 0.5);
		Vector3 p1 = t.p(1.5, 3.5, 5.5);
		Vector3 q0 = p0.min(p1);
		Vector3 q1 = p0.max(p1);
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(q0.x, q0.y, q0.z, q1.x, q1.y, q1.z);
		// System.out.printf("SGBaseTE.performTransientDamage: players in world:\n");
		// for (Entity ent : (List<Entity>)worldObj.loadedEntityList)
		// if (ent instanceof EntityPlayer)
		// System.out.printf("--- %s\n", ent);
		// System.out.printf("SGBaseTE.performTransientDamage: box = %s\n",
		// box);
		List<EntityLiving> ents = worldObj.getEntitiesWithinAABB(EntityLiving.class, box);
		// System.out.printf("SGBaseTE.performTransientDamage: entities in box:\n",
		// box);
		for (EntityLiving ent : ents) {
			Vector3 ep = new Vector3(ent.posX, ent.posY, ent.posZ);
			Vector3 gp = t.p(0, 2, 0.5);
			double dist = ep.distance(gp);
			// System.out.printf("SGBaseTE.performTransientDamage: found %s\n",
			// ent);
			if (dist > 1.0)
				dist = 1.0;
			int damage = (int) Math.ceil(dist * transientDamageRate);
			// System.out.printf("SGBaseTE.performTransientDamage: distance = %s, damage = %s\n",
			// dist, damage);
			ent.attackEntityFrom(transientDamage, damage);
		}
	}

	boolean undialledDigitsRemaining() {
		int n = numEngagedChevrons;
		return n < 7 && n < dialledAddress.length();
	}

	void startDiallingNextSymbol() {
		startDiallingSymbol(dialledAddress.charAt(numEngagedChevrons));
	}

	void startDiallingSymbol(char c) {
		int i = Character.getNumericValue(c) - Character.getNumericValue('A');
		if (i >= 0 && i < numRingSymbols) {
			startDiallingToAngle(i * ringSymbolAngle - 45 * numEngagedChevrons);
			playSoundEffect("gcewing_sg:sg_dial", 1.0F, 1.0F);
		}
	}

	void startDiallingToAngle(double a) {
		targetRingAngle = Utils.normaliseAngle(a);
		enterState(EnumStargateState.Dialling, diallingTime);
	}

	void finishDiallingSymbol() {
		++numEngagedChevrons;
		if (numEngagedChevrons == GateAddressHelper.addressLength)
			finishDiallingAddress();
		else if (undialledDigitsRemaining())
			enterState(EnumStargateState.InterDialling, interDiallingTime);
		else
			enterState(EnumStargateState.Idle, 0);
	}

	void finishDiallingAddress() {
		// System.out.printf("SGBaseTE: Connecting to '%s'\n", dialledAddress);
		if (!isInitiator || useFuel(fuelToOpen)) {
			enterState(EnumStargateState.Transient, transientDuration);
			playSoundEffect("gcewing_sg:sg_open", 1.0F, 1.0F);
		} else
			// enterState(SGState.Idle, 0);
			// playSoundEffect("gcewing_sg:sg_abort", 1.0F, 1.0F);
			disconnect();
	}

	boolean canTravelFromThisEnd() {
		return isInitiator || !oneWayTravel;
	}

	static String repr(Entity entity) {
		if (entity != null) {
			String s = String.format("%s#%s", entity.getClass().getSimpleName(), entity.entityId);
			if (entity.isDead)
				s += "(dead)";
			return s;
		} else
			return "null";
	}

	class TrackedEntity {
		public Entity entity;
		public Vector3 lastPos;

		public TrackedEntity(Entity entity) {
			this.entity = entity;
			lastPos = new Vector3(entity.posX, entity.posY, entity.posZ);
		}

	}

	List<TrackedEntity> trackedEntities = new ArrayList<TrackedEntity>();

	void checkForEntitiesInPortal() {
		if (state == EnumStargateState.Connected) {
			for (TrackedEntity trk : trackedEntities)
				entityInPortal(trk.entity, trk.lastPos);
			trackedEntities.clear();
			Vector3 p0 = new Vector3(-1.5, 0.5, -3.5);
			Vector3 p1 = new Vector3(1.5, 3.5, 3.5);
			Trans3 t = localToGlobalTransformation();
			AxisAlignedBB box = t.box(p0, p1);
			// System.out.printf("SGBaseTE.checkForEntitiesInPortal: %s\n",
			// box);
			List<Entity> ents = worldObj.getEntitiesWithinAABB(Entity.class, box);
			for (Entity entity : ents)
				if (!entity.isDead && entity.ridingEntity == null)
					// if (!(entity instanceof EntityPlayer))
					// System.out.printf("SGBaseTE.checkForEntitiesInPortal: Tracking %s\n",
					// repr(entity));
					trackedEntities.add(new TrackedEntity(entity));
		}
	}

	// ArrayList<PendingRemount> remountQueue = new ArrayList<PendingRemount>();
	//
	// void performPendingRemounts() {
	// PendingRemount m;
	// int i = 0;
	// while (i < remountQueue.size()) {
	// m = remountQueue.get(i);
	// if (m.delay == 0) {
	// System.out.printf("SGBaseTE.performPendingRemounts: Remounting %s on %s\n",
	// repr(m.rider), repr(m.ridden));
	// m.rider.mountEntity(m.ridden);
	// remountQueue.remove(i);
	// }
	// else {
	// //System.out.printf("SGBaseTE.performPendingRemounts: Deferring %s on %s for %s ticks\n",
	// // repr(m.rider), repr(m.ridden), m.delay);
	// m.delay -= 1;
	// i += 1;
	// }
	// }
	// }

	public void entityInPortal(Entity entity, Vector3 prevPos) {
		if (!entity.isDead && state == EnumStargateState.Connected && canTravelFromThisEnd()) {
			Trans3 t = localToGlobalTransformation();
			double vx = entity.posX - prevPos.x;
			double vy = entity.posY - prevPos.y;
			double vz = entity.posZ - prevPos.z;
			Vector3 p1 = t.ip(entity.posX, entity.posY, entity.posZ);
			Vector3 p0 = t.ip(2 * prevPos.x - entity.posX, 2 * prevPos.y - entity.posY, 2 * prevPos.z - entity.posZ);
			// if (!(entity instanceof EntityPlayer))
			// System.out.printf("SGBaseTE.entityInPortal: z0 = %.3f z1 = %.3f\n",
			// p0.z, p1.z);
			double z0 = 0.0;
			if (p0.z >= z0 && p1.z < z0) {
				// System.out.printf("SGBaseTE.entityInPortal: %s passed through event horizon of stargate at (%d,%d,%d) in %s\n",
				// repr(entity), xCoord, yCoord, zCoord, worldObj);
				entity.motionX = vx;
				entity.motionY = vy;
				entity.motionZ = vz;
				// System.out.printf("SGBaseTE.entityInPortal: %s pos (%.2f, %.2f, %.2f) prev (%.2f, %.2f, %.2f) motion (%.2f, %.2f, %.2f)\n",
				// repr(entity),
				// entity.posX, entity.posY, entity.posZ,
				// prevPos.x, prevPos.y, prevPos.z,
				// entity.motionX, entity.motionY, entity.motionZ);
				TileEntityStargateBase dte = getConnectedStargateTE();
				if (dte != null) {
					Trans3 dt = dte.localToGlobalTransformation();
					while (entity.ridingEntity != null)
						entity = entity.ridingEntity;
					teleportEntityAndRider(entity, t, dt, connectedLocation.dimension);
				}
			}
		}
	}

	Entity teleportEntityAndRider(Entity entity, Trans3 t1, Trans3 t2, int dimension) {
		Entity rider = entity.riddenByEntity;
		if (rider != null)
			// System.out.printf("SGBaseTE.teleportEntityAndRider: Unmounting %s from %s\n",
			// repr(rider), repr(entity));
			rider.mountEntity(null);
		entity = teleportEntity(entity, t1, t2, dimension);
		if (rider != null) {
			rider = teleportEntityAndRider(rider, t1, t2, dimension);
			// System.out.printf("SGBaseTE.teleportEntityAndRider: Adding (%s on %s) to remount queue\n",
			// repr(rider), repr(entity));
			// remountQueue.add(new PendingRemount(rider, entity));
			// System.out.printf("SGBaseTE.teleportEntityAndRider: Mounting %s on %s\n",
			// repr(rider), repr(entity));
			rider.mountEntity(entity);
			entity.forceSpawn = false;
		}
		return entity;
	}

	static Entity teleportEntity(Entity entity, Trans3 t1, Trans3 t2, int dimension) {
		Entity newEntity;
		// System.out.printf("SGBaseTE.teleportEntity: %s (in dimension %d)  to dimension %d\n",
		// repr(entity), entity.dimension, dimension);
		// System.out.printf("SGBaseTE.teleportEntity: pos (%.2f, %.2f, %.2f) prev (%.2f, %.2f, %.2f) last (%.2f, %.2f, %.2f)\n",
		// entity.posX, entity.posY, entity.posZ,
		// entity.prevPosX, entity.prevPosY, entity.prevPosZ,
		// entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ);
		Vector3 p = t1.ip(entity.posX, entity.posY, entity.posZ); // local
																	// position
		Vector3 v = t1.iv(entity.motionX, entity.motionY, entity.motionZ); // local
																			// velocity
		Vector3 r = t1.iv(yawVector(entity)); // local facing
		Vector3 q = t2.p(-p.x, p.y, -p.z); // new global position
		Vector3 u = t2.v(-v.x, v.y, -v.z); // new global velocity
		Vector3 s = t2.v(r.mul(-1)); // new global facing
		double a = yawAngle(s); // new global yaw angle
		if (entity.dimension == dimension)
			newEntity = teleportWithinDimension(entity, q, u, a);
		else {
			newEntity = teleportToOtherDimension(entity, q, u, a, dimension);
			newEntity.dimension = dimension;
		}
		// System.out.printf("SGBaseTE.teleportEntity: %s is now %s\n",
		// repr(entity), repr(newEntity));
		return newEntity;
	}

	static Entity teleportWithinDimension(Entity entity, Vector3 p, Vector3 v, double a) {
		if (entity instanceof EntityPlayerMP)
			return teleportPlayerWithinDimension((EntityPlayerMP) entity, p, v, a);
		else
			return teleportEntityToWorld(entity, p, v, a, (WorldServer) entity.worldObj);
	}

	static Entity teleportPlayerWithinDimension(EntityPlayerMP entity, Vector3 p, Vector3 v, double a) {
		entity.rotationYaw = (float) a;
		entity.setPositionAndUpdate(p.x, p.y, p.z);
		entity.worldObj.updateEntityWithOptionalForce(entity, false);
		return entity;
	}

	static Entity teleportToOtherDimension(Entity entity, Vector3 p, Vector3 v, double a, int dimension) {
		if (entity instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) entity;
			Vector3 q = p.add(yawVector(a));
			transferPlayerToDimension(player, dimension, q, a);
			return player;
		} else
			return teleportEntityToDimension(entity, p, v, a, dimension);
	}

	static void transferPlayerToDimension(EntityPlayerMP player, int newDimension, Vector3 p, double a) {
		// System.out.printf("SGBaseTE.transferPlayerToDimension: %s to dimension %d\n",
		// repr(player), newDimension);
		MinecraftServer server = MinecraftServer.getServer();
		ServerConfigurationManager scm = server.getConfigurationManager();
		int oldDimension = player.dimension;
		player.dimension = newDimension;
		WorldServer oldWorld = server.worldServerForDimension(oldDimension);
		WorldServer newWorld = server.worldServerForDimension(newDimension);
		// System.out.printf("SGBaseTE.transferPlayerToDimension: %s with %s\n",
		// newWorld, newWorld.getEntityTracker());
		player.closeScreen();
		player.playerNetServerHandler.sendPacketToPlayer(new Packet9Respawn(player.dimension,
				(byte) player.worldObj.difficultySetting, newWorld.getWorldInfo().getTerrainType(), newWorld
						.getHeight(), player.theItemInWorldManager.getGameType()));
		oldWorld.removePlayerEntityDangerously(player); // Removes player right
														// now instead of
														// waiting for next tick
		player.isDead = false;
		// player.setLocationAndAngles(player.posX, player.posY, player.posZ,
		// player.rotationYaw, player.rotationPitch);
		player.setLocationAndAngles(p.x, p.y, p.z, (float) a, player.rotationPitch);
		newWorld.spawnEntityInWorld(player);
		player.setWorld(newWorld);
		// newWorld.updateEntityWithOptionalForce(player, false);
		scm.func_72375_a(player, oldWorld);
		player.playerNetServerHandler.setPlayerLocation(p.x, p.y, p.z, (float) a, player.rotationPitch);
		player.theItemInWorldManager.setWorld(newWorld);
		scm.updateTimeAndWeatherForPlayer(player, newWorld);
		scm.syncPlayerInventory(player);
		Iterator var6 = player.getActivePotionEffects().iterator();
		while (var6.hasNext()) {
			PotionEffect effect = (PotionEffect) var6.next();
			player.playerNetServerHandler.sendPacketToPlayer(new Packet41EntityEffect(player.entityId, effect));
		}
		player.playerNetServerHandler.sendPacketToPlayer(new Packet43Experience(player.experience,
				player.experienceTotal, player.experienceLevel));
		GameRegistry.onPlayerChangedDimension(player);
		// System.out.printf("SGBaseTE.transferPlayerToDimension: Transferred %s\n",
		// repr(player));
	}

	static Entity teleportEntityToDimension(Entity entity, Vector3 p, Vector3 v, double a, int dimension) {
		// System.out.printf("SGBaseTE.teleportEntityToDimension: %s to dimension %d\n",
		// repr(entity), dimension);
		MinecraftServer server = MinecraftServer.getServer();
		WorldServer world = server.worldServerForDimension(dimension);
		return teleportEntityToWorld(entity, p, v, a, world);
	}

	static Entity teleportEntityToWorld(Entity oldEntity, Vector3 p, Vector3 v, double a, WorldServer newWorld) {
		// System.out.printf("SGBaseTE.teleportEntityToWorld: %s to %s\n",
		// repr(oldEntity), newWorld);
		WorldServer oldWorld = (WorldServer) oldEntity.worldObj;
		NBTTagCompound nbt = new NBTTagCompound();
		oldEntity.writeToNBTOptional(nbt);
		extractEntityFromWorld(oldWorld, oldEntity);
		Entity newEntity = EntityList.createEntityFromNBT(nbt, newWorld);
		if (newEntity != null) {
			if (oldEntity instanceof EntityLiving)
				copyMoreEntityData((EntityLiving) oldEntity, (EntityLiving) newEntity);
			setVelocity(newEntity, v);
			// System.out.printf("SGBaseTE.teleportEntityToWorld: Set velocity of %s to (%.2f, %.2f, %.2f)\n",
			// repr(newEntity), newEntity.motionX, newEntity.motionY,
			// newEntity.motionZ);
			newEntity.setLocationAndAngles(p.x, p.y, p.z, (float) a, oldEntity.rotationPitch);
			checkChunk(newWorld, newEntity);
			// System.out.printf("SGBaseTE.teleportEntityToWorld: Spawning %s in %s with %s\n",
			// repr(newEntity), newWorld, newWorld.getEntityTracker());
			newEntity.forceSpawn = true; // Force spawn packet to be sent as
											// soon as possible
			newWorld.spawnEntityInWorld(newEntity);
			newEntity.setWorld(newWorld);
			// System.out.printf(
			// "SGBaseTE.teleportEntityToWorld: Spawned %s pos (%.2f, %.2f, %.2f) vel (%.2f, %.2f, %.2f)\n",
			// repr(newEntity),
			// newEntity.posX, newEntity.posY, newEntity.posZ,
			// newEntity.motionX, newEntity.motionY, newEntity.motionZ);
		}
		oldWorld.resetUpdateEntityTick();
		if (oldWorld != newWorld)
			newWorld.resetUpdateEntityTick();
		return newEntity;
	}

	static void copyMoreEntityData(EntityLiving oldEntity, EntityLiving newEntity) {
		float s = oldEntity.getAIMoveSpeed();
		if (s != 0)
			newEntity.setAIMoveSpeed(s);
	}

	static void setVelocity(Entity entity, Vector3 v) {
		entity.motionX = v.x;
		entity.motionY = v.y;
		entity.motionZ = v.z;
	}

	static void extractEntityFromWorld(World world, Entity entity) {
		// Immediately remove entity from world without calling setDead(), which
		// has
		// undesirable side effects on some entities.
		if (entity instanceof EntityPlayer) {
			world.playerEntities.remove(entity);
			world.updateAllPlayersSleepingFlag();
		}
		int i = entity.chunkCoordX;
		int j = entity.chunkCoordZ;
		if (entity.addedToChunk && world.getChunkProvider().chunkExists(i, j))
			world.getChunkFromChunkCoords(i, j).removeEntity(entity);
		world.loadedEntityList.remove(entity);
		world.onEntityRemoved(entity);
	}

	static void checkChunk(World world, Entity entity) {
		int cx = MathHelper.floor_double(entity.posX / 16.0D);
		int cy = MathHelper.floor_double(entity.posZ / 16.0D);
		Chunk chunk = world.getChunkFromChunkCoords(cx, cy);
	}

	static Vector3 yawVector(Entity entity) {
		return yawVector(entity.rotationYaw);
	}

	static Vector3 yawVector(double yaw) {
		double a = Math.toRadians(yaw);
		Vector3 v = new Vector3(-Math.sin(a), 0, Math.cos(a));
		// System.out.printf("SGBaseTE.yawVector: %.2f --> (%.3f, %.3f)\n", yaw,
		// v.x, v.z);
		return v;
	}

	static double yawAngle(Vector3 v) {
		double a = Math.atan2(-v.x, v.z);
		double d = Math.toDegrees(a);
		// System.out.printf("SGBaseTE.yawAngle: (%.3f, %.3f) --> %.2f\n", v.x,
		// v.z, d);
		return d;
	}

	TileEntityStargateBase getConnectedStargateTE() {
		if (connectedLocation != null)
			return connectedLocation.getStargateTE();
		else
			return null;
	}

	// ------------------------------------ Client
	// --------------------------------------------

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		// System.out.printf("SGBaseTE.onDataPacket: with state %s numEngagedChevrons %s\n",
		// SGState.valueOf(pkt.customParam1.getInteger("state")),
		// pkt.customParam1.getInteger("numEngagedChevrons"));
		EnumStargateState oldState = state;
		super.onDataPacket(net, pkt);
		if (isMerged && state != oldState)
			switch (state) {
			case Transient:
				initiateOpeningTransient();
				break;
			case Disconnecting:
				initiateClosingTransient();
				break;
			}
	}

	void clientUpdate() {
		lastRingAngle = ringAngle;
		applyRandomImpulse();
		updateEventHorizon();
		switch (state) {
		case Dialling:
			// System.out.printf("SGBaseTe: Relaxing angle %s towards %s at rate %s\n",
			// ringAngle, targetRingAngle, diallingRelaxationRate);
			// setRingAngle(Utils.relaxAngle(ringAngle, targetRingAngle,
			// diallingRelaxationRate));
			updateRingAngle();
			// System.out.printf("SGBaseTe: Ring angle now %s\n", ringAngle);
			break;
		}
	}

	void setRingAngle(double a) {
		ringAngle = a;
	}

	void updateRingAngle() {
		if (timeout > 0) {
			double da = Utils.diffAngle(ringAngle, targetRingAngle) / timeout;
			setRingAngle(Utils.addAngle(ringAngle, da));
			--timeout;
		} else
			setRingAngle(targetRingAngle);
	}

	public double[][][] getEventHorizonGrid() {
		if (ehGrid == null) {
			int m = TileEntityStargateBaseRenderer.ehGridRadialSize;
			int n = TileEntityStargateBaseRenderer.ehGridPolarSize;
			ehGrid = new double[2][n + 2][m + 1];
			for (int i = 0; i < 2; i++) {
				ehGrid[i][0] = ehGrid[i][n];
				ehGrid[i][n + 1] = ehGrid[i][1];
			}
		}
		return ehGrid;
	}

	public boolean isIrisClosed() {
		return false;
	}

	void initiateOpeningTransient() {
		if (!isIrisClosed()) {
			double v[][] = getEventHorizonGrid()[1];
			int n = TileEntityStargateBaseRenderer.ehGridPolarSize;
			for (int j = 0; j <= n + 1; j++) {
				v[j][0] = openingTransientIntensity;
				v[j][1] = v[j][0] + openingTransientRandomness * random.nextGaussian();
			}
		}
	}

	void initiateClosingTransient() {
		if (!isIrisClosed()) {
			double v[][] = getEventHorizonGrid()[1];
			int m = TileEntityStargateBaseRenderer.ehGridRadialSize;
			int n = TileEntityStargateBaseRenderer.ehGridPolarSize;
			for (int i = 1; i < m; i++)
				for (int j = 1; j <= n; j++)
					v[j][i] += closingTransientRandomness * random.nextGaussian();
		}
	}

	void applyRandomImpulse() {
		if (!isIrisClosed()) {
			double v[][] = getEventHorizonGrid()[1];
			int m = TileEntityStargateBaseRenderer.ehGridRadialSize;
			int n = TileEntityStargateBaseRenderer.ehGridPolarSize;
			int i = random.nextInt(m - 1) + 1;
			int j = random.nextInt(n) + 1;
			v[j][i] += 0.05 * random.nextGaussian();
		}
	}

	void updateEventHorizon() {
		double grid[][][] = getEventHorizonGrid();
		double u[][] = grid[0];
		double v[][] = grid[1];
		int m = TileEntityStargateBaseRenderer.ehGridRadialSize;
		int n = TileEntityStargateBaseRenderer.ehGridPolarSize;
		double dt = 1.0;
		double asq = 0.03;
		double d = 0.95;
		for (int i = 1; i < m; i++)
			for (int j = 1; j <= n; j++) {
				double du_dr = 0.5 * (u[j][i + 1] - u[j][i - 1]);
				double d2u_drsq = u[j][i + 1] - 2 * u[j][i] + u[j][i - 1];
				double d2u_dthsq = u[j + 1][i] - 2 * u[j][i] + u[j - 1][i];
				v[j][i] = d * v[j][i] + asq * dt * (d2u_drsq + du_dr / i + d2u_dthsq / (i * i));
			}
		for (int i = 1; i < m; i++)
			for (int j = 1; j <= n; j++)
				u[j][i] += v[j][i] * dt;
		double u0 = 0, v0 = 0;
		for (int j = 1; j <= n; j++) {
			u0 += u[j][1];
			v0 += v[j][1];
		}
		u0 /= n;
		v0 /= n;
		for (int j = 1; j <= n; j++) {
			u[j][0] = u0;
			v[j][0] = v0;
		}
		// dumpGrid("u", u);
		// dumpGrid("v", v);
	}

	void dumpGrid(String label, double g[][]) {
		// System.out.printf("SGBaseTE: %s:\n", label);
		int m = TileEntityStargateBaseRenderer.ehGridRadialSize;
		int n = TileEntityStargateBaseRenderer.ehGridPolarSize;
		// for (int j = 0; j <= n+1; j++) {
		// for (int i = 0; i <= m; i++)
		// System.out.printf(" %6.3f", g[j][i]);
		// System.out.printf("\n");
		// }
	}

	@Override
	public BaseTEChunkManager getChunkManager() {
		return SGCraft.getProxy().chunkManager;
	}

	@Override
	@Method(modid = "ComputerCraft")
	public String getType() {
		return "stargate";
	}

	@Override
	@Method(modid = "ComputerCraft")
	public String[] getMethodNames() {
		return new String[] { "dial", "connect", "disconnect", "isConnected", "getAddress", "isDialing", "isComplete",
				"isBusy", "hasFuel", "isValidAddress" };
	}

	@Override
	@Method(modid = "ComputerCraft")
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments)
			throws Exception {
		if (method == 0 || method == 1) {
			String address = arguments[0].toString().toUpperCase();
			if (address.length() != 7)
				return new Object[] { "Stargate addresses must be 7 characters" };
			else
				connect(address, null);
		} else if (method == 2)
			disconnect();
		else if (method == 3)
			return new Object[] { isConnected() };
		else if (method == 4)
			return new Object[] { getHomeAddress() };
		else if (method == 5)
			return new Object[] { isDialing() };
		else if (method == 6)
			return new Object[] { isMerged };
		else if (method == 7) {
			String address = arguments[0].toString().toUpperCase();
			TileEntityStargateBase dte = GateAddressHelper.findAddressedStargate(address);
			if (address.length() != 7)
				return new Object[] { "Stargate addresses must be 7 characters" };
			else if (dte.state != EnumStargateState.Idle)
				return new Object[] { "true" };
			else
				return new Object[] { "false" };
		} else if (method == 8) {
			TileEntityStargateBase dte = GateAddressHelper.findAddressedStargate(getHomeAddress());
			if (!reloadFuel(fuelToOpen))
				return new Object[] { false };
			else
				return new Object[] { true };
		} else if (method == 9) {
			String address = arguments[0].toString().toUpperCase();
			TileEntityStargateBase dte = GateAddressHelper.findAddressedStargate(address);
			if (address.length() != 7)
				return new Object[] { "Stargate addresses must be 7 characters" };
			else {
				if (dte == null)
					return new Object[] { false };
				if (address == getHomeAddress())
					return new Object[] { "Stargate cannot connect to itself" };
				else
					return new Object[] { true };
			}
		}
		return null;
	}

	public boolean isDialing() {
		return state == EnumStargateState.InterDialling || state == EnumStargateState.Dialling;
	}

	@Override
	@Method(modid = "ComputerCraft")
	public boolean canAttachToSide(int side) {
		return true;
	}

	@Override
	@Method(modid = "ComputerCraft")
	public void attach(IComputerAccess computer) {
		// TODO Auto-generated method stub
		m_computer = computer;
	}

	@Override
	@Method(modid = "ComputerCraft")
	public void detach(IComputerAccess computer) {
		// TODO Auto-generated method stub
		m_computer = null;
	}

}

// ------------------------------------------------------------------------------------------------

class TransientDamageSource extends DamageSource {

	public TransientDamageSource() {
		super("sgTransient");
	}

	public String getDeathMessage(EntityPlayer player) {
		return player.username + " was torn apart by an event horizon";
	}

}

class irisDamageSource extends DamageSource {

	public irisDamageSource() {
		super("sgIris");
	}

	public String getDeathMessage(EntityPlayer player) {
		return player.username + " walked into an iris";
	}

}

class recieveDamageSource extends DamageSource {

	public recieveDamageSource() {
		super("sgRecieve");
	}

	public String getDeathMessage(EntityPlayer player) {
		return player.username + " walked through a receiving stargate";
	}

}

// ------------------------------------------------------------------------------------------------

// class PendingRemount {
// public Entity rider;
// public Entity ridden;
// public int delay;
//
// public PendingRemount(Entity rider, Entity ridden) {
// this.rider = rider;
// this.ridden = ridden;
// this.delay = 4;
// }
//
// }
