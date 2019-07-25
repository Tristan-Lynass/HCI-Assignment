package hci;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class MainWindowController
{
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
        if (Validate.validateDimension(input)) // input is good
        {
            if (Validate.validateDimension(width.getText()) && Validate.validateDimension(height.getText())) // other input is good
            {
                create.setDisable(false);
            }
            textfield.setStyle("");
        }
        else // input is bad
        {
            textfield.setStyle("-fx-background-color:#ff7567;");
            create.setDisable(true);
        }
    }

    private ImageController img = null;
    private TabController tc = null;
    private BorderPane editorOverlay = null;
    private int newFileCounter = 0;
    private Tab settingsTab = null;
    private Tab tutorialTab = null;
    private Tab newTab = null;
    private ImageView importImage;
    private HBox fileChooseOverlay;
    private Label dropError;
    private Button create;
    private TextField width;
    private TextField height;
    private List<String> validExtensions = Arrays.asList("jpg", "png", "tiff", "jpeg", "bmp", "webp");

    @FXML
    private TabPane tabPane;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    public void initialize()
    {
        // CLASSFIELD INIT
        img = ImageController.getInstance();
        tc = new TabController();
        EditorController editor = new EditorController();
        editorOverlay = editor.getEditorOverlay();
        fileChooseOverlay = createFileChooseOverlay();

        // TAB-PANE CONFIG
        //primaryContainer.addEventHandler(new EventType<KeyEvent>() {e -> this::onKeyPress});
        tabPane.addEventHandler(KeyEvent.KEY_PRESSED, this::onKeyPress); // Register keybinds
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);

        // WELCOME
        Tab welcome = tc.constructTab(img.logoMicro, false,"Welcome", "Start here.");
        WelcomeController wc = new WelcomeController();
        welcome.setContent(wc.getContent(e -> openNewEditor(), e -> openSettings(), e -> openTutorial()));
        tabPane.getTabs().add(0, welcome);
        tabPane.getSelectionModel().select(0);

        // NEW-TAB
        newTab = tc.constructNewTab();
        newTab.setOnSelectionChanged(this::onNewTabSelectionChanged); // Or e -> newTabSelectionChanged()
        tabPane.getTabs().add(1, newTab);

        // SETTINGS
        settingsTab = tc.constructTab(img.cogMicro, true, "Settings");
        SettingsController sc = new SettingsController();
        settingsTab.setContent(sc.getContent());

        // TUTORIAL
        tutorialTab = tc.constructTab(img.questionMicro, true,"Tutorial");
        TutorialController tutorialController = new TutorialController();

        tutorialTab.setContent(tutorialController.getContent());

        ImageView settingsLink = ImageController.createImageView(img.cogMini);
        settingsLink.getStyleClass().add("clickable");
        settingsLink.addEventHandler(MouseEvent.MOUSE_CLICKED, e ->
        {
            openSettings();
        });
        ImageView tutorialLink = ImageController.createImageView(img.questionMini);
        tutorialLink.getStyleClass().add("clickable");
        tutorialLink.addEventHandler(MouseEvent.MOUSE_CLICKED, e ->
        {
            openTutorial();
        });
        HBox quickLinks = new HBox(5, settingsLink, tutorialLink);
        quickLinks.setAlignment(Pos.TOP_RIGHT);
        anchorPane.getChildren().add(quickLinks);
        AnchorPane.setTopAnchor(quickLinks, 2.);
        AnchorPane.setRightAnchor(quickLinks, 2.);
    }

    /** TAB CREATION **/
    private void openSettings()
    {
        if (tabPane.getTabs().contains(settingsTab)) tabPane.getSelectionModel().select(settingsTab);
        else openTab(settingsTab);
    }

    private void openTutorial()
    {
        if (tabPane.getTabs().contains(tutorialTab)) tabPane.getSelectionModel().select(tutorialTab);
        else openTab(tutorialTab);
    }

    private void openNewEditor()
    {
        String title = "Untitled-" + newFileCounter;
        Tab newEditor = tc.constructTab(img.brushMicro, true, title, title);
        newEditor.setOnSelectionChanged(this::onEditorSelectionChange);
        newEditor.setContent(createNewTabContent());
        openTab(newEditor);
    }

    private StackPane createNewTabContent()
    {
        // Register callbacks and stuff for if an image is dragged
        StackPane contentContainer = new StackPane();
        contentContainer.setOnDragOver(this::onDragOver);
        contentContainer.setOnDragDropped(this::onDragDrop);
        contentContainer.setOnDragExited(this::onDragExit);

        return contentContainer;
    }



    private void onDragOver(DragEvent e)
    {
        if (e.getDragboard().getFiles().size() == 1)
        {
            if (!validExtensions.containsAll(e.getDragboard().getFiles().stream().map(file -> getExtension(file.getName())).collect(Collectors.toList()))) {

                updateImportIcon(ImageController.createImageView(img.importFile, new Color(1.0, .46, .4, 1)));
            }
            else {
                updateImportIcon(ImageController.createImageView(img.importFile, new Color(0.13, 1.0, .53, 1)));
                e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
        }
        else
        {
            updateImportIcon(ImageController.createImageView(img.importFile, new Color(1.0, .46, .4, 1)));
        }
        e.consume();
    }

    private void updateImportIcon(ImageView newIcon)
    {
        ObservableList<Node> containerChildren = ((VBox) fileChooseOverlay.getChildren().get(0)).getChildren();
        containerChildren.remove(0);
        containerChildren.add(0, newIcon);
    }

    // TODO
    private void onDragDrop(DragEvent e)
    {
        Dragboard db = e.getDragboard();
        boolean success = false;
        if (db.getFiles().size() == 1)
        {
            // If condition copied from: https://stackoverflow.com/questions/37962364/javafx-drag-and-drop-accept-only-some-file-extensions
            if (!validExtensions.containsAll(db.getFiles().stream().map(file -> getExtension(file.getName())).collect(Collectors.toList()))) {
                System.out.println("RESET IMAGE");
                updateImportIcon(ImageController.createImageView(img.importFile));
            }
            else {
                System.out.println("RESET IMAGE");
                updateImportIcon(ImageController.createImageView(img.importFile));
                System.out.println(db.getFiles().get(0).getAbsolutePath());
                File file = new File(db.getFiles().get(0).getAbsolutePath());
                try {
                    initTabContent(new ImageView(new Image(file.toURI().toURL().toExternalForm())));
                    success = true;
                } catch (MalformedURLException ex){}
            }
        }

        e.setDropCompleted(success);
        e.consume();
    }

    // TODO
    private void onDragExit(DragEvent e)
    {
        System.out.println("RESET IMAGE");
        updateImportIcon(ImageController.createImageView(img.importFile));

        e.consume();
    }

    // Method copied from: https://stackoverflow.com/questions/37962364/javafx-drag-and-drop-accept-only-some-file-extensions
    private String getExtension(String fileName){
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0 && i < fileName.length() - 1) //if the name is not empty
            return fileName.substring(i + 1).toLowerCase();

        return extension;
    }

    //new Rectangle(300, 300, Color.WHITE)
    private HBox createFileChooseOverlay()
    {
        Label dropText = new Label("Drag your picture here!");

        dropText.getStyleClass().add("settings-entry-title");
        dropText.setStyle("-fx-padding: 0 -30 0 0;");
        importImage = ImageController.createImageView(img.importFile);
        dropError = new Label("Only images!");
        dropError.getStyleClass().add("errorBox");
        //dropError.setStyle("-fx-padding: 0 -30 0 0;");
        dropError.setVisible(true);
        VBox dropContainer = new VBox(importImage, dropText);
        dropContainer.setAlignment(Pos.CENTER);

        Label or = new Label("or");
        or.getStyleClass().add("settings-entry-title");
        or.setStyle("-fx-font-size: 34");

        Label x = new Label("x");
        x.getStyleClass().add("settings-entry-title");
        width = new TextField("400");
        width.getStyleClass().add("color-code-textfield");
        width.setStyle("-fx-text-size:19");
        width.setPrefWidth(100);
        width.textProperty().addListener(new DimensionsListener(width));
        height = new TextField("400");
        height.getStyleClass().add("color-code-textfield");
        height.setStyle("-fx-text-size:19");
        height.setPrefWidth(100);
        height.textProperty().addListener(new DimensionsListener(height));
        HBox dimensions = new HBox(10, width, x, height);
        dimensions.setAlignment(Pos.CENTER);
        create = new Button ("Create");
        create.getStyleClass().add("toggle");
        create.setStyle("-fx-text-fill:#1e1e1e; -fx-background-color:white;-fx-font-size: 30; padding:7px;");
        create.addEventHandler(MouseEvent.MOUSE_CLICKED, e ->
        {
            initTabContent(new Rectangle(Double.parseDouble(width.getText()),Double.parseDouble(height.getText()), Color.WHITE));
        });
        VBox customCanvas = new VBox(40, dimensions, create);
        customCanvas.setAlignment(Pos.CENTER);

        HBox mainContent = new HBox(25, dropContainer, or, customCanvas);
        mainContent.setAlignment(Pos.CENTER);

        return mainContent;
    }

    private void initTabContent(Node node)
    {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        ObservableList<Node> contentChildren = ((StackPane) selectedTab.getContent()).getChildren();
        contentChildren.remove(fileChooseOverlay);
        contentChildren.add(node);
        contentChildren.add(editorOverlay);
    }

    private void openTab(Tab tab)
    {
        int newTabIndex = tabPane.getTabs().size() - 1;
        tabPane.getTabs().add(newTabIndex, tab);         // Add the tab
        tabPane.getSelectionModel().select(newTabIndex); // Focus on new tab
        newFileCounter++;
    }

    /** EVENT HANDLES **/
    private void onKeyPress(KeyEvent e)
    {
        final KeyCombination newTabShortcut = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);
        final KeyCombination settingsShortcut = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
        final KeyCombination tutorialShortcut = new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN);
        if (newTabShortcut.match(e))
        {
            openNewEditor();
        }
        else if (settingsShortcut.match(e))
        {
            openSettings();
        }
        else if (tutorialShortcut.match(e))
        {
            openTutorial();
        }
    }

    private void onNewTabSelectionChanged(Event e)
    {
        if (newTab.isSelected())
        {
            if (tabPane.getTabs().size() == 1) Platform.exit();
            else openNewEditor();
        }
    }

    // Will get called by any tab if its losing or gaining selected
    private void onEditorSelectionChange(Event e)
    {
        Tab tab = ((Tab)e.getTarget());
        ObservableList<Node> stackChildren = ((StackPane)tab.getContent()).getChildren();

        if (tab.isSelected() && !stackChildren.contains(editorOverlay))
        {
            if (((StackPane) tab.getContent()).getChildren().size() == 0) // Image hasn't been added yet
            {
                stackChildren.add(fileChooseOverlay);
            }
            else
            {
                stackChildren.add(editorOverlay);
            }
        }
        else
        {
            stackChildren.remove(fileChooseOverlay);
            stackChildren.remove(editorOverlay);
        }
    }
}