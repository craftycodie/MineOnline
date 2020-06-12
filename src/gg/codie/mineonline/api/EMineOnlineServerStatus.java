package gg.codie.mineonline.api;

import org.lwjgl.util.vector.Vector3f;

public enum EMineOnlineServerStatus {
    ON_THE_WHITELIST,
    BANNED,
    OFFLINEMODE,
    NOT_ON_THE_WHITELIST,
    NONE;

    public String toString() {
        switch(this) {
            case BANNED:
                return "Banned";
            case OFFLINEMODE:
                return "Offline Mode";
            case ON_THE_WHITELIST:
                return "Whitelisted";
            case NOT_ON_THE_WHITELIST:
                return "You're not Whitelisted";
        }
        return null;
    }

    public Vector3f getColor() {
        switch (this) {
            case NOT_ON_THE_WHITELIST:
            case OFFLINEMODE:
            case BANNED:
                return new Vector3f(1f, 0.33f, 0.33f);
            case ON_THE_WHITELIST:
                return new Vector3f(0.33f, 1f, 0.33f);
        }
        return new Vector3f(1, 1, 1);
    }

    public boolean canJoin() {
        switch (this) {
            case NOT_ON_THE_WHITELIST:
            case BANNED:
                return false;
            case OFFLINEMODE:
            case ON_THE_WHITELIST:
            default:
                return true;
        }
    }
}
