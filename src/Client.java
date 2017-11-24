import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

@SuppressWarnings({"deprecation","unused"})
public class Client 
{
	static Socket clientSocket = null;
	static DataInputStream input = null;
	static PrintStream output = null;
	static DataInputStream k = null;
	
	public static void main(String[] args) throws Exception
	{
	    boolean connected = setUpClient();
	    String string, response;
	    
	    if(connected) 
	    {
	    	string = k.readLine();
	    	
	    	while(!(string.indexOf("/quit") != -1))
	    	{
		    	sendString(string);
		    	response = getString();
		    	System.out.println("Response: "+response);
		    	string = k.readLine();
	    	}
	    	
	    	closeClient();
	    }
	    else
	    	System.out.println("Exitting program");
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
	    	k = new DataInputStream(new BufferedInputStream(System.in));
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
















