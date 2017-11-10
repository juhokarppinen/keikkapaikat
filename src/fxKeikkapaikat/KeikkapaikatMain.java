package fxKeikkapaikat;
	
import javafx.application.Application;
import javafx.stage.Stage;
import keikkapaikat.Tietokanta;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.fxml.FXMLLoader;

/**
 * Ohjelman käynnistävä pääluokka.
 * @author Juho Karppinen
 * @version 8.4.2016
 */
public class KeikkapaikatMain extends Application {
	@Override
	public void start(Stage primaryStage) {			
		try {					
			FXMLLoader ldr = new FXMLLoader(getClass().getResource("KeikkapaikatGUIView.fxml"));
			final Pane root = (Pane)ldr.load();
			final KeikkapaikatGUIController keikkapaikatCtrl = (KeikkapaikatGUIController)ldr.getController();
			
			final Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("keikkapaikat.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("Keikkapaikat");
			
			Tietokanta tietokanta = new Tietokanta();
			keikkapaikatCtrl.setTietokanta(tietokanta);
			keikkapaikatCtrl.lataaYhteystiedot();
			keikkapaikatCtrl.lataaKunnat();
			keikkapaikatCtrl.lataaKeikkapaikat();
			
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
