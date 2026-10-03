// Stores the result of scanning one file: file details, hash, risk score, risk level and the reason.

import java.io.File;

public class ScanResult {

    private FileInfo info;
    private String hash;
    private int score;
    private String riskLevel;
    private String reason;

    public ScanResult(FileInfo info, String hash, int score, String riskLevel, String reason) {
        this.info = info;
        this.hash = hash;
        this.score = score;
        this.riskLevel = riskLevel;
        this.reason = reason;
    }

    public FileInfo getInfo() {
        return info;
    }

    public File getFile() {
        return info.getFile();
    }

    public String getHash() {
        return hash;
    }

    public int getScore() {
        return score;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public String getReason() {
        return reason;
    }
}