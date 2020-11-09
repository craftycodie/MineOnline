// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package gg.codie.mineonline.gui.input;

public abstract class InputSanitization
{
    public static final char allowedCharactersArray[] = {
            '/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\',
            '<', '>', '|', '"', ':',
    };
    public static final String allowedCharacters = new String(allowedCharactersArray);
}
