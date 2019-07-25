package hci;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class WelcomeController
{
    private ImageController img;

    public WelcomeController()
    {
        img = ImageController.getInstance();
    }

    public StackPane getContent(EventHandler<MouseEvent> newTabCallback, EventHandler<MouseEvent> settingsCallback, EventHandler<MouseEvent> tutorialCallback)
    {
        Label title = new Label("UltraPicasso");
        title.getStyleClass().add("settings-heading");
        title.setStyle("-fx-font-size:45px;");
        Label motto = new Label("Editing Evolved");
        motto.getStyleClass().add("settings-entry-title");
        motto.setStyle("-fx-font-size:30px;-fx-font-weight:normal;-fx-padding:0 0 40px 0;-fx-opacity:.6;");

        Label start = new Label("Start");
        Label startInfo = new Label("The + at the top or Ctrl+n will also work");
        startInfo.setStyle("-fx-text-fill:white;-fx-opacity:.4;-fx-padding:0 0 5px 0;");
        start.setStyle("-fx-font-weight:normal;-fx-font-size:20px;");
        start.getStyleClass().add("settings-entry-title");
        Label newFile = new Label("New file");
        newFile.setStyle("-fx-text-fill:#007acc;-fx-font-size:14px;");
        newFile.getStyleClass().add("clickable");
        newFile.addEventHandler(MouseEvent.MOUSE_CLICKED, newTabCallback);
        Label openFile = new Label("Open from folder");
        openFile.setStyle("-fx-text-fill:#007acc;-fx-font-size:14px;");

        Label recent = new Label("Recent");
        recent.getStyleClass().add("settings-entry-title");
        recent.setStyle("-fx-font-weight:normal;-fx-padding:30px 0 0 0;-fx-font-size:20px;");
        Label errorRecent = new Label("No files to show.");
        errorRecent.setStyle("-fx-text-fill:#007acc;");

        Label quickLinks = new Label("Quick Links");
        quickLinks.getStyleClass().add("settings-entry-title");
        quickLinks.setStyle("-fx-font-weight:normal;-fx-padding:30px 0 0 0;-fx-font-size:20px;");

        Label quickLinkInfo = new Label("You can find these in the top right of the screen at any time!");
        quickLinkInfo.setStyle("-fx-text-fill:white;-fx-opacity:.4;-fx-padding:0 0 10px 0;");


        Label settings = new Label("Settings");
        settings.setStyle("-fx-font-size:17px;-fx-text-fill:white;");
        HBox settingsLink = new HBox(8, ImageController.createImageView(img.cogMini), settings);
        settingsLink.setStyle("-fx-background-color:#151515;-fx-padding:6px;-fx-background-radius:2px;");
        settingsLink.setAlignment(Pos.CENTER_LEFT);
        settingsLink.getStyleClass().add("clickable");
        settingsLink.addEventHandler(MouseEvent.MOUSE_CLICKED, settingsCallback);

        Label tutorial = new Label("Tutorial");
        tutorial.setStyle("-fx-font-size:17px;-fx-text-fill:white;");
        HBox tutorialLink = new HBox(8, ImageController.createImageView(img.questionMini), tutorial);
        tutorialLink.setStyle("-fx-background-color:#151515;-fx-padding:6px;-fx-background-radius:2px;");
        tutorialLink.setAlignment(Pos.CENTER_LEFT);
        tutorialLink.getStyleClass().add("clickable");
        tutorialLink.addEventHandler(MouseEvent.MOUSE_CLICKED, tutorialCallback);



        Label welcomeLabel = new Label("Show welcome page on startup");
        welcomeLabel.setStyle("-fx-text-fill:white;-fx-opacity:.8;");
        Circle welcomeRadio = SettingsController.createRadio(welcomeLabel);
        HBox welcomeContainer = new HBox(12, welcomeRadio, welcomeLabel);

        VBox quickLinkContainer = new VBox(8, settingsLink, tutorialLink, welcomeContainer);

        VBox mainColumn = new VBox(title, motto, start, startInfo, newFile, recent, errorRecent, quickLinks, quickLinkInfo, quickLinkContainer);
        mainColumn.setStyle("-fx-padding:60px 80px");

        VBox rightColumn = new VBox();

        HBox content = new HBox(mainColumn, rightColumn);
        ImageView iconDark = new ImageView(img.iconDark);
        iconDark.setStyle("-fx-padding: 30px;");
        StackPane.setAlignment(iconDark, Pos.CENTER_RIGHT);
        StackPane stack = new StackPane(iconDark, content);

        return stack;
    }
}
