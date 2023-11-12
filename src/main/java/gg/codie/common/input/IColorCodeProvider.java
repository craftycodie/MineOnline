package gg.codie.common.input;

public interface IColorCodeProvider {
    String getColorCode(EColorCodeColor chatColor);
    String removeColorCodes(String text);
}
