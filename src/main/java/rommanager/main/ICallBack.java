package rommanager.main;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public interface ICallBack {

	public void completed();
	public void interrupted();
	public void error(Exception ex);
    public void saved();
}
