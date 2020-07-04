package gg.codie.mineonline.patchagent;

import java.io.*;
import java.lang.String;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.jar.JarEntry;

public class PatchAgent {
    public static void premain(String args, Instrumentation inst) {
        System.out.println("Patch Agent Premain");
        main(inst);
    }

    public static void agentmain(String args, Instrumentation inst) {
        System.out.println("Patch Agent Agentmain");
        main(inst);
    }

    private static void main(Instrumentation inst) {
        try {
            System.out.println("Piss");
            inst.redefineClasses(new ClassDefinition[]{
                    new ClassDefinition(URL.class, getPatchedClassBytes("java/net/URL.class")),
                    //new ClassDefinition(String.class, getPatchedClassBytes("java/lang/String.class"))
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
//        inst.addTransformer(new ClassFileTransformer() {
//            @Override
//            public byte[] transform(ClassLoader loader,
//                                    String className,
//                                    Class<?> classBeingRedefined,
//                                    ProtectionDomain protectionDomain,
//                                    byte[] classfileBuffer) {
//                System.out.println("Transforming: " + className);
//                if (className.equals("java/net/URLConnection$1")) {
//                    System.out.println("Transforming URLConnection");
//                    try {
//                        return getPatchedClassBytes("java/net/URLConnection$1.class"); // as found in the repository
//                        // Consider removing the transformer for future class loading
//                    } catch (IOException | URISyntaxException e) {
//                        e.printStackTrace();
//                        return null;
//                    }
//                } else {
//                    return null; // skips instrumentation for other classes
//                }
//            }
//        });
    }

    public static byte[] getPatchedClassBytes(String path) throws IOException
    {
        System.out.println(PatcherFiles.PATCH_AGENT_JAR);

        File jarFile = new File(PatcherFiles.PATCH_AGENT_JAR);

        System.out.println(jarFile.getPath());


        if(!jarFile.exists() || jarFile.isDirectory())
            throw new FileNotFoundException("Jar file not found.");

        java.util.jar.JarFile jar = new java.util.jar.JarFile(jarFile.getPath());

        System.out.println(path);

        JarEntry patchedClass = jar.getJarEntry(path);

        System.out.println(patchedClass.getName());

        InputStream iStream = jar.getInputStream(patchedClass);
        try
        {
            ByteArrayOutputStream oStream = new ByteArrayOutputStream();

            while(iStream.available() > 0) {
                oStream.write(iStream.read());
            }

            return oStream.toByteArray();
        }
        finally
        {
            iStream.close();
        }
    }
}