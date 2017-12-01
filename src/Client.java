import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

@SuppressWarnings({"deprecation","unused"})
public class Client 
{
	static Socket clientSocket = null;
	static DataInputStream input = null;
	static PrintStream printStream = null;
	static DataOutputStream output = null;
//	static DataInputStream k = null;
	static Scanner k = new Scanner(System.in);
	static File[] files;
	static File directory;
	
	public static void main(String[] args) throws Exception
	{
		String string, response;
	    boolean connected; 
	    
	    setUpFiles();
	    System.out.println("Press enter to connect to server");
	    k.nextLine();
	    connected = setUpClient();
	    
	    while(!connected)
	    {
	    	String line = "";
	    	System.out.println("ERROR: Error connecting to server");
	    	System.out.println("Enter \"exit\" to quit, or enter to try again");
	    	line = k.nextLine();
	    	if(line.equalsIgnoreCase("exit"))
	    		break;
	    	else
	    		connected = setUpClient();
	    }
	    
	    while(connected)
	    	connected = getCommand();
	    
	    if(connected)
	    	closeClient();
	    
//	    if(connected) 
//	    {
//	    	string = k.readLine();
//	    	
//	    	while(!(string.indexOf("/quit") != -1))
//	    	{
//		    	sendString(string);
//		    	response = getString();
//		    	System.out.println("Response: "+response);
//		    	string = k.readLine();
//	    	}
//	    	
//	    	closeClient();
//	    }
//	    else
//	    	System.out.println("Exitting program");
	}
	
	public static void updateFiles()
	{
		files = directory.listFiles();
	}
	
	public static void setUpFiles()
	{
		try
		{
			directory = new File("clientFolder");
			updateFiles();
		}
		catch (Exception e)
		{
			System.err.println("ERROR: Error finding file \"clientFolder\"");
		}
	}
	
	public static boolean getCommand()
	{
		int command = -1;
		showMenu();
		System.out.print("Command: ");
		command = k.nextInt();
		k.nextLine();
		
		switch(command)
		{
			case 1: listServerFiles();//LIST_FILES
					return true;
			case 2: listLocalFiles();
					return true;
			case 3: sendFile();//SENDING_FILE
					return true;
			case 4: recieveFile();//GET_FILE
					return true;
			case 5: closeClient();//CLOSE_CONNECTION
					return false;
			default: System.out.println("Invalid command");
		}
		
		return true;
	}
	
	public static void listLocalFiles()
	{
		for(File file : files)
			System.out.println(file.toString().substring(13));
	}
	
	public static void listServerFiles()
	{
		String serverFiles = "";
		String currentFile = "";
		
		sendString("/LIST_FILES");
		
		currentFile = getString();
		while(!(currentFile.equals("-1")))
		{
			serverFiles += currentFile+"\n";
			currentFile = getString();
		}
		
		System.out.println(serverFiles);
	}
	
	public static void showMenu()
	{
		System.out.println("Select commad: ");
		System.out.println("1. List files on server");
		System.out.println("2. List local files");
		System.out.println("3. Send file");
		System.out.println("4. Recieve file");
		System.out.println("5. Exit");
	}
	
	public static void recieveFile()
	{
		File file;
		FileOutputStream fileOut;
		boolean fileExsits = false;
		System.out.println("What file do you want to recieve?");
		listServerFiles();
		String fileName = k.nextLine();
	    
	    int bytesLength = 0;
	    byte[] buffer = new byte[1024];
	    
	    try 
	    {
	    	sendString("/GET_FILE");
	    	sendString(fileName);
	    	
	    	if(!(getString().equals("-1")))
	    		fileExsits = true;
	    	
	    	if(fileExsits)
	    	{
//		    	String fileName = getString();
		    	file = new File(fileName);
		    	fileOut = new FileOutputStream("clientFolder/"+file);
		    	
		    	while(true)
		    	{
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
	    }
	    catch(Exception e)
	    {System.out.println("ERROR: Error recieving file");}
	}
	
	public static void sendFile()
	{
		try
		{
			System.out.println("What file do you want to send?");
			listLocalFiles();
			String fileName = k.nextLine();
			sendString("/SENDING_FILE");
			System.out.println("File name: "+fileName);
			File file = new File("clientFolder/"+fileName);
			
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
	
	public static void closeClient()
	{
		try
		{
			sendString("/CLOSE_CONNECTION");
			printStream.close();
			input.close();
			clientSocket.close();
		}
		catch(Exception e)
		{
			System.err.println("ERROR: Error closing client");
		}
	}
	
	public static String getString()
	{
		String response = "";
		
		try
		{
			response = input.readLine();
		}
		catch (UnknownHostException e) 
	    {
	    	System.err.println("Trying to connect to unknown host: " + e);
	    }
	    catch (IOException e) 
	    {
	    	System.err.println("IOException:  " + e);
	    }
		
		return response;
	}

	public static void sendString(String string)
	{
		printStream.println(string);
	}
	
	public static boolean setUpClient()
	{
		try 
	    {
	    	clientSocket = new Socket("localhost", 2222);
	    	printStream = new PrintStream(clientSocket.getOutputStream());
	    	input = new DataInputStream(clientSocket.getInputStream());
	    	output = new DataOutputStream(clientSocket.getOutputStream());
//	    	k = new DataInputStream(new BufferedInputStream(System.in));
	    } 
	    catch (UnknownHostException e) 
	    {
	    	System.err.println("Don't know about host");
	    	return false;
	    }
	    catch (IOException e) 
	    {
	    	System.err.println("Couldn't get I/O for the connection to host");
	    	return false;
	    }
		
		return true;
	}

}
















