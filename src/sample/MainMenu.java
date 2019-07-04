package sample;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MainMenu {

    public static String dirPath;

    private static List<Path> files = new ArrayList<Path>();
    private static Map<Path, Map<Integer,String>> infoString = new HashMap<Path, Map<Integer,String>>();
    private static  Map<Integer,Path> pathKeyLineValue = new HashMap<Integer,Path>();
    private static ArrayList<List<Integer>> arrArr = new ArrayList<List<Integer>>();
    private static List<Path> keyList = new ArrayList<Path>();
    private static int selctedTabIndex;
    private TabsPane tP = new TabsPane();
    private int i2=0;
    private static int iterLines =0;
    private Stage myStage;



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
    private Button nextLine;


    public void setStage(Stage stage) {
        myStage = stage;
    }

    public static String getPath(){
        return dirPath;
    }


    public static void findText(Path path,String searchText,String fileExtension) throws IOException {
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
                                oldPath=f;
                            }

                        }
                    }
                });
    }


    @FXML
    void initialize() {


                    tabsPane.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
                                selctedTabIndex = tabsPane.getSelectionModel().getSelectedIndex();
                                System.out.println("CURRENT TAB IS " + tabsPane.getSelectionModel().getSelectedItem().getText() + tabsPane.getSelectionModel().getSelectedIndex());
                                System.err.println("changed");
                            });

                    nextLine.setOnAction(event -> {
                        System.out.println(tabsPane.getSelectionModel().getSelectedItem() + "TQWTW");
                        try {
                            List<Integer> lines = arrArr.get(selctedTabIndex);
                            ObservableList<Tab> t = tabsPane.getTabs();
                            System.out.println("OBS LIST TAB: " + t.get(tabsPane.getSelectionModel().getSelectedIndex()));
                            if (iterLines < lines.size()) {
                                tP.getFindedLine(tP.getTextArea(tabsPane.getSelectionModel().getSelectedIndex()), lines.get(iterLines));
                                System.out.println("LINE " + lines.get(iterLines));
                                System.out.println("SELECTED TEXT AREA: " + tP.getTextArea(tabsPane.getSelectionModel().getSelectedIndex()));
                                System.out.println(iterLines + " Выполнено!");
                                iterLines++;
                            } else {
                                iterLines = 0;
                                System.out.println(iterLines + " ЗАНОГО!");
                            }
                        } catch (Exception e) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setHeaderText(null);
                            alert.setTitle("ERROR");
                            alert.setContentText("Данные не загружены");
                            alert.showAndWait();
                        }
                    }

        );


        buttonSearch.setOnAction(event -> {

            if (dirPath != null) {
                if (!InputTextSearch.getText().isEmpty()) {
                    if (!inputFileExtension.getText().isEmpty()) {
                        searchResult.clear();
                        arrArr.clear();
                        Path oldkey = null;
                        int ee = tabsPane.getTabs().size();
                        tabsPane.getTabs().remove(0, ee);
                        try {
                            files.clear();
                            findText(Paths.get(dirPath), InputTextSearch.getText(), inputFileExtension.getText());
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
                                        tP.Init(tabBox, tabsPane, key);
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
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
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