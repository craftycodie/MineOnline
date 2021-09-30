package gg.codie.mineonline.patches.minecraft;

import gg.codie.mineonline.Session;
import net.bytebuddy.asm.Advice;

public class PCGamerDemoAdvice {
    @Advice.OnMethodExit()
    static void intercept(
            @Advice.FieldValue(value = "b", readOnly = false) String username,
            @Advice.FieldValue(value = "c", readOnly = false) String sessionToken
    ) {
        username = Session.session.getUsername();
        sessionToken = Session.session.getAccessToken();
    }
}
