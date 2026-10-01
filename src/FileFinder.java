// finds files inside a folder

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileFinder {

    public List<File> findFiles(File folder) {
        List<File> files = new ArrayList<>();

        File[] items = folder.listFiles();
        if (items == null) {
            return files;
        }

        for (File item : items) {
            if (item.isFile()) {
                files.add(item);
            }
        }
        return files;
    }
}