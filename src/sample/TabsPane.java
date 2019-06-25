package sample;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class TabsPane {

    private int tabNum = 0;
    private static final Random random = new Random(42);
    private static String line;
    public TextArea tArea;
    public static Map<Tab,TextArea> hm = new HashMap<Tab,TextArea>();
    private static List<Tab> tabList = new ArrayList<Tab>();
    private static  List<Path> filePath = new ArrayList<Path>();
    javafx.scene.control.TabPane tabPane;



    public void Init(VBox layout, TabPane tabPane1, Button b,Path path) throws Exception {

        //tabPane = new javafx.scene.control.TabPane();
        // tabPane1.setPrefSize(200, 150);

        layout.setSpacing(10);
        //layout = createTabControls(tabPane);

        createTabControls(tabPane1, b, Path path);
        layout.setPadding(new Insets(10));
        VBox.setVgrow(tabPane1, Priority.ALWAYS);

        //final Scene scene = new Scene(layout);
        //stage.setScene(scene);
        //stage.show();
    }

    public static List<Tab> getTabList(){
        return tabList;
    }

    public static Map<Tab,TextArea> getHM(){
        return hm;
    }

    private Button createTabControls(javafx.scene.control.TabPane tabPane, Button b, Path path) {
        //Button addTab = new Button("New Tab");
        List<Path> p = MainMenu.getFiles();

        b.setOnAction(event -> {
            try {
                int ee = tabPane.getTabs().size();
                if(ee!=0)
                    tabPane.getTabs().remove(0, ee);
                for(int i = 0;i<p.size();i++) {
                    tabPane.getTabs().add(
                            createTab(i,path)
                    );
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            for(int i = 0; i<tabPane.getTabs().size();i++){
                System.out.println(tabList.add(tabPane.getTabs().get(i)));
            }
            for(Map.Entry<Tab, TextArea> entry1 : hm.entrySet()){
                Tab i2 = entry1.getKey();
                System.out.println(i2);
                //textLines.add(i2);
                TextArea str = entry1.getValue();
                System.out.println(str);
            }
            //tabPane.getSelectionModel().selectLast();
        });
        b.setMinSize(
                b.USE_PREF_SIZE,
                b.USE_PREF_SIZE
        );
        return b;
    }

    private static List<Path> getFiles()
    {
        return filePath;
    }

    private Tab createTab(int i,Path path) throws IOException {
        tabNum++;
        List<Path> files = MainMenu.getFiles();
        Tab tab = new Tab(files.get(i).toString());
        StackPane tabLayout = new StackPane();
        tabLayout.setStyle("-fx-background-color: " + randomRgbColorString());
        tArea = readFile(files.get(i), ".txt");
        filePath.add(files.get(i));

        //Label tabText = new Label();
        tArea.setStyle("-fx-font-size: 15px;");
        tabLayout.getChildren().add(tArea);

        tab.setContent(tabLayout);

        hm.put(tab,tArea);
        System.out.println("PARENT: " + tArea.getParent());
        return tab;
    }

    public TextArea getTextArea() {
        return tArea;
    }

    public void getFindedLine(TextArea ta, int line) {
        //ta.setStyle("-fx-font-size: 12");
        Platform.runLater(() -> {

            // Define desired line
            //final int line = 30;

            // Index of the first character in line that we look for.
            int index = 0;
            // for this example following line will work:
            // int index = ta.getText().indexOf("Line " + line);

            // for lines that do not contain its index we rely on "\n" count
            int linesEncountered = 0;
            boolean lineFound = false;
            for (int i = 0; i < ta.getText().length(); i++) {
                // count characters on our way to our desired line
                index++;

                if (ta.getText().charAt(i) == '\n') {
                    // next line char encountered
                    linesEncountered++;
                    if (linesEncountered == line - 1) {
                        // next line is what we're looking for, stop now
                        lineFound = true;
                        break;
                    }
                }
            }
            Highlighter.HighlightPainter painter =
                    new DefaultHighlighter.DefaultHighlightPainter( Color.cyan );
            // scroll only if line found
            if (lineFound) {
                // Get bounds of the first character in the line using internal API (see comment below the code)
                //final HighlightableTextArea highlightableTextArea = new HighlightableTextArea();
                Rectangle2D lineBounds = ((com.sun.javafx.scene.control.skin.TextAreaSkin) ta.getSkin()).getCharacterBounds(index);
                //highlightableTextArea.setText(lineBounds);


                // Scroll to the top-Y of our line
                ta.setScrollTop(lineBounds.getMinY () + this.tArea.getScrollTop ());
            }
        });
    }

    private String randomRgbColorString() {
        return "rgb("
                + random.nextInt(255) + ", "
                + random.nextInt(255) + ", "
                + random.nextInt(255) +
                ");";
    }

    public static TextArea readFile(Path path, String fileExtension) throws IOException {
        //String line = null;
        TextArea tx = new TextArea();
        Label tabText = new Label();
        //tabText.addText
        Files.walk(path)
                .filter(Files::isRegularFile)
                .forEach((f) -> {
                    String file = f.toString();
                    if (file.endsWith(fileExtension)) {

                        StringBuilder sb = new StringBuilder();

                        try (BufferedReader br = Files.newBufferedReader(f)) {

                            // read line by line
                            String line;
                            while ((line = br.readLine()) != null) {
                                tx.appendText(line);
                                tx.appendText("\n");
                            }

                        } catch (IOException e) {
                            System.err.format("IOException: %s%n", e);
                        }

                        System.out.println(tx);

                        /*FileReader fileIn = null;
                        try {
                            fileIn = new FileReader(file);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        BufferedReader reader = new BufferedReader(fileIn);

                        while (true) {
                            try {
                                if (!(reader.readLine() != null)) break;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            try {
                                System.out.println(reader.readLine());
                                tx.appendText(reader.readLine());
                                tx.appendText("\n");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }*/
                    }
                });
        System.out.println(line);
        return tx;
    }
}