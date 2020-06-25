package gg.codie.utils;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

public class FileChangeListener implements Runnable {

    public interface FileChangeEvent {
        void onFileChange(String filePath);
    }

    public final String fileName;
    public final String fullFilePath;
    public final FileChangeEvent callback;

    boolean stopFlag;

    public FileChangeListener(final String filePath, final FileChangeEvent callback) {
        this.fullFilePath = filePath;
        this.fileName = Paths.get(filePath).getFileName().toString();
        this.callback = callback;
    }

    public void run() {
        try {
            stopFlag = false;
            startWatcher(Paths.get(fullFilePath).getParent().toString(), fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        stopFlag = true;
    }

    private void startWatcher(String dirPath, String file) throws IOException {
        final WatchService watchService = FileSystems.getDefault()
                .newWatchService();
        Path path = Paths.get(dirPath);
        path.register(watchService, ENTRY_MODIFY);

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
                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.context().toString().equals(fileName)) {
                        callback.onFileChange(fullFilePath);
                    }
                }
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