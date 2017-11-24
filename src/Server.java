import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

@SuppressWarnings("deprecation")
public class Server 
{
	static ServerSocket echoServer = null;
	static DataInputStream is;
	static PrintStream os;
	static Socket clientSocket = null;
	
	public static void main(String args[]) 
	{
		String line;

		setUpServer();
		setUpClient();

		while (true) 
		{
			line = getString();

			if(line.indexOf("/CLOSE_CONNECTION") != -1)
			{
				closeClient(clientSocket);
				break;
			}
			else
				os.println("From server: " + line);
		}
	}
	
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
			string = is.readLine();
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
			is = new DataInputStream(clientSocket.getInputStream());
			os = new PrintStream(clientSocket.getOutputStream());
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
}

















