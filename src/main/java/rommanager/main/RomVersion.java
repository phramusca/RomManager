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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class RomVersion {
    private final String filename;
    private String alternativeName;
    private List<String> countries;
    private List<String> standards;
	private int score;
	private int errorLevel;
	private boolean best;
	private Game game;
	
    /**
     *
     * @param name
     * @param filename
     */
    public RomVersion(String name, String filename) {
        this.filename = filename;
        System.out.println("name="+name);
        System.out.println("version="+filename);
        countries = new ArrayList<>();
        standards = new ArrayList<>();
        alternativeName = "";
        try {
            String attributes = "";
            if(!filename.startsWith(name)) {
                int posPar = filename.indexOf("(");
                int posBra = filename.indexOf("[");
                System.out.println("posPar="+posPar);
                System.out.println("posBra="+posBra);
                
                if(posBra >=0 || posPar >= 0) {
                    int pos;
                    if(posBra >=0 && posPar >= 0) {
                        pos = posBra<posPar?posBra:posPar;
                    }
                    else {
                        pos = posBra>=0?posBra:posPar;
                    }
                    
                    System.out.println("pos="+pos);
                    alternativeName = filename.substring(0, pos).trim();
                    attributes = filename.substring(pos);
                }
                else {
                    alternativeName = filename;
                }
            }
            else {
                attributes = filename.substring(name.length()).trim();
            }
            System.out.println("attributes="+attributes);
            System.out.println("alternativeName="+alternativeName);
            System.out.println("******************************************************************************************************************");
            parseAttributes(attributes);

			//FIXME 1 Make scoring customizable (no gui, use GoodToolsConfig.ods)
			
			if(countries.size()<=0) {
				score-=200;
			}
			
			setScore(countries, "F", 100); // France
			setScore(countries, "E", 40); // Europe
			setScore(countries, "FC", 15); // (FC) - French Canadian
			setScore(countries, "UE", 15); // (JU), (UE), (JUE) - Combination of the above
			setScore(countries, "JUE", 15);
			setScore(countries, "W", 15); // (W) - World (same as (JUE))
			setScore(countries, "U", 10); // (U) - USA
			setScore(countries, "UK", 10);
			setScore(countries, "PD", 5); // Public domain, free software and freeware
			setScore(countries, "Unl", -50); // Unlicensed //FIXME 3 Manage Unlicensed
												// Keep those if no other available (F, U,...)
												// OR Extract "special" games to a "special" folders(s)
			
			if(score>0) {
				setScore(standards, "!", 50); // The ROM is an exact copy of the original game; it has not had any hacks or modifications. 
				setScore(standards, "f", 40); // A fixed dump is a ROM that has been altered to run better on a flashcart or an emulator. 
				setScore(standards, "h", -40); // A fixed dump is a ROM that has been altered to run better on a flashcart or an emulator. 
				setScore(standards, "t", -40); // [tX] - The game has been supplied with a trainer
				if(standards.size()<=0) {
					score+=30;
				}
				setScore(standards, "a", 20); // The ROM is a copy of an alternative release of the game. Many games have been re-released to fix bugs or to eliminate Game Genie codes. 
				setScore(standards, "b", -10000); // [bX] - The game is a bad dump. These are useless. A bad dump often occurs with an older game or a faulty dumper (bad connection). Another common source of [b] ROMs is a corrupted upload to a release FTP. 
			}
			errorLevel=score>=40?0:
				score>0?1:
				score<0?2:3;
			System.out.println("score="+score);
        }
        catch(java.lang.StringIndexOutOfBoundsException ex) {
            Popup.error(ex);
        }
    }

	public RomVersion(String filename, String alternativeName, 
			String countries, 
			String standards, 
			int score, 
			int errorLevel, 
			boolean best) {
		this.filename = filename;
		this.alternativeName = alternativeName;
		this.countries = Arrays.asList(countries.substring(1, countries.length()-1).split(","));
        this.standards = Arrays.asList(standards.substring(1, standards.length()-1).split(","));
		this.score = score;
		this.errorLevel = errorLevel;
		this.best = best;
	}

	public int getErrorLevel() {
		return errorLevel;
	}
	
	private void setScore(List<String> list, String value, int score) {
		if(list.contains(value)) {
			this.score+=score;
		}
	}

	// https://segaretro.org/GoodTools
	// http://emulation.gametechwiki.com/index.php/GoodTools
	
	//FIXME 1 Manage all attributes (that can be different for some consoles)
				// incl. attribute standard code "values" (attributes.substring(2, end) )
				// - (VX.X) 	Version number (1.0 is earliest) 
				// - [fX] et autres avec un X qui peux etre une filename surtout
    private void parseAttributes(String attributes) {

        while(!attributes.equals("")) {
            attributes=attributes.trim();
            int end = 0;
            if(attributes.startsWith("(")) {
                end = attributes.indexOf(")");
                countries.add(attributes.substring(1, end));
            }
            else if(attributes.startsWith("[")) {
                end = attributes.indexOf("]");
                standards.add(attributes.substring(1, end));
            }
            attributes = attributes.substring(end+1).trim();
        }
    }
    
    public String getFilename() {
        return filename;
    }
    
    public List<String> getCountries() {
        return countries;
    }

    public List<String> getStandards() {
        return standards;
    }

	public String getAlternativeName() {
		return alternativeName;
	}
	
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append(score).append(" : ");
        if(!alternativeName.equals("")) {
            out.append(alternativeName).append(" ");
        }
        if(countries.size()>0) {
            out.append(countries).append(" ");
        }
        if(standards.size()>0) {
            out.append(standards).append(" ");
        }
		
		return colorField(out.toString(), errorLevel, true);
    }
	
	/**
	 * Color text HTML based on errorLevel
	 * if html param is false, do not enclose within html tags
	 * @param text
	 * @param errorLevel
	 * @param html
	 * @return
	 */
	public static String colorField(String text, int errorLevel, boolean html) {
		String color;
		
		if(text==null) {
			text="{null}";  //NOI18N
		}
		else if(text.equals("")) {  //NOI18N
			text="{Empty}";  //NOI18N
		}
		
		switch (errorLevel)
		{
			case 0: color="#32cd32"; break; //lime green	OK  //NOI18N
			case 1: color="#ffa500"; break; //orange		Warning  //NOI18N
			case 2: color="#FF0000"; break; //red			KO  //NOI18N
			case 3: color="#9400d3"; break; //violet		extra value  //NOI18N
			default: color="#ffff00"; break; //yellow		Default, shall not be used  //NOI18N
		}

		String out="";  //NOI18N
		if(html) {
			out+="<html>";  //NOI18N
		}
		out+="<font color=\""+color+"\">"+text+"</font>";  //NOI18N
		if(html) {
			out+="</html>";  //NOI18N
		}
		
		return out;
	}

	public int getScore() {
		return score;
	}

	public boolean isBest() {
		return best;
	}

	public void setBest(boolean selected) {
		this.best = selected;
	}
	
	public void setGame(Game game) {
		this.game=game;
	}

	public Game getGame() {
		return game;
	}
}