// Finds all files inside a folder and its subfolders.

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileFinder {

    private int foldersScanned = 0;
    private int foldersSkipped = 0;

    public List<File> findFiles(File folder) {
        foldersScanned = 0;
        foldersSkipped = 0;
        List<File> files = new ArrayList<>();
        scanFolder(folder, files);
        return files;
    }

    private void scanFolder(File folder, List<File> files) {
        File[] items = folder.listFiles();
        if (items == null) {
            foldersSkipped++;
            return;
        }
        foldersScanned++;
        Arrays.sort(items);
        for (File item : items) {
            if (item.isFile()) {
                files.add(item);
            } else if (item.isDirectory()) {
                scanFolder(item, files);
            }
        }
    }

    public int getFoldersScanned() {
        return foldersScanned;
    }

    public int getFoldersSkipped() {
        return foldersSkipped;
    }
}