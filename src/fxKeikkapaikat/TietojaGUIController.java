package fxKeikkapaikat;
import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Näytetään tietoja ohjelmasta.
 * 
 * @author Juho Karppinen (Vesa Lappalaisen koodia mukaillen)
 * @version 11.9.2016
 */
public class TietojaGUIController {
    
    @FXML private Button buttonOK;
    
    @FXML private void handleOK() {
        ((Stage)buttonOK.getScene().getWindow()).close();
    }
        
    /**
     * Luodaan modaalinen tietojennäyttämisdialogi.
     * @param modalityStage Mille ollaan modaalisia, null = sovellukselle
     */
    public static void naytaTietoja(Stage modalityStage) {
        try {
            URL url = TietojaGUIController.class.getResource("TietojaGUIView.fxml");
            FXMLLoader ldr = new FXMLLoader(url);
            Parent root = ldr.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Keikkapaikat");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(modalityStage);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }
}