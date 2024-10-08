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

import java.util.stream.Collectors;
import rommanager.utils.Popup;
import rommanager.utils.ProcessAbstract;
import rommanager.utils.ProgressBar;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class ProcessSetScore extends ProcessAbstract {

	private final ProgressBar progressBarGame;
	private final TableModelRom tableModel;
	private final ICallBackProcess callBack;
	private final String sourcePath;
	
	public ProcessSetScore(
			ProgressBar progressBar, 
			TableModelRom tableModel, 
			ICallBackProcess callBack,
			String sourcePath) {
		super("Thread.ProcessSetScore");
		this.progressBarGame = progressBar;
		this.tableModel = tableModel;
		this.callBack = callBack;
		this.sourcePath = sourcePath;
	}

	@Override
	public void run() {
		try {
			setScore();
			progressBarGame.setIndeterminate("Saving ods file");
            if(RomManagerOds.createFile(tableModel, progressBarGame, sourcePath)) {
                callBack.actionPerformed();
            }
			Popup.info("Set Score complete.");
			progressBarGame.reset();
		} catch (InterruptedException ex) {
//			Popup.info("Aborted by user");
		} finally {
			callBack.completed();
		}
	}

    private void setScore() throws InterruptedException {
		progressBarGame.setup(tableModel.getRowCount());
		for(RomContainer romContainer : tableModel.getRoms().values().stream()
                .filter(r->r.getConsole().isSelected())
					.collect(Collectors.toList())) {
			checkAbort();
			progressBarGame.progress(romContainer.getConsoleStr()+" \\ "+romContainer.getFilename());
			for(RomVersion romVersion : romContainer.getVersions()) {
				checkAbort();
				romVersion.setScore(romContainer.console);
				romVersion.setExportable(false);
			}
			romContainer.setExportableVersions();
		}
    }
}