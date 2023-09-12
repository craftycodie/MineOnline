package com.ahnewark.mineonline.client;

public interface IMinecraftAppletWrapper {
    void closeApplet();
    Class getMinecraftAppletClass();
    int getWidth();
    int getHeight();
}
