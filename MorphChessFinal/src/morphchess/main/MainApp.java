package morphchess.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application
{
	@Override
	public void start(Stage stage) throws Exception 
	{
		try 
		{
			System.out.println("start is called");
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MorphChessBoard.fxml"));
			
			Scene scene = new Scene(loader.load());
			stage.setTitle("Morph Chess");
			stage.setScene(scene);
			stage.show();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			System.out.print("EROR" + e.getMessage());
		}
	}
	
	public static void main(String[] args) 
	{
	    System.out.println("main starts");
	    launch(args);
	    System.out.println("after launch args");
	}
}