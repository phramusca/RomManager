/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.rommanager;

import org.jopendocument.dom.spreadsheet.Sheet;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class Row {
    private final Sheet sheet;
    private final int index;

    public Row(Sheet sheet, int index) {
        this.sheet = sheet;
        this.index = index;
    }

    public String getArg(int colIndex) {
        return sheet.getCellAt(colIndex, this.index).getValue().toString().toLowerCase();
    }

    public String getValue(int colIndex) {
        return sheet.getCellAt(colIndex, this.index).getValue().toString();
    }
}
