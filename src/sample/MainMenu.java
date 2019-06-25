package sample;

import java.io.*;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import static sample.fileSearchConst.ROOT_FOLDER;

public class MainMenu {

    private String fileExtension, inputTextSearch;
    public static String dirPath;

    //READ FILE
    private static List<Path> files = new ArrayList<Path>();
    private static List<String> strings = new ArrayList<String>();
    private static Map<Path, Map<Integer,String>> infoString = new HashMap<Path, Map<Integer,String>>();
    private static ArrayList<Integer> textLines = new ArrayList<Integer>();
    private static  Map<Integer,Path> pathKeyLineValue = new HashMap<Integer,Path>();
    private static List<Integer> lineArray = new ArrayList<Integer>();
    private static List<Integer> lineArrayNew = new ArrayList<Integer>();
    private static ArrayList<List<Integer>> arrArr = new ArrayList<List<Integer>>();
    private static List<Path> keyList = new ArrayList<Path>();
    private static int selctedTabIndex;
    ///////////


    public static String getPath(){
        return dirPath;
    }

    @FXML
    private ResourceBundle resources;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private URL location;

    @FXML
    private TextField InputTextSearch;

    @FXML
    private TextField inputFileExtension;

    @FXML
    public Button buttonSearch;
    public Button wqf;
    @FXML
    private Button buttonChooseDir;

    @FXML
    private TextArea searchResult;

    @FXML
    private VBox root;

    @FXML
    private VBox tabBox;

    @FXML
    private TabPane tabsPane;

    @FXML
    public TreeView<FolderTreeViewWithFilter.FilePath> treeView;

    @FXML
    private Button c;

    @FXML
    private Button nextLine;

    private Stage myStage;

    public void setStage(Stage stage) {
        myStage = stage;
    }
    private static List<Path> filePath1 = new ArrayList<Path>();
    private TabsPane tP = new TabsPane();
    private int i2=0;
    private int iterLines =0;

