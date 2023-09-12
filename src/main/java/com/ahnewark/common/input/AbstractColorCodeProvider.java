package com.ahnewark.common.input;

public abstract class AbstractColorCodeProvider implements IColorCodeProvider {
    public String removeColorCodes(String in){
        for (EColorCodeColor chatColor : EColorCodeColor.values()) {
            in = in.replace(getColorCode(chatColor), "");
        }

        return in;
    }
}
