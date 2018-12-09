/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rommanager.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.io.FilenameUtils;
import rommanager.gamelist.Game;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class RomSevenZipFile {
    
//    private final File file;
    
    private final String path;
    private String filename;
    private final List<RomVersion> versions;
	private Game game=new Game("", "", "", "", "", -1, "", "", "", "", "", -1, "", false);
	
	//For 7z files including versions
    public RomSevenZipFile(File file) throws IOException {
        this.path = FilenameUtils.getFullPath(file.getAbsolutePath());
        this.filename = FilenameUtils.getName(file.getAbsolutePath());
		versions = new ArrayList<>();
    }
	
	//For dsk (Amstrad) files that are not groupped in 7z
	//FIXME 6 Split from there
	public RomSevenZipFile(File file, String filename) throws IOException {
		this(file);
		this.filename=filename;
	}
	
	public void setVersions() throws IOException {
        if(FilenameUtils.getExtension(filename).equals("7z")) {
			readFrom7z();
			setScore(true);
		}
	}
	
	public final void setScore(boolean addBestForExport) {
		int bestScore=Integer.MIN_VALUE;
		RomVersion bestVersion=null;
		for(RomVersion version : versions) {
			if(version.getScore()>bestScore) {
				bestVersion=version;
				bestScore=version.getScore();
			} 
		}
		if(addBestForExport && bestVersion!=null) {
			bestVersion.setBest(true);
		}
	}
	
    public List<RomVersion> getVersions() {
        return versions;
    }

	//Amstrad only
	public void addAmstradVersion(RomVersion version) {
		versions.add(version);
		
		//FIXME 3 Only extract several if "Disk" inside versions, otherwise do as for 7z: take best version
		if(version.getErrorLevel()==0) {
			version.setBest(true);
		}
	}
    
    public String getFilename() {
        return filename;
    }

    private void readFrom7z() throws IOException {
		try (SevenZFile sevenZFile = new SevenZFile(new File(FilenameUtils.concat(path, filename)))) {
			SevenZArchiveEntry entry = sevenZFile.getNextEntry();
			while(entry!=null){
				versions.add(new RomVersion(
						FilenameUtils.getBaseName(filename), 
						entry.getName()));
				entry = sevenZFile.getNextEntry();
			}
		}
    }

    @Override
    public String toString() {
		List<RomVersion> export = versions.stream().filter(v -> v.isBest())
				.collect(Collectors.toList());
		
        return export.size()==1
					?export.get(0).toString()
					:export.isEmpty()
						?RomVersion.colorField("NO files to export.", 2, true)
						:RomVersion.colorField(export.size()+" files to export.", 3, true);
    }

	public void setGame(Game game) {
		this.game=game;
	}

	Game getGame() {
		return game;
	}
}
