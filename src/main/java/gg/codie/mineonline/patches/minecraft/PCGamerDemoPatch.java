package gg.codie.mineonline.patches.minecraft;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class PCGamerDemoPatch {
    public static void login() {
        try {
            new ByteBuddy()
                    .redefine(Class.forName("fd"))
                    .visit(Advice.to(PCGamerDemoAdvice.class).on(ElementMatchers.isConstructor()))
                    .make()
                    .load(ClassLoader.getSystemClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    // Ideally we'd loop through resources, but that doesn't work when debugging in IDE.
    public static List<String> patchedClasses = Arrays.asList(
            "a","cj","db","dm","dn","ex","fh","fp","fv","fx","gk","gy","hd","hj","hq","hu","hv","ia","jg","ji","jp","ju","jv","kb","kc","ke","kl","kr","kx","ld","ln","lt","lu","m","mj","mp","mv","nf","nj","nq","nx","oc","of","op","ov","pi","pq","ps","px","qc","qd","qq","qz","rf","rh","sb","sl","tp","x"
    );

    public static void addNetcode() {
        ByteBuddyAgent.getInstrumentation().addTransformer((loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {
            if (patchedClasses.contains(className)) {
                try {
                    InputStream is = PCGamerDemoPatch.class.getResourceAsStream("/patches/b1.3-demo-multiplayer-mod/" + className + ".class");
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                    int nRead;
                    byte[] data = new byte[16384];

                    while ((nRead = is.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, nRead);
                    }

                    return buffer.toByteArray();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return classfileBuffer;
        });
    }
}
