// Holds basic information about one file: name, path, size, extension and timestamps.

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileInfo {

    private File file;
    private String name;
    private String path;
    private long size;
    private String extension;
    private String created = "unknown";
    private String modified = "unknown";
    private String accessed = "unknown";

    public FileInfo(File file) {
        this.file = file;
        this.name = file.getName();
        this.path = file.getAbsolutePath();
        this.size = file.length();
        this.extension = findExtension(file.getName());

        try {
            BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            created = formatTime(attrs.creationTime().toMillis());
            modified = formatTime(attrs.lastModifiedTime().toMillis());
            accessed = formatTime(attrs.lastAccessTime().toMillis());
        } catch (IOException e) {
            System.out.println("Could not read timestamps for " + file.getName());
        }
    }

    private String findExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        if (dot <= 0 || dot == fileName.length() - 1) {
            return "none";
        }
        return fileName.substring(dot + 1).toLowerCase();
    }

    private String formatTime(long millis) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date(millis));
    }

    public String getReadableSize() {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.1f KB", size / 1024.0);
        } else {
            return String.format("%.1f MB", size / (1024.0 * 1024.0));
        }
    }

    public File getFile() {
        return file;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public long getSize() {
        return size;
    }

    public String getExtension() {
        return extension;
    }

    public String getCreated() {
        return created;
    }

    public String getModified() {
        return modified;
    }

    public String getAccessed() {
        return accessed;
    }
}