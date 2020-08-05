package gg.codie.mineonline.patches;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.matcher.ElementMatchers;

import java.net.URL;

public class URLPatch {
    public static void redefineURL() {
//        new ByteBuddy()
//                .with(Implementation.Context.Disabled.Factory.INSTANCE)
//                .redefine(URL.class)
//                .visit(Advice.to(URLSetAdvice.class).on(ElementMatchers.named("set").and(ElementMatchers.takesArguments(
//                        String.class, String.class, int.class, String.class, String.class
//
//
//                ))))
//                .make()
//                .load(URL.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
//
//        new ByteBuddy()
//                .with(Implementation.Context.Disabled.Factory.INSTANCE)
//                .redefine(URL.class)
//                .visit(Advice.to(URLSetAdvice.class).on(ElementMatchers.named("set").and(ElementMatchers.takesArguments(
//                        String.class, String.class, int.class, String.class, String.class, String.class, String.class, String.class
//                ))))
//                .make()
//                .load(URL.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

        new ByteBuddy()
                .with(Implementation.Context.Disabled.Factory.INSTANCE)
                .redefine(URL.class)
                .visit(Advice.to(URLConstructAdvice.class).on(ElementMatchers.isConstructor().and(ElementMatchers.takesArguments(
                        String.class
                ))))
                .make()
                .load(URL.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

//        new ByteBuddy()
//            .redefine(UUID.class)
//            .visit(Advice.to(UUIDToStringAdvice.class).on(ElementMatchers.named("toString")))
//            //.method(ElementMatchers.named("getId"))
//            //.intercept(FixedValue.value(UUID.nameUUIDFromBytes(Session.session.getUsername().getBytes())))
//            .make()
//            .load(UUID.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

        try {
//            new ByteBuddy()
//                    .redefine(URLPatch.class.getClassLoader().loadClass("com.mojang.authlib.yggdrasil.YggdrasilEnvironment"))
//                    .method(ElementMatchers.named("getName"))
//                    .intercept(FixedValue.value("PROD"))
//                    .method(ElementMatchers.named("getAuthHost"))
//                    .intercept(FixedValue.value("http://" + Globals.API_HOSTNAME))
//                    .method(ElementMatchers.named("getAccountsHost"))
//                    .intercept(FixedValue.value("http://" + Globals.API_HOSTNAME))
//                    .method(ElementMatchers.named("getSessionHost"))
//                    .intercept(FixedValue.value("http://" + Globals.API_HOSTNAME))
//                    .make()
//                    .load(Class.forName("com.mojang.authlib.yggdrasil.YggdrasilEnvironment").getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());



//            new ByteBuddy()
//                    .redefine(UUID.class)
//                    .visit(Advice.to(UUIDToStringAdvice.class).on(ElementMatchers.named("toString")))
//                    //.method(ElementMatchers.named("getId"))
//                    //.intercept(FixedValue.value(UUID.nameUUIDFromBytes(Session.session.getUsername().getBytes())))
//                    .make()
//                    .load(UUID.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

//            new ByteBuddy()
//                    .redefine(URLPatch.class.getClassLoader().loadClass("com.mojang.authlib.yggdrasil.YggdrasilEnvironment"))
//                    .method(ElementMatchers.named("getName"))
//                    .intercept(FixedValue.value("MineOnline"))
//                    .method(ElementMatchers.named("getAuthHost"))
//                    .intercept(FixedValue.value("http://" + Globals.API_HOSTNAME))
//                    .method(ElementMatchers.named("getAccountsHost"))
//                    .intercept(FixedValue.value("http://" + Globals.API_HOSTNAME))
//                    .method(ElementMatchers.named("getSessionHost"))
//                    .intercept(FixedValue.value("http://" + Globals.API_HOSTNAME))
//                    .visit(Advice.to(EnvironmentConstructAdvice.class).on(ElementMatchers.isConstructor().and(ElementMatchers.takesArguments(
//                            String.class, String.class, String.class
//                    ))))
//                    .make()
//                    .load(Class.forName("com.mojang.authlib.yggdrasil.YggdrasilEnvironment").getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

//            new ByteBuddy()
//                    .redefine(URLPatch.class.getClassLoader().loadClass("com.mojang.authlib.yggdrasil.YggdrasilEnvironment"))
//                    .method(ElementMatchers.named("getAuthHost"))
//                    .intercept(FixedValue.value("http://" + Globals.API_HOSTNAME))
//                    .make()
//                    .load(Class.forName("com.mojang.authlib.yggdrasil.YggdrasilEnvironment").getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

//            new ByteBuddy()
//                    .redefine(URLPatch.class.getClassLoader().loadClass("com.mojang.authlib.Environment"))
//                    .method(ElementMatchers.named("getName"))
//                    .intercept(FixedValue.value("PROD"))
//                    .method(ElementMatchers.named("getAuthHost"))
//                    .intercept(FixedValue.value("http://" + Globals.API_HOSTNAME))
//                    .method(ElementMatchers.named("getAccountsHost"))
//                    .intercept(FixedValue.value("http://" + Globals.API_HOSTNAME))
//                    .method(ElementMatchers.named("getSessionHost"))
//                    .intercept(FixedValue.value("http://" + Globals.API_HOSTNAME))
//                    .visit(Advice.to(EnvironmentConstructAdvice.class).on(ElementMatchers.named("create").and(ElementMatchers.takesArguments(
//                            String.class, String.class, String.class, String.class
//                    ))))
//                    .make()
//                    .load(Class.forName("com.mojang.authlib.Environment").getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

//            new ByteBuddy()
//                    .redefine(URLPatch.class.getClassLoader().loadClass("com.mojang.authlib.YggdrasilEnvironment"))
//                    .visit(Advice.to(EnvironmentConstructAdvice.class).on(ElementMatchers.named("create").and(ElementMatchers.takesArguments(
//                            String.class, String.class, String.class, String.class
//                    ))))
//                    .make()
//                    .load(Class.forName("com.mojang.authlib.YggdrasilEnvironment").getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

        } catch (Exception ex) {
            ex.printStackTrace();
        }

//        try {
//            new ByteBuddy()
//                    .with(Implementation.Context.Disabled.Factory.INSTANCE)
//                    .redefine(URLPatch.class.getClassLoader().loadClass("com.mojang.authlib.yggdrasil.YggdrasilEnvironment"))
//                    .visit(Advice.to(EnvironmentConstructAdvice.class).on(ElementMatchers.isConstructor().and(ElementMatchers.takesArguments(
//                            String.class
//                    ))))
//                    .make()
//                    .load(Class.forName("com.mojang.authlib.yggdrasil.YggdrasilEnvironment").getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
    }
}
