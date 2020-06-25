package gg.codie.minecraft.skins;

import gg.codie.mineonline.gui.IMenuScreen;
import gg.codie.mineonline.gui.rendering.Renderer;
import gg.codie.mineonline.gui.rendering.*;
import gg.codie.mineonline.gui.rendering.animation.WalkPlayerAnimation;
import gg.codie.utils.FileChangeListener;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.List;

public class SkinPreviewMenuScreen implements IMenuScreen {
    //GUIText label;

    String skinPath = "";

    DropTargetAdapter dropTargetAdapter;
    static DropTarget dropTarget = new DropTarget();
    FileChangeListener skinFileChangedListener;

    public SkinPreviewMenuScreen() {
        PlayerGameObject.thePlayer.setPlayerAnimation(new WalkPlayerAnimation());

        dropTarget.setComponent(DisplayManager.getCanvas());

        dropTargetAdapter = new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent event) {
                // Accept copy drops
                event.acceptDrop(DnDConstants.ACTION_COPY);

                // Get the transfer which can provide the dropped item data
                Transferable transferable = event.getTransferable();

                // Get the data formats of the dropped item
                DataFlavor[] flavors = transferable.getTransferDataFlavors();

                // Loop through the flavors
                for (DataFlavor flavor : flavors) {

                    try {

                        // If the drop items are files
                        if (flavor.isFlavorJavaFileListType()) {

                            // Get all of the dropped files
                            java.util.List<File> files = (List<File>) transferable.getTransferData(flavor);

                            skinPath = files.get(0).getPath();

                            if(skinPath.isEmpty())
                                return;

                            File skinTexture = new File(skinPath);
                            if (skinTexture.exists() && PlayerGameObject.thePlayer != null) {
                                try {
                                    PlayerGameObject.thePlayer.setSkin(Paths.get(skinTexture.getPath()).toUri().toURL());

                                    if(skinFileChangedListener != null) {
                                        skinFileChangedListener.stop();
                                    }

                                    skinFileChangedListener = new FileChangeListener(skinTexture.getPath(), new FileChangeListener.FileChangeEvent() {
                                        @Override
                                        public void onFileChange(String filePath) {
                                            try {
                                                PlayerGameObject.thePlayer.setSkin(Paths.get(skinTexture.getPath()).toUri().toURL());
                                            } catch (Exception ex) {
                                                // this shouldn't be possible
                                            }
                                        }
                                    });

                                    new Thread(skinFileChangedListener).start();

                                } catch (MalformedURLException mx) {
                                    // Might want to show the user somehow.
                                }
                            }

                        }

                    } catch (Exception e) {

                        // Print out the error stack
                        e.printStackTrace();

                    }
                }

                // Inform that the drop is complete
                event.dropComplete(true);
            }
        };

        try {
            dropTarget.addDropTargetListener(dropTargetAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {

    }

    public void render(Renderer renderer) {

    }

    public boolean showPlayer() {
        return true;
    }

    public void resize() {

    }

    @Override
    public void cleanUp() {

    }
}
