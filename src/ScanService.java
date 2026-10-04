// Scans one file: gets its info, calculates the hash, scores it and returns a ScanResult.

import java.io.File;

public class ScanService {

    private ThreatDatabase database;
    private HashCalculator hasher = new HashCalculator();

    public ScanService(ThreatDatabase database) {
        this.database = database;
    }

    public ScanResult scanFile(File file) {
        FileInfo info = new FileInfo(file);
        String hash = hasher.calculateSha256(file);

        RiskScorer scorer = new RiskScorer();
        scorer.calculate(info, hash, database);

        return new ScanResult(info, hash, scorer.getScore(), scorer.getRiskLevel(), scorer.getReason());
    }
}