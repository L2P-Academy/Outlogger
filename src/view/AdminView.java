
package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

public class AdminView extends JFrame {

    // Log file
    private static final Path LOG_FILE_PATH =
            Path.of("logs", "local_activity.log");

    // Colors
    private static final Color BACKGROUND = new Color(13, 21, 36);
    private static final Color PANEL = new Color(25, 42, 64);
    private static final Color ROW_DARK = new Color(17, 29, 47);
    private static final Color ROW_LIGHT = new Color(22, 36, 55);
    private static final Color BORDER = new Color(48, 65, 88);
    private static final Color TEXT = new Color(230, 237, 247);
    private static final Color MUTED = new Color(145, 163, 185);
    private static final Color ACCENT = new Color(56, 189, 248);

    // Components
    private final DefaultTableModel tableModel;
    private final JTable logTable;
    private final TableRowSorter<DefaultTableModel> sorter;
    private final JComboBox<String> severityFilter;
    private final JTextField searchField;
    private final JLabel statusLabel;

    public AdminView() {

        // Window settings
        setTitle("OutLogger - Admin Panel");
        setSize(1000, 620);
        setMinimumSize(new Dimension(800, 500));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBackground(BACKGROUND);
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(22, 22, 16, 22)
        );

        setContentPane(mainPanel);

        // Header
        JLabel titleLabel = new JLabel("OUTLOGGER");
        titleLabel.setFont(
                new Font(Font.SANS_SERIF, Font.BOLD, 26)
        );
        titleLabel.setForeground(TEXT);

        JLabel subtitleLabel = new JLabel(
                "SECURITY MONITORING / ADMINISTRATION"
        );
        subtitleLabel.setFont(
                new Font(Font.SANS_SERIF, Font.PLAIN, 11)
        );
        subtitleLabel.setForeground(MUTED);

