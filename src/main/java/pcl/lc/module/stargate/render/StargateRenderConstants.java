package pcl.lc.module.stargate.render;

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
	public final static double ringInnerMovingRadius = ringInnerRadius + 0.1;
	public final static double ringOuterRadius = 3.5;
	public final static double ringMidRadius = ringInnerMovingRadius + ((ringOuterRadius - ringInnerMovingRadius) / 2);

	public final static double ringDepth = 0.15;

	public final static int numChevrons = 9;
	public final static double chevronAngle = 360.0 / numChevrons;
	public final static double chevronAngleOffset = -90.0;
	public final static double chevronInnerRadius = ringMidRadius;
	public final static double chevronOuterRadius = ringOuterRadius + 1 / 16.0;
	public final static double chevronWidth = 0.5;
	public final static double chevronDepth = 0.0625d;
	public final static double chevronBorderWidth = chevronWidth / 6;
	public final static double chevronMotionDistance = 1 / 16.0d;

	public final static int ringFaceTextureIndex = 0x14;
	public final static int ringTextureIndex = 0x15;
	public final static int ringSymbolTextureIndex = 0x20;
	public final static int chevronTextureIndex = 0x05;
	public final static int chevronLitTextureIndex = 0x16;

	public final static int[] standardRenderQueue = { -1, 8, 7, 6, 3, 2, 1, 0 };
	public final static int[] extendedRenderQueue = { -1, 8, 7, 6, 3, 2, 1, 5, 4, 0 };

}
