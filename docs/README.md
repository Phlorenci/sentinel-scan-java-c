# SecondLook

A Java Swing desktop application in the cybersecurity area. It scans a folder, gives every file a simple, explainable risk score, and stores scan history and scoring rules in a MySQL database

> SentinelScan gives risk indicators only. It does not claim to detect malware, and it is not a replacement for antivirus software. Only harmless test files are used in this project.

**Status:** in development (individual course project)

## Why this project

During the summer break I took an Ethical Hacking course and learned a lot about cybersecurity. I wanted to turn part of that knowledge into a working application, and I used the Java skills from my course (GUI, classes, file handling, MySQL) on a practical security problem.

## What it does

1. The user selects a folder in the GUI.
2. The application lists the files in it: name, size, extension, last-modified date.
3. Each file gets a risk score and a risk level (LOW, MEDIUM, HIGH) based on simple rules, with a written reason.
4. The scan and its results are saved in MySQL, so past scans can be reopened later.

## Features

- [ ] Folder selection and file list in a table
- [ ] File details: name, path, size, extension, last-modified date
- [ ] Rule-based risk scoring with a written reason for each score
- [ ] Details area for the selected file
- [ ] Scan history stored in MySQL (view, reopen, delete)
- [ ] Risk rules stored in MySQL (add, edit, delete from the GUI)
- [ ] Search and filter stored results (by risk level or extension)
- [ ] Report export to a text file (if time allows)
- [ ] Summary statistics (if time allows)

## How MySQL is used

| Feature | Database operations |
|---|---|
| Save a scan and its results | INSERT |
| Show scan history and reopen a past scan | SELECT |
| Edit risk rules from the GUI | INSERT, UPDATE, DELETE |
| Delete an old scan | DELETE |
| Filter stored results | SELECT with conditions |

Planned tables (the final design may change slightly):

| Table | Columns |
|---|---|
| `scans` | id, folder path, scan date and time, total files, high count, medium count, low count |
| `scan_results` | id, scan id (links to `scans`), file name, file path, size, extension, last modified, score, risk level, reason |
| `risk_rules` | id, extension, points, reason |

## Risk scoring

Every rule that matches adds points and a written reason. The total decides the level. The values below are examples and will be finalized during development.

| Rule | Points |
|---|---|
| Risky extension (for example exe, bat, vbs, scr) | to be decided |
| Double extension hiding the real type (for example `invoice.pdf.exe`) | to be decided |
| Unusual file size | to be decided |

The extension rules and their points are stored in MySQL, so they can be changed in the GUI without changing the code.

## Technologies

- Java (JDK)
- Java Swing for the GUI
- MySQL with JDBC
- Git and GitHub for version control

## Setup

### Requirements

- Java JDK [version]
- MySQL Server [version]
- MySQL Connector/J [version]

### Database

1. Create the database and tables by running the script `database.sql` from this repository.
2. Put your own MySQL connection settings in the local configuration file described below. Do not commit your password.

## Testing

Planned test cases, all with safe files:

- a normal text file
- a normal image
- a normal executable
- a file with a renamed extension
- a file with a double extension (an empty or plain-text file with that name)
- an empty folder and an invalid folder path

## Limitations

- Scoring is based on simple rules, so it can produce false positives and false negatives.
- A LOW score does not mean a file is safe, and a HIGH score does not mean a file is harmful.
- The application only checks file properties such as name, size and extension.

## Author

Bobur Mirzarakhimov, individual project for Problem Solving and Lab : Java course.

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
