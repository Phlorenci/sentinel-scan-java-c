// Swing window: choose a folder, start a scan in the background, see progress and counts, a results table and file details.

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ScannerWindow extends JFrame {

    private JTextField folderField = new JTextField();
    private JButton chooseButton = new JButton("Choose Folder...");
    private JButton startButton = new JButton("Start Scan");
    private JProgressBar progressBar = new JProgressBar();
    private JLabel scannedLabel = new JLabel("Scanned files: 0");
    private JLabel threatsLabel = new JLabel("Threats found: 0");
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextArea detailsArea = new JTextArea();

    private ThreatDatabase database = new ThreatDatabase();
    private List<ScanResult> results = new ArrayList<>();
    private File scannedFolder;
    private int scannedCount = 0;
    private int threatCount = 0;

    public ScannerWindow() {
        super("SentinelScan");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        buildTopPanel();
        buildCenterPanel();
        buildBottomPanel();

        chooseButton.addActionListener(e -> chooseFolder());
        startButton.addActionListener(e -> startScan());
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showDetails();
            }
        });
    }

    private void buildTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout(8, 8));
        topPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 8));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttonPanel.add(chooseButton);
        buttonPanel.add(startButton);

        topPanel.add(new JLabel("Folder:"), BorderLayout.WEST);
        topPanel.add(folderField, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
    }

    private void buildCenterPanel() {
        String[] columns = {"File", "Size", "Extension", "Score", "Risk"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setDefaultRenderer(Object.class, new RiskRowRenderer());
        table.getColumnModel().getColumn(0).setPreferredWidth(350);

        detailsArea.setEditable(false);
        detailsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Results"));
        JScrollPane detailsScroll = new JScrollPane(detailsArea);
        detailsScroll.setBorder(BorderFactory.createTitledBorder("Details"));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScroll, detailsScroll);
        split.setResizeWeight(0.6);
        add(split, BorderLayout.CENTER);
    }

    private void buildBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout(8, 8));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));

        progressBar.setStringPainted(true);

        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        labelPanel.add(scannedLabel);
        labelPanel.add(threatsLabel);

        bottomPanel.add(progressBar, BorderLayout.CENTER);
        bottomPanel.add(labelPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void chooseFolder() {
        JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.dir")));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            folderField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void startScan() {
        File folder = new File(folderField.getText().trim());
        if (!folder.isDirectory()) {
            JOptionPane.showMessageDialog(this, "Please choose a valid folder first.", "No folder", JOptionPane.WARNING_MESSAGE);
            return;
        }

        scannedFolder = folder;
        database.load(new File("known_threats.txt"));
        tableModel.setRowCount(0);
        results.clear();
        scannedCount = 0;
        threatCount = 0;

        detailsArea.setText("Scanning...");
        progressBar.setValue(0);
        progressBar.setIndeterminate(true);
        progressBar.setString("Finding files...");
        scannedLabel.setText("Scanned files: 0");
        threatsLabel.setText("Threats found: 0");
        startButton.setEnabled(false);
        chooseButton.setEnabled(false);

        ScanWorker worker = new ScanWorker(folder);
        worker.addPropertyChangeListener(e -> {
            if (e.getPropertyName().equals("progress")) {
                progressBar.setValue((Integer) e.getNewValue());
            }
        });
        worker.execute();
    }

    private class ScanWorker extends SwingWorker<Void, ScanResult> {

        private File folder;
        private int totalFiles = 0;

        public ScanWorker(File folder) {
            this.folder = folder;
        }

        @Override
        protected Void doInBackground() {
            FileFinder finder = new FileFinder();
            List<File> files = finder.findFiles(folder);
            totalFiles = files.size();

            ScanService service = new ScanService(database);
            int count = 0;
            for (File file : files) {
                ScanResult result = service.scanFile(file);
                publish(result);
                count++;
                setProgress(count * 100 / totalFiles);
            }
            return null;
        }

        @Override
        protected void process(List<ScanResult> chunks) {
            if (progressBar.isIndeterminate()) {
                progressBar.setIndeterminate(false);
                progressBar.setString(null);
            }

            for (ScanResult result : chunks) {
                results.add(result);

                String relative = folder.toPath().relativize(result.getFile().toPath()).toString();
                tableModel.addRow(new Object[]{
                    relative,
                    result.getInfo().getReadableSize(),
                    result.getInfo().getExtension(),
                    result.getScore(),
                    result.getRiskLevel()
                });

                scannedCount++;
                if (result.getRiskLevel().equals("HIGH")) {
                    threatCount++;
                }
            }

            scannedLabel.setText("Scanned files: " + scannedCount);
            threatsLabel.setText("Threats found: " + threatCount);
        }

        @Override
        protected void done() {
            progressBar.setIndeterminate(false);
            progressBar.setString(null);
            startButton.setEnabled(true);
            chooseButton.setEnabled(true);

            try {
                get();
            } catch (Exception e) {
                detailsArea.setText("Scan failed: " + e.getMessage());
                return;
            }

            if (totalFiles == 0) {
                detailsArea.setText("No files were found in this folder.");
                JOptionPane.showMessageDialog(ScannerWindow.this, "No files were found in this folder.", "Nothing to scan", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            progressBar.setValue(100);
            String message = "Scan finished. Select a file in the table to see its details.";
            if (database.getCount() == 0) {
                message = message + "\n\nNote: the threat list is empty or missing (known_threats.txt), so hash matching is off.";
            }
            detailsArea.setText(message);
        }
    }

    private void showDetails() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= results.size()) {
            return;
        }

        ScanResult result = results.get(row);
        FileInfo info = result.getInfo();
        String relative = scannedFolder.toPath().relativize(result.getFile().toPath()).toString();

        String text = "File:      " + relative + "\n"
                + "Path:      " + info.getPath() + "\n"
                + "Size:      " + info.getReadableSize() + " (" + info.getSize() + " bytes)\n"
                + "Extension: " + info.getExtension() + "\n"
                + "Created:   " + info.getCreated() + "\n"
                + "Modified:  " + info.getModified() + "\n"
                + "Accessed:  " + info.getAccessed() + "\n"
                + "SHA-256:   " + result.getHash() + "\n"
                + "Score:     " + result.getScore() + "\n"
                + "Risk:      " + result.getRiskLevel() + "\n"
                + "Reason:    " + result.getReason();

        detailsArea.setText(text);
        detailsArea.setCaretPosition(0);
    }

    private static class RiskRowRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                String risk = table.getValueAt(row, 4).toString();
                if (risk.equals("HIGH")) {
                    cell.setBackground(new Color(255, 205, 205));
                } else if (risk.equals("MEDIUM")) {
                    cell.setBackground(new Color(255, 235, 180));
                } else {
                    cell.setBackground(Color.WHITE);
                }
                cell.setForeground(Color.BLACK);
            }
            return cell;
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Could not set system look and feel.");
        }

        SwingUtilities.invokeLater(() -> {
            ScannerWindow window = new ScannerWindow();
            window.setVisible(true);
        });
    }
}