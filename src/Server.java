import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;
import java.net.ServerSocket;

@SuppressWarnings({"unused","deprecation"})
public class Server implements Observer
{
	static ServerSocket echoServer = null;
	static DataInputStream input;
	static DataOutputStream output;
	static PrintStream printStream;
	static Socket clientSocket = null;
	static FileManager fileManager;
	static File directory;
	static File[] files;
	
	public Server()
	{fileManager = new FileManager(this);}
	
	public static void main(String args[]) 
	{
		String line;
		Server server = new Server();
		
		setUpServer();
		Scanner k = new Scanner(System.in);
		System.out.println("Press enter to connect a client");
		k.nextLine();
		setUpClient();
		System.out.println("Connected");
		
		files = fileManager.getFiles();
		
		while(true)
		{
			line = getString();
			if(line.indexOf("/LIST_FILES") != -1)
				sendServerFiles();
			else if(line.indexOf("/GET_FILE") != -1)
				sendFile();
			else if(line.indexOf("/SENDING_FILE") != -1)
				recieveFile();
			else if(line.indexOf("/CLOSE_CONNECTION") != -1)
				break;
		}
		
		
		
		k.close();
//		setUpFiles();
//		Thread thread = new Thread(fileManager);
//		thread.start();
		
//		System.out.println("After");
		

//		for(int i=0;i<files.length;i++)
//			System.out.println(files[i]);
		
//		setUpServer();
//		setUpClient();
//
//		try
//		{
//			File file = files[1];
//			FileInputStream fileIn = new FileInputStream(file);
//			
//			int bytesLength = 0;
//			byte[] buffer = new byte[1024];
//			
//			while(true)
//			{
//				bytesLength = fileIn.read(buffer);
//				
//				output.writeInt(bytesLength);
//				
//				if(bytesLength == -1)
//					break;
//				
//				output.write(buffer, 0, bytesLength);
//			}
//		}
//		catch (Exception e) {}
		
//		while (true) 
//		{
//			line = getString();
//
//			if(line.indexOf("/CLOSE_CONNECTION") != -1)
//			{
//				closeClient(clientSocket);
//				break;
//			}
//			else
//				printStream.println("From server: " + line);
//		}
	}
	
	public static void sendString(String string)
	{
		printStream.println(string);
	}
	
	public static void sendServerFiles()
	{
		String[] fileList = new String[files.length];
		
		for(int i=0;i<files.length;i++)
			fileList[i] = files[i].toString().substring(13);
		
		for(int i=0;i<fileList.length;i++)
			sendString(fileList[i]);
		
		sendString("-1");
	}
	
	public static void recieveFile()
	{
		System.out.println("In recieve file");
		File file;
		FileOutputStream fileOut;
		boolean fileExsits = false;
		String fileName = getString();
	    
	    int bytesLength = 0;
	    byte[] buffer = new byte[1024];
	    
	    try 
	    {
	    	System.out.println("About to recieve");
//		    String fileName = getString();
	    	file = new File(fileName);
	    	fileOut = new FileOutputStream("serverFolder/"+file);
	    	System.out.println("Got the server folder");
	    	
	    	while(true)
	    	{
	    		System.out.println("Reading in first int");
		    	bytesLength = input.readInt();
		    	
		    	System.out.println(bytesLength);
		    	
		    	if(bytesLength == -1)
		    		break;
		    	
		    	input.read(buffer, 0, bytesLength);
		    	fileOut.write(buffer, 0, bytesLength);
	    	}
	    	fileOut.close();
	    	System.out.println("Finished read");
	    }
	    catch(Exception e)
	    {System.out.println("ERROR: Error recieving file");}
	}
	
	public static void sendFile()
	{
		try
		{
			String fileName = getString();
			System.out.println("File name: "+fileName);
			File file = new File("serverFolder/"+fileName);
			
			if(file.exists())
			{
				System.out.println("File Exists");
				System.out.println(file.toString());
				sendString(file.toString().substring(13));
				FileInputStream fileIn = new FileInputStream(file);
				
				int bytesLength = 0;
				byte[] buffer = new byte[1024];
				
				while(true)
				{
					bytesLength = fileIn.read(buffer);
					
					output.writeInt(bytesLength);
					System.out.println("wrote" + bytesLength);
					
					if(bytesLength == -1)
						break;
					
					output.write(buffer, 0, bytesLength);
				}
				fileIn.close();
			}
			else
			{
				System.out.println("File not found");
				sendString("-1");
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		System.out.println("Finished write");
	}
//	
//	public static void setUpFiles()
//	{
//		fileManager = new FileManager(server);
//		fileManager.addObserver(this);
//	}
	
//	public static void updateFiles()
//	{
//		System.out.println("Updating files");
//		files = directory.listFiles();
//	}
//	
//	public static boolean checkForChange()
//	{
//		if(files.length != directory.listFiles().length)
//			return true;
//		
//		for(int i=0;i<files.length;i++)
//			if(!(files[i].equals(directory.listFiles()[i])))
//				return true;
//		
//		return false;
//	}
	
	public static void closeClient(Socket client)
	{
		try
		{
			System.out.println("Closing connection");
			client.close();
		}
		catch(Exception e)
		{System.err.println("ERROR: Error closing connection to client");}
	}
	
	public static String getString()
	{
		String string = "";
		
		try
		{
			string = input.readLine();
		}
		catch(Exception e)
		{System.err.println("ERROR: Error reading from client");}
		
		return string;
	}
	
	public static void setUpClient()
	{
		try
		{
			clientSocket = echoServer.accept();
			input = new DataInputStream(clientSocket.getInputStream());
			printStream = new PrintStream(clientSocket.getOutputStream());
			output = new DataOutputStream(clientSocket.getOutputStream());
		}
		catch(Exception e)
		{System.err.println("ERROR: Error connecting to client");}
	}
	
	public static void setUpServer()
	{
		try
		{
			echoServer = new ServerSocket(2222);
		}
		catch (IOException e) 
		{
			System.err.println("ERROR: Error starting server");
		}
		System.out.println("The server started. To stop it press <CTRL><C>.");
	}

	public void update(Observable o, Object arg) 
	{
		System.out.println("updated");
		files = fileManager.getFiles();
	}
}

















