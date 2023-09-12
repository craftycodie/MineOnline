package com.ahnewark.mineonline.patches.minecraft;

import com.ahnewark.mineonline.gui.rendering.Renderer;
import com.ahnewark.mineonline.patches.HashMapPutAdvice;
import net.bytebuddy.asm.Advice;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ItemRendererAdvice {
    public static float terrainScale = 1;
    public static float itemScale = 1;

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    static boolean intercept(@Advice.AllArguments Object[] args, @Advice.This Object thisObj) {
        Object entityLiving = null;
        Object itemStack = null;
        int itemIcon = 0;

        try {
            if (args.length == 2) {
                entityLiving = args[0];
                itemStack = args[1];
                for (Method method : entityLiving.getClass().getMethods()) {
                    if (method.getParameterCount() == 1 && method.getParameterTypes()[0] == itemStack.getClass() && method.getReturnType() == int.class) {
                        itemIcon = (int) method.invoke(entityLiving, itemStack);
                        break;
                    }
                }
            } else {
                if (!args[0].getClass().isAssignableFrom(float.class)) {
                    itemStack = args[0];
                    for (Method method : itemStack.getClass().getMethods()) {
                        if (method.getParameterCount() == 0 && method.getReturnType() == int.class && method.getName().equals("b")) {
                            itemIcon = (int) method.invoke(itemStack);
                            break;
                        }
                    }
                }
                else {
                    // Pre alpha is not currently supported.
                    System.err.println("Pre-alpha item renderer patch is currently not supported.");
                    return false;
                    //itemStack = thisObj.getClass().getField("b").get(thisObj);
                }
            }

            int itemID = itemStack.getClass().getField("c").getInt(itemStack);

            Set<Integer> blocksRenderedLikeItems = new HashSet(Arrays.asList(new Integer[] {
                    6,          // sapling
                    8, 9,       // water
                    10, 11,     //lava
                    26,         // bed
                    27, 28, 66, // rails
                    30,         // cobweb
                    31,         // tall grass
                    32,         // dead bush
                    34,         // piston head
                    36,         // piston in motion?
                    37, 38,     // flowers
                    39, 40,     // mushrooms
                    50,         // torch
                    51,         // fire
                    55,         // redstone dust
                    59,         // wheat crops
                    63, 68,     // signs
                    64, 71,     // doors
                    65,         // ladder
                    69,         // lever
                    75, 76,     // redstone torch
                    83,         // sugar cane
                    93, 94,     // ???
            }));

            if (itemID < 256 && !blocksRenderedLikeItems.contains(itemID)) {
                return false;
            } else {
                int num = 16;

                GL11.glPushMatrix();
                if (itemID < 256) {
                    num = (int)(terrainScale * 16);
                    GL11.glBindTexture(3553, HashMapPutAdvice.textures.get("/terrain.png"));
                } else {
                    num = (int)(itemScale * 16);
                    GL11.glBindTexture(3553, HashMapPutAdvice.textures.get("/gui/items.png"));
                }

                Renderer tessellator = Renderer.singleton;
                float f = ((float) (itemIcon % 16 * 16) + 0.0F) / 256.0F;
                float f1 = ((float) (itemIcon % 16 * 16) + 15.99F) / 256.0F;
                float f2 = ((float) (itemIcon / 16 * 16) + 0.0F) / 256.0F;
                float f3 = ((float) (itemIcon / 16 * 16) + 15.99F) / 256.0F;
                float f4 = 1.0F;
                float f5 = 0.0F;
                float f6 = 0.3F;

                GL11.glEnable('耺');
                GL11.glTranslatef(-f5, -f6, 0.0F);
                float f7 = 1.5F;

                GL11.glScalef(f7, f7, f7);
                GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
                GL11.glTranslatef(-0.9375F, -0.0625F, 0.0F);
                float f8 = 0.0625F;

                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, 0.0F, 1.0F);
                tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, (double) f1, (double) f3);
                tessellator.addVertexWithUV((double) f4, 0.0D, 0.0D, (double) f, (double) f3);
                tessellator.addVertexWithUV((double) f4, 1.0D, 0.0D, (double) f, (double) f2);
                tessellator.addVertexWithUV(0.0D, 1.0D, 0.0D, (double) f1, (double) f2);
                tessellator.draw();
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, 0.0F, -1.0F);
                tessellator.addVertexWithUV(0.0D, 1.0D, (double) (0.0F - f8), (double) f1, (double) f2);
                tessellator.addVertexWithUV((double) f4, 1.0D, (double) (0.0F - f8), (double) f, (double) f2);
                tessellator.addVertexWithUV((double) f4, 0.0D, (double) (0.0F - f8), (double) f, (double) f3);
                tessellator.addVertexWithUV(0.0D, 0.0D, (double) (0.0F - f8), (double) f1, (double) f3);
                tessellator.draw();
                float du = 1.0F / (float) (32 * num);
                float dz = 1.0F / (float) num;

                tessellator.startDrawingQuads();
                tessellator.setNormal(-1.0F, 0.0F, 0.0F);

                int i1;
                float f12;
                float f16;
                float f20;

                for (i1 = 0; i1 < num; ++i1) {
                    f12 = (float) i1 / ((float) num * 1.0F);
                    f16 = f1 + (f - f1) * f12 - du;
                    f20 = f4 * f12;
                    tessellator.addVertexWithUV((double) f20, 0.0D, (double) (0.0F - f8), (double) f16, (double) f3);
                    tessellator.addVertexWithUV((double) f20, 0.0D, 0.0D, (double) f16, (double) f3);
                    tessellator.addVertexWithUV((double) f20, 1.0D, 0.0D, (double) f16, (double) f2);
                    tessellator.addVertexWithUV((double) f20, 1.0D, (double) (0.0F - f8), (double) f16, (double) f2);
                }

                tessellator.draw();
                tessellator.startDrawingQuads();
                tessellator.setNormal(1.0F, 0.0F, 0.0F);

                for (i1 = 0; i1 < num; ++i1) {
                    f12 = (float) i1 / ((float) num * 1.0F);
                    f16 = f1 + (f - f1) * f12 - du;
                    f20 = f4 * f12 + dz;
                    tessellator.addVertexWithUV((double) f20, 1.0D, (double) (0.0F - f8), (double) f16, (double) f2);
                    tessellator.addVertexWithUV((double) f20, 1.0D, 0.0D, (double) f16, (double) f2);
                    tessellator.addVertexWithUV((double) f20, 0.0D, 0.0D, (double) f16, (double) f3);
                    tessellator.addVertexWithUV((double) f20, 0.0D, (double) (0.0F - f8), (double) f16, (double) f3);
                }

                tessellator.draw();
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, 1.0F, 0.0F);

                for (i1 = 0; i1 < num; ++i1) {
                    f12 = (float) i1 / ((float) num * 1.0F);
                    f16 = f3 + (f2 - f3) * f12 - du;
                    f20 = f4 * f12 + dz;
                    tessellator.addVertexWithUV(0.0D, (double) f20, 0.0D, (double) f1, (double) f16);
                    tessellator.addVertexWithUV((double) f4, (double) f20, 0.0D, (double) f, (double) f16);
                    tessellator.addVertexWithUV((double) f4, (double) f20, (double) (0.0F - f8), (double) f, (double) f16);
                    tessellator.addVertexWithUV(0.0D, (double) f20, (double) (0.0F - f8), (double) f1, (double) f16);
                }

                tessellator.draw();
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, -1.0F, 0.0F);

                for (i1 = 0; i1 < num; ++i1) {
                    f12 = (float) i1 / ((float) num * 1.0F);
                    f16 = f3 + (f2 - f3) * f12 - du;
                    f20 = f4 * f12;
                    tessellator.addVertexWithUV((double) f4, (double) f20, 0.0D, (double) f, (double) f16);
                    tessellator.addVertexWithUV(0.0D, (double) f20, 0.0D, (double) f1, (double) f16);
                    tessellator.addVertexWithUV(0.0D, (double) f20, (double) (0.0F - f8), (double) f1, (double) f16);
                    tessellator.addVertexWithUV((double) f4, (double) f20, (double) (0.0F - f8), (double) f, (double) f16);
                }

                tessellator.draw();
                GL11.glDisable('耺');
                GL11.glPopMatrix();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }
}
