import java.io.DataInputStream;
import java.io.File;
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
	static PrintStream output = null;
//	static DataInputStream k = null;
	static Scanner k = new Scanner(System.in);
	
	public static void main(String[] args) throws Exception
	{
		String string, response;
	    boolean connected; 
	    
	    System.out.println("Press enter to connect to server");
	    k.nextLine();
	    connected = setUpClient();
	    
	    listServerFiles();
	    
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
	
	public static void getCommand() throws Exception
	{
		int command = -1;
		showMenu();
		System.out.print("Command: ");
		command = k.nextInt();
		k.nextLine();
		
		switch(command)
		{
			case 1: listServerFiles();
					break;
			case 2: listLocalFiles();
					break;
			case 3: sendFile();
					break;
			case 4: recieveFile();
					break;
			case 5: closeClient();
					break;
			default: System.out.println("Invalid command");
		}
	}
	
	public static void sendFile()
	{
		
	}
	
	public static void listLocalFiles()
	{
		
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
	    
	    int bytesLength = 0;
	    byte[] buffer = new byte[1024];
	    
	    try 
	    {
	    	file = new File("new.txt");
	    	fileOut = new FileOutputStream(file);
	    	
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
	    catch(Exception e)
	    {System.out.println("ERROR: Error recieving file");}
	}
	
	public static void closeClient()
	{
		try
		{
			sendString("/CLOSE_CONNECTION");
			output.close();
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
		output.println(string);
	}
	
	public static boolean setUpClient()
	{
		try 
	    {
	    	clientSocket = new Socket("localhost", 2222);
	    	output = new PrintStream(clientSocket.getOutputStream());
	    	input = new DataInputStream(clientSocket.getInputStream());
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
















