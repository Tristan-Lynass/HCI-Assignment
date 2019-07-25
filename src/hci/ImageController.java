package hci;

import javafx.scene.CacheHint;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class ImageController
{
    private static ImageController instance = null;
    private Main ref;

    public final Image logo;
    //public final Image closeTabIcon;
    //public final Image newTabIcon;
    //public final Image pencil;
    //public final Image cross;
    //public final Image dot;

    //public final Image cog;
    public final Image crop;
   // public final Image cross;
    public final Image dropper;
    public final Image move;
    //public final Image plus;
    //public final Image question;
    public final Image resize;
    public final Image select;
    public final Image text;
    public final Image colourWheel;
    public final Image arrowLeft;
    public final Image arrowRight;
    //public final Image red;


    public final Image cogMicro;
    public final Image questionMicro;
    public final Image logoMicro;
    public final Image brushMicro;

    public final Image toolBrush;
    public final Image toolRubber;
    public final Image toolBucket;
    public final Image bucketMask;
    public final Image brushMask;
    public final Image importFile;
    //public final Image crossMicro;
    public final Image searchMicro;

    public final Image cogMini;
    public final Image questionMini;
    public final Image iconDark;
    public final Image shapes;
    public final Image logoLarge;




    private ImageController(Main ref)
    {
        this.ref = ref;
        logo         = new Image(ref.pathToURI("res/new-icon-mini.png").toString());
        logoMicro    = new Image(ref.pathToURI("res/new-icon-micro.png").toString());
        logoLarge    = new Image(ref.pathToURI("res/logo-large.png").toString());



        //closeTabIcon = new Image("file:C:\\Users\\Tristan\\Desktop\\cross-32.png");
        //newTabIcon   = new Image("file:C:\\Users\\Tristan\\Desktop\\plus.png");
        //pencil       = new Image("file:C:\\Users\\Tristan\\Desktop\\pencil.png");
        //cross        = new Image("file:C:\\Users\\Tristan\\Desktop\\cross-a.png");
        //dot          = new Image("file:C:\\Users\\Tristan\\Desktop\\dot.png");
        //plus = new Image("http://localhost/hci/ui-plus.png");
        //cross = new Image("http://localhost/hci/ui-cross.png");
        //crossMicro = new Image("http://localhost/hci/ui-cross-micro.png");
        //red = new Image("http://localhost/hci/red.jpg");

        //cog = new Image("http://localhost/hci/ui-cog.png");
        //question = new Image("http://localhost/hci/ui-question-circle-micro.png");

        // Tool Bar
        select = new Image(ref.pathToURI("res/ui-select-mini.png").toString());
        text = new Image(ref.pathToURI("res/ui-text-mini-mini.png").toString());
        resize = new Image(ref.pathToURI("res/ui-resize-mini.png").toString());
        crop = new Image(ref.pathToURI("res/ui-crop-mini.png").toString());
        dropper = new Image(ref.pathToURI("res/ui-dropper-mini.png").toString());
        move = new Image(ref.pathToURI("res/ui-move-mini.png").toString());

        // Colour Picker
        colourWheel = new Image(ref.pathToURI("res/rgbwheel-medium.png").toString());
        arrowLeft = new Image(ref.pathToURI("res/ui-arrow-left.png").toString());
        arrowRight = new Image(ref.pathToURI("res/ui-arrow-right.png").toString());

        questionMini = new Image(ref.pathToURI("res/ui-question-circle-mini.png").toString());
        questionMicro = new Image(ref.pathToURI("res/ui-question-circle-micro.png").toString());

        // Micro
        cogMicro = new Image(ref.pathToURI("res/ui-cog-micro.png").toString());
        cogMini = new Image(ref.pathToURI("res/ui-cog-mini.png").toString());
        brushMicro = new Image(ref.pathToURI("res/ui-brush-micro.png").toString());

        // Tools
        toolBrush = new Image(ref.pathToURI("res/brush-mini-trans.png").toString());
        toolRubber = new Image(ref.pathToURI("res/rubber-mini.png").toString());
        toolBucket = new Image(ref.pathToURI("res/bucket-mini-trans.png").toString());
        bucketMask  = new Image(ref.pathToURI("res/bucket-mini-mask.png").toString());
        brushMask  = new Image(ref.pathToURI("res/brush-mini-mask.png").toString());

        importFile  = new Image(ref.pathToURI("res/ui-import-file.png").toString());
        searchMicro = new Image(ref.pathToURI("res/ui-search-micro.png").toString());
        iconDark = new Image(ref.pathToURI("res/icon-dark-grey.png").toString());
        shapes = new Image(ref.pathToURI("res/ui-shapes-mini.png").toString());
    }

    public static ImageController getInstance()
    {
        if (instance == null)
            throw new IllegalStateException("Class not initialised.");

        return instance;
    }

    public static ImageController getInstance(Main ref)
    {
        if (instance == null)
            instance = new ImageController(ref);

        return instance;
    }


    public static ImageView createImageView(Image img)
    {
        return createImageView(img, Color.WHITE);
    }

    public static ImageView createImageView(Image img, Color colour)
    {
        ColorAdjust monochrome = new ColorAdjust();
        monochrome.setSaturation(-1.0);

        Blend blushA = new Blend(
                BlendMode.DIFFERENCE,
                monochrome,
                new ColorInput(0,0,img.getWidth(), img.getHeight(), colour)
        );

        ImageView imgView = new ImageView(img);
        imgView.setClip(new ImageView(img));

        imgView.setEffect(blushA);

        imgView.setCache(true);
        imgView.setCacheHint(CacheHint.SPEED); // https://docs.oracle.com/javase/8/javafx/api/javafx/scene/CacheHint.html

        return imgView;
    }
}
