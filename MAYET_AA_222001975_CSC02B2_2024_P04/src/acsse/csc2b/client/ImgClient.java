package acsse.csc2b.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ImgClient extends Application
{
	
	public static void main(String[] args)
	{
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception
	{
		primaryStage.setTitle("Server File Sender");
		
		ClientPane clientPane = new ClientPane();
		
		Scene scene = new Scene(clientPane, 800, 600);
		
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}