package acsse.csc2b.client;

import java.io.File;
import java.util.ArrayList;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;

public class ClientPane extends GridPane
{
	
	private Client client = null;
	private ArrayList<String> fileList = null;
	
	Label lblHeader = null;
	Button btnCommand = null;
	TextArea txtResponse = null;
	
	public ClientPane()
	{
		setUpGUI();
		
		btnCommand.setOnAction((event)->{
			client = new Client("localhost",5432);
			
			txtResponse.appendText("Connected to server on port " + client.getClientSocket().getPort() + "\n");
			
			makeLISTButton();
		});
		
//		btnDownload.setOnAction((event)->{
//			txtout.println("RETURN");
//			txtout.flush();
//			
//			FileOutputStream fos = null;
//			
//			
//			try {
//				String response = txtin.readLine();
//				txtResponse.appendText("Retrieving file. \n");
//				int fileSize = Integer.parseInt(response);				
//				File fileToDownload = new File("data/client/beautifulSong.mp3");			
//				
//				fos = new FileOutputStream(fileToDownload);
//				byte[] buffer = new byte[2048];
//				int n=0;
//				int totalBytes = 0;
//				
//				while(totalBytes!=fileSize) {
//					n=dis.read(buffer,0,buffer.length);
//					fos.write(buffer,0,n);
//					fos.flush();
//					totalBytes+=n;
//				}
//				
//				txtResponse.appendText("File Download Complete. \n");
//				
//			} catch (FileNotFoundException e)
//			{
//				e.printStackTrace();
//			} catch (IOException e)
//			{
//				e.printStackTrace();
//			}finally {
//				if(fos!=null && clientSocket!=null) {
//					try {
//						fos.close();
//						clientSocket.close();
//					} catch (IOException e)
//					{
//						e.printStackTrace();
//					}
//				}
//			}
//			
//		});
	}
	
	private void makeLISTButton()
	{
		this.btnCommand.setText("LIST Files");
		this.lblHeader.setText("Retrieve a list of files on the server");
		
		btnCommand.setOnAction((event)->{
			fileList = client.listFilesCommand();
			
			txtResponse.clear();
			
			for (int i = 1; i == fileList.size(); i++)
			{
				txtResponse.appendText((i) + " " + fileList.get(i));
			}
			makeDownButton();
		});
	}
	
	private void makeDownButton()
	{
		btnCommand.setText("DWON a File");
		lblHeader.setText("Download a file from the server");
		
		btnCommand.setOnAction((event) ->{
			this.getChildren().clear();
			
			TextField textFieldID = new TextField("Enter File ID here:");
			Button downloadButton = new Button("Download file");
			
			this.getChildren().addAll(textFieldID, downloadButton, txtResponse);
			
			downloadButton.setOnAction((down_event)->{
				client.downloadFile(textFieldID.getText());
				
				txtResponse.clear();
				txtResponse.appendText("File successfully downloaded in data/client");
				
				makeUpButton();
			});
		});
	}

	private void makeUpButton()
	{
		btnCommand.setText("UP a file");
		lblHeader.setText("Upload a file to the server");
		
		btnCommand.setOnAction((event) ->{
			this.getChildren().clear();
			
			Button upButton = new Button("Upload file");
			
			this.getChildren().addAll(upButton, txtResponse);
			
			upButton.setOnAction((down_event)->{
				
				FileChooser fileChooser = new FileChooser();
				File fileToUp = fileChooser.showOpenDialog(null);
				
				String responseString = client.uploadFile(fileToUp);
				
				txtResponse.clear();
				txtResponse.appendText("File upload in data/server was a " + responseString);
			});
		});
	}

	private void setUpGUI()
	{
		setHgap(10);
		setVgap(10);
		setAlignment(Pos.CENTER);
		
		this.lblHeader = new Label("Server Connection");
		this.lblHeader.setFont(Font.font("Arial",FontWeight.BOLD,16));		
		this.add(lblHeader,0,0,1,1);
		
		this.btnCommand = new Button("Connect to Server");
		this.add(btnCommand,0,1,1,1);
		
//		this.btnDownload = new Button("Download Song");
//		this.add(btnDownload,0,2,1,1);
//
		this.txtResponse = new TextArea();
		this.txtResponse.setPrefHeight(200);
		this.add(txtResponse,0,2,1,1);
	}
}
