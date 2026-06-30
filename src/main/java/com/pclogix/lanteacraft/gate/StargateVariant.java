package com.pclogix.lanteacraft.gate;

public enum StargateVariant {
    MILKY_WAY("milky_way", ""),
    NOX("nox", "_nox"),
    WRAITH("wraith", "_wraith"),
    PEGASUS("pegasus", "_pegasus");

    private final String id;
    private final String textureSuffix;

    StargateVariant(String id, String textureSuffix) {
        this.id = id;
        this.textureSuffix = textureSuffix;
    }

    public String id() {
        return id;
    }

    public String textureSuffix() {
        return textureSuffix;
    }
}
