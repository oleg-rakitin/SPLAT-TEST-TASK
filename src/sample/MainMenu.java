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
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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

    @FXML
    private Button buttonChooseDir;

    @FXML
    private TextArea searchResult;

    @FXML
    private VBox root;

    @FXML
    public TreeView<FolderTreeViewWithFilter.FilePath> treeView;

    @FXML
    private Button c;

    private Stage myStage;

    public void setStage(Stage stage) {
        myStage = stage;
    }



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
                action.accept(new NumberedLine(++line, s));
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

    @FXML
    void initialize() throws IOException {


        /*//treeView = new TreeView<String>();
        c.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                DirectoryChooser dc = new DirectoryChooser();
                dc.setInitialDirectory(new File(System.getProperty("user.home")));
                File choice = dc.showDialog(myStage);
                if(choice == null || ! choice.isDirectory()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Could not open directory");
                    alert.setContentText("The file is invalid.");

                    alert.showAndWait();
                } else {
                    treeView.setRoot(getNodesForDirectory(choice));
                }
            }
        });*/

        buttonSearch.setOnAction(event -> {
            fileExtension = inputFileExtension.getText();
            inputTextSearch = InputTextSearch.getText();
            System.out.println(fileExtension);
            List<File> filesInFolder = null;
            try {
                /*filesInFolder = Files.walk(Paths.get(dirPath))
                        .filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .collect(Collectors.toList());*/
                Files.walk(Paths.get(dirPath)).parallel().filter(Files::isRegularFile).forEach(filePath -> {

                    String name = filePath.getFileName().toString();

                    if (name.endsWith(fileExtension) && !InputTextSearch.getText().equals("")) {
                        List<String> all = null;
                        try (Stream<NumberedLine> s = lines(filePath)) {
                            all = s.filter(nl -> nl.getLine().contains(inputTextSearch))
                                    .map(NumberedLine::toString)
                                    .collect(Collectors.toList());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        for (int i = 0; i < all.size(); i++) {
                            if (!all.isEmpty()) {
                                searchResult.appendText(filePath + " : " + all.get(i));
                                searchResult.appendText("\n");
                            } else {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setHeaderText(null);
                                alert.setTitle("ERROR");
                                alert.setContentText("Запрашиваемый текст не найден");
                                alert.showAndWait();
                            }
                        }

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("error!");

            }
            //System.out.println(getFilesExtension(filesInFolder));
        });
        buttonChooseDir.setOnAction(event -> {
            try {
                dirChoose(myStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private List<String> getFilesExtension(List<File> filesInFolder) {
        List<String> extension = null;
        for (int files = 0; files < filesInFolder.size(); files++) {
            File file = filesInFolder.get(files);
            String name = file.getName();
            extension.add(name.substring(name.lastIndexOf(".")));
        }
        return extension;
    }

    private void dirChoose(Stage primaryStage) throws NullPointerException {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("src"));
        File selectedDirectory = directoryChooser.showDialog(primaryStage);
        try {
            dirPath = selectedDirectory.getAbsolutePath();
            FolderTreeViewWithFilter.setDirPath(dirPath);
            FolderTreeViewWithFilter fl = new FolderTreeViewWithFilter();
            fl.start(myStage,treeView,inputFileExtension,root);
        } catch (RuntimeException | IOException exc) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setTitle("ERROR");
            alert.setContentText("Директория не выбрана!");
            alert.showAndWait();
        }
        System.out.println(dirPath);

    }

    private void configuringDirectoryChooser(DirectoryChooser directoryChooser) {
        // Set title for DirectoryChooser
        directoryChooser.setTitle("Select Some Directories");

        // Set Initial Directory
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    }
}