import java.io.File;
import java.util.Observable;
import java.util.Observer;

public class FileManager extends Observable implements Runnable
{
	private File directory;
	private File[] files;
	
	public FileManager(Observer o)
	{
		directory = new File("folder");
		files = directory.listFiles();
		addObserver(o);
	}
	
	public File[] getFiles()
	{
		return this.files;
	}
	
	public void run() 
	{
		while(true)
		{
			System.out.println("Checking changes");
			if(checkForChange())
				updateFiles();
			
			try 
			{
				Thread.sleep(5000);
			}
			catch (InterruptedException e)
			{
				System.err.println("Thread sleep interrupted");
			}
		}
	}
	
	
	public void updateFiles()
	{
		System.out.println("Updating files");
		files = directory.listFiles();
		setChanged();
		notifyObservers();
	}
	
	public boolean checkForChange()
	{
		if(files.length != directory.listFiles().length)
			return true;
		
		for(int i=0;i<files.length;i++)
			if(!(files[i].equals(directory.listFiles()[i])))
				return true;
		
		return false;
	}
}
