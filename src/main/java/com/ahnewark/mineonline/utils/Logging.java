package com.ahnewark.mineonline.utils;

import com.ahnewark.mineonline.LauncherFiles;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class Logging {
    public static void deleteLog() {
        File log = new File(LauncherFiles.MINEONLINE_LATEST_LOG);
        if (log.exists())
            log.delete();
    }

    public static void enableLogging() {
        try {
            PrintStream fileStream = new PrintStream(new BufferedOutputStream(new FileOutputStream(LauncherFiles.MINEONLINE_LATEST_LOG, true)), true);
            System.setOut(new LogAndConsolePrintStream(fileStream, System.out));
            System.setErr(new LogAndConsolePrintStream(fileStream, System.err));
        } catch (Exception ex) {
            System.err.println("Failed to enable logging.");
        }
    }

    // File might be missing some logs.
    private static class LogAndConsolePrintStream extends PrintStream
    {
        PrintStream fileStream;
        public LogAndConsolePrintStream(PrintStream fileStream, PrintStream out)
        {
            super(out);
            this.fileStream = fileStream;
        }

        public void println(String x) {
            super.println(x);
            fileStream.println(x);
        }

        public void println(Object x) {
            super.println(x);
            fileStream.println(x);
        }
    }
}
