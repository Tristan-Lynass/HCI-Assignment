package hci;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.LinkedList;
import java.util.List;

public class ColourController
{
    private class RgbListener implements ChangeListener<String>
    {
        private final TextField textField;

        RgbListener(TextField textField)
        {
            this.textField = textField;
        }

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
        {
            onRgbInput(textField);
        }
    }

    private class HexListener implements ChangeListener<String>
    {
        private final TextField textField;

        HexListener(TextField textField)
        {
            this.textField = textField;
        }

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
        {
            onHexInput(textField);
        }
    }


    private LinkedList<IColourWatcher> watchers;
    private ImageController img;
    private Color sliderColour;
    private Color toolColour;
    private ImageView pickerWheel;
    private Slider pickerSlider;
    private int NUM_PAINT_BUCKETS = 7;
    private HBox rgbInput;
    private TextField rIn;
    private TextField gIn;
    private TextField bIn;
    private RgbListener rInListener;
    private RgbListener gInListener;
    private RgbListener bInListener;
    private VBox colourTools;
    private Tooltip bucketTooltip = new Tooltip("Right-Click to set colour.");
    private Tooltip rgbTooltip = new Tooltip("Value must be between 0 and 255.");
    private Tooltip hexTooltip = new Tooltip("Value must be 6 hexadecimal numbers (0-F).");
    private HBox colourCodeInput;
    private HBox hexInput;
    private ImageView leftArrow;
    private ImageView rightArrow;
    private TextField hexIn;
    private HexListener hexInListener;


    public ColourController()
    {
        this.img = ImageController.getInstance();
        this.sliderColour = new Color(1,1,1,1);
        this.toolColour = new Color(1,1,1,1);
        this.watchers = new LinkedList<>();

        Label r = new Label("r");
        r.getStyleClass().add("colour-code-label");
        Label g = new Label("g");
        g.getStyleClass().add("colour-code-label");
        Label b = new Label("b");
        b.getStyleClass().add("colour-code-label");
        rIn = new TextField();
        rIn.getStyleClass().add("color-code-textfield");
        gIn = new TextField();
        gIn.getStyleClass().add("color-code-textfield");
        bIn = new TextField();
        bIn.getStyleClass().add("color-code-textfield");
        rInListener = new RgbListener(rIn);
        gInListener = new RgbListener(gIn);
        bInListener = new RgbListener(bIn);
        setRgbInputListeners();
        rgbInput = new HBox(4,r,rIn,g,gIn,b,bIn);
        rgbInput.setPrefWidth(190.0);
        rgbInput.setAlignment(Pos.CENTER);
        rgbInput.getStyleClass().add("color-code-input");


        Label hash = new Label("#");
        hash.getStyleClass().add("colour-code-label");
        hexIn = new TextField();
        hexIn.getStyleClass().add("color-code-textfield");
        hexInListener = new HexListener(hexIn);
        hexIn.textProperty().addListener(hexInListener);
        hexInput = new HBox(4, hash, hexIn);
        hexInput.setPrefWidth(190.0);
        hexInput.setAlignment(Pos.CENTER);
        hexInput.getStyleClass().add("color-code-input");

        leftArrow = img.createImageView(img.arrowLeft);
        leftArrow.getStyleClass().add("disabled");
        leftArrow.addEventHandler(MouseEvent.MOUSE_CLICKED, this::toggleColourCodeType);
        rightArrow = img.createImageView(img.arrowRight);
        rightArrow.getStyleClass().add("clickable");
        rightArrow.addEventHandler(MouseEvent.MOUSE_CLICKED, this::toggleColourCodeType);
        syncColourCodeInputBorders();
        syncRgbInput();
        syncHexInput();

        colourCodeInput = new HBox(5, leftArrow, rgbInput, rightArrow);
        colourCodeInput.setAlignment(Pos.CENTER);
        colourCodeInput.setStyle("-fx-padding:5px;");

        pickerSlider = new Slider(-1,1, 0);
        pickerSlider.setOrientation(Orientation.VERTICAL);
        pickerSlider.getStyleClass().addAll("colour-slider", "clickable");
        pickerSlider.setStyle("-fx-background-color: linear-gradient(to top, #000000, #c5c5c5, #FFFFFF);");
        pickerSlider.valueProperty().addListener(this::onColourSliderChange);


        pickerWheel = new ImageView(img.colourWheel);
        pickerWheel.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::onColourWheelInput);
        pickerWheel.addEventHandler(MouseEvent.MOUSE_PRESSED, this::onColourWheelInput);
        pickerWheel.getStyleClass().add("clickable");
        HBox colourPickerGroup = new HBox(10, pickerWheel, pickerSlider);
        colourPickerGroup.setStyle("-fx-padding:8px;");

