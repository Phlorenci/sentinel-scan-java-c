// Stores the result of scanning one file: file details, hash, risk level and the reason.

import java.io.File;

public class ScanResult {

    private FileInfo info;
    private String hash;
    private String riskLevel;
    private String reason;

    public ScanResult(FileInfo info, String hash, String riskLevel, String reason) {
        this.info = info;
        this.hash = hash;
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

    public String getRiskLevel() {
        return riskLevel;
    }

    public String getReason() {
        return reason;
    }
}