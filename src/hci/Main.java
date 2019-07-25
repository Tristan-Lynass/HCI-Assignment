

package hci;

import javafx.application.Application;
//import javafx.fxml.FXMLLoader;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
/***
 * ALL ICONS MODIFIED FROM https://fontawesome.com/
 ***/
public class Main extends Application
{
    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage mainStage) throws Exception
    {
        // TODO: Add icon
        // TODO: Reload previous application state
        ImageController img = ImageController.getInstance(this);
        Parent root = FXMLLoader.load(pathToURI("fxml/main_window_layout.fxml"));
        Scene scene = new Scene(root, 1200, 700); // TODO: Retrieve window dimensions from store
        scene.getStylesheets().add(pathToURI("css/main_window.css").toString());
        mainStage.getIcons().setAll(img.logoLarge);
        

        System.out.println(pathToURI("css/main_window.css").toString());
        mainStage.setTitle("UltraPicasso");
        mainStage.setScene(scene);
        /*mainStage.setOnCloseRequest(e -> {
            Platform.exit();
            //System.exit(0);
        });*/
        mainStage.show();
    }

    public URL pathToURI(String filePath)
    {
        return getClass().getResource(filePath);
    }
}