        JPanel titlePanel = new JPanel(new BorderLayout(0, 4));
        titlePanel.setBackground(BACKGROUND);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);

        JLabel adminLabel = new JLabel("ADMIN CONSOLE");
        adminLabel.setFont(
                new Font(Font.SANS_SERIF, Font.BOLD, 11)
        );
        adminLabel.setForeground(ACCENT);
        adminLabel.setBackground(PANEL);
        adminLabel.setOpaque(true);
        adminLabel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                )
        );

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BACKGROUND);
        header.add(titlePanel, BorderLayout.WEST);
        header.add(adminLabel, BorderLayout.EAST);

        // Activity monitor
        JLabel monitorLabel = new JLabel("ACTIVITY MONITOR");
        monitorLabel.setFont(
                new Font(Font.SANS_SERIF, Font.BOLD, 12)
        );
        monitorLabel.setForeground(MUTED);

        // Severity filter
        severityFilter = new JComboBox<>(new String[]{
                "ALL", "EMERGENCY", "ALERT", "CRITICAL",
                "ERROR", "WARNING", "NOTICE", "INFO", "DEBUG"
        });

        severityFilter.setBackground(PANEL);
        severityFilter.setForeground(TEXT);
        severityFilter.setPreferredSize(new Dimension(145, 32));

        // Search field
        searchField = new JTextField(18);
        searchField.setFont(
                new Font(Font.SANS_SERIF, Font.PLAIN, 12)
        );
        searchField.setForeground(TEXT);
        searchField.setBackground(ROW_DARK);
        searchField.setCaretColor(ACCENT);
        searchField.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        BorderFactory.createEmptyBorder(5, 8, 5, 8)
                )
        );
        searchField.setPreferredSize(new Dimension(220, 32));
        searchField.setToolTipText("Search log entries");

        // Refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(
                new Font(Font.SANS_SERIF, Font.BOLD, 12)
        );
        refreshButton.setForeground(BACKGROUND);
        refreshButton.setBackground(ACCENT);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorder(
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        );
        refreshButton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        // Toolbar
        JPanel filterPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 0)
        );
        filterPanel.setBackground(PANEL);
        filterPanel.setBorder(
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        );

        filterPanel.add(monitorLabel);
        filterPanel.add(severityFilter);
        filterPanel.add(searchField);
        filterPanel.add(refreshButton);

        JPanel topPanel = new JPanel(new BorderLayout(0, 15));
        topPanel.setBackground(BACKGROUND);
        topPanel.add(header, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.SOUTH);

        // Table model
        tableModel = new DefaultTableModel(
                new String[]{"Timestamp", "Severity", "Message"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Log table
        logTable = new JTable(tableModel);
        logTable.setRowHeight(32);
        logTable.setFont(
                new Font(Font.SANS_SERIF, Font.PLAIN, 12)
        );
        logTable.setBackground(ROW_DARK);
        logTable.setForeground(TEXT);
        logTable.setSelectionBackground(new Color(38, 76, 108));
        logTable.setSelectionForeground(Color.WHITE);
        logTable.setGridColor(BORDER);
        logTable.setShowVerticalLines(false);
        logTable.setFillsViewportHeight(true);
        logTable.setIntercellSpacing(new Dimension(0, 1));

        // Table header
        JTableHeader tableHeader = logTable.getTableHeader();
        tableHeader.setBackground(PANEL);
        tableHeader.setForeground(MUTED);
        tableHeader.setFont(
                new Font(Font.SANS_SERIF, Font.BOLD, 12)
        );
        tableHeader.setPreferredSize(new Dimension(0, 36));
        tableHeader.setReorderingAllowed(false);

        // Column widths
        logTable.getColumnModel().getColumn(0).setPreferredWidth(175);
        logTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        logTable.getColumnModel().getColumn(2).setPreferredWidth(600);

        // Cell rendering
        logTable.setDefaultRenderer(
                Object.class, new LogCellRenderer()
        );

        // Sorting and filtering
        sorter = new TableRowSorter<>(tableModel);
        sorter.setSortable(0, false);
        logTable.setRowSorter(sorter);

        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(logTable);
        scrollPane.setBorder(
                BorderFactory.createLineBorder(BORDER)
        );
        scrollPane.getViewport().setBackground(ROW_DARK);

        // Footer
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(
                new Font(Font.SANS_SERIF, Font.PLAIN, 11)
        );
        statusLabel.setForeground(MUTED);

        JLabel sourceLabel = new JLabel(
                "SOURCE: logs/local_activity.log"
        );
        sourceLabel.setFont(
                new Font(Font.SANS_SERIF, Font.PLAIN, 10)
        );
        sourceLabel.setForeground(MUTED);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(BACKGROUND);
        footer.add(statusLabel, BorderLayout.WEST);
        footer.add(sourceLabel, BorderLayout.EAST);

        // Main layout
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(footer, BorderLayout.SOUTH);

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

        // Load logs and display window
        loadLogs();
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

                int timestampEnd = line.indexOf(" [");

                int severityStart = timestampEnd >= 0
                        ? line.indexOf(" - ", timestampEnd) : -1;

                int messageStart = severityStart >= 0
                        ? line.indexOf("] ", severityStart + 3) : -1;

                if (timestampEnd >= 0 &&
                    severityStart > timestampEnd &&
                    messageStart > severityStart) {

                    String timestamp = line.substring(0, timestampEnd);
                    String severity = line.substring(severityStart + 3, messageStart);
                    String message = line.substring(messageStart + 2);

                    tableModel.addRow(new Object[]{
                            timestamp, severity, message
                    });

                } else {

                    tableModel.addRow(new Object[]{
                            "-", "UNKNOWN", line
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

        String severity = (String) severityFilter.getSelectedItem();
        String search = searchField.getText()
                .trim().toLowerCase(Locale.ROOT);

        sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {

            @Override
            public boolean include(
                    Entry<? extends DefaultTableModel, ? extends Integer> entry) {

                String timestamp = entry.getStringValue(0)
                        .toLowerCase(Locale.ROOT);

                String rowSeverity = entry.getStringValue(1);
                String message = entry.getStringValue(2)
                        .toLowerCase(Locale.ROOT);

                boolean matchesSeverity =
                        severity == null ||
                        severity.equals("ALL") ||
                        severity.equalsIgnoreCase(rowSeverity);

                boolean matchesSearch =
                        search.isEmpty() ||
                        timestamp.contains(search) ||
                        rowSeverity.toLowerCase(Locale.ROOT).contains(search) ||
                        message.contains(search);

                return matchesSeverity && matchesSearch;
            }
        });

        // Update status
        statusLabel.setText(
                "Showing " + logTable.getRowCount()
                + " of " + tableModel.getRowCount() + " events"
        );
    }

    // Table cell styling
    private class LogCellRenderer extends DefaultTableCellRenderer {

        @Override
        public java.awt.Component getTableCellRendererComponent(
                JTable table, Object value, boolean selected,
                boolean focused, int row, int column) {

            super.getTableCellRendererComponent(
                    table, value, selected, focused, row, column
            );

            setBorder(
                    BorderFactory.createEmptyBorder(0, 10, 0, 10)
            );

            if (selected) {
                setBackground(table.getSelectionBackground());
                setForeground(Color.WHITE);

            } else {

                setBackground(row % 2 == 0 ? ROW_DARK : ROW_LIGHT);

                if (column == 0) {
                    setForeground(MUTED);

                } else if (column == 1) {

                    String level = value == null
                            ? "UNKNOWN" : value.toString();

                    switch (level) {
                        case "EMERGENCY":
                        case "ALERT":
                        case "CRITICAL":
                            setForeground(new Color(251, 113, 133));
                            break;

                        case "ERROR":
                            setForeground(new Color(251, 146, 60));
                            break;

                        case "WARNING":
                            setForeground(new Color(251, 191, 36));
                            break;

                        case "NOTICE":
                            setForeground(new Color(56, 189, 248));
                            break;

                        case "INFO":
                            setForeground(new Color(74, 222, 128));
                            break;

                        default:
                            setForeground(MUTED);
                    }

                } else {
                    setForeground(TEXT);
                }
            }

            return this;
        }
    }
}



