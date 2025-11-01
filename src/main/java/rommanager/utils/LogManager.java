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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Centralized logging manager for RomManager application.
 * Provides file-based logging with levels, context preservation, and thread safety.
 * 
 * @author RomManager
 */
public class LogManager {
    
    public enum LogLevel {
        DEBUG(0, "DEBUG"),
        INFO(1, "INFO"),
        WARNING(2, "WARN"),
        ERROR(3, "ERROR");
        
        private final int priority;
        private final String label;
        
        LogLevel(int priority, String label) {
            this.priority = priority;
            this.label = label;
        }
        
        public int getPriority() {
            return priority;
        }
        
        public String getLabel() {
            return label;
        }
        
        public boolean includes(LogLevel other) {
            return other.priority >= this.priority;
        }
    }
    
    private static LogManager instance;
    private static final ReentrantLock lock = new ReentrantLock();
    
    private final String logDirectory = "cache/logs";
    private final String logFileName;
    private final File logFile;
    private PrintWriter writer;
    private LogLevel minLevel = LogLevel.INFO;
    private final List<LogEntry> inMemoryLogs = Collections.synchronizedList(new ArrayList<>());
    private final int maxInMemoryLogs = 10000; // Keep last 10000 entries in memory for viewer
    private boolean initialized = false;
    
    private LogManager() {
        // Create log file with timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        logFileName = "rommanager-" + timestamp + ".log";
        
        // Ensure cache directory exists first
        File cacheDir = new File("cache");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        
        // Ensure log directory exists
        File logDir = new File(logDirectory);
        if (!logDir.exists()) {
            if (!logDir.mkdirs()) {
                System.err.println("Failed to create log directory: " + logDirectory);
                // Initialize logFile even if directory creation failed (will fail later in try-catch)
                logFile = new File(logDir, logFileName);
                initialized = false;
                return;
            }
        }
        
        logFile = new File(logDir, logFileName);
        
        try {
            writer = new PrintWriter(new FileWriter(logFile, true));
            initialized = true;
            log(LogLevel.INFO, LogManager.class, "LogManager initialized", null);
        } catch (IOException ex) {
            System.err.println("Failed to initialize LogManager: " + ex.getMessage());
            ex.printStackTrace();
            initialized = false;
        }
    }
    
    /**
     * Get singleton instance of LogManager
     */
    public static LogManager getInstance() {
        if (instance == null) {
            lock.lock();
            try {
                if (instance == null) {
                    instance = new LogManager();
                }
            } finally {
                lock.unlock();
            }
        }
        return instance;
    }
    
    /**
     * Set minimum log level (only logs at or above this level will be written)
     */
    public void setMinLevel(LogLevel level) {
        this.minLevel = level;
    }
    
    /**
     * Get minimum log level
     */
    public LogLevel getMinLevel() {
        return minLevel;
    }
    
    /**
     * Log a message with automatic context detection
     */
    public void log(LogLevel level, Class<?> clazz, String message, Throwable throwable) {
        if (!initialized || level.priority < minLevel.priority) {
            return;
        }
        
        String className = clazz != null ? clazz.getSimpleName() : "Unknown";
        String fullClassName = clazz != null ? clazz.getName() : "Unknown";
        
        // Try to get calling method from stack trace
        String methodName = "unknown";
        int lineNumber = -1;
        try {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            // Skip LogManager and caller methods, find the actual calling method
            for (int i = 3; i < stackTrace.length && i < 10; i++) {
                StackTraceElement element = stackTrace[i];
                String elemClassName = element.getClassName();
                if (!elemClassName.equals(LogManager.class.getName()) &&
                    !elemClassName.equals(Popup.class.getName())) {
                    methodName = element.getMethodName();
                    lineNumber = element.getLineNumber();
                    break;
                }
            }
        } catch (Exception ex) {
            // Ignore stack trace errors
        }
        
        log(level, fullClassName, className, methodName, lineNumber, message, throwable);
    }
    
