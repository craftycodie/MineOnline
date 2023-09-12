package com.ahnewark.common.utils;

import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;

public class FolderChangeListener implements Runnable {

    public interface FolderChangeEvent {
        void onFolderChange();
    }

    public final String fullFilePath;
    public final FolderChangeEvent callback;

    boolean stopFlag;

    public FolderChangeListener(final String filePath, final FolderChangeEvent callback) {
        this.fullFilePath = filePath;
        this.callback = callback;
    }

    public void run() {
        try {
            stopFlag = false;
            startWatcher(Paths.get(fullFilePath).getParent().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        stopFlag = true;
    }

    private void startWatcher(String dirPath) throws IOException {
        final WatchService watchService = FileSystems.getDefault()
                .newWatchService();
        Path path = Paths.get(dirPath);
        path.register(watchService, ENTRY_MODIFY, ENTRY_CREATE, ENTRY_DELETE);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    watchService.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        WatchKey key;
        while (!stopFlag) {
            try {
                key = watchService.take();
                if (key.pollEvents().size() > 0)
                    callback.onFolderChange();
                boolean reset = key.reset();
                if (!reset) {
                    System.out.println("Could not reset the watch key.");
                    break;
                }
            } catch (Exception e) {
                System.out.println("InterruptedException: " + e.getMessage());
            }
        }

        watchService.close();
    }
}