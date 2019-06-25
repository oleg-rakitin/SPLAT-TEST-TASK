package sample;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FolderTreeViewWithFilter {


    private static String dirPath = "src";
    private static TextField fileExt = null;

    TreeItem<FilePath> rootTreeItem;

    //TreeView<FilePath> treeView;
    MainMenu controller = new MainMenu();

    public static void setDirPath(String dir){
        dirPath= dir;
    }

    public static void setFileExtension(TextField fExt){
        fileExt= fExt;
    }

    public void start(Stage primaryStage,TreeView<FilePath> trView, TextField txField, VBox root) throws IOException {

        System.out.println(txField.getText() + "TEST");

        // treeview
        root.setVgrow(trView, Priority.ALWAYS);
        createTree();
        filterChanged(txField.getText(),trView);

    }

    private void createTree() throws IOException {

        rootTreeItem = createTreeRoot();

        createTree( rootTreeItem);

        rootTreeItem.getChildren().sort( Comparator.comparing( new Function<TreeItem<FilePath>, String>() {
            @Override
            public String apply(TreeItem<FilePath> t) {
                return t.getValue().toString().toLowerCase();
            }
        }));

    }

    static boolean useLoop(List<Path> arr, Path targetValue) {
        for(Path s: arr){
            if(s.equals(targetValue) || s.getParent().equals(targetValue))
                return true;
        }
        return false;
    }

    public static void createTree(TreeItem<FilePath> rootItem) throws IOException {

        List<Path> pathFiles = MainMenu.getFiles();
        TreeItem<FilePath> newItem = null;
        System.out.println(pathFiles);
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(rootItem.getValue().getPath())) {
            for (Path path : directoryStream) {

                if (useLoop(pathFiles, path)) {
                    newItem = new TreeItem<FilePath>(new FilePath(path));
                    newItem.setExpanded(true);

                    rootItem.getChildren().add(newItem);

                    System.out.println("FWQFQWFQWFQWFQWFQWFQWF");
                    if (Files.isDirectory(path)) {
                        createTree(newItem);
                    }
                    System.out.println(path + " PATH");
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void filter(TreeItem<FilePath> root, String filter, TreeItem<FilePath> filteredRoot) {

        for (TreeItem<FilePath> child : root.getChildren()) {

            TreeItem<FilePath> filteredChild = new TreeItem<>( child.getValue());
            filteredChild.setExpanded(true);

            filter(child, filter, filteredChild );

            if (!filteredChild.getChildren().isEmpty() || isMatch(filteredChild.getValue(), filter)) {
                filteredRoot.getChildren().add(filteredChild);
            }

        }
    }


    private boolean isMatch(FilePath value, String filter) {
        return value.toString().toLowerCase().contains( filter.toLowerCase()); // TODO: optimize or change (check file extension, etc)
    }


    private void filterChanged(String filter,TreeView<FilePath> trView) {
        if (filter.isEmpty()) {
            trView.setRoot(rootTreeItem);
        }
        else {
            TreeItem<FilePath> filteredRoot = createTreeRoot();
            filter(rootTreeItem, filter, filteredRoot);
            trView.setRoot(filteredRoot);
        }
    }

    private TreeItem<FilePath> createTreeRoot() {
        TreeItem<FilePath> root = new TreeItem<FilePath>( new FilePath( Paths.get(dirPath)));
        root.setExpanded(true);
        return root;
    }

   static class FilePath {

        Path path;
        String text;

        public FilePath( Path path) {

            this.path = path;

            if( path.getNameCount() == 0) {
                this.text = path.toString();
            }
            else {
                this.text = path.getName( path.getNameCount() - 1).toString();
            }

        }

        public Path getPath() {
            return path;
        }

        public String toString() {

            return text;

        }
    }
}