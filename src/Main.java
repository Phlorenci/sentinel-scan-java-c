// Console program: scans a folder, shows file details and SHA-256, and checks each hash against the local threat list.

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("=== Simple File Scanner (Stage 5) ===");
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

        ThreatDatabase database = new ThreatDatabase();
        database.load(new File("known_threats.txt"));
        System.out.println("Threat list loaded: " + database.getCount() + " hash(es)");

        FileFinder finder = new FileFinder();
        HashCalculator hasher = new HashCalculator();
        List<File> files = finder.findFiles(folder);

        System.out.println();
        System.out.println("Scanning: " + folder.getAbsolutePath());
        System.out.println("-----------------------------------");

        long totalSize = 0;
        int threatsFound = 0;
        int number = 1;
        for (File file : files) {
            FileInfo info = new FileInfo(file);
            String hash = hasher.calculateSha256(file);
            String relative = folder.toPath().relativize(file.toPath()).toString();

            System.out.println(number + ". " + relative);
            System.out.println("   Path:      " + info.getPath());
            System.out.println("   Size:      " + info.getReadableSize());
            System.out.println("   Extension: " + info.getExtension());
            System.out.println("   Created:   " + info.getCreated());
            System.out.println("   Modified:  " + info.getModified());
            System.out.println("   Accessed:  " + info.getAccessed());
            System.out.println("   SHA-256:   " + hash);

            if (database.isKnownThreat(hash)) {
                System.out.println("   Status:    KNOWN TEST THREAT - " + database.getThreatName(hash));
                threatsFound++;
            } else {
                System.out.println("   Status:    not in threat list");
            }
            System.out.println();

            totalSize = totalSize + info.getSize();
            number++;
        }

        System.out.println("-----------------------------------");
        System.out.println("Total files:     " + files.size());
        System.out.println("Total size:      " + totalSize + " bytes");
        System.out.println("Threats found:   " + threatsFound);
        System.out.println("Folders scanned: " + finder.getFoldersScanned());
        System.out.println("Folders skipped: " + finder.getFoldersSkipped());
    }
}