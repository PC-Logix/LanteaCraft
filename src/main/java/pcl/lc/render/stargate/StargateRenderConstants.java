package pcl.lc.render.stargate;

import pcl.lc.core.GateAddressHelper;

public class StargateRenderConstants {

	public final static int numRingSegments = 38;
	public final static double ringSymbolAngle = 360.0 / GateAddressHelper.singleton().radixSize;
	public final static double ringSymbolTextureLength = 38 * 8;
	public final static double ringSymbolTextureHeight = 12;
	public final static double ringSymbolSegmentWidth = ringSymbolTextureLength / numRingSegments;

	public final static double openingTransientIntensity = 1.3;
	public final static double openingTransientRandomness = 0.25;
	public final static double closingTransientRandomness = 0.25;

	public final static double ringInnerRadius = 3.0;
	public final static double ringMidRadius = 3.25;
	public final static double ringOuterRadius = 3.5;
	public final static double ringDepth = 0.5;

	public final static double chevronInnerRadius = 3.25;
	public final static double chevronOuterRadius = ringOuterRadius + 1 / 16.0;
	public final static double chevronWidth = (chevronOuterRadius - chevronInnerRadius) * 1.5;
	public final static double chevronDepth = 0.125;
	public final static double chevronBorderWidth = chevronWidth / 6;
	public final static double chevronMotionDistance = 1 / 8.0;

	public final static int ringFaceTextureIndex = 0x14;
	public final static int ringTextureIndex = 0x15;
	public final static int ringSymbolTextureIndex = 0x20;
	public final static int chevronTextureIndex = 0x05;
	public final static int chevronLitTextureIndex = 0x16;

	public final static int[] standardRenderQueue = { -1, 1, 2, 3, 9, 8, 7, 0 };
	public final static int[] extendedRenderQueue = { -1, 1, 2, 3, 4, 8, 7, 6, 5, 0 };

}
