/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rommanager.main;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class RomManager {

    protected static Options options;
    
    public static void main(String[] args) {
        options = new Options("RomManager.properties");
        options.read();
        RomManagerGUI.main(args);
    }
}
