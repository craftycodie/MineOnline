package com.ahnewark.mineonline.patches;

import net.bytebuddy.asm.Advice;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.Month;

public class ClassGetResourceAdvice {

    @Advice.OnMethodExit
    static void intercept(@Advice.Argument(0) String resourceName, @Advice.Return(readOnly = false) InputStream inputStream) {
        try {
            if(resourceName.endsWith("splashes.txt")) {
                LocalDate today = LocalDate.now();
                if(today.getMonth() == Month.MARCH && today.getDayOfMonth() == 26) {
                    inputStream = new ByteArrayInputStream("Happy Birthday Alex!".getBytes());
                    return;
                }

                String customSplashes =
                        "Black lives matter!\n" +
                        "Be anti-racist!\n" +
                        "Learn about allyship!\n" +
                        "Speak OUT against injustice and UP for equality!\n" +
                        "Amplify and listen to BIPOC voices!\n" +
                        "Educate your friends on anti-racism!\n" +
                        "Support the BIPOC community and creators!\n" +
                        "Stand up for equality in your community!\n" +
                        "Trans Rights!\n" +
                        "Now Playing: Home - Resonance\n" +
                        "Now Playing: C418 - Figure 8\n" +
                        "MineOnline!\n" +
                        "@ahnewark\n";

                byte[] splashesTxt = new byte[inputStream.available()];
                inputStream.read(splashesTxt);

                byte[] splashBytes = (byte[])ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.common.utils.ArrayUtils").getMethod("concatenate", byte[].class, byte[].class).invoke(null, customSplashes.getBytes(), splashesTxt);

                inputStream = new ByteArrayInputStream(splashBytes);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
