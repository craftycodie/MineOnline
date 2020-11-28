package gg.codie.mineonline.client.options;

public enum EMineOnlineAntiAliasing {
    OFF,
    TWO,
    FOUR,
    EIGHT,
    SIXTEEN;

    public int getIntValue() {
        switch (this){
            default:
            case OFF:
                return 0;
            case TWO:
                return 2;
            case FOUR:
                return 4;
            case EIGHT:
                return 8;
            case SIXTEEN:
                return 16;
        }
    }

    public String getName() {
        switch (this){
            default:
            case OFF:
                return "Off";
            case TWO:
                return "2x";
            case FOUR:
                return "4x";
            case EIGHT:
                return "8x";
            case SIXTEEN:
                return "16x";
        }
    }
}
