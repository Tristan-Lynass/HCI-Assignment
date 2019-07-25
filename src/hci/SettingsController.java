package hci;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.LinkedList;
import java.util.List;


public class SettingsController
{
    private class LanguageSearchListener implements ChangeListener<String>
    {
        private final TextField textField;

        LanguageSearchListener(TextField textField)
        {
            this.textField = textField;
        }

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
        {
            onLanguageChange(textField);
        }
    }

    private class DimensionsListener implements ChangeListener<String>
    {
        private final TextField textField;

        DimensionsListener(TextField textField)
        {
            this.textField = textField;
        }

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
        {
            onDimensionInput(textField);
        }
    }

    private void onDimensionInput(TextField textfield)
    {
        String input = textfield.getText();
        if (Validate.validateDimension(input)) textfield.setStyle("");
        else textfield.setStyle("-fx-background-color:#ff7567;");
    }

    /*** SETTINGS ****/
    private boolean darkTheme = true;
    private boolean english = true;
    private int numBuckets = 7;
    private boolean displayBuckets = true;
    private boolean displayColourInput = true;
    private boolean showWelcomeOnStartup = true;


    private Slider numBucketsSlider;
    private Label numBucketsLabel;
    private StackPane trackPane;

    private Button activeTab;
    private HBox content;

    private Button general;
    private VBox generalContent;

    private Button keyBinding;
    private VBox keyBindingContent;

    private List<ToggleButton> languages;
    private ImageController img;
    private TextField width;
    private TextField height;



    public SettingsController()
    {
        img = ImageController.getInstance();
        languages = new LinkedList<>();
        generalContent = constructGeneral();
        keyBindingContent = constructKeyBinding();
        content = new HBox(0, constructCategories(), generalContent);

        HBox.setHgrow(generalContent, Priority.ALWAYS);
    }

    public HBox getContent()
    {
        return content;
    }

    private VBox constructCategories()
    {
        VBox categoryColumn = new VBox(10);


        general = new Button("General");
        general.getStyleClass().addAll("settings-category", "category-selected");
        general.setPrefWidth(200);
        general.addEventHandler(MouseEvent.MOUSE_CLICKED, this::changeFocus);
        activeTab = general;

        keyBinding = new Button("Key Bindings");
        keyBinding.getStyleClass().add("settings-category");
        keyBinding.addEventHandler(MouseEvent.MOUSE_CLICKED, this::changeFocus);
        keyBinding.setPrefWidth(200);

        categoryColumn.getChildren().addAll(general, keyBinding);
        categoryColumn.setAlignment(Pos.TOP_CENTER);
        categoryColumn.setStyle("-fx-padding:15px;");

        return categoryColumn;
    }

