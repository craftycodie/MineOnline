package gg.codie.minecraft.server;

import gg.codie.common.input.AbstractColorCodeProvider;
import gg.codie.common.input.EColorCodeColor;

public abstract class AbstractMinecraftColorCodeProvider extends AbstractColorCodeProvider {
    public abstract String getPrefix();

    @Override
    public String getColorCode(EColorCodeColor chatColor) {
        switch (chatColor) {
            case Black:
                return getPrefix() + '0';
            case DarkBlue:
                return getPrefix() + '1';
            case DarkGreen:
                return getPrefix() + '2';
            case DarkTeal:
                return getPrefix() + '3';
            case DarkRed:
                return getPrefix() + '4';
            case Purple:
                return getPrefix() + '5';
            case Gold:
                return getPrefix() + '6';
            case Gray:
                return getPrefix() + '7';
            case DarkGray:
                return getPrefix() + '8';
            case Blue:
                return getPrefix() + '9';
            case BrightGreen:
                return getPrefix() + 'a';
            case Teal:
                return getPrefix() + 'b';
            case Red:
                return getPrefix() + 'c';
            case Pink:
                return getPrefix() + 'd';
            case Yellow:
                return getPrefix() + 'e';
            case White:
            default:
                return getPrefix() + 'f';
        }
    }
}
