/*
 * Copyright (C) 2011 phramusca ( https://github.com/phramusca/JaMuz/ )
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

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Popup class
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class Popup {
	
	//TODO: Make appName configurable
	private static final String APP_NAME = "Rom Manager";  //NOI18N
	private static final LogManager logManager = LogManager.getInstance();
	
	/**
	 * Popup an info to the user (ex: "Process complete.")
	 * @param str
	 */
	public static void info(String str) {
		javax.swing.JOptionPane.showMessageDialog(null, str, APP_NAME, JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Show a potentially long text in a scrollable, word-wrapped dialog.
	 * @param title dialog title
	 * @param text full text to display
	 */
	public static void showText(String title, String text) {
		JTextArea ta = new JTextArea(text);
		ta.setLineWrap(true);
		ta.setWrapStyleWord(true);
		ta.setEditable(false);
		JScrollPane scroll = new JScrollPane(ta);
		scroll.setPreferredSize(new Dimension(700, 420));
		javax.swing.JOptionPane.showMessageDialog(null, scroll, APP_NAME + " - " + title, JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Show a potentially long text in a scrollable, word-wrapped dialog with a clickable log file link.
	 * @param title dialog title
	 * @param text full text to display
	 * @param logFilePath path to the log file to open when button is clicked
	 */
	public static void showTextWithLogLink(String title, String text, String logFilePath) {
		JTextArea ta = new JTextArea(text);
		ta.setLineWrap(true);
		ta.setWrapStyleWord(true);
		ta.setEditable(false);
		JScrollPane scroll = new JScrollPane(ta);
		scroll.setPreferredSize(new Dimension(700, 420));
		
		// Create a button to open the log file
		JButton openLogButton = new JButton("Open Log File");
		openLogButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Desktop.openFile(logFilePath);
			}
		});
		
		// Create a panel with the scroll pane and button
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(scroll, BorderLayout.CENTER);
		panel.add(openLogButton, BorderLayout.SOUTH);
		
		javax.swing.JOptionPane.showMessageDialog(null, panel, APP_NAME + " - " + title, JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Warn user (ex: "Cannot find configuration file ...")
	 * @param str
	 */
	public static void warning(String str) {
		logManager.warning(Popup.class, str);
		javax.swing.JOptionPane.showMessageDialog(null, str, APP_NAME + " - Warning", JOptionPane.WARNING_MESSAGE);  //NOI18N
	}
	
	/**
	 * Log and display an error message
	 * @param str Error message
	 */
	public static void error(String str) {
		logManager.error(Popup.class, str);
		popupError(str);
    }
	
	/**
	 * Popup an error with additional text
	 * @param str Error message
	 * @param ex Exception that occurred
	 */
	public static void error(String str, Exception ex) {
		logManager.error(Popup.class, str, ex);
		popupError(str+":\n\n"+ex.toString());  //NOI18N
	}
	
	/**
	 * Popup an error (Exception only)
	 * @param ex Exception that occurred
	 */
	public static void error(Exception ex) {
		logManager.error(Popup.class, "An unexpected error occurred", ex);
		popupError("An unexpected error occured:\n\n"+ex.toString());  //NOI18N
	}

	/**
	 * Popup an SQL error
	 * @param methodName
	 * @param sql
	 * @param ex
	 */
	public static void error(String methodName, String sql, Exception ex) {
		popupError("An SQL Exception occured"+  //NOI18N
				":\n\n"+methodName+"\n"+sql+  //NOI18N
				":\n\n"+ex.toString());  //NOI18N
	}
	
	private static void popupError(String str) {
		javax.swing.JOptionPane.showMessageDialog(null, str, APP_NAME + " - Error", JOptionPane.ERROR_MESSAGE);  //NOI18N
	}
	
}
