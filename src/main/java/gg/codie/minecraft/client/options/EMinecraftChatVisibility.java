package gg.codie.minecraft.client.options;

public enum EMinecraftChatVisibility {
    SHOWN,
    COMMANDS_ONLY,
    HIDDEN;

    public int getIntValue() {
        switch (this){
            case SHOWN:
                return 0;
            case COMMANDS_ONLY:
                return 1;
            case HIDDEN:
                return 2;
        }

        return 0;
    }
}
