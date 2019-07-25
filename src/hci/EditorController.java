package hci;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class EditorController implements IColourWatcher
{
    private ImageController img;
    private BorderPane editorOverlay;
    private ImageView activeTool;
    private StackPane activeDrawTool = null;
    private ColourController colour;
    private ImageView brushMask;
    private StackPane rubber;
    private ImageView bucketMask;

    public EditorController()
    {
        img = ImageController.getInstance();
        colour = new ColourController();
        colour.registerWatcher(this);
        editorOverlay = createEditorOverlay();
    }

    @Override
    public void updateColour(Color colour)
    {
        if (colour.getOpacity() < 0.9999)
        {
            colour = Color.BLACK;
        }
        brushMask.setEffect(getToolColourEffect(brushMask, colour));
        bucketMask.setEffect(getToolColourEffect(bucketMask, colour));

    }

    private Blend getToolColourEffect(ImageView img, Color c)
    {
        Image image = img.getImage();

        ColorAdjust monochrome = new ColorAdjust();
        monochrome.setSaturation(-1.0);

        Blend effect = new Blend(
                BlendMode.DIFFERENCE,
                monochrome,
                new ColorInput(0,0,image.getWidth(), image.getHeight(), c)
        );

        return effect;
    }


    private BorderPane createEditorOverlay()
    {
        VBox sideBar = new VBox(10, createSideBarButton(img.move, "Move"),
                createSideBarButton(img.shapes, "Shapes"),
                createSideBarButton(img.select, "Select Area"),
                createSideBarButton(img.text, "Text"),
                createSideBarButton(img.crop, "Crop"),
                createSideBarButton(img.dropper, "Color Picker"),
                createSideBarButton(img.resize, "Resize"));
        sideBar.getStyleClass().add("editor-sidebar");
        sideBar.setAlignment(Pos.CENTER);

        rubber = createToolStack(img.toolRubber, null, "Eraser", 90);

        brushMask = new ImageView(img.brushMask);
        brushMask.setClip(new ImageView(img.brushMask));
        StackPane brushStack = createToolStack(img.toolBrush, brushMask, "Brush", 70);


        bucketMask = new ImageView(img.bucketMask);
        bucketMask.setClip(new ImageView(img.bucketMask));
        StackPane bucketStack = createToolStack(img.toolBucket, bucketMask, "Fill", 80);//new StackPane(bucketMask, bucket);


        HBox paintTools = new HBox(brushStack, rubber, bucketStack);
        paintTools.setAlignment(Pos.BOTTOM_RIGHT);

        BorderPane bottomBar = new BorderPane();
        bottomBar.setRight(paintTools);
        bottomBar.setLeft(colour.getColourTools());

        BorderPane innerOverlay = new BorderPane();
        innerOverlay.setBottom(bottomBar);

        BorderPane overlay = new BorderPane();
        overlay.setRight(sideBar);
        overlay.setCenter(innerOverlay);

        activeTool = (ImageView)sideBar.getChildren().get(0);
        activeTool.getStyleClass().add("active-tool");

        return overlay;
    }

    private ImageView createSideBarButton(Image image, String tt)
    {
        ImageView newButton = img.createImageView(image);
        newButton.getStyleClass().add("side-bar-button");
        Tooltip.install(newButton, new Tooltip(tt));

        newButton.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onToolClickEvent);
        return newButton;
    }

    private StackPane createToolStack(Image toolImg, ImageView toolMask, String tooltip, int offset)
    {
        ImageView tool = new ImageView(toolImg);
        StackPane toolStack;
        if (toolMask == null) toolStack = new StackPane(tool);
        else toolStack = new StackPane(toolMask, tool);
        toolStack.setTranslateY(offset);
        toolStack.getStyleClass().add("clickable");
        Tooltip.install(toolStack, new Tooltip(tooltip));
        toolStack.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onToolClickEvent);
        toolStack.setStyle("-fx-opacity:.5;");

        return toolStack;
    }

    private ImageView createToolStack(Image toolImg, String tooltip, int offset)
    {
        ImageView tool = new ImageView(toolImg);
        tool.setTranslateY(offset);
        tool.getStyleClass().add("clickable");
        Tooltip.install(tool, new Tooltip(tooltip));
        tool.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onToolClickEvent);

        return tool;
    }

    public BorderPane getEditorOverlay()
    {
        return editorOverlay;
    }

    private void onToolClickEvent(MouseEvent e)
    {
        if(e.getButton().equals(MouseButton.PRIMARY))
        {
            Object src = e.getSource();

            if (src instanceof StackPane)
            {
                StackPane tool = (StackPane)src;
                tool.setStyle("");
                if (activeTool != null)
                {
                    activeTool.getStyleClass().remove("active-tool");
                    activeTool = null;
                }
                if (activeDrawTool != null) activeDrawTool.setStyle("-fx-opacity:.5;");
                activeDrawTool = tool;
            }
            else if (src instanceof ImageView)
            {
                ImageView tool = (ImageView)src;
                tool.getStyleClass().add("active-tool");

                if (activeTool != null) activeTool.getStyleClass().remove("active-tool");
                if (activeDrawTool != null)
                {
                    activeDrawTool.setStyle("-fx-opacity:.5;");
                    activeDrawTool = null;
                }

                activeTool = tool;
            }
        }
    }

    public void disableTools()
    {

    }

}