    /**
     * Log a message with explicit context
     */
    public void log(LogLevel level, String fullClassName, String className, 
                   String methodName, int lineNumber, String message, Throwable throwable) {
        if (!initialized || level.priority < minLevel.priority) {
            return;
        }
        
        lock.lock();
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
            
            // Format log entry
            StringBuilder logEntry = new StringBuilder();
            logEntry.append(timestamp)
                    .append(" [").append(level.getLabel()).append("] ")
                    .append(className);
            
            if (!methodName.equals("unknown")) {
                logEntry.append(".").append(methodName);
            }
            if (lineNumber > 0) {
                logEntry.append(":").append(lineNumber);
            }
            logEntry.append(" - ").append(message);
            
            // Add exception details if present
            if (throwable != null) {
                logEntry.append("\n").append(getStackTrace(throwable));
            }
            
            String logLine = logEntry.toString();
            
            // Write to file
            if (writer != null) {
                writer.println(logLine);
                writer.flush();
            }
            
            // Store in memory for viewer (with limit)
            LogEntry entry = new LogEntry(
                timestamp, level, fullClassName, className, methodName, 
                lineNumber, message, throwable, logLine
            );
            inMemoryLogs.add(entry);
            
            // Limit memory usage
            if (inMemoryLogs.size() > maxInMemoryLogs) {
                inMemoryLogs.remove(0);
            }
            
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Convenience methods for different log levels
     */
    public void debug(Class<?> clazz, String message) {
        log(LogLevel.DEBUG, clazz, message, null);
    }
    
    public void info(Class<?> clazz, String message) {
        log(LogLevel.INFO, clazz, message, null);
    }
    
    public void warning(Class<?> clazz, String message) {
        log(LogLevel.WARNING, clazz, message, null);
    }
    
    public void warning(Class<?> clazz, String message, Throwable throwable) {
        log(LogLevel.WARNING, clazz, message, throwable);
    }
    
    public void error(Class<?> clazz, String message) {
        log(LogLevel.ERROR, clazz, message, null);
    }
    
    public void error(Class<?> clazz, String message, Throwable throwable) {
        log(LogLevel.ERROR, clazz, message, throwable);
    }
    
    /**
     * Get in-memory log entries (for viewer)
     */
    public List<LogEntry> getLogs() {
        return new ArrayList<>(inMemoryLogs);
    }
    
    /**
     * Get filtered log entries
     */
    public List<LogEntry> getLogs(LogLevel minLevel, String classNameFilter, String messageFilter) {
        return inMemoryLogs.stream()
            .filter(entry -> minLevel.includes(entry.level))
            .filter(entry -> classNameFilter == null || classNameFilter.isEmpty() || 
                            entry.className.toLowerCase().contains(classNameFilter.toLowerCase()) ||
                            entry.fullClassName.toLowerCase().contains(classNameFilter.toLowerCase()))
            .filter(entry -> messageFilter == null || messageFilter.isEmpty() || 
                            entry.message.toLowerCase().contains(messageFilter.toLowerCase()))
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Get path to current log file
     */
    public String getLogFilePath() {
        return logFile != null ? logFile.getAbsolutePath() : null;
    }
    
    /**
     * Get log directory
     */
    public String getLogDirectory() {
        return logDirectory;
    }
    
    /**
     * Close log manager and flush all data
     */
    public void close() {
        lock.lock();
        try {
            if (writer != null) {
                log(LogLevel.INFO, LogManager.class, "LogManager shutting down", null);
                writer.flush();
                writer.close();
                writer = null;
            }
            initialized = false;
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Get stack trace as string
     */
    private String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
    
    /**
     * Represents a log entry
     */
    public static class LogEntry {
        public final String timestamp;
        public final LogLevel level;
        public final String fullClassName;
        public final String className;
        public final String methodName;
        public final int lineNumber;
        public final String message;
        public final Throwable throwable;
        public final String fullLogLine;
        
        public LogEntry(String timestamp, LogLevel level, String fullClassName, 
                       String className, String methodName, int lineNumber,
                       String message, Throwable throwable, String fullLogLine) {
            this.timestamp = timestamp;
            this.level = level;
            this.fullClassName = fullClassName;
            this.className = className;
            this.methodName = methodName;
            this.lineNumber = lineNumber;
            this.message = message;
            this.throwable = throwable;
            this.fullLogLine = fullLogLine;
        }
    }
}

