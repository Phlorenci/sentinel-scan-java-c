// console program that accepts a folder, finds files, displays filenames, counts files

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("=== Simple File Scanner (Stage 1) ===");
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
        System.out.println("Files found in: " + folder.getAbsolutePath());
        System.out.println("-----------------------------------");

        int number = 1;
        for (File file : files) {
            System.out.println(number + ". " + file.getName());
            number++;
        }

        System.out.println("-----------------------------------");
        System.out.println("Total files: " + files.size());
    }
}