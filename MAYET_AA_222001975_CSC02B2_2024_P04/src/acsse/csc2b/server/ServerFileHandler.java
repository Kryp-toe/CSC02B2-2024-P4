package acsse.csc2b.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class ServerFileHandler implements Runnable
{
	//private Socket incomingConnectionSocket = null;
	private PrintWriter printWriter = null;
	private BufferedReader bufferedReader = null;
	private DataOutputStream dataOutputStream = null;
	private DataInputStream dataInputStream = null;
	private boolean isBusy = false;

	private ArrayList<String> imageList = null;

	/**
	 * @param incomingConnectionSocket
	 */
	public ServerFileHandler(Socket incomingConnectionSocket)
	{
		try
		{
			this.printWriter = new PrintWriter(incomingConnectionSocket.getOutputStream());
			this.bufferedReader = new BufferedReader(new InputStreamReader(incomingConnectionSocket.getInputStream()));

			this.dataOutputStream = new DataOutputStream(incomingConnectionSocket.getOutputStream());
			this.dataInputStream = new DataInputStream(incomingConnectionSocket.getInputStream());

		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public ArrayList<String> readTextFile()
	{
		imageList = new ArrayList<String>();
		File listOfImagesFile = new File("data/server/ImgList.txt");

		if (listOfImagesFile.exists())
		{
			try (Scanner sc = new Scanner(listOfImagesFile))
			{
				// check the file is not empty
				if (!sc.hasNext())
				{
					System.err.println("Error: the provided file is empty: %s");
				}

				String imageInfoString = null;
				StringTokenizer token = null;

				while (sc.hasNext())
				{
					imageInfoString = sc.nextLine();
					token = new StringTokenizer(imageInfoString);

					while (token.hasMoreTokens())
					{
						token.nextToken();
						imageList.add(token.nextToken());
					}
				}
			} catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
		}
		return imageList;
	}

	public void sendImgFile(int index)
	{
		//get file instance
		File returnFile = new File("data/server/" + imageList.get(index));

		//ensure the file exists
		if(returnFile.exists())
		{
			//send the file length
			printWriter.println(returnFile.length());
			printWriter.flush();

			try
			{
				//stream to send file
				FileInputStream fileInputStream = new FileInputStream(returnFile);

				//buffer to send file in pieces
				byte[] buffer = new byte[2048];
				int n = 0;

				//while there is data to be sent
				while ((n = fileInputStream.read(buffer))>0)
				{
					//write the data to the stream
					dataOutputStream.write(buffer,0,n);
				}

				//file is finished sending
				dataOutputStream.flush();
				fileInputStream.close();

				System.out.println("File " + returnFile.getPath() + " has been sent to client.");

			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}else
		{
			printWriter.println("File does not exist.");
			printWriter.flush();
		}
	}

	public void recieveImgFile(String id, String name, int size)
	{
		try
		{
			//create file
			File recieveFile = new File("data/server/" + name);

			//recieve the file
			FileOutputStream fileOutputStream = new FileOutputStream(recieveFile);

			byte[] buffer = new byte[2024];
			int n=0;
			int totalbytes = 0;

			//while there are bytes to be read
			while(totalbytes < size)
			{

				n = dataInputStream.read(buffer, 0, buffer.length);

				//write the bytes to the file
				fileOutputStream.write(buffer,0,n);
				System.err.println(n + " read now, its here: " + totalbytes + " / " + size);
				totalbytes += n;
			}

			//add the file to the list
			addFileToList(name);

			//recieved the file
			fileOutputStream.flush();
			fileOutputStream.close();

			printWriter.println("SUCCESS");
			printWriter.flush();

			System.out.println("File " + recieveFile.getPath() + " has been recieved from the client.");

		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void addFileToList(String name)
	{
		File file = new File("data/server/ImgList.txt");
		
		try(PrintWriter pw = new PrintWriter(file))
		{
			int i=0;
			imageList.add(name);
			
			for (String string : imageList)
			{
				//add file names to list
				i++;
				pw.println(i + " " + string);
			}
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void run()
	{
		isBusy = true;

		while (isBusy)
		{
			String commandString;
			try
			{
				//get users command and convert it to upper case
				commandString = bufferedReader.readLine();
				commandString = commandString.toUpperCase();
				System.out.println("Command: " + commandString);

				//break up command into tokens:
				//1. the command
				//2. command params
				StringTokenizer tokensString = new StringTokenizer(commandString.toUpperCase());
				String commandToken = tokensString.nextToken().toUpperCase();

				if(commandToken.equals("LIST"))
				{
					//get the list of images
					imageList = readTextFile();
					
					//display the list to the client
					String printFileLiString = null;
					for (String file : imageList)
					{
						printFileLiString += file + "\t";
					}
					printWriter.println(printFileLiString);
					printWriter.flush();

					System.out.println(imageList);
				}
				else if(commandToken.equals("DOWN"))
				{
					//get image id
					int id = Integer.parseInt(tokensString.nextToken());

					//let client download an image
					sendImgFile(id);
				}
				else if (commandToken.equals("UP"))
				{

					//get img id,name,size
					String id = tokensString.nextToken();
					String name = tokensString.nextToken();
					int size = Integer.parseInt(tokensString.nextToken());
					System.err.println("its here2");
					//download file to server
					recieveImgFile(id, name, size);
					System.err.println("its here3");
					//allow others to connect
					isBusy = false;
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}




















