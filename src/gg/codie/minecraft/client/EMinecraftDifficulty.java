package gg.codie.minecraft.client;

public enum EMinecraftDifficulty {
    PEACEFUL,
    EASY,
    NORMAL,
    HARD;

    public int getIntValue() {
        switch (this){
            case PEACEFUL:
                return 0;
            case EASY:
                return 1;
            default:
            case NORMAL:
                return 2;
            case HARD:
                return 3;
        }
    }
}
