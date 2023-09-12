package com.ahnewark.minecraft.client.options;

public enum ELegacyMinecraftRenderDistance {
    FAR,
    NORMAL,
    SHORT,
    TINY;

    public int getIntValue() {
        switch (this){
            default:
            case FAR:
                return 0;
            case NORMAL:
                return 1;
            case SHORT:
                return 2;
            case TINY:
                return 3;
        }
    }
}
