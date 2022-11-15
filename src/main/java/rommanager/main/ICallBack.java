package rommanager.main;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public interface ICallBack {

	public void setup(int size);
	public void read(JeuVideo jeuVideo);
	public void completed();
	public void interrupted();
	public void error(Exception ex);
}
