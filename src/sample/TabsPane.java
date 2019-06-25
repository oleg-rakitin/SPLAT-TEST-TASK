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
    public static Map<Integer,TextArea> hm = new HashMap<Integer,TextArea>();
    private static List<Tab> tabList = new ArrayList<Tab>();
    private static  List<Path> filePath = new ArrayList<Path>();
    javafx.scene.control.TabPane tabPane;



    public void Init(VBox layout, TabPane tabPane1, Path path) throws Exception {
        layout.setSpacing(10);

        createTabControls(tabPane1, path);
        layout.setPadding(new Insets(10));
        VBox.setVgrow(tabPane1, Priority.ALWAYS);

    }

    public static List<Tab> getTabList(){
        return tabList;
    }

    public static Map<Integer,TextArea> getHM(){
        return hm;
    }

    private void createTabControls(javafx.scene.control.TabPane tabPane, Path path) {

        try {
            tabPane.getTabs().add(
                    createTab(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < tabPane.getTabs().size(); i++) {
            System.out.println(tabList.add(tabPane.getTabs().get(i)));
        }
        for (Map.Entry<Integer, TextArea> entry1 : hm.entrySet()) {
            Integer i2 = entry1.getKey();
            System.out.println(i2);
            TextArea str = entry1.getValue();
            System.out.println(str);
        }
    }


    private static List<Path> getFiles()
    {
        return filePath;
    }

    private Tab createTab(Path path) throws IOException {



        List<Path> files = MainMenu.getFiles();
        Tab tab = new Tab(path.toString());
        StackPane tabLayout = new StackPane();
        tabLayout.setStyle("-fx-background-color: " + randomRgbColorString());
        tArea = readFile(path, ".txt");
        filePath.add(path);

        //Label tabText = new Label();
        tArea.setStyle("-fx-font-size: 15px;");
        tabLayout.getChildren().add(tArea);

        tab.setContent(tabLayout);

        hm.put(tabNum,tArea);
        tabNum++;
        System.out.println("PARENT: " + tArea.getParent());
        return tab;
    }

    public TextArea getTextArea(int index) {
        TextArea tx = hm.get(index);
        System.out.println("GETTED TX: " +tx + " " + hm);
        return tx;
    }

    public void getFindedLine(TextArea ta, int line) {
        Platform.runLater(() -> {

            System.out.println("TabsPane : " + ta);
            int index = 0;

            int linesEncountered = 0;
            boolean lineFound = false;
            for (int i = 0; i < ta.getText().length(); i++) {
                index++;

                if (ta.getText().charAt(i) == '\n') {

                    linesEncountered++;
                    if (linesEncountered == line - 1) {
                        lineFound = true;
                        break;
                    }
                }
            }

            if (lineFound) {
                Rectangle2D lineBounds = ((com.sun.javafx.scene.control.skin.TextAreaSkin) ta.getSkin()).getCharacterBounds(index);

                ta.setScrollTop(lineBounds.getMinY () + this.tArea.getScrollTop ());
                ta.selectRange(5,line);
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
        TextArea tx = new TextArea();
        Files.walk(path)
                .filter(Files::isRegularFile)
                .forEach((f) -> {
                    String file = f.toString();
                    if (file.endsWith(fileExtension)) {

                        StringBuilder sb = new StringBuilder();

                        try (BufferedReader br = Files.newBufferedReader(f)) {

                            String line;
                            while ((line = br.readLine()) != null) {
                                tx.appendText(line);
                                tx.appendText("\n");
                            }

                        } catch (IOException e) {
                            System.err.format("IOException: %s%n", e);
                        }

                        System.out.println(tx);
                    }
                });
        System.out.println(line);
        return tx;
    }
}