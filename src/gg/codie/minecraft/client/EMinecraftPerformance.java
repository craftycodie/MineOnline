package gg.codie.minecraft.client;

public enum EMinecraftPerformance {
    MAX_FPS,
    BALANCED,
    POWER_SAVER;

    public int getIntValue() {
        switch (this){
            case MAX_FPS:
                return 0;
            case BALANCED:
                return 1;
            case POWER_SAVER:
                return 2;
        }

        return 0;
    }
}