    public static Stream<NumberedLine> lines(Path p) throws IOException {
        BufferedReader b = new BufferedReader(new InputStreamReader(new FileInputStream(p.toString()), "utf-8"));
        Spliterator<NumberedLine> sp = new Spliterators.AbstractSpliterator<NumberedLine>(
                Long.MAX_VALUE, Spliterator.ORDERED | Spliterator.NONNULL) {
            int line;

            public boolean tryAdvance(Consumer<? super NumberedLine> action) {
                String s;
                try {
                    s = b.readLine();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
                if (s == null) return false;
                action.accept(new NumberedLine(++line, s,p));
                System.out.println(p);
                return true;
            }
        };
        return StreamSupport.stream(sp, false).onClose(() -> {
            try {
                b.close();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }




    public static void testSe(Path path,String searchText,String fileExtension) throws IOException {
        Files.walk(path)
                .filter(Files::isRegularFile)
                .forEach((f) -> {
                    String file = f.toString();
                    if (file.endsWith(fileExtension)) {
                        int count = 0;
                        int count1=0;
                        Path oldPath=null;
                        Map<Integer,String> map = new HashMap<Integer,String>();
                        FileReader fileIn = null;
                        try {
                            fileIn = new FileReader(file);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        BufferedReader reader = new BufferedReader(fileIn);
                        String line = null;
                        while(true) {
                            count1++;
                            try {
                                if (!((line = reader.readLine()) != null)) break;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if((line.contains(searchText))) {
                                count++;
                                map.put(count1,line);
                                infoString.put(f,map);

                                if(f != oldPath)
                                    files.add(f);
                                //else
                                //    files.clear();
                                oldPath=f;
                            }

                        }
                    }
                });
    }

    public static Object getKeyFromValue(Map hm, Object value) {
        for (Object o : hm.keySet()) {
            if (hm.get(o).equals(value)) {
                return o;
            }
        }
        return null;
    }

    @FXML
    void initialize() throws Exception {


                    /*tabsPane.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> { ////////UNUSED////////
                                selctedTabIndex = tabsPane.getSelectionModel().getSelectedIndex();
                                System.out.println("CURRENT TAB IS " + tabsPane.getSelectionModel().getSelectedItem().getText() + tabsPane.getSelectionModel().getSelectedIndex());
                                System.err.println("changed");
                            });*/

                    nextLine.setOnAction(event -> {
                        System.out.println(tabsPane.getSelectionModel().getSelectedItem() + "TQWTW");
                        List<Integer> lines = arrArr.get(selctedTabIndex);
                        ObservableList<Tab> t = tabsPane.getTabs();
                        System.out.println("OBS LIST TAB: " + t.get(tabsPane.getSelectionModel().getSelectedIndex()));
                        if (iterLines < lines.size()) {
                            tP.getFindedLine(tP.getTextArea(tabsPane.getSelectionModel().getSelectedIndex()), lines.get(iterLines));
                            System.out.println("LINE "+lines.get(iterLines));
                            System.out.println("SELECTED TEXT AREA: " + tP.getTextArea(tabsPane.getSelectionModel().getSelectedIndex()));
                            System.out.println(iterLines + " Выполнено!");
                            iterLines++;
                        } else {
                            iterLines = 0;
                            System.out.println(iterLines + " ЗАНОГО!");
                        }
                    });


        buttonSearch.setOnAction(event -> {
            if (dirPath != null) {
                if (!InputTextSearch.getText().isEmpty()) {
                    if (!inputFileExtension.getText().isEmpty()) {
                        searchResult.clear();
                        arrArr.clear();
                        Path oldkey = null;
                        try {
                            files.clear();
                            testSe(Paths.get(dirPath), InputTextSearch.getText(), inputFileExtension.getText());
                            FolderTreeViewWithFilter.setDirPath(String.valueOf(dirPath));
                            FolderTreeViewWithFilter fl = new FolderTreeViewWithFilter();
                            fl.start(myStage, treeView, inputFileExtension, root);
                        } catch (IOException e) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setHeaderText(null);
                            alert.setTitle("ERROR");
                            alert.setContentText("Директория не выбрана!");
                            alert.showAndWait();
                        }


                        for (Map.Entry<Path, Map<Integer, String>> entry : infoString.entrySet()) {
                            Path key = entry.getKey();

                            List<Integer> lineArray = new ArrayList<Integer>();
                            lineArray.clear();
                            if (keyList.isEmpty())
                                keyList.add(key);

                            System.out.println("KEY IS : " + key);
                            for (Map.Entry<Integer, String> entry1 : infoString.get(entry.getKey()).entrySet()) {
                                i2 = entry1.getKey();
                                System.out.println(i2);
                                System.out.println(keyList.get(keyList.size() - 1) + " AND " + key);
                                lineArray.add(i2);
                                String str = entry1.getValue();
                                try {
                                    if (oldkey != key)
                                        tP.Init(tabBox, tabsPane, wqf, key);
                                    oldkey = key;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                searchResult.appendText(key.toString() + " " + i2 + " " + str);
                                searchResult.appendText("\n");
                                keyList.add(key);

                            }
                            arrArr.add(lineArray);
                        }
                        System.out.println("RESULT : " + pathKeyLineValue);
                        System.out.println("ARR " + arrArr);
                        infoString.clear();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText(null);
                        alert.setTitle("ERROR");
                        alert.setContentText("Расширение файла не введено");
                        alert.showAndWait();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText(null);
                    alert.setTitle("ERROR");
                    alert.setContentText("Текст для поиска не введен");
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setTitle("ERROR");
                alert.setContentText("Дерриктория не выбрана");
                alert.showAndWait();
            }
        });
        buttonChooseDir.setOnAction(event -> {
            try {
                dirChoose(myStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

   /* private List<String> getFilesExtension(List<File> filesInFolder) { //////////UNUSED//////////
        List<String> extension = null;
        for (int files = 0; files < filesInFolder.size(); files++) {
            File file = filesInFolder.get(files);
            String name = file.getName();
            extension.add(name.substring(name.lastIndexOf(".")));
        }
        return extension;
    }*/

    private void dirChoose(Stage primaryStage) throws NullPointerException {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("src"));
        File selectedDirectory = directoryChooser.showDialog(primaryStage);
        try {
            dirPath = selectedDirectory.getAbsolutePath();

        } catch (RuntimeException e ) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setTitle("ERROR");
            alert.setContentText("Директория не выбрана!");
            alert.showAndWait();
        }
        System.out.println(dirPath);

    }

    public static List<Path> getFiles()
    {
        return files;
    }
}