    private VBox constructGeneral()
    {
        VBox content = new VBox();
        content.getStyleClass().add("settings-content");

        /***
         * Appearence
         * 1. Theme
         * 2. Language
         * Behaviour
         * 1. Number buckets
         * 2. Show 'Welcome' on startup
         * 3. File history
         * 4. Default canvas size
         */

        /*** THEME ***/
        Label theme = new Label("Theme");
        theme.getStyleClass().add("settings-entry-title");
       /* ToggleButton light = new ToggleButton("Light");
        light.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onToggleClick);
        light.getStyleClass().add("toggle");
        ToggleButton dark = new ToggleButton("Dark");
        dark.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onToggleClick);
        dark.getStyleClass().add("toggle");
        dark.setSelected(true);
        ToggleGroup themeToggle = new ToggleGroup();
        light.setToggleGroup(themeToggle);
        dark.setToggleGroup(themeToggle);*/
        HBox themeContainer = createToggleGroup(12, SettingsController::onToggleClick, null, "Dark", "Light");
        themeContainer.getStyleClass().add("settings-entry");

        /*** LANGUAGE ***/
        Label language = new Label("Language");
        language.getStyleClass().add("settings-entry-title");
        TextField languageSearch = new TextField();
        languageSearch.getStyleClass().add("color-code-textfield");
        languageSearch.textProperty().addListener(new LanguageSearchListener(languageSearch));
        languageSearch.setStyle("-fx-background-color:#1e1e1e");
        languageSearch.setPrefWidth(130);

        ImageView serachImg = ImageController.createImageView(img.searchMicro);
        //serachImg.setStyle("-fx-padding:-50;");
        HBox languageHeaderContainer = new HBox(10, language, languageSearch, serachImg); // Possible add callback for 'x'

        HBox languageContainer = createToggleGroup(12, SettingsController::onToggleClick, languages, "English", "Deutsch", "Français", "العربية", "國語");
        languageContainer.getStyleClass().add("settings-entry");
        languageContainer.setAlignment(Pos.CENTER_LEFT);

        /*** BUCKETS ***/
        numBucketsLabel = new Label("Paint Buckets: 7");
        numBucketsLabel.getStyleClass().add("settings-entry-title");
        HBox bucketLabelContainer = new HBox(12, numBucketsLabel);
        numBucketsSlider = new Slider(1, 49, 7);
        numBucketsSlider.getStyleClass().addAll("settings-slider");
        numBucketsSlider.setPrefWidth(350);
        numBucketsSlider.valueProperty().addListener(this::onBucketSliderChange);
        numBucketsSlider.setStyle("-fx-padding:3px 3px 3px 3px;");
        HBox.setHgrow(numBucketsSlider, Priority.NEVER);
        Label min = new Label("1");
        min.getStyleClass().add("settings-entry-text");
        Label max = new Label("49");
        max.getStyleClass().add("settings-entry-text");
        HBox onToggle = createToggleGroup(12, this::onToggleBuckets, null, "On", "Off");
        onToggle.setStyle("-fx-padding:0 5px 0 0;");
        HBox bucketSliderContainer = new HBox(min, numBucketsSlider, max);
        bucketSliderContainer.getStyleClass().add("settings-entry");
        bucketSliderContainer.setAlignment(Pos.CENTER_LEFT);
        //System.out.println(numBucketsSlider.lookup(".track"));
        //trackPane = (StackPane) numBucketsSlider.lookup(".track");
        Circle bucketRadio = createRadio(numBucketsLabel, bucketSliderContainer);
        bucketLabelContainer.getChildren().add(0, bucketRadio);
        //DecimalFormat df = new DecimalFormat("#.###");
        //String percent = df.format((numBucketsSlider.getValue()-1.0)/48.0);
        //trackPane.setStyle("-fx-background-color: linear-gradient(to right, #72E300 " + percent + "%, #969696 " + percent + "%);");


        /*** Show welcome on startup ****/
        Label welcomePage = new Label("Welcome Page on Startup");
        welcomePage.getStyleClass().add("settings-entry-title");
        Circle welcomePageRadio = createRadio(welcomePage);
        HBox welcomePageContainer = new HBox(12, welcomePageRadio, welcomePage);// createToggleGroup(12, this::onToggleClick,null, "On", "Off");
        welcomePageContainer.getStyleClass().add("settings-entry");


        Label showAdvColourInput = new Label("Advanced Colour Input");
        showAdvColourInput.getStyleClass().add("settings-entry-title");
        Circle colourInputRadio = createRadio(showAdvColourInput);
        HBox advColorInputContainer = new HBox(12, colourInputRadio, showAdvColourInput);//createToggleGroup(12, this::onToggleClick,null, "On", "Off");
        advColorInputContainer.getStyleClass().add("settings-entry");

        /*** Default canvas size ****/
        Label canvasSize = new Label("Default Canvas Size");
        canvasSize.getStyleClass().add("settings-entry-title");
        HBox canvasSizeContainer = createToggleGroup(12, this::onDimensionToggle, null, "Small", "Medium", "Large", "Custom");

        Label x = new Label("x");
        x.getStyleClass().add("settings-entry-title");
        width = new TextField("400");
        width.getStyleClass().add("color-code-textfield");
        width.setStyle("-fx-text-size:17");
        width.setPrefWidth(100);
        width.textProperty().addListener(new DimensionsListener(width));
        height = new TextField("400");
        height.getStyleClass().add("color-code-textfield");
        height.setStyle("-fx-text-size:17");
        height.setPrefWidth(100);
        height.textProperty().addListener(new DimensionsListener(height));
        HBox dimensions = new HBox(10, width, x, height);
        dimensions.setAlignment(Pos.CENTER_LEFT);
        width.setDisable(true);
        height.setDisable(true);

        canvasSizeContainer.getStyleClass().add("settings-entry");


        //bucketSliderContainer.setAlignment(Pos.CENTER);


        Label appearance = new Label("Customise");
        appearance.getStyleClass().add("settings-heading");
        Label behaviour = new Label("Editor");
        behaviour.getStyleClass().addAll("settings-heading", "top-spacing");
        content.getChildren().addAll(appearance,
                                    welcomePageContainer,
                                    theme, themeContainer,
                                    languageHeaderContainer, languageContainer,

                                    behaviour,
                                    advColorInputContainer,
                                    bucketLabelContainer, bucketSliderContainer,
                                    canvasSize, canvasSizeContainer, dimensions);
        return content;
    }

