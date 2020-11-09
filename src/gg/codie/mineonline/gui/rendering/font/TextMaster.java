package gg.codie.mineonline.gui.rendering.font;

import gg.codie.mineonline.gui.font.FontType;
import gg.codie.mineonline.gui.font.GUIText;
import gg.codie.mineonline.gui.font.TextMeshData;
import gg.codie.mineonline.gui.rendering.Loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextMaster {

    private static Loader loader;
    private static Map<FontType, List<GUIText>> texts = new HashMap<FontType, List<GUIText>>();
    private static FontRenderer renderer;
    public static FontType minecraftFont;


    public static void init(Loader theLoader){
        minecraftFont = new FontType(theLoader.loadTexture("/font/font.png", TextMaster.class.getResource("/font/font.png")), TextMaster.class.getResourceAsStream("/font/font.fnt"));
        renderer = new FontRenderer();
        loader = theLoader;
    }

    public static void render(){
        renderer.render(texts);
    }

    public static void loadText(GUIText text){
        FontType font = text.getFont();
        TextMeshData data = font.loadText(text);
        int vao = loader.loadToVAO(data.getVertexPositions(), data.getTextureCoords());
        text.setMeshInfo(vao, data.getVertexCount());
        List<GUIText> textBatch = texts.get(font);
        if(textBatch == null){
            textBatch = new ArrayList();
            texts.put(font, textBatch);
        }
        textBatch.add(text);
    }

    public static boolean hasText(GUIText text){
        return texts.get(text.getFont()).contains(text);
    }

    public static void removeText(GUIText text){
        try {
            List<GUIText> textBatch = texts.get(text.getFont());
            textBatch.remove(text);
            if (textBatch.isEmpty()) {
                texts.remove(texts.get(text.getFont()));
            }
        } catch (NullPointerException ex) {
            // if there's no text then there's no need to remove it.
        }
    }

    public static void cleanUp(){
        renderer.cleanUp();
    }

}
