package gg.codie.mineonline.patches.minecraft;

import net.bytebuddy.asm.Advice;

public class FoliageColorizerM1Advice {
    @Advice.OnMethodExit()
    public static void intercept(@Advice.Return(readOnly = false) int returnColor) {
        if (returnColor == 0x80a755)
            returnColor = 0x55a780;
    }
}
