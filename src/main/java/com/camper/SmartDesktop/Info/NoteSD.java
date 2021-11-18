package com.camper.SmartDesktop.Info;

import com.camper.SmartDesktop.Main;
import com.camper.SmartDesktop.NodeDragger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.camper.SmartDesktop.Main.*;

public class NoteSD extends Application implements Initializable
{
    @FXML
    private ImageView closeButtonImage;
    @FXML
    private ToolBar noteToolBar;
    @FXML
    private TextArea noteTextArea;
    @FXML
    private ComboBox<String> noteFamilyComboBox;
    @FXML
    private ComboBox<String> noteSizeComboBox;
    @FXML
    private CheckBox noteBoldCheckBox;
    @FXML
    private CheckBox noteItalicCheckBox;
    @FXML
    private Button noteCloseButton;
    private boolean load = false;
    private AnchorPane NoteRoot;
    private int id;
    private Font font = Font.font("System", FontWeight.NORMAL, FontPosture.REGULAR, 12);
    private FontWeight fontWeight = FontWeight.NORMAL;
    private FontPosture fontPosture = FontPosture.REGULAR;
    private static AnchorPane selectedNote;
    private static Map<Integer, NoteSD> notes = new HashMap<>();
    private static int nextId = 1;

    public NoteSD()
    {
    }

    private NoteSD(boolean load)
    {
        this.load = load;
    }

    private AnchorPane getNoteRoot()
    {
        return NoteRoot;
    }

    public static void clearSaveList()
    {
        notes.clear();
        nextId = 1;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        logger.info("NoteSD: begin start method");

        NoteRoot = FXMLLoader.load(Objects.requireNonNull(mainCL.getResource("FXMLs/note.fxml")));
        NoteRoot.setLayoutX(80);
        NoteRoot.setLayoutY(30);
        this.id = nextId;
        nextId++;
        notes.put(this.id, this);
        NoteRoot.setAccessibleHelp(String.valueOf(this.id));
        addChild(NoteRoot);
        if (!load)
        {
            NoteRoot.setAccessibleText(String.valueOf(idOfSelectedTab));
            List<Node> elementsOfSelectedTab = tabs.get(idOfSelectedTab);
            elementsOfSelectedTab.add(NoteRoot);
        }
        logger.info("NoteSD: end start method");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        logger.info("NoteSD: begin initialize method");
        noteBoldCheckBox.setText(languageBundle.getString("noteBoldCheckBox"));
        noteItalicCheckBox.setText(languageBundle.getString("noteItalicCheckBox"));

        Image image = new Image("Images/closeButton40.png", 40, 40, false, false);
        closeButtonImage.setImage(image);

        noteFamilyComboBox.getItems().addAll(Font.getFamilies());
        noteFamilyComboBox.setValue("System");

        List<String> sizeValues = new ArrayList<>();
        sizeValues.addAll(Stream.iterate(0,  n -> ++n).limit(10).map(Object::toString).map(n -> "0" + n).collect(Collectors.toList()));
        sizeValues.addAll(IntStream.iterate(10, n -> ++n).limit(60).mapToObj(Integer::toString).collect(Collectors.toList()));
        noteSizeComboBox.getItems().addAll(sizeValues);
        noteSizeComboBox.setValue("12");

        EventHandler<ActionEvent> listener = event ->
        {
            NoteSD noteSD = notes.get(Integer.parseInt(((AnchorPane) (((Node) event.getSource()).getParent())).getAccessibleHelp()));
            int size = Integer.parseInt(noteSizeComboBox.getValue());
            FontWeight fontWeight = noteBoldCheckBox.isSelected() ? FontWeight.BOLD : FontWeight.NORMAL;
            FontPosture fontPosture = noteItalicCheckBox.isSelected() ? FontPosture.ITALIC : FontPosture.REGULAR;
            Font font = Font.font(noteFamilyComboBox.getValue(), fontWeight, fontPosture, size);
            noteTextArea.setFont(font);
            noteSD.font = font;
            noteSD.fontWeight = fontWeight;
            noteSD.fontPosture = fontPosture;
        };

        noteTextArea.setWrapText(true);

        noteFamilyComboBox.setOnAction(listener);
        noteBoldCheckBox.setOnAction(listener);
        noteItalicCheckBox.setOnAction(listener);
        noteSizeComboBox.setOnAction(listener);

        noteCloseButton.setOnAction(event ->
        {
            selectedNote = (AnchorPane) (((Button) event.getSource()).getParent());
            notes.remove(Integer.parseInt(selectedNote.getAccessibleHelp()));
            Main.root.getChildren().remove(selectedNote);
            logger.info("NoteSD: note was delete");
        });

        noteToolBar.setOnMouseDragged(event ->
        {
            selectedNote = (AnchorPane) (((ToolBar) event.getSource()).getParent());
            NodeDragger.addDraggingProperty(selectedNote, event);
        });
        logger.info("NoteSD: end initialize method");
    }

