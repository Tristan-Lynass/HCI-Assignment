package hci;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class TabController
{
    private ImageController img;

    public TabController()
    {
        this.img = ImageController.getInstance();
    }

    private HBox tabTitleContent(String labelText, Image img, boolean shadeImage)
    {
        // Tab Title
        ImageView tabIcon = (shadeImage)?ImageController.createImageView(img):new ImageView(img);
        tabIcon.setFitWidth(17);
        tabIcon.setFitHeight(17);
        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 15px; -fx-text-fill: white; -fx-opacity: 0.5;");
        HBox titleContent = new HBox(7);
        titleContent.getChildren().addAll(tabIcon, label);
        titleContent.setAlignment(Pos.CENTER);
        titleContent.setStyle("");

        return titleContent;
    }

    public Tab constructNewTab()
    {
        Tab newTab = new Tab();
        Label label = new Label("+");
        label.setStyle("-fx-font-size: 15px; -fx-text-fill: white; -fx-opacity: 1; -fx-font-weight: bold; -fx-padding: 0;");
        newTab.setGraphic(label);
        newTab.setTooltip(new Tooltip("New tab (Ctrl + n)")); // Should retrieve this from settings file
        newTab.setClosable(false);
        newTab.getStyleClass().add("clickable");
        newTab.setStyle("-fx-border-width:0;");
        //newTab.setId("newTab");

        return newTab;
    }

    public Tab constructTab(Image tabIcon, boolean shadeImage, String title)
    {
        return constructTab(tabIcon, shadeImage, title, title);
    }

    public Tab constructTab(Image tabIcon, boolean shadeImage, String title, String tooltip)
    {
        Tab tab = new Tab();// create a Tab object and set the Graphic
        tab.setGraphic(tabTitleContent(title, tabIcon, shadeImage));
        tab.setTooltip(new Tooltip(tooltip));

        return tab;
    }
}
