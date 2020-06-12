package gg.codie.mineonline.gui.font;

import gg.codie.mineonline.gui.rendering.DisplayManager;
import gg.codie.mineonline.gui.rendering.font.TextMaster;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/**
 * Represents a piece of text in the game.
 *
 */
public class GUIText {

    public final String textString;
    private float unscaledFontSize;
    private float fontSize;

    private int textMeshVao;
    private int vertexCount;
    private Vector3f colour = new Vector3f(1f, 1f, 1f);

    private Vector2f position;
    private float lineMaxSize;
    private int numberOfLines;

    public int getMaxLines() {
        return maxLines;
    }

    public void setMaxLines(int maxLines) {
        this.maxLines = maxLines;
        this.remove();
        TextMaster.loadText(this);
    }

    private int maxLines = 1;

    private FontType font;

    private boolean centerText;

    public boolean isCenterAnchored() {
        return centerAnchored;
    }

    private boolean centerAnchored = true;

    public Vector2f getYBounds() {
        return yBounds;
    }

    public void setYBounds(Vector2f yBounds) {
        this.yBounds = yBounds;
    }

    private Vector2f yBounds = new Vector2f();

    /**
     * Creates a new text, loads the text's quads into a VAO, and adds the text
     * to the screen.
     *
     * @param text
     *            - the text.
     * @param unscaledFontSize
     *            - the font size of the text, where a font size of 1 is the
     *            default size.
     * @param font
     *            - the font that this text should use.
     * @param position
     *            - the position on the screen where the top left corner of the
     *            text should be rendered. The top left corner of the screen is
     *            (0, 0) and the bottom right is (1, 1).
     * @param maxLineLength
     *            - basically the width of the virtual page in terms of screen
     *            width (1 is full screen width, 0.5 is half the width of the
     *            screen, etc.) Text cannot go off the edge of the page, so if
     *            the text is longer than this length it will go onto the next
     *            line. When text is centered it is centered into the middle of
     *            the line, based on this line length value.
     * @param centered
     *            - whether the text should be centered or not.
     */
    public GUIText(String text, float unscaledFontSize, FontType font, Vector2f position, float maxLineLength,
                   boolean centered, boolean centerAnchored) {
        this.textString = text;
        this.unscaledFontSize = unscaledFontSize;
        this.fontSize = unscaledFontSize;
        this.font = font;
        this.position = position;
        this.lineMaxSize = maxLineLength;
        this.centerText = centered;
        this.centerAnchored = centerAnchored;
        this.setNumberOfLines(1);

        if(!this.centerAnchored) {
            double xScale = (double) Display.getWidth() / DisplayManager.getDefaultWidth();
            double yScale = (double) Display.getHeight() / DisplayManager.getDefaultHeight();

            double scale = yScale;

            if(xScale < yScale)
                scale = xScale;

            fontSize = unscaledFontSize * (1 / (float)scale);
            fontSize = fontSize * (float)DisplayManager.getScale();
        }

        TextMaster.loadText(this);
    }

    public void resize() {
        if(!this.centerAnchored) {
            remove();

            double xScale = (double) Display.getWidth() / DisplayManager.getDefaultWidth();
            double yScale = (double) Display.getHeight() / DisplayManager.getDefaultHeight();

            double scale = yScale;

            if(xScale < yScale)
                scale = xScale;

            fontSize = unscaledFontSize * (1 / (float)scale);
            fontSize = fontSize * (float)DisplayManager.getScale();

            TextMaster.loadText(this);
        }
    }

    /**
     * Remove the text from the screen.
     */
    public void remove() {
        TextMaster.removeText(this);
    }

    /**
     * @return The font used by this text.
     */
    public FontType getFont() {
        return font;
    }

    /**
     * Set the colour of the text.
     *
     * @param r
     *            - red value, between 0 and 1.
     * @param g
     *            - green value, between 0 and 1.
     * @param b
     *            - blue value, between 0 and 1.
     */
    public void setColour(float r, float g, float b) {
        colour.set(r, g, b);
    }

    /**
     * @return the colour of the text.
     */
    public Vector3f getColour() {
        return colour;
    }

    /**
     * @return The number of lines of text. This is determined when the text is
     *         loaded, based on the length of the text and the max line length
     *         that is set.
     */
    public int getNumberOfLines() {
        return numberOfLines;
    }

    /**
     * @return The position of the top-left corner of the text in screen-space.
     *         (0, 0) is the top left corner of the screen, (1, 1) is the bottom
     *         right.
     */
    public Vector2f getPosition() {
        return new Vector2f((DisplayManager.scaledWidth(position.x)) / DisplayManager.scaledWidth(DisplayManager.getDefaultWidth()), (DisplayManager.scaledHeight(position.y)) / DisplayManager.scaledHeight(DisplayManager.getDefaultHeight()));
    }

    /**
     * @return the ID of the text's VAO, which contains all the vertex data for
     *         the quads on which the text will be rendered.
     */
    public int getMesh() {
        return textMeshVao;
    }

    /**
     * Set the VAO and vertex count for this text.
     *
     * @param vao
     *            - the VAO containing all the vertex data for the quads on
     *            which the text will be rendered.
     * @param verticesCount
     *            - the total number of vertices in all of the quads.
     */
    public void setMeshInfo(int vao, int verticesCount) {
        this.textMeshVao = vao;
        this.vertexCount = verticesCount;
    }

    /**
     * @return The total number of vertices of all the text's quads.
     */
    public int getVertexCount() {
        return this.vertexCount;
    }

    /**
     * @return the font size of the text (a font size of 1 is normal).
     */
    protected float getFontSize() {
        return fontSize;
    }

    /**
     * Sets the number of lines that this text covers (method used only in
     * loading).
     *
     * @param number
     */
    protected void setNumberOfLines(int number) {
        this.numberOfLines = number;
    }

    /**
     * @return {@code true} if the text should be centered.
     */
    protected boolean isCentered() {
        return centerText;
    }

    /**
     * @return The maximum length of a line of this text.
     */
    protected float getMaxLineSize() {
        return (lineMaxSize) / DisplayManager.getDefaultWidth();
    }

    /**
     * @return The string of text.
     */
    protected String getTextString() {
        return textString;
    }


    public int getLineLength() {
        return (int)(font.loader.createStructure(this).get(0).getLineLength() * DisplayManager.getDefaultWidth());
    }

}
