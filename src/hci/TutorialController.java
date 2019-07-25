package hci;

import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class TutorialController
{
    private ImageView move;
    private ImageView select;
    private ImageView text;
    private ImageView crop;
    private ImageView colorPicker;
    private ImageView resize;

    public TutorialController()
    {

    }

    public StackPane getContent()
    {

        EditorController ec = new EditorController();
        BorderPane content = ec.getEditorOverlay();

        StackPane conatiner = new StackPane(content);
        return conatiner;
    }
}
