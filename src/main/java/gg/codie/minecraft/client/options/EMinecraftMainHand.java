package gg.codie.minecraft.client.options;

public enum EMinecraftMainHand {
    LEFT,
    RIGHT;

    public String getStringValue() {
        switch (this){
            case LEFT:
                return "left";
            default:
            case RIGHT:
                return "right";
        }
    }

    public static EMinecraftMainHand fromString(String value) {
        switch (value.toLowerCase()) {
            case "left":
                return LEFT;
            default:
            case "right":
                return RIGHT;
        }
    }
}
