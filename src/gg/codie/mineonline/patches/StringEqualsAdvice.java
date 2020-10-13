package gg.codie.mineonline.patches;

import net.bytebuddy.asm.Advice;

import java.util.Arrays;

public class StringEqualsAdvice {
    @Advice.OnMethodExit
    public static void intercept(@Advice.This String thisObj, @Advice.Argument(0) Object other, @Advice.Return(readOnly = false) boolean returnObj) {
        if (!(other instanceof String))
            return;

        // If it's comparing with a legacy checkserv response...
        if (Arrays.equals(((String)other).toCharArray(), new char[] {'Y', 'E', 'S'})) {
            System.out.println("Wanted: " + Arrays.toString(new char[] { '{' }));
            System.out.println("Got: " + Arrays.toString(thisObj.toCharArray()));

            // If it looks like a modern checkserv response....
            if (Arrays.equals(thisObj.toCharArray(), new char[] { '{' })) {
                // Say they're equal.
                returnObj = true;
            }
        }
    }
}
