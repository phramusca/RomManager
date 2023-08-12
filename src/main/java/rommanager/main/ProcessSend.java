/* 
 * Copyright (C) 2018 phramusca ( https://github.com/phramusca/ )
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
package rommanager.main;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FilenameUtils;
import rommanager.utils.FileSystem;
import rommanager.utils.Popup;
import rommanager.utils.ProcessAbstract;
import rommanager.utils.ProgressBar;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class ProcessSend extends ProcessAbstract {

    private final String sourcePath;
	private final String exportPath;
    private final ProgressBar progressBarConsole;
	private final ProgressBar progressBarGame;
	private final ICallBackProcess callBack;
	
	public ProcessSend(
            String sourcePath,
			String exportPath, 
            ProgressBar progressBarConsole, 
			ProgressBar progressBarGame, 
			ICallBackProcess callBack) {
		super("Thread.ProcessRead");
        this.sourcePath = sourcePath;
		this.exportPath = exportPath;
        this.progressBarConsole = progressBarConsole;
		this.progressBarGame = progressBarGame;
		this.callBack = callBack;
	}
   
	@Override
	public void run() {
		try {
            progressBarConsole.setup(Console.values().length);
			for(Console console : Console.values()) {
				checkAbort();
                progressBarConsole.progress(console.getName());
                File localFile = new File(FilenameUtils.concat(FilenameUtils.concat(sourcePath, console.name()), "gamelist.xml"));
                File remoteFile = new File(FilenameUtils.concat(FilenameUtils.concat(exportPath, console.name()), "gamelist.xml"));
                if(localFile.exists()) {
                    FileSystem.copyFile(localFile, remoteFile);
                }
			}
			progressBarConsole.reset();
            
			Popup.info("Sending complete.");
			progressBarGame.reset();
		} catch (InterruptedException ex) {
//			Popup.info("Aborted by user");
		} catch (IOException ex) {
            Popup.error(ex);
        } finally {
			callBack.completed();
		}
	}
}