    private void onBucketSliderChange(ObservableValue arg0, Object arg1, Object arg2)
    {
        numBuckets = (int)numBucketsSlider.getValue();
        numBucketsLabel.setText("Paint Buckets: " + numBuckets);

        //DecimalFormat df = new DecimalFormat("#.###");
        //String percent = df.format((numBucketsSlider.getValue()-1.0)/48.0);
        //trackPane.setStyle("-fx-background-color: linear-gradient(to right, #72E300 " + percent + "%, #969696 " + percent + "%);");
    }

    private void onDimensionToggle(MouseEvent e)
    {
        String type = ((ToggleButton)e.getSource()).getText();

        width.setDisable(true);
        height.setDisable(true);

        if (type.equals("Small"))
        {
            width.setText("400");
            height.setText("400");
        }
        else if (type.equals("Medium"))
        {
            width.setText("800");
            height.setText("800");
        }
        else if (type.equals("Large"))
        {
            width.setText("1200");
            height.setText("1200");
        }
        else if (type.equals("Custom"))
        {
            width.setDisable(false);
            height.setDisable(false);
        }

        onToggleClick(e);
    }


    public static void onToggleClick(MouseEvent e)
    {
        ToggleButton tb = (ToggleButton)e.getSource();

        if (!tb.isSelected()) tb.setSelected(true);
    }

    private void onToggleBuckets(MouseEvent e)
    {
        ToggleButton tb = (ToggleButton)e.getSource();

        if (!tb.isSelected()) tb.setSelected(true);

        if (tb.getText().equals("On")) numBucketsSlider.setDisable(false);
        else numBucketsSlider.setDisable(true);
    }

    public static HBox createToggleGroup(int fontSize, EventHandler<MouseEvent> callback, List<ToggleButton> buttonList, String... options)
    {
        ToggleGroup tg = new ToggleGroup();
        HBox container = new HBox(10);
        boolean first = true;
        for (String option : options)
        {
            ToggleButton optionBtn = new ToggleButton(option);
            optionBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, callback);
            optionBtn.getStyleClass().add("toggle");
            optionBtn.setStyle("-fx-font-size: " + fontSize + "px;");

            if (first)
            {
                optionBtn.setSelected(true);
                first = false;
            }

            optionBtn.setToggleGroup(tg);
            container.getChildren().add(optionBtn);
            if (buttonList != null) buttonList.add(optionBtn);
        }
        container.getStyleClass().add("settings-entry");

        return container;
    }

    private void onLanguageChange(TextField textField)
    {
        String newLang = textField.getText().toLowerCase();
        for (ToggleButton lngBtn : languages)
        {
            if (!lngBtn.getText().toLowerCase().contains(newLang)) lngBtn.setDisable(true); // lngBtn.setStyle("-fx-opacity:.5");
            else lngBtn.setDisable(false);//lngBtn.setStyle("");
        }
    }

    private VBox constructKeyBinding()
    {
        VBox content = new VBox();
        return content;
    }

    private void changeFocus(MouseEvent e)
    {
        Button newFocus = (Button)e.getSource();
        activeTab.getStyleClass().remove("category-selected");

        newFocus.getStyleClass().add("category-selected");
        activeTab = newFocus;
    }

    public static Circle createRadio(Node... assoc)
    {
        final Color green = new Color(.45, .89, 0, 1.0);
        final Color grey = new Color(.22, .22, .22, 1.0);

        Circle newRadio = new Circle(9);
        newRadio.setFill(green);
        newRadio.setStroke(grey);
        newRadio.setStrokeWidth(3);
        newRadio.addEventHandler(MouseEvent.MOUSE_CLICKED, e ->
        {
            Circle radioBtn = (Circle)e.getSource();

            if (radioBtn.getFill().equals(green))
            {
                radioBtn.setFill(grey);
                for (Node node : assoc) node.setDisable(true);
            }
            else
            {
                radioBtn.setFill(green);
                for (Node node : assoc) node.setDisable(false);
            }
        });
        newRadio.getStyleClass().add("clickable");

        return newRadio;
    }
}
