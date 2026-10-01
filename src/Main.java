// Console program: asks for a folder, lists all files in it with their details, and shows the counts.

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("=== Simple File Scanner (Stage 3) ===");
        System.out.print("Enter folder path: ");
        String path = input.nextLine().trim();

        File folder = new File(path);

        if (!folder.exists()) {
            System.out.println("Error: this path does not exist.");
            return;
        }
        if (!folder.isDirectory()) {
            System.out.println("Error: this path is not a folder.");
            return;
        }

        FileFinder finder = new FileFinder();
        List<File> files = finder.findFiles(folder);

        System.out.println();
        System.out.println("Scanning: " + folder.getAbsolutePath());
        System.out.println("-----------------------------------");

        long totalSize = 0;
        int number = 1;
        for (File file : files) {
            FileInfo info = new FileInfo(file);
            String relative = folder.toPath().relativize(file.toPath()).toString();

            System.out.println(number + ". " + relative);
            System.out.println("   Path:      " + info.getPath());
            System.out.println("   Size:      " + info.getReadableSize());
            System.out.println("   Extension: " + info.getExtension());
            System.out.println("   Created:   " + info.getCreated());
            System.out.println("   Modified:  " + info.getModified());
            System.out.println("   Accessed:  " + info.getAccessed());
            System.out.println();

            totalSize = totalSize + info.getSize();
            number++;
        }

        System.out.println("-----------------------------------");
        System.out.println("Total files:     " + files.size());
        System.out.println("Total size:      " + totalSize + " bytes");
        System.out.println("Folders scanned: " + finder.getFoldersScanned());
        System.out.println("Folders skipped: " + finder.getFoldersSkipped());
    }
}