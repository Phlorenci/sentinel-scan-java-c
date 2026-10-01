// Loads known test hashes from a text file and checks if a hash is in the list.

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class ThreatDatabase {

    private HashMap<String, String> threats = new HashMap<>();

    public void load(File file) {
        threats.clear();

        if (!file.exists()) {
            System.out.println("Warning: threat file not found: " + file.getPath());
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.replace("\uFEFF", "").trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] parts = line.split(",", 2);
                String hash = parts[0].trim().toLowerCase();
                String name = "test threat";
                if (parts.length > 1) {
                    name = parts[1].trim();
                }
                threats.put(hash, name);
            }
        } catch (IOException e) {
            System.out.println("Could not read threat file.");
        }
    }

    public boolean isKnownThreat(String hash) {
        return threats.containsKey(hash.toLowerCase());
    }

    public String getThreatName(String hash) {
        return threats.get(hash.toLowerCase());
    }

    public int getCount() {
        return threats.size();
    }
}