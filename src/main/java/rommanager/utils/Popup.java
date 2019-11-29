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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * Popup class
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class Popup {
	
	//TODO: Make appName configurable
	private static final String APP_NAME = "Rom Manager";  //NOI18N
	private static Logger LOGGER=null; //Can't be static. Why should it be (as netbeans says) ?
	
	/**
	 * Set the LOGGER
	 * @param logger
	 */
	public static void setLogger(Logger logger) {
		Popup.LOGGER = logger;
	}
	
	/**
	 * Popup an info to the user (ex: "Process complete.")
	 * @param str
	 */
	public static void info(String str) {
		javax.swing.JOptionPane.showMessageDialog(null, str, APP_NAME, JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Warn user (ex: "Cannot find configuration file ...")
	 * @param str
	 */
	public static void warning(String str) {
        if(LOGGER!=null) {
			Popup.LOGGER.log(Level.WARNING, str);
		}
		javax.swing.JOptionPane.showMessageDialog(null, str, APP_NAME + " - Warning", JOptionPane.WARNING_MESSAGE);  //NOI18N
	}

	//TODO: do not call OptionsEnv.LOGGER from this class
	//as we loose the impacted class and method
	//=> Need to go through the code and for each method, make sure:
	//- we return a boolean (pass/fail)
	//- we popup an error to the user (at some level)
	//- we log the error
	
	/**
	 * TODO: Parcourir utilisations et MAJ selon le cas
	 * @param str
	 */
	public static void error(String str) {
		if(LOGGER!=null) {
			Popup.LOGGER.severe(str);
		}
		popupError(str);
    }
	
	/**
	 * Popup an error with additional text
	 * @param str
	 * @param ex
	 */
	public static void error(String str, Exception ex) {
		if(LOGGER!=null) {
			Popup.LOGGER.log(Level.SEVERE, str, ex);
		}
		popupError(str+":\n\n"+ex.toString());  //NOI18N
	}
	
	/**
	 * Popup an error (Exception only)
	 * @param ex
	 */
	public static void error(Exception ex) {
		if(LOGGER!=null) {
			Popup.LOGGER.log(Level.SEVERE, APP_NAME, ex);
		}
		else {
			System.out.println(ex.toString());
			StackTraceElement[] stackTrace = ex.getStackTrace();
			for (StackTraceElement stackTraceElement : stackTrace) {
				System.out.println(stackTraceElement.toString());
			}
		}
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
