package gg.codie.minecraft.client.options;

public enum EMinecraftParticles {
    ALL,
    DECREASED,
    MINIMAL;

    public int getIntValue() {
        switch (this){
            case ALL:
                return 0;
            case DECREASED:
                return 1;
            case MINIMAL:
                return 2;
        }

        return 0;
    }
}
