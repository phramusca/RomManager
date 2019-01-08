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

import rommanager.utils.Popup;
import rommanager.utils.ProcessAbstract;
import rommanager.utils.ProgressBar;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class ProcessSetScore extends ProcessAbstract {

	private final ProgressBar progressBar;
	private final TableModelRomSevenZip tableModel;
	private final ICallBackProcess callBack;
	
	public ProcessSetScore(
			ProgressBar progressBar, 
			TableModelRomSevenZip tableModel, 
			ICallBackProcess callBack) {
		super("Thread.ProcessSetScore");
		this.progressBar = progressBar;
		this.tableModel = tableModel;
		this.callBack = callBack;
	}

	@Override
	public void run() {
		try {
			setScore();
			progressBar.setIndeterminate("Saving ods file");
			RomManagerOds.createFile(tableModel, progressBar);
			Popup.info("Set Score complete.");
			progressBar.reset();
		} catch (InterruptedException ex) {
//			Popup.info("Aborted by user");
		} finally {
			callBack.completed();
		}
	}

    public void setScore() throws InterruptedException {
		progressBar.setup(tableModel.getRowCount());
		for(RomSevenZipFile romSevenZipFile : tableModel.getRoms().values()) {
			checkAbort();
			progressBar.progress(romSevenZipFile.getConsoleStr()+" \\ "+romSevenZipFile.getFilename());
			for(RomVersion romVersion : romSevenZipFile.getVersions()) {
				checkAbort();
				romVersion.setScore();
				romVersion.setBest(false);
			}
			romSevenZipFile.setScore(true);
		}
    }
}