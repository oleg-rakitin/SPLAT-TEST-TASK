package sample;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
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

    TreeItem<FilePath> rootTreeItem;

    //TreeView<FilePath> treeView;
    MainMenu controller = new MainMenu();

    public static void setDirPath(String dir){
        dirPath= dir;
    }

    public void start(Stage primaryStage,TreeView<FilePath> trView, TextField txField, VBox root) throws IOException {

        // root component
        //TreeItem<Path> rootTreeItem = new TreeItem<Path>(Paths.get( ROOT_FOLDER));
        //rootTreeItem.setExpanded(true);


        // filter
        txField.textProperty().addListener((observable, oldValue, newValue) -> filterChanged(newValue,trView));

        // treeview
        VBox.setVgrow(trView, Priority.ALWAYS);
        createTree();


        trView.setRoot(rootTreeItem);
    }

    private void createTree() throws IOException {

        // create root
        rootTreeItem = createTreeRoot();

        // create tree structure recursively
        createTree( rootTreeItem);

        // sort tree structure by name
        rootTreeItem.getChildren().sort( Comparator.comparing( new Function<TreeItem<FilePath>, String>() {
            @Override
            public String apply(TreeItem<FilePath> t) {
                return t.getValue().toString().toLowerCase();
            }
        }));

    }

    public static void createTree(TreeItem<FilePath> rootItem) throws IOException {

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(rootItem.getValue().getPath())) {

            for (Path path : directoryStream) {

                TreeItem<FilePath> newItem = new TreeItem<FilePath>( new FilePath( path));
                newItem.setExpanded(true);

                rootItem.getChildren().add(newItem);

                if (Files.isDirectory(path)) {
                    createTree(newItem);
                }
            }
        }
        // catch exceptions, e. g. java.nio.file.AccessDeniedException: c:\System Volume Information, c:\$RECYCLE.BIN
        catch( Exception ex) {
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

            // display text: the last path part
            // consider root, e. g. c:\
            if( path.getNameCount() == 0) {
                this.text = path.toString();
            }
            // consider folder structure
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