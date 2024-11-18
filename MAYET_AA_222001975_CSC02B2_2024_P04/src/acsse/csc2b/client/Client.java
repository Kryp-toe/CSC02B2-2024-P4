package acsse.csc2b.client;

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
import java.util.StringTokenizer;

public class Client
{
	private Socket clientSocket = null;
	private BufferedReader bufferedReader = null;
	private PrintWriter printWriter = null;
	private DataInputStream dataInputStream = null;
	private DataOutputStream dataOutputStream = null;
	private ArrayList<String> fileList = null;
	
	/**
	 * @param clientSocket
	 * @param txtin
	 * @param txtout
	 * @param dis
	 * @param dos
	 */
	public Client(String address, int port)
	{
		try
		{
			this.clientSocket = new Socket(address, port);
			this.bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			this.printWriter = new PrintWriter(clientSocket.getOutputStream());
			
			this.dataInputStream = new DataInputStream(clientSocket.getInputStream());
			this.dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
			
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public ArrayList<String> listFilesCommand()
	{
		this.fileList = new ArrayList<String>();
		
		printWriter.println("LIST");
		printWriter.flush();
		
		try
		{
			String responseString = bufferedReader.readLine();
			StringTokenizer tokenizer = new StringTokenizer(responseString);
			
			while (tokenizer.hasMoreTokens()) {
				this.fileList.add(tokenizer.nextToken());
			}
			
			return this.fileList;
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public void downloadFile(String fileIndexString)
	{
		int index = Integer.parseInt(fileIndexString);
		
		printWriter.println("DOWN " + index);
		printWriter.flush();
		
		String responseString = " ";
		
		FileOutputStream fileOutputStream = null;
		
		try {
			responseString = bufferedReader.readLine();
			int filesize = Integer.parseInt(responseString);
			
			File fileToDown = new File("data/client/" + fileList.get(index));
			
			fileOutputStream = new FileOutputStream(fileToDown);
			
			byte[] buffer = new byte[2048];
			int n = 0;
			int totalbytes = 0;
			
			while(totalbytes != filesize)
			{
				n = dataInputStream.read(buffer, 0, buffer.length);
				fileOutputStream.write(buffer, 0, n);
				fileOutputStream.flush();
				totalbytes += n;
			}
			
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public String uploadFile(File fileToUp)
	{
		String fileName = fileToUp.getName();
		int fileID = fileList.size() + 1;
		long size = fileToUp.length();
		
		printWriter.println("UP " + fileID + " " + fileName + " " + size);
		printWriter.flush();
		
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(fileToUp);
			byte[] buffer = new byte[2048];
			int n = 0;
			
			while((n = fileInputStream.read(buffer)) > 0)
			{
				dataOutputStream.write(buffer, 0, n);
				dataOutputStream.flush();
			}
			fileInputStream.close();
			
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		try
		{
			String responseString = bufferedReader.readLine();
			return responseString;
		} catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
		
	}

	/**
	 * @return the clientSocket
	 */
	public Socket getClientSocket() {
		return clientSocket;
	}

	/**
	 * @param clientSocket the clientSocket to set
	 */
	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	/**
	 * @return the txtin
	 */
	public BufferedReader getTxtin() {
		return bufferedReader;
	}

	/**
	 * @param txtin the txtin to set
	 */
	public void setTxtin(BufferedReader txtin) {
		this.bufferedReader = txtin;
	}

	/**
	 * @return the txtout
	 */
	public PrintWriter getTxtout() {
		return printWriter;
	}

	/**
	 * @param txtout the txtout to set
	 */
	public void setTxtout(PrintWriter txtout) {
		this.printWriter = txtout;
	}

	/**
	 * @return the dis
	 */
	public DataInputStream getDis() {
		return dataInputStream;
	}

	/**
	 * @param dis the dis to set
	 */
	public void setDis(DataInputStream dis) {
		this.dataInputStream = dis;
	}

	/**
	 * @return the dos
	 */
	public DataOutputStream getDos() {
		return dataOutputStream;
	}

	/**
	 * @param dos the dos to set
	 */
	public void setDos(DataOutputStream dos) {
		this.dataOutputStream = dos;
	}
}