        List<HBox> bucketRows = new LinkedList<HBox>();
        for (int ii = 0; ii < Math.ceil(NUM_PAINT_BUCKETS/7); ii++)
        {
            HBox paintBuckets = new HBox(6);
            for (int jj = 0; jj < Math.min(NUM_PAINT_BUCKETS, 7); jj++)
            {
                Circle newBucket = new Circle(10);
                newBucket.setFill(Color.GREY);
                newBucket.setStroke(Color.WHITE);
                newBucket.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onBucketInput);
                newBucket.getStyleClass().add("clickable");
                Tooltip.install(newBucket, bucketTooltip);
                paintBuckets.getChildren().add(newBucket);
            }
            paintBuckets.setAlignment(Pos.BOTTOM_CENTER);
            paintBuckets.setStyle("-fx-padding:10px;");
            bucketRows.add(paintBuckets);
        }

        colourTools = new VBox(colourCodeInput);
        for (HBox bucketRow : bucketRows)
        {
            colourTools.getChildren().add(bucketRow);
        }
        colourTools.getChildren().addAll(colourPickerGroup);
        colourTools.setAlignment(Pos.BOTTOM_LEFT);

    }

    public VBox getColourTools()
    {
        return colourTools;
    }

    private void toggleColourCodeType(Event e)
    {
        ObservableList<Node> children = colourCodeInput.getChildren();

        if (children.get(1).equals(rgbInput))
        {
            children.remove(rgbInput);
            children.add(1, hexInput);

            leftArrow.getStyleClass().remove("disabled");
            leftArrow.getStyleClass().add("clickable");
            leftArrow.setDisable(false);

            rightArrow.getStyleClass().remove("clickable");
            rightArrow.getStyleClass().add("disabled");
            rightArrow.setDisable(true);
        }
        else
        {
            children.remove(hexInput);
            children.add(1, rgbInput);

            rightArrow.getStyleClass().remove("disabled");
            rightArrow.getStyleClass().add("clickable");
            rightArrow.setDisable(false);

            leftArrow.getStyleClass().remove("clickable");
            leftArrow.getStyleClass().add("disabled");
            leftArrow.setDisable(true);
        }
    }

    /**** COLOUR EVENT HANDLERS ***/
    // DONE
    private void onBucketInput(MouseEvent e)
    {
        Circle bucket = (Circle)e.getTarget();
        if (e.getButton().equals(MouseButton.PRIMARY))
        {
            setColour((Color)bucket.getFill(), true);
            syncRgbInput();
            syncHexInput();
            syncSliderInput();
        }
        else if (e.getButton().equals(MouseButton.SECONDARY))
        {
            bucket.setFill(toolColour);
            Tooltip.uninstall(bucket, bucketTooltip);
        }
    }

    // DONE
    private void onRgbInput(TextField inputField)
    {
        //TextField inputField = (TextField)e.getTarget();
        String input = inputField.getText();

        if (isValidRgbInput(input))
        {
            if (isValidRgbInput(rIn.getText()) && isValidRgbInput(gIn.getText()) && isValidRgbInput(bIn.getText()))
            {
                setColour(getRgbInput(), true);
                syncHexInput();
                syncSliderInput();
            }
            inputField.getStyleClass().remove("input-error");
            Tooltip.uninstall(inputField, rgbTooltip);
        }
        else if (!inputField.getStyleClass().contains("input-error"))
        {
            inputField.getStyleClass().add("input-error");
            Tooltip.install(inputField, rgbTooltip);
        }
    }

    /* DONE: Called when the colour wheel is clicked */
    private void onColourWheelInput(MouseEvent e)
    {
        int x = (int)e.getX(), y = (int)e.getY();
        try
        {
            setColour(pickerWheel.getImage().getPixelReader().getColor(x, y), true);
            syncSliderInput();
            syncHexInput();
            syncRgbInput();
        } catch (IndexOutOfBoundsException ex){}
    }

    // DONE
    private void onColourSliderChange(ObservableValue arg0, Object arg1, Object arg2)
    {
        double scaleFactor = pickerSlider.getValue();
        double r = sliderColour.getRed();
        double g = sliderColour.getGreen();
        double b = sliderColour.getBlue();
        Color newColour;

        if (scaleFactor > 0) // Increase color brightness
        {
            double newR = ((1.0 - r) * scaleFactor + r);
            double newG = ((1.0 - g) * scaleFactor + g);
            double newB = ((1.0 - b) * scaleFactor + b);

            newColour = new Color(newR, newG, newB, 1.0);
        }
        else // decrease colour brightness
        {
            scaleFactor = scaleFactor + 1.0;
            newColour = new Color(r * scaleFactor, g * scaleFactor, b * scaleFactor, 1.0);
        }

        setColour(newColour, false);
        syncRgbInput();
        syncHexInput();
    }

    private void onHexInput(TextField inputField)
    {
        String newHex = inputField.getText();
        if (isValidHexInput(newHex))
        {
            setColour(getHexInput(), true);
            syncRgbInput();
            syncSliderInput();
            inputField.getStyleClass().remove("input-error");
            Tooltip.uninstall(inputField, hexTooltip);
        }
        else if (!inputField.getStyleClass().contains("input-error"))
        {
            inputField.getStyleClass().add("input-error");
            Tooltip.install(inputField, hexTooltip);
        }
    }

    /**** COLOUR SYNC METHODS ***/
    // DONE
    private void syncSliderInput()
    {
        pickerSlider.valueProperty().removeListener(this::onColourSliderChange);
        pickerSlider.setStyle("-fx-background-color: linear-gradient(to top, #000000, " + colourToRgb(toolColour) + ", #FFFFFF);");
        pickerSlider.setValue(0.0);
        pickerSlider.valueProperty().addListener(this::onColourSliderChange);
    }

    /* DONE: Change the colour indicator border at the bottom of the inputs to be the same as toolColour */
    private void syncColourCodeInputBorders()
    {
        String newStyle = "-fx-border-style: hidden hidden solid hidden; -fx-border-width: 3; -fx-border-color: " + colourToRgb(toolColour) + ";-fx-border-radius:4px;";
        rgbInput.setStyle(newStyle);
        hexInput.setStyle(newStyle);
    }

    private void syncHexInput()
    {
        hexIn.textProperty().removeListener(hexInListener);
        hexIn.setText(String.format("%02X%02X%02X", (int)(255 * toolColour.getRed()),
                                                    (int)(255 * toolColour.getGreen()),
                                                    (int)(255 * toolColour.getBlue())));
        hexIn.textProperty().addListener(hexInListener);
    }

    // DONE
    private void syncRgbInput()
    {
        removeRgbInputListeners();
        rIn.setText(Integer.toString((int)(255*toolColour.getRed())));
        gIn.setText(Integer.toString((int)(255*toolColour.getGreen())));
        bIn.setText(Integer.toString((int)(255*toolColour.getBlue())));

        rIn.getStyleClass().remove("input-error");
        gIn.getStyleClass().remove("input-error");
        bIn.getStyleClass().remove("input-error");
        setRgbInputListeners();
    }

    /**** COLOUR UTIL METHODS ***/
    // Convert a colour object to an rgb formatted string
    private String colourToRgb(Color c)
    {
        return "rgb(" + (int)(c.getRed()*255) + "," + (int)(c.getGreen()*255) + "," + (int)(c.getBlue()*255) + ")";
    }

    private Color getRgbInput()
    {
        return new Color(Integer.parseInt(rIn.getText())/255.0,
                       Integer.parseInt(gIn.getText())/255.0,
                        Integer.parseInt(bIn.getText())/255.0, 1.0);
    }

    // DONE
    private void setColour(Color newColour, boolean changeSlider)
    {
        if (changeSlider) sliderColour = newColour;
        toolColour = newColour;
        syncColourCodeInputBorders();
        notifyWatchers(toolColour);
    }

    // DONE
    private void setRgbInputListeners()
    {
        rIn.textProperty().addListener(rInListener);
        gIn.textProperty().addListener(gInListener);
        bIn.textProperty().addListener(bInListener);
    }

    // DONE
    private void removeRgbInputListeners()
    {
        rIn.textProperty().removeListener(rInListener);
        gIn.textProperty().removeListener(gInListener);
        bIn.textProperty().removeListener(bInListener);
    }

    private boolean isValidRgbInput(String input)
    {
        boolean valid;
        if (input.matches("[0-9]{1,3}"))
        {
            try
            {
                int inputNum = Integer.parseInt(input);

                if (inputNum >= 0 && inputNum <= 255)
                {
                    valid = true;
                }
                else valid = false;

            }
            catch (NumberFormatException ex) { valid = false; }
        }
        else valid = false;

        return valid;
    }

    private boolean isValidHexInput(String input)
    {
        return input.matches("[0-9a-fA-F]{6}");
    }

    private Color getHexInput()
    {
        return Color.web(hexIn.getText());

    }

    // DONE
    public Color getColor()
    {
        return toolColour;
    }

    public void notifyWatchers(Color colour)
    {
        for (IColourWatcher watcher : watchers)
        {
            watcher.updateColour(colour);
        }
    }

    public void registerWatcher(IColourWatcher watcher)
    {
        watchers.add(watcher);
    }

}