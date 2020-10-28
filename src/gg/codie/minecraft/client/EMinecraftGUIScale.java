package gg.codie.minecraft.client;

public enum EMinecraftGUIScale {
    AUTO,
    SMALL,
    MEDIUM,
    LARGE;

    public int getIntValue() {
        switch (this){
            default:
            case AUTO:
                return 0;
            case SMALL:
                return 1;
            case MEDIUM:
                return 2;
            case LARGE:
                return 3;
        }
    }

    public String getName() {
        switch (this){
            default:
            case AUTO:
                return "Auto";
            case SMALL:
                return "Small";
            case MEDIUM:
                return "Medium";
            case LARGE:
                return "Large";
        }
    }
}
