package hci;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;

import java.util.LinkedList;
import java.util.List;

public class LanguageWatcher
{
    private class TranslationListener
    {
        public Node node;
        public String english;
        public String german;

        public TranslationListener(Node node, String english, String german)
        {
            this.node = node;
            this.english = english;
            this.german = german;
        }
    }

    public enum Lang
    {
        ENGLISH,
        GERMAN
    }

    private static LanguageWatcher instance = null;
    private List<TranslationListener> tooltips;
    private List<TranslationListener> textNodes;
    private Lang language;

    private LanguageWatcher()
    {
        tooltips = new LinkedList<>();
        textNodes = new LinkedList<>();
    }

    public static LanguageWatcher getInstance()
    {
        if (instance == null) instance = new LanguageWatcher();
        return instance;
    }

    public void addToolTipListener(Node node, String english, String german)
    {
        tooltips.add(new TranslationListener(node, english, german));
    }

    public void addTextNodeListener(Node node, String english, String german)
    {
        textNodes.add(new TranslationListener(node, english, german));
    }


    public void setLanguage(Lang lang)
    {
        if (language != lang)
        {
            language = lang;

            for (TranslationListener tl : textNodes)
            {
                String text = (language == Lang.ENGLISH) ? tl.english : tl.german;

                if (tl.node instanceof Button) ((Button)tl.node).setText(text);
                if (tl.node instanceof Label) ((Label)tl.node).setText(text);
            }

            for (TranslationListener tl : tooltips)
            {
                String text = (language == Lang.ENGLISH) ? tl.english : tl.german;
                Tooltip.install(tl.node, new Tooltip(text));
            }
        }
    }
}
