/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.rommanager;

import java.util.ArrayList;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class RomVersion {
    private final String version;
    private String alternativeName;
    private ArrayList<String> countries;
    private ArrayList<String> standards;
	private int score=0;
	private int errorLevel;
    /**
     *
     * @param name
     * @param version
     */
    public RomVersion(String name, String version) {
        this.version = version;
        System.out.println("name="+name);
        System.out.println("version="+version);
        countries = new ArrayList<>();
        standards = new ArrayList<>();
        alternativeName = "";
        try {
            String attributes = "";
            if(!version.startsWith(name)) {
                int posPar = version.indexOf("(");
                int posBra = version.indexOf("[");
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
                    alternativeName = version.substring(0, pos).trim();
                    attributes = version.substring(pos);
                }
                else {
                    alternativeName = version;
                }
            }
            else {
                attributes = version.substring(name.length()).trim();
            }
            System.out.println("attributes="+attributes);
            System.out.println("alternativeName="+alternativeName);
            System.out.println("******************************************************************************************************************");
            parseAttributes(attributes);

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
			setScore(countries, "Unl", -50); // Unlicensed //FIXME: Keep those if no other available (F, U,...)
			
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
	
	private void setScore(ArrayList<String> list, String value, int score) {
		if(list.contains(value)) {
			this.score+=score;
		}
	}

	// https://segaretro.org/GoodTools
	// http://emulation.gametechwiki.com/index.php/GoodTools
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
				
				//FIXME: Manage attribute standard code "values" (attributes.substring(2, end) )
				// - (VX.X) 	Version number (1.0 is earliest) 
				// - [fX] et autres avec un X qui peux etre une version surtout
                standards.add(attributes.substring(1, 2));
            }
            attributes = attributes.substring(end+1).trim();
        }
    }
    
    public String getVersion() {
        return version;
    }
    
    public ArrayList<String> getCountries() {
        return countries;
    }

    public ArrayList<String> getStandards() {
        return standards;
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
	 * if html param is false, do not enclose within <html> tags
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

}
