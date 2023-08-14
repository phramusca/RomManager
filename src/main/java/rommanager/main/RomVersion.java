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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class RomVersion {
    private final String filename;
	private final String name;
    private String alternativeName;
    private List<Attribute> attributes;
	private int score;
	private int errorLevel;
	private boolean exportable;
	private Game game;
    private JeuVideo jeuVideo;
	private boolean toCopy;
    private List<String> tags = new ArrayList<>();
    private final long crcValue;
    private final long size;
	
    /**
     * Create a Rom Version, read from fileSystem
     * @param name
     * @param filename
     * @param console
     * @param crcValue
     * @param size
     */
    public RomVersion(String name, String filename, Console console, long crcValue, long size) {
        this.filename = filename;
        System.out.println("name="+name);
        System.out.println("version="+filename);
        attributes = new ArrayList<>();
        alternativeName = "";
		this.name=name;
        setScore(console);
        this.crcValue = crcValue;
        this.size = size;
    }

	/**
	 * Create a Rom Version, read from ODS file
	 * @param name
	 * @param filename
	 * @param alternativeName
	 * @param attributes
	 * @param score
	 * @param errorLevel
	 * @param isExportable
     * @param tags
     * @param crcValue
     * @param size
	 */
	public RomVersion(String name, String filename, String alternativeName, 
			String attributes,
			int score, 
			int errorLevel, 
			boolean isExportable,
            String tags, 
            long crcValue, 
            long size) {
		this.name = name;
		this.filename = filename;
		this.alternativeName = alternativeName;
		this.attributes = new ArrayList<>();
		JSONObject jsonObject;
		try {
			jsonObject = (JSONObject) new JSONParser().parse(attributes);
			JSONArray attrArray = (JSONArray) jsonObject.get("attributes");
			String raw;String key;String value;
			for(int i=0; i<attrArray.size(); i++) {
				JSONObject attr = (JSONObject) attrArray.get(i);
				key = (String) attr.get("key");
				raw = (String) attr.get("raw");
				value = (String) attr.get("value");
				this.attributes.add(new Attribute(raw, key, value));
			}
		} catch (ParseException ex) {
			Logger.getLogger(RomVersion.class.getName()).log(Level.SEVERE, null, ex);
		}

		this.score = score;
		this.errorLevel = errorLevel;
		this.exportable = isExportable;
        if(!tags.trim().equals("")) {
            this.tags = Arrays.asList(tags.split(","));
        }
        this.crcValue = crcValue;
        this.size = size;
	}

	public final void setScore(Console console) {
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
            
			//Parsing attributes
			while(!attrWork.equals("")) {
				attrWork=attrWork.trim();
				int end = 0;
				if(attrWork.startsWith("(") 
						|| attrWork.startsWith("[")) {
					end = attrWork.indexOf(attrWork.startsWith("(")?")":"]");
					attributes.add(new Attribute(attrWork.substring(0, end+1)));
				}
				attrWork = attrWork.substring(end+1).trim();
			}
			//Get attributes scores
			Map<String, GoodCode> codes = GoodToolsConfigOds.getCodes();
			int found=0;
			for(GoodCode gc : codes.values()) {
				List<Attribute> contains = contains(gc.getCode());
				if(contains.size()==1) {
					Attribute attribute = contains.get(0);
					attribute.setKey(gc.getCode());
					this.score+=gc.getScore();
					found++;
					//TODO: Make custom behavior below configurable from ods file (=> no more custom)
					if(attribute.getRaw().startsWith("[T+")) {
						String language = attribute.getRaw().substring(3, 6);
						if(GoodToolsConfigOds.getTranslations().containsKey(language)) {
							GoodCountry translation = GoodToolsConfigOds
									.getTranslations().get(language);
							attribute.setValue(translation.getLanguage());
							this.score+=translation.getScore();
						}
					} else if(
							gc.getCode().startsWith("\\[")
									&& gc.getCode().endsWith("\\d*\\]")
							&& !gc.getCode().startsWith("\\[R-")) {
						String value = attribute.getRaw()
								.substring(2, attribute.getRaw().indexOf("]"));
						this.score+=Integer.valueOf(value);
						attribute.setValue("+"+value);
					} else if(attribute.getRaw().startsWith("(REV")
							|| attribute.getRaw().startsWith("(Vol")) {
//					\(REV\d*\)		1	Revision number (00 is earliest) 
//					\(Vol \d*\) 	1	Official multicart //Not seen until now. TODO: Is this a multi-volume rom or a version number ?
						String value = attribute.getRaw()
								.substring(4, attribute.getRaw().indexOf(")"));
                        try {
                            this.score+=Integer.parseInt(value);
                        } catch(java.lang.NumberFormatException ex) {
                            Logger.getLogger(RomVersion.class.getName())
									.log(Level.WARNING, attribute.toString(), ex);
                        }
						attribute.setValue("{+"+value+"}");
					} else if(attribute.getRaw().startsWith("(V")
							&& !attribute.getRaw().equals("(VS)")) {
//					\(V\d*\.\d*\) 	1	Version number (1.0 is earliest) 
						try {
							String value = attribute.getRaw()
									.substring(2, attribute.getRaw().indexOf(")"));
							String[] split = value.split("\\.");//major.minor
							int score1=Integer.parseInt((split[0]))+1;//major
							int score2=Integer.parseInt((split[1]));//minor
							int scoreToAdd = score1+score2;
							this.score+=scoreToAdd;
							attribute.setValue("{+"+scoreToAdd+"}");
						} catch(PatternSyntaxException | NumberFormatException ex) {
							Logger.getLogger(RomVersion.class.getName())
									.log(Level.WARNING, attribute.toString(), ex);
						}
						
					} 
                    
					//TODO: Manage specific codes:
//					\(\d*k\) 	1	ROM size in kilobits 
//					\(\d*Mbit\) 	1	ROM size in megabits 
//					\(19\d*\)	1	Release year (20th Century) 
//					\(20\d*\)	1	Release year (21st Century) 
//					\(Mapper \d*\) 	1	Mapper number 
//					\[R-\d*\] 	1	Language 
//					\d*-in-1 	1	Pirate multicart 
//					\+ \d* NES 	1	Unlicensed multicart with ## NES games 
//					s\d*e\d* 	1	Series number and episode number for videos 
//					SMB\d* 	1	Unspecified Super Mario Bros. hack 
//					\[h\d*+\d*C\] 	-1000	Hacked internal cartridge information; #th variant 
//					\[h\d*C\]		-1000	Hacked internal cartridge information.
				}
			}
            
            if(console.excludeUnknownAttributes()) {
                if(found<attributes.size()) {
                    this.score-=20;
                }
                if(attributes.size()<=0) {
                    this.score-=10;
                }
            }
			if(attributes.size()<=0 && !filename.equals(name)) {
                alternativeName = FilenameUtils.getBaseName(filename);
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
	    
	private List<Attribute> contains(String regex) {
		return attributes.stream()
			.filter(a -> a.getRaw().matches(regex))
			.collect(Collectors.toList());
	}
	
	public int getErrorLevel() {
		return errorLevel;
	}
    
    public String getFilename() {
        return filename;
    }
    
    public String getExportFolder(Console console, String exportPath) {
        return FilenameUtils.concat(
				FilenameUtils.concat(
						exportPath, console.name()), 
				console.getName());
    }
    
	public String getExportPath(Console console, String exportPath) {
        return FilenameUtils.concat(getExportFolder(console, exportPath), getExportFilename(console));
	}
    
    public String getExportFilename(Console console) {
        if(console.isZip()) {
            return FilenameUtils.getBaseName(filename).concat(".zip");
        } else {
            return filename;
        }
	}
    
    public String getAttributes() {
		JSONArray tagsAsMap = new JSONArray();
		attributes.stream().forEach((attr) -> {
			Map jsonAsMap = new HashMap();
			jsonAsMap.put("key", attr.getKey());
			jsonAsMap.put("raw", attr.getRaw());
			jsonAsMap.put("value", attr.getValue());
			tagsAsMap.add(jsonAsMap);
		});
		JSONObject obj = new JSONObject();
		obj.put("attributes", tagsAsMap);
		return obj.toString();
    }

	public String getAlternativeName() {
		return alternativeName;
	}
	
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
		out.append(score);
        out.append("<BR/>").append(filename);
        out.append("<BR/>").append(name);
        if(!alternativeName.equals("")) {
            out.append("<BR/>").append(alternativeName);
        }
        if(!attributes.isEmpty()) {
			for(Attribute attr : attributes) {
				out.append("<BR/>");
				if(!attr.getKey().equals("")) {
					out.append(GoodToolsConfigOds.getCodes().get(attr.getKey()).getDescription()).append(" ").append(attr.getRaw());
				} else {
					out.append("Unknown: ").append("<b>").append(attr.getRaw()).append("</b>");
				}
				out.append(" ").append(attr.getValue());
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

	public boolean isExportable() {
		return exportable;
	}

	public void setExportable(boolean exportable) {
		this.exportable = exportable;
	}
	
	public void setGame(Game game) {
		this.game=game;
	}
    
	public Game getGame() {
		return game;
	}

    public JeuVideo getJeuVideo() {
        return jeuVideo;
    }

    public void setJeuVideo(JeuVideo jeuVideo) {
        this.jeuVideo = jeuVideo;
    }
    
	void setToCopy(boolean toCopy) {
		this.toCopy = toCopy;
	}

	boolean isToCopy() {
		return toCopy;
	}

    public List<String> getTags() {
        return tags;
    }

    public String getName() {
        return name;
    }
    
    void addTag(String tag) {
        if(!tags.contains(tag)) {
            tags.add(tag);
        }
    }

    public long getCrcValue() {
        return crcValue;
    }

    public long getSize() {
        return size;
    }
}