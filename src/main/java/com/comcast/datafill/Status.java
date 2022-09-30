package com.comcast.datafill;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;

/**
 *
 * @author bremed200
 */
public class Status {

    private Path lastStatusPath;
    private long filesCreated = 0;
    private long recordsCreated = 0;
    private long filespaceConsumed = 0;

    private long nextStatusUpdate = System.currentTimeMillis();
    private final Path statusDirectory;
    private final int updateSeconds;

    public final long startTime;

    private static final DecimalFormat df = new DecimalFormat("###,###,###");

    public Status(Path theStatusDirectory, Integer theUpdateInterval) {
        startTime = System.currentTimeMillis();
        statusDirectory = theStatusDirectory;
        updateSeconds = theUpdateInterval == null ? -1 : theUpdateInterval;
    }

    public long filesCreated() { return filesCreated; }
    public long recordsCreated() { return recordsCreated; }
    public long filespaceConsumed() { return filespaceConsumed; }

    public void incrementRecordCount(int count) {
        recordsCreated += count;
    }

    public void incrementFileCount(int count) {
        filesCreated += count;
    }

    public void addConsumed(long bytes) {
        filespaceConsumed += bytes;
    }

    private long elapsedTime() {
        return System.currentTimeMillis() - startTime;
    }

    public void showStats(PrintStream ps) {

        long elapsed = elapsedTime();

        ps.println();
        ps.println("  Files: " + df.format(filesCreated));
        ps.println("Records: " + df.format(recordsCreated));
        ps.println("  Space: " + df.format(filespaceConsumed) + " bytes");
        ps.println("   Time: " + df.format(elapsed/1000f) + " seconds");
        ps.println("  Speed: " + (float)elapsed/recordsCreated + " ms per record");
    }

    public String createFilename() {
        return "json-gen_f" + filesCreated + "_r" + recordsCreated + ".txt";
    }

    /**
     * Create an empty file whose name denotes the operation status.
     * If the file gets deleted, this is taken as a sign to terminate.
     *
     * @return
     * @throws IOException
     */
    public boolean renderStatus() throws IOException {

        if (statusDirectory == null) return true;

        long now = System.currentTimeMillis();
        if (now < nextStatusUpdate) return true;

        nextStatusUpdate = now + (updateSeconds * 1000);

        String newName = createFilename();

        if (lastStatusPath == null) {
            lastStatusPath = statusDirectory.resolve(newName);
            Files.write(lastStatusPath, "nothing here".getBytes(), StandardOpenOption.CREATE);
            return true;
        }

        File lastFile = lastStatusPath.toFile();
        if (!lastFile.exists()) return false;       // user deleted it

        Path newPath = statusDirectory.resolve(newName);
        lastFile.renameTo(newPath.toFile());
        lastStatusPath = newPath;
        return true;
    }
}
