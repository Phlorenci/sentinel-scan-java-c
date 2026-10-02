// Console program: scans a folder, stores each file's result in a ScanResult object, and prints the results.

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("=== Simple File Scanner (Stage 6) ===");
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
        List<ScanResult> results = new ArrayList<>();

        for (File file : files) {
            FileInfo info = new FileInfo(file);
            String hash = hasher.calculateSha256(file);
            String riskLevel;
            String reason;

            if (hash.startsWith("error")) {
                riskLevel = "UNKNOWN";
                reason = "File could not be read";
            } else if (database.isKnownThreat(hash)) {
                riskLevel = "HIGH";
                reason = "SHA-256 hash found in local threat list: " + database.getThreatName(hash);
            } else {
                riskLevel = "LOW";
                reason = "Hash not found in local threat list";
            }

            results.add(new ScanResult(info, hash, riskLevel, reason));
        }

        System.out.println();
        System.out.println("Scanning: " + folder.getAbsolutePath());
        System.out.println("-----------------------------------");

        long totalSize = 0;
        int threatsFound = 0;
        int number = 1;
        for (ScanResult result : results) {
            FileInfo info = result.getInfo();
            String relative = folder.toPath().relativize(result.getFile().toPath()).toString();

            System.out.println(number + ". " + relative);
            System.out.println("   Path:      " + info.getPath());
            System.out.println("   Size:      " + info.getReadableSize());
            System.out.println("   Extension: " + info.getExtension());
            System.out.println("   Created:   " + info.getCreated());
            System.out.println("   Modified:  " + info.getModified());
            System.out.println("   Accessed:  " + info.getAccessed());
            System.out.println("   SHA-256:   " + result.getHash());
            System.out.println("   Risk:      " + result.getRiskLevel());
            System.out.println("   Reason:    " + result.getReason());
            System.out.println();

            totalSize = totalSize + info.getSize();
            if (result.getRiskLevel().equals("HIGH")) {
                threatsFound++;
            }
            number++;
        }

        System.out.println("-----------------------------------");
        System.out.println("Total files:     " + results.size());
        System.out.println("Total size:      " + totalSize + " bytes");
        System.out.println("Threats found:   " + threatsFound);
        System.out.println("Folders scanned: " + finder.getFoldersScanned());
        System.out.println("Folders skipped: " + finder.getFoldersSkipped());
    }
}