    public static void addNotesToXML(Document doc, boolean createEmptyXML)
    {
        logger.info("NoteSD: start notes saving");

        org.w3c.dom.Node rootElement = doc.getFirstChild();

        Element notesElement = doc.createElement("notes");
        rootElement.appendChild(notesElement);
        if (!createEmptyXML)
        {
            int id = 1;
            for (Map.Entry<Integer, NoteSD> entry : notes.entrySet())
            {
                NoteSD noteSD = entry.getValue();
                AnchorPane note = noteSD.getNoteRoot();
                Element noteElement = doc.createElement("note" + id);
                //Получить значение таба, при котором был создан элемент
                noteElement.setAttribute("tab", note.getAccessibleText());

                notesElement.appendChild(noteElement);

                Element visibilityElement = doc.createElement("visibility");
                noteElement.appendChild(visibilityElement);
                Text visibilityValue = doc.createTextNode(String.valueOf(note.isVisible()));
                visibilityElement.appendChild(visibilityValue);

                Element layoutElement = doc.createElement("layout");
                noteElement.appendChild(layoutElement);

                Element layoutX = doc.createElement("layoutX");
                layoutElement.appendChild(layoutX);
                Text layoutXValue = doc.createTextNode(String.valueOf((int) (note.getLayoutX())));
                layoutX.appendChild(layoutXValue);

                Element layoutY = doc.createElement("layoutY");
                layoutElement.appendChild(layoutY);
                Text layoutYValue = doc.createTextNode(String.valueOf((int) (note.getLayoutY())));
                layoutY.appendChild(layoutYValue);

                Element fontElement = doc.createElement("font");
                noteElement.appendChild(fontElement);

                Element familyElement = doc.createElement("family");
                fontElement.appendChild(familyElement);
                Text familyElementValue = doc.createTextNode(noteSD.font.getFamily());
                familyElement.appendChild(familyElementValue);

                Element weightElement = doc.createElement("weight");
                fontElement.appendChild(weightElement);
                Text weightElementValue = doc.createTextNode(noteSD.fontWeight.name());
                weightElement.appendChild(weightElementValue);

                Element postureElement = doc.createElement("posture");
                fontElement.appendChild(postureElement);
                Text postureElementValue = doc.createTextNode(noteSD.fontPosture.name());
                postureElement.appendChild(postureElementValue);

                Element sizeElement = doc.createElement("size");
                fontElement.appendChild(sizeElement);
                Text sizeElementValue = doc.createTextNode(String.valueOf(noteSD.font.getSize()));
                sizeElement.appendChild(sizeElementValue);

                Element textElement = doc.createElement("text");
                noteElement.appendChild(textElement);
                for (Node node : note.getChildren())
                {
                    if (node instanceof TextArea)
                    {
                        String text = ((TextArea) node).getText();
                        Text textElementValue = doc.createTextNode(text);
                        textElement.appendChild(textElementValue);
                    }
                }
                id++;
            }
        }
        logger.info("NoteSD: end notes saving");
    }

