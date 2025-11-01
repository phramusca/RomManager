/*
 * Copyright (C) 2025 RomManager
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package rommanager.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import rommanager.utils.LogManager.LogEntry;
import rommanager.utils.LogManager.LogLevel;

/**
 * Dialog to view and filter application logs
 * 
 * @author RomManager
 */
public class DialogLogViewer extends JDialog {
    
    private final LogManager logManager;
    private JTable logTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<TableModel> sorter;
    private JComboBox<LogLevel> levelComboBox;
    private JTextField classNameFilter;
    private JTextField messageFilter;
    private JCheckBox autoRefreshCheckBox;
    
    public DialogLogViewer(JFrame parent, LogManager logManager) {
        super(parent, "Log Viewer", false);
        this.logManager = logManager;
        initComponents();
        setupTable();
        refreshLogs();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setSize(1000, 700);
        setLocationRelativeTo(getParent());
        
        // Top panel with filters
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // Level filter
        topPanel.add(new JLabel("Level:"));
        levelComboBox = new JComboBox<>(LogLevel.values());
        levelComboBox.setSelectedItem(LogLevel.DEBUG); // Show all levels by default
        levelComboBox.addActionListener(e -> applyFilters());
        topPanel.add(levelComboBox);
        
        // Class name filter
        topPanel.add(new JLabel("Class:"));
        classNameFilter = new JTextField(20);
        classNameFilter.addActionListener(e -> applyFilters());
        topPanel.add(classNameFilter);
        
        // Message filter
        topPanel.add(new JLabel("Message:"));
        messageFilter = new JTextField(20);
        messageFilter.addActionListener(e -> applyFilters());
        topPanel.add(messageFilter);
        
        // Auto refresh
        autoRefreshCheckBox = new JCheckBox("Auto-refresh (5s)");
        autoRefreshCheckBox.addActionListener(e -> {
            if (autoRefreshCheckBox.isSelected()) {
                startAutoRefresh();
            } else {
                stopAutoRefresh();
            }
        });
        topPanel.add(autoRefreshCheckBox);
        
        // Buttons
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshLogs());
        topPanel.add(refreshButton);
        
        JButton clearButton = new JButton("Clear Filters");
        clearButton.addActionListener(e -> clearFilters());
        topPanel.add(clearButton);
        
        JButton openFileButton = new JButton("Open Log File");
        openFileButton.addActionListener(e -> {
            String logPath = logManager.getLogFilePath();
            if (logPath != null) {
                Desktop.openFile(logPath);
            }
        });
        topPanel.add(openFileButton);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Table with scroll pane
        tableModel = new DefaultTableModel(new String[]{"Time", "Level", "Class", "Method", "Line", "Message"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        logTable = new JTable(tableModel);
        logTable.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        logTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        logTable.setRowHeight(20);
        
        // Setup column widths
        logTable.getColumnModel().getColumn(0).setPreferredWidth(180); // Time
        logTable.getColumnModel().getColumn(1).setPreferredWidth(60);  // Level
        logTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Class
        logTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Method
        logTable.getColumnModel().getColumn(4).setPreferredWidth(50);  // Line
        logTable.getColumnModel().getColumn(5).setPreferredWidth(600); // Message
        
        // Color code rows by level
        logTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    String level = table.getValueAt(row, 1).toString();
                    switch (level) {
                        case "ERROR":
                            c.setBackground(new Color(255, 230, 230));
                            break;
                        case "WARN":
                            c.setBackground(new Color(255, 255, 230));
                            break;
                        case "INFO":
                            c.setBackground(Color.WHITE);
                            break;
                        case "DEBUG":
                            c.setBackground(new Color(240, 240, 240));
                            break;
                        default:
                            c.setBackground(Color.WHITE);
                    }
                }
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(logTable);
        scrollPane.setPreferredSize(new Dimension(980, 600));
        add(scrollPane, BorderLayout.CENTER);
        
        // Bottom panel with close button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        bottomPanel.add(closeButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void setupTable() {
        sorter = new TableRowSorter<>(tableModel);
        logTable.setRowSorter(sorter);
    }
    
    private void refreshLogs() {
        tableModel.setRowCount(0);
        
        LogLevel minLevel = (LogLevel) levelComboBox.getSelectedItem();
        String classNameFilterText = classNameFilter.getText().trim();
        String messageFilterText = messageFilter.getText().trim();
        
        List<LogEntry> entries = logManager.getLogs(minLevel, 
            classNameFilterText.isEmpty() ? null : classNameFilterText,
            messageFilterText.isEmpty() ? null : messageFilterText);
        
        for (LogEntry entry : entries) {
            Object[] row = {
                entry.timestamp,
                entry.level.getLabel(),
                entry.className,
                entry.methodName,
                entry.lineNumber > 0 ? entry.lineNumber : "",
                entry.message
            };
            tableModel.addRow(row);
        }
        
        // Scroll to bottom (most recent)
        if (tableModel.getRowCount() > 0) {
            logTable.scrollRectToVisible(logTable.getCellRect(
                tableModel.getRowCount() - 1, 0, true));
        }
    }
    
    private void applyFilters() {
        refreshLogs();
    }
    
    private void clearFilters() {
        levelComboBox.setSelectedItem(LogLevel.DEBUG);
        classNameFilter.setText("");
        messageFilter.setText("");
        refreshLogs();
    }
    
    private javax.swing.Timer refreshTimer;
    
    private void startAutoRefresh() {
        if (refreshTimer != null) {
            refreshTimer.stop();
        }
        refreshTimer = new javax.swing.Timer(5000, e -> refreshLogs());
        refreshTimer.start();
    }
    
    private void stopAutoRefresh() {
        if (refreshTimer != null) {
            refreshTimer.stop();
        }
    }
    
    /**
     * Show the log viewer dialog
     */
    public static void show(JFrame parent) {
        DialogLogViewer dialog = new DialogLogViewer(parent, LogManager.getInstance());
        dialog.setVisible(true);
    }
}

