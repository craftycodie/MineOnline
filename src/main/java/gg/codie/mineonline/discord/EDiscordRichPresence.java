package gg.codie.mineonline.discord;

public enum EDiscordRichPresence {
    /** Show what you're playing, including the server address, and allow "Ask to Join". */
    ON,
    /** Show what you're playing, but never the server address. */
    PRIVATE,
    /** Don't show anything on Discord. */
    OFF;

    public EDiscordRichPresence next() {
        return values()[(ordinal() + 1) % values().length];
    }

    public static EDiscordRichPresence fromString(String value) {
        for (EDiscordRichPresence mode : values()) {
            if (mode.name().equalsIgnoreCase(value))
                return mode;
        }
        return ON;
    }
}