    public static void loadNotesFromXML(Document doc, XPath xPath) throws Exception
    {
        logger.info("NoteSD: start notes loading");
        XPathExpression notesCompile = xPath.compile("count(/save/notes/*)");
        int numberOfNotes = Integer.parseInt((String)notesCompile.evaluate(doc, XPathConstants.STRING));
        for (int id = 1; id < numberOfNotes + 1; id++)
        {
            NoteSD loadingNote = new NoteSD(true);
            loadingNote.start(Main.Stage);
            AnchorPane rootOfLoadingNote = loadingNote.getNoteRoot();

            int numberOfTab = Integer.parseInt(xPath.evaluate("/save/notes/note" + id + "/@tab", doc));
            //Установить в созданный элемент дополнительный текст, в котором будет лежать значение того таба, на котором элемент был создан
            rootOfLoadingNote.setAccessibleText(String.valueOf(numberOfTab));

            List<Node> tab = tabs.get(numberOfTab);
            tab.add(rootOfLoadingNote);
            boolean visibility = Boolean.parseBoolean(xPath.evaluate("/save/notes/note" + id + "/visibility/text()", doc));
            rootOfLoadingNote.setVisible(visibility);

            double layoutX = Double.parseDouble(xPath.evaluate("/save/notes/note" + id + "/layout/layoutX/text()", doc));
            double layoutY = Double.parseDouble(xPath.evaluate("/save/notes/note" + id + "/layout/layoutY/text()", doc));
            rootOfLoadingNote.setLayoutX(layoutX);
            rootOfLoadingNote.setLayoutY(layoutY);

            String family = xPath.evaluate("/save/notes/note" + id + "/font/family/text()", doc);
            FontWeight weight = FontWeight.valueOf(xPath.evaluate("/save/notes/note" + id + "/font/weight/text()", doc));
            FontPosture posture = FontPosture.valueOf(xPath.evaluate("/save/notes/note" + id + "/font/posture/text()", doc));
            double size = Double.parseDouble(xPath.evaluate("/save/notes/note" + id + "/font/size/text()", doc));

            loadingNote.font = Font.font(family, weight, posture, size);
            loadingNote.fontWeight = weight;
            loadingNote.fontPosture = posture;

            for (Node node : rootOfLoadingNote.getChildren())
            {
                if (node instanceof ComboBox && node.getAccessibleHelp()!=null && node.getAccessibleHelp().equals("noteFamilyComboBox"))
                {
                    ((ComboBox<String>) node).setValue(family);
                    break;
                }
            }

            for (Node node : rootOfLoadingNote.getChildren())
            {
                if (node instanceof CheckBox && node.getAccessibleHelp()!=null && node.getAccessibleHelp().equals("noteBoldCheckBox") && loadingNote.fontWeight.equals(FontWeight.BOLD))
                {
                    ((CheckBox) node).setSelected(true);
                    break;
                }
            }

            for (Node node : rootOfLoadingNote.getChildren())
            {
                if (node instanceof CheckBox && node.getAccessibleHelp()!=null && node.getAccessibleHelp().equals("noteItalicCheckBox") && loadingNote.fontPosture.equals(FontPosture.ITALIC))
                {
                    ((CheckBox) node).setSelected(true);
                    break;
                }
            }

            for (Node node : rootOfLoadingNote.getChildren())
            {
                if (node instanceof ComboBox && node.getAccessibleHelp()!=null && node.getAccessibleHelp().equals("noteSizeComboBox"))
                {
                    ((ComboBox<String>) node).setValue(String.valueOf((int)size));
                    break;
                }
            }

            for (Node node : rootOfLoadingNote.getChildren())
            {
                if (node instanceof TextArea && node.getAccessibleHelp()!=null && node.getAccessibleHelp().equals("noteTextArea"))
                {
                    String text = xPath.evaluate("save/notes/note" + id + "/text/text()", doc);
                    ((TextArea) node).setFont(loadingNote.font);
                    ((TextArea) node).setText(text);
                    break;
                }
            }
        }
        logger.info("NoteSD: end notes loading");
    }
}