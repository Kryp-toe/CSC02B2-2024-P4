package acsse.csc2b.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ImgServer
{
	private ServerSocket serverSocket = null;
	private boolean isActive = false;
	
	public ImgServer(int port)
	{
		port = 5432;
		try
		{
			serverSocket = new ServerSocket(port);
			isActive = true;
			System.out.println("Server Connection established with port: " + port);
			
			while (isActive)
			{
				Socket connectionSocket = serverSocket.accept();
				System.out.println("Server accepted Client");
				
				Thread thread = new Thread(new ServerFileHandler(connectionSocket));
				thread.start();
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		ImgServer imgServer = new ImgServer(5432);
	}
}