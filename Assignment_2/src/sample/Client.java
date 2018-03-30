// Agilan Ampigaipathar (100553054)
package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class Client extends Application {

    // Initialize and declare variables
    private Socket socket;
    private BufferedReader input;
    private PrintStream output;
    serverSource s;
    clientSource c;
    private Stage primaryStage;

    private Button upload;
    private Button download;
    private TableView<TestFile> table;
    private TableView<TestFile> table2;

    public Client() {
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        dir();
        //Call instance of the two observable lists
        s = new serverSource();
        c = new clientSource();

        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Assignment 2");

        BorderPane layout = new BorderPane();

        this.primaryStage = primaryStage;
        this.upload = new Button("Upload");
        this.download = new Button("Download");

        //Set table with observable list from server
        table = new TableView<>();
        table.setItems(s.getFiles());

        TableColumn<TestFile,String> fileName = null;
        fileName = new TableColumn<>("FileName");
        fileName.setMinWidth(100);
        fileName.setCellValueFactory(new PropertyValueFactory<>("file"));

        //Set table with observable list from client
        table2 = new TableView<>();
        table2.setItems(c.getFiles());
        table2.setEditable(true);

        TableColumn<TestFile,String> fileName2 = null;
        fileName2 = new TableColumn<>("FileName");
        fileName2.setMinWidth(100);
        fileName2.setCellValueFactory(new PropertyValueFactory<>("file"));

        table.getColumns().add(fileName);
        table2.getColumns().add(fileName2);

        GridPane bottom = new GridPane();
        bottom.setPadding(new Insets(10));
        bottom.setHgap(5);
        bottom.setVgap(10);

        bottom.add(upload, 1, 0);
        bottom.add(download, 0, 0);

        layout.setTop(bottom);

        TitledPane downloadPane = new TitledPane();
        downloadPane.setText("Client");
        VBox downloadBox = new VBox(downloadPane);
        //Fill download box with table contents
        downloadBox.getChildren().addAll(table2);
        //For filling the size of the vbox and having the columns centered so file names are fully visible
        table2.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table2, Priority.ALWAYS);
        // On download click, go to download function
        download.setOnAction(e -> downloadFunc());

        //Create right-hand titled pane for the books list and centre it in Vbox
        TitledPane uploadPane = new TitledPane();
        uploadPane.setText("Server");
        VBox uploadBox = new VBox(uploadPane);
        uploadBox.getChildren().addAll(table);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);
        upload.setOnAction(e -> uploadFunc());

        // To create the split pane design
        SplitPane mainSplit = new SplitPane();
        mainSplit.getItems().addAll(downloadBox, uploadBox);

        mainSplit.setDividerPosition(1,11/(double)12);
        SplitPane roots = new SplitPane();
        roots.setOrientation(Orientation.VERTICAL);
        roots.getItems().addAll(mainSplit, uploadBox, layout);
        roots.setDividerPosition(0,0.9);
        roots.setPrefWidth(1000);
        roots.setPrefHeight(750);

        Scene scene = new Scene (roots);
        primaryStage.setScene(scene);

        primaryStage.show();

    }

    public void dir() {
        try {

            socket = new Socket("localhost", 9999);
            output = new PrintStream(socket.getOutputStream());
            // Output dir to the server
            output.println("DIR");

            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while(true) {
                String message = input.readLine();
                System.out.println(message);
                s.setFiles(message);
                if(message == null){
                    break;
                }
            }

            File folder = new File("clientFiles");
            File[] list = folder.listFiles();
            for (int i =0; i< list.length; i++) {
                c.setFiles(list[i].getName());
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void downloadFunc() {
        try {
            // Get filename from the table cell
            String fileName = table.getSelectionModel().getSelectedItem().getFile();
            System.out.println(fileName);

            socket = new Socket("localhost", 9999);
            output = new PrintStream(socket.getOutputStream());
            output.println("DOWNLOAD");
            output.println(fileName);

            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            BufferedWriter writer = null;
            writer = new BufferedWriter(new FileWriter("clientFiles/"+fileName));
            String message;
            while((message = input.readLine()) != null) {
                writer.append(message);
            }
            writer.close();
            c.setFiles(fileName);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void uploadFunc() {
        try {

            String fileName = table2.getSelectionModel().getSelectedItem().getFile();
            System.out.println(fileName);

            socket = new Socket("localhost", 9999);
            output = new PrintStream(socket.getOutputStream());
            output.println("UPLOAD");
            output.println(fileName);

            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            BufferedReader br = new BufferedReader(new FileReader("clientFiles/"+fileName));
            String line;
            while ((line = br.readLine()) != null) {
                // process the line.
                output.println(line);
            }
            br.close();
            s.setFiles(fileName);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main (String[] args) {
        launch(args);
    }

}
