/*
 * Copyright (C) 2019 phramusca ( https://github.com/phramusca/ )
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author phramusca ( https://github.com/phramusca/ )
 */
public abstract class RomContainer {
	
    protected final String filename;
    protected final List<RomVersion> versions;
	protected Console console;
	private Game game = null;
    private JeuVideo jeuVideo = null;
	
	RomContainer(Console console, String filename) {
        this.filename = filename;
		this.versions = new ArrayList<>();
		this.console = console;
    }
	
	public abstract void setExportableVersions();

    public void setBestExportable() {
        int bestScore=Integer.MIN_VALUE;
		RomVersion bestVersion=null;
		for(RomVersion version : versions) {
			version.setExportable(false);
			if(version.getScore()>bestScore) {
				bestVersion=version;
				bestScore=version.getScore();
			} 
		}
		if(bestVersion!=null && bestVersion.getScore()>=0) {
			bestVersion.setExportable(true);
		}
    }
    
	 public List<RomVersion> getVersions() {
        return versions;
    }

	 public void addVersion(RomVersion version) {
		versions.add(version);
	}
	 
    public String getFilename() {
        return filename;
    }

    @Override
    public String toString() {
		List<RomVersion> export = versions.stream().filter(v -> v.isExportable())
				.collect(Collectors.toList());
		
        return export.size()==1
					?export.get(0).toString()
					:export.isEmpty()
						?RomVersion.colorField("NO files to export.", 2, true)
						:RomVersion.colorField(export.size()+" files to export.", 3, true);
    }

	public Console getConsole() {
		return console;
	}
	
	public String getConsoleStr() {
		return console==null?"Unknown":console.getName();
	}
	
    public String getName() {
        String name = getGame().getName();
        if(name.isEmpty()) {
            name = getJeuVideo().getTitle();
        }
        if(name.isEmpty()) {
            name = getFilename();
        }
        return name;
    }
    
    public void resetGame() {
        game = null;
    }
    
	public Game getGame() {
		if(game==null) {
			List<Game> games = versions.stream()
				.filter(v -> v.isExportable() && v.getGame() != null && !v.getGame().getName().equals(""))
				.map(v -> v.getGame())
				.collect(Collectors.toList());
		
			if(!games.isEmpty()) {
				game = games.get(0);
			}
		}
		return game==null?new Game("","","", "", "", "", "", -1, "","", "", "", "", "", -1, "", false, 0, false, false, "", ""):game;
	}
    
    public JeuVideo getJeuVideo() {
        if(jeuVideo==null) {
			List<JeuVideo> jeuxVideos = versions.stream()
				.filter(v -> v.isExportable() && v.getJeuVideo()!=null && !v.getJeuVideo().getTitle().equals(""))
				.map(v -> v.getJeuVideo())
				.collect(Collectors.toList());

            if(!jeuxVideos.isEmpty()) {
				jeuVideo = jeuxVideos.get(0);
			}
		}
		return jeuVideo==null?new JeuVideo("", "", "", "", "", ""):jeuVideo;
    }

	public void setToCopyTrue() {
		versions.forEach(v->v.setToCopy(false));
		getExportableVersions().forEach(v->v.setToCopy(true));
	}
	
	public List<RomVersion> getExportableVersions() {
		return versions.stream()							
			.filter(r -> r.isExportable())
			.collect(Collectors.toList());
	}
	
	public List<RomVersion> getToCopyVersions() {
		return versions.stream()							
			.filter(r -> r.isToCopy())
			.collect(Collectors.toList());
	}

}
