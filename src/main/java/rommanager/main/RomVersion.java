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
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class RomVersion {
    private final String filename;
	private final String name;
    private String alternativeName;
    private List<String> attributes;
	private int score;
	private int errorLevel;
	private boolean best;
	private Game game;
	
    /**
     * Create a Rom Version, read from fileSystem
     * @param name
     * @param filename
     */
    public RomVersion(String name, String filename) {
        this.filename = filename;
        System.out.println("name="+name);
        System.out.println("version="+filename);
        attributes = new ArrayList<>();
        alternativeName = "";
		this.name=name;
        setScore();
    }

	/**
	 * Create a Rom Version, read from ODS file
	 * @param name
	 * @param filename
	 * @param alternativeName
	 * @param attributes
	 * @param score
	 * @param errorLevel
	 * @param best
	 */
	public RomVersion(String name, String filename, String alternativeName, 
			String attributes,
			int score, 
			int errorLevel, 
			boolean best) {
		this.name = name;
		this.filename = filename;
		this.alternativeName = alternativeName;
		this.attributes = Arrays.asList(attributes.substring(1, attributes.length()-1).split(","));
		this.score = score;
		this.errorLevel = errorLevel;
		this.best = best;
	}

	public final void setScore() {
		try {
			attributes = new ArrayList<>();
			score=0;
            String attrWork = "";
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
                    attrWork = filename.substring(pos);
                }
                else {
                    alternativeName = filename;
                }
            }
            else {
                attrWork = filename.substring(name.length()).trim();
            }
            System.out.println("attributes="+attrWork);
            System.out.println("alternativeName="+alternativeName);
            System.out.println("******************************************************************************************************************");
            parseAttributes(attrWork);

			//FIXME 1 Make scoring customizable (no gui, use GoodToolsConfig.ods)

			//FIXME 1 Manage code "values" (attributes.substring(2, end) )
				// - (VX.X) 	Version number (1.0 is earliest) 
				// - [fX] et autres avec un X qui peux etre une filename surtout
				
			Map<String, GoodCode> codes = GoodToolsConfigOds.getCodes();
			for(GoodCode gc : codes.values().stream()
							.filter(r -> r.getScore()!=0)
							.collect(Collectors.toList())) {
				setScore(attributes, gc.getCode(), gc.getScore());
			}
			//FIXME 1 Manage "" type (need to parse but no delimiters => use contains()
//			for(GoodCode gc : codes.stream()
//							.filter(r -> r.getScore()!=0 && r.getType().equals(""))
//							.collect(Collectors.toList())) {
//			}
			
			
			errorLevel=score>=40?0:
				score>0?1:
				score<0?2:3;
			System.out.println("score="+score);
        }
        catch(java.lang.StringIndexOutOfBoundsException ex) {
            Popup.error(ex);
        }
	}
	
	public int getErrorLevel() {
		return errorLevel;
	}
	
	private void setScore(List<String> list, String value, int score) {
		if(list.contains(value)) {
			this.score+=score;
		}
	}

    private void parseAttributes(String attrWork) {
        while(!attrWork.equals("")) {
            attrWork=attrWork.trim();
            int end = 0;
            if(attrWork.startsWith("(") 
					|| attrWork.startsWith("[")) {
                end = attrWork.indexOf(attrWork.startsWith("(")?")":"]");
                attributes.add(attrWork.substring(0, end+1));
            }
            attrWork = attrWork.substring(end+1).trim();
        }
    }
    
    public String getFilename() {
        return filename;
    }
    
    public List<String> getAttributes() {
        return attributes;
    }

	public String getAlternativeName() {
		return alternativeName;
	}
	
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        
		out.append(score).append(" ");
        if(!alternativeName.equals("")) {
            out.append("<BR/>").append(alternativeName).append(" ");
        }
        if(attributes.size()>0) {
			Map<String, GoodCode> collect = GoodToolsConfigOds.getCodes().entrySet().stream()
						.filter(r -> r.getValue().getScore()!=0)
						.collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));
			for(String attr : attributes) {
				out.append("<BR/>");
				if(collect.containsKey(attr)) {
					out.append(GoodToolsConfigOds.getCodes().get(attr).getDescription()).append(" ").append(attr);
				} else {
					out.append("Unknown: ").append("<b>").append(attr).append("</b>");
				}
			}
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