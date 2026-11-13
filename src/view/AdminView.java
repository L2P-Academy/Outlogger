
package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class AdminView extends JFrame {

    // Log file path
    private static final Path LOG_FILE_PATH =
            Path.of("logs", "local_activity.log");

    // Log entry format
    private static final Pattern LOG_PATTERN = Pattern.compile(
            "^(\\d{2}\\.\\d{2}\\.\\d{4} \\d{2}:\\d{2}:\\d{2}) " +
            "\\[(\\d+) - ([A-Z]+)] (.*)$"
    );

    // GUI components
    private final DefaultTableModel tableModel;
    private final JTable logTable;
    private final TableRowSorter<DefaultTableModel> sorter;

    private final JComboBox<String> severityFilter;
    private final JTextField searchField;
    private final JLabel statusLabel;

    public AdminView() {

        // Window settings
        setTitle("OutLogger - Admin Panel");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));

        // Header
        JLabel header = new JLabel(
                "OUTLOGGER - ADMIN PANEL",
                SwingConstants.CENTER
        );

        header.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        header.setBorder(
                BorderFactory.createEmptyBorder(15, 0, 15, 0)
        );

        // Log table
        tableModel = new DefaultTableModel(
                new String[]{"Timestamp", "Severity", "Message"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        logTable = new JTable(tableModel);
        logTable.setRowHeight(25);
        logTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        logTable.getTableHeader().setFont(
                new Font(Font.SANS_SERIF, Font.BOLD, 13)
        );

        sorter = new TableRowSorter<>(tableModel);
        sorter.setSortable(0, false);
        logTable.setRowSorter(sorter);

        // Filter controls
        severityFilter = new JComboBox<>(new String[]{
                "ALL",
                "EMERGENCY",
                "ALERT",
                "CRITICAL",
                "ERROR",
                "WARNING",
                "NOTICE",
                "INFO",
                "DEBUG"
        });

        searchField = new JTextField(20);
        JButton refreshButton = new JButton("Refresh");

        JPanel filterPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        filterPanel.add(new JLabel("Severity:"));
        filterPanel.add(severityFilter);
        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(searchField);
        filterPanel.add(refreshButton);

        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(header, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.SOUTH);

        // Status bar
        statusLabel = new JLabel("Ready");

        JPanel bottomPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        bottomPanel.add(statusLabel);

        // Main layout
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(logTable), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Event listeners
        refreshButton.addActionListener(e -> loadLogs());

        severityFilter.addActionListener(e -> applyFilters());

        searchField.getDocument().addDocumentListener(
                new DocumentListener() {

                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        applyFilters();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        applyFilters();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        applyFilters();
                    }
                }
        );

        // Initial log loading
        loadLogs();

        // Display window
        setVisible(true);
    }

    // Read log file
    private void loadLogs() {

        tableModel.setRowCount(0);

        if (!Files.exists(LOG_FILE_PATH)) {
            statusLabel.setText("Log file not found.");
            return;
        }

        try {

            List<String> lines = Files.readAllLines(LOG_FILE_PATH);

            for (String line : lines) {

                Matcher matcher = LOG_PATTERN.matcher(line);

                if (matcher.matches()) {

                    tableModel.addRow(new Object[]{
                            matcher.group(1),
                            matcher.group(3),
                            matcher.group(4)
                    });

                } else {

                    tableModel.addRow(new Object[]{
                            "-",
                            "UNKNOWN",
                            line
                    });
                }
            }

            applyFilters();

        } catch (IOException e) {

            statusLabel.setText("Error reading log file.");
            System.err.println(e.getMessage());
        }
    }

    // Apply severity and search filters
    private void applyFilters() {

        String severity =
                (String) severityFilter.getSelectedItem();

        String search = searchField.getText().trim();

        List<RowFilter<DefaultTableModel, Integer>> filters =
                new ArrayList<>();

        // Severity filter
        if (severity != null && !severity.equals("ALL")) {

            filters.add(RowFilter.regexFilter(
                    "^" + Pattern.quote(severity) + "$", 1
            ));
        }

        // Search filter
        if (!search.isEmpty()) {

            filters.add(RowFilter.regexFilter(
                    "(?i)" + Pattern.quote(search)
            ));
        }

        // Apply filters
        if (filters.isEmpty()) {

            sorter.setRowFilter(null);

        } else {

            sorter.setRowFilter(RowFilter.andFilter(filters));
        }

        // Update status
        statusLabel.setText(
                "Showing " + logTable.getRowCount()
                + " of " + tableModel.getRowCount() + " events"
        );
    }
}


