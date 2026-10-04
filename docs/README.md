# SentinelScan

**A Hybrid Java/C Platform for Explainable File Risk Assessment**

A student cybersecurity project built step by step. It scans a folder, calculates SHA-256 hashes, compares them with a local list of test hashes and gives every file an explainable risk score.

> SentinelScan gives risk indicators, not a definitive malware verdict. It is not a replacement for antivirus software.

## Features

- Scans a folder and all its subfolders
- Shows file size, extension, path and timestamps
- Calculates SHA-256 for every file
- Checks hashes against a local list of test hashes (`known_threats.txt`)
- Rule-based risk score (LOW, MEDIUM, HIGH, UNKNOWN) with a written reason for each rule
- Summary with file count, total size and risk counts
- Desktop GUI (Swing) with results table, progress bar and details; scanning runs in the background so the window stays responsive

## Requirements

- Java JDK 11 or newer

## Run

From the project root:

```
javac src/*.java -d out
java -cp out ScannerWindow
```

Enter a folder path when asked, for example `test-data`.

## Example output

```
2. notes.txt
   Path:      C:\games\Studies\sentinel-scan-java-c\test-data\notes.txt
   Size:      16 B
   Extension: txt
   SHA-256:   99f86e1be6de5ae82b3f23977b65ebe43cb8359b37b6cf0ab8e6c2eaa0c99193
   Score:     100
   Risk:      HIGH
   Reason:    Hash found in local threat list: Test sample (notes.txt) (+100)
```

## Risk scoring

| Rule | Points |
|---|---|
| SHA-256 found in the local threat list | +100 |
| Executable or script extension | +20 |
| Double extension hiding an executable type (`invoice.pdf.exe`) | +30 |

70 or more is HIGH, 30 to 69 is MEDIUM, below 30 is LOW.

## Test data

`test-data/` contains harmless files. The files in `test-data/samples/` are plain text that only have executable-looking names such as `tool.exe`. They are not real programs. `known_threats.txt` contains only test hashes. No real malware is used or required anywhere in this project.

## Roadmap

- [x] Console scanner with recursive folder search
- [x] File information (size, extension, path, timestamps)
- [x] SHA-256 hashing
- [x] Local threat hash list
- [x] Result class and explainable risk scoring
- [x] Swing desktop GUI
- [x] Background scanning so the window does not freeze
- [ ] C component for PE header analysis
- [ ] Entropy and extra heuristics
- [ ] Automated test cases
- [ ] Scan history, report export and summary statistics

## Documentation

See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) for the design, class overview, data flow and limitations.

## License

Copyright 2026 Bobur Mirzarakhimov.
Licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE).