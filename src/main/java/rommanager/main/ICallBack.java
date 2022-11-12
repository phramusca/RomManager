/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rommanager.main;

import java.io.IOException;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public interface ICallBack {

	public void setup(int size);
	public void read(JeuVideo via);
	public void completed();
	public void interrupted();
	public void error(IOException ex);
}
