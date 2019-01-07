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
public class Attribute {
	private final String raw;
	private String key;
	private String value;

	public Attribute(String rawValue) {
		this.raw = rawValue;
		key="";
		value="";
	}

	public Attribute(String raw, String key, String value) {
		this.raw = raw;
		this.key = key;
		this.value = value;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getRaw() {
		return raw;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Attribute{" + "raw=" + raw + ", key=" + key + ", value=" + value + '}';
	}
}
