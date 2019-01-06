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

/**
 *
 * @author phramusca ( https://github.com/phramusca/ )
 */
public class GoodCountry {

	private final String code;
	private final int score;
	private final String language;

	/**
	 *
	 * @param code
	 * @param score
	 * @param description
	 */
	public GoodCountry(String code, int score, String description) {
		this.code = code;
		this.score = score;
		this.language = description;
	}

	public String getCode() {
		return code;
	}

	public String getLanguage() {
		return language;
	}

	public int getScore() {
		return score;
	}
	
}
