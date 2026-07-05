package com.pclogix.lanteacraft.gate;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

public final class StargateAddress {
    public static final String LEGACY_GLYPHS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890-+";
    public static final int GATE_ID_LENGTH = 6;
    public static final int ADDRESS_LENGTH = GATE_ID_LENGTH + 1;
    public static final int EXTENDED_ADDRESS_LENGTH = ADDRESS_LENGTH + 1;
    public static final char OVERWORLD_GLYPH = 'A';
    public static final char NETHER_GLYPH = 'N';
    public static final char END_GLYPH = 'E';

    private static final char[] SYMBOLS = LEGACY_GLYPHS.toCharArray();

    private StargateAddress() {
    }

    public static String forGate(ServerLevel level, BlockPos basePos, char dimensionGlyph) {
        ChunkPos chunkPos = new ChunkPos(basePos);
        long state = 0x4C414E544541L;
        state = mix(state, chunkPos.x);
        state = mix(state, chunkPos.z);

        StringBuilder address = new StringBuilder(ADDRESS_LENGTH);
        for (int i = 0; i < GATE_ID_LENGTH; i++) {
            state = mix(state, i);
            address.append(SYMBOLS[Math.floorMod(state, SYMBOLS.length)]);
        }
        address.append(normalizeGlyph(dimensionGlyph));

        return address.toString();
    }

    public static char symbolForSalt(int salt) {
        return SYMBOLS[Math.floorMod(salt, SYMBOLS.length)];
    }

    public static boolean isAddressGlyph(char glyph) {
        return LEGACY_GLYPHS.indexOf(Character.toUpperCase(glyph)) >= 0;
    }

    public static char normalizeGlyph(char glyph) {
        char normalized = Character.toUpperCase(glyph);
        return isAddressGlyph(normalized) ? normalized : OVERWORLD_GLYPH;
    }

    public static String withSalt(String address, int salt) {
        if (address == null || address.length() != ADDRESS_LENGTH) {
            return address;
        }

        StringBuilder salted = new StringBuilder(address);
        int remaining = Math.max(1, salt);
        for (int i = GATE_ID_LENGTH - 1; i >= 0 && remaining > 0; i--) {
            int current = Math.max(0, LEGACY_GLYPHS.indexOf(salted.charAt(i)));
            salted.setCharAt(i, SYMBOLS[Math.floorMod(current + remaining, SYMBOLS.length)]);
            remaining /= SYMBOLS.length;
        }

        return salted.toString();
    }

    private static long mix(long state, int value) {
        long mixed = state ^ value;
        mixed ^= mixed >>> 33;
        mixed *= 0xff51afd7ed558ccdL;
        mixed ^= mixed >>> 33;
        mixed *= 0xc4ceb9fe1a85ec53L;
        mixed ^= mixed >>> 33;
        return mixed;
    }
}
