package com.pclogix.lanteacraft.gate;

public enum IrisState {
    NONE("none"),
    OPEN("open"),
    CLOSED("closed"),
    OPENING("opening"),
    CLOSING("closing");

    private final String serializedName;

    IrisState(String serializedName) {
        this.serializedName = serializedName;
    }

    public String serializedName() {
        return serializedName;
    }

    public static IrisState byName(String name) {
        for (IrisState state : values()) {
            if (state.serializedName.equals(name)) {
                return state;
            }
        }

        return NONE;
    }
}
