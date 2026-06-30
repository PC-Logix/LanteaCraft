package com.pclogix.lanteacraft.gate;

public enum IrisType {
    MECHANICAL("mechanical", false),
    ENERGY("energy", true);

    private final String serializedName;
    private final boolean invulnerable;

    IrisType(String serializedName, boolean invulnerable) {
        this.serializedName = serializedName;
        this.invulnerable = invulnerable;
    }

    public String serializedName() {
        return serializedName;
    }

    public boolean isInvulnerable() {
        return invulnerable;
    }

    public static IrisType byName(String name) {
        for (IrisType type : values()) {
            if (type.serializedName.equals(name)) {
                return type;
            }
        }

        return null;
    }
}
