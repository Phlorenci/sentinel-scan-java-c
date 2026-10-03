// Gives a file a risk score using simple rules and keeps a written reason for every rule that matched.

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RiskScorer {

    private List<String> riskyExtensions = Arrays.asList("exe", "dll", "bat", "cmd", "ps1", "vbs", "js", "scr", "jar", "msi");
    private List<String> documentExtensions = Arrays.asList("pdf", "doc", "docx", "txt", "jpg", "jpeg", "png", "xls", "xlsx");

    private int score = 0;
    private boolean unreadable = false;
    private List<String> reasons = new ArrayList<>();

    public void calculate(FileInfo info, String hash, ThreatDatabase database) {
        score = 0;
        unreadable = false;
        reasons.clear();

        if (hash.startsWith("error")) {
            unreadable = true;
            reasons.add("File could not be read");
            return;
        }

        if (database.isKnownThreat(hash)) {
            score = score + 100;
            reasons.add("Hash found in local threat list: " + database.getThreatName(hash) + " (+100)");
        }

        if (riskyExtensions.contains(info.getExtension())) {
            score = score + 20;
            reasons.add("Executable or script file type ." + info.getExtension() + " (+20)");
        }

        String[] parts = info.getName().toLowerCase().split("\\.");
        if (parts.length >= 3) {
            String last = parts[parts.length - 1];
            String before = parts[parts.length - 2];
            if (riskyExtensions.contains(last) && documentExtensions.contains(before)) {
                score = score + 30;
                reasons.add("Double extension hides an executable type (+30)");
            }
        }
    }

    public int getScore() {
        return score;
    }

    public String getRiskLevel() {
        if (unreadable) {
            return "UNKNOWN";
        } else if (score >= 70) {
            return "HIGH";
        } else if (score >= 30) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }

    public String getReason() {
        if (reasons.isEmpty()) {
            return "No risk indicators found";
        }
        return String.join("; ", reasons);
    }
}