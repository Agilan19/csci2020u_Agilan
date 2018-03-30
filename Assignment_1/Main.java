// Agilan Ampigaipathar (100553054)

// Training works, testing almost works, having trouble displaying to a screen and so the lab 5 code used for displaying
// has been commented out

package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;


import java.io.*;
import java.util.*;

public class Main extends Application {


    // Declare the maps to be used
    static Map<String, Integer> ham = new HashMap<>();
    static Map<String, Integer> spam = new HashMap<>();

    static Map<String, Integer> testHam = new HashMap<>();
    static Map<String, Integer> testSpam = new HashMap<>();

    static Map<String, Double> probabilitySpam = new HashMap<>();
    static Map<String, Double> probabilityinSpam = new HashMap<>();
    static Map<String, Double> probabilityinHam = new HashMap<>();

    static int spamFiles = 0;
    static int hamFiles = 0;

    public static void main(String[] args) { Application.launch(args); }


    @Override public void start(Stage primaryStage) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("sample.fxml"));

        primaryStage.setScene(new Scene(parent, 400, 500));
        primaryStage.setTitle("Assignment 1");

        BorderPane layout = new BorderPane();

        //Create the table of TestFile records
        TableColumn<TestFile, String> fileCol = new TableColumn<>("File");
        fileCol.setPrefWidth(100);
        fileCol.setCellValueFactory(new PropertyValueFactory<>("file"));

        TableColumn<TestFile, Float> classCol = new TableColumn<>("Actual Class");
        classCol.setPrefWidth(100);
        classCol.setCellValueFactory(new PropertyValueFactory<>("actual class"));

        TableColumn<TestFile, String> spamProbCol = new TableColumn<>("Spam Probability");
        spamProbCol.setPrefWidth(100);
        spamProbCol.setCellValueFactory(new PropertyValueFactory<>("spam probability "));

//        this.students = new TableView<>();
//        this.students.getColumns().add(fileCol);
//        this.students.getColumns().add(classCol);
//        this.students.getColumns().add(spamProbCol);
//
//        //Form at the bottom
//        this.filename = new TextField();
//        this.filename.setPromptText("File");
//
//        this.actualClass = new TextField();
//        this.actualClass.setPromptText("Actual Class");
//
//        this.spamProbability = new TextField();
//        this.spamProbability.setPromptText("Spam Probability");
//
//        this.addResult = new Label("");
//
//        layout.setCenter(item);
//        layout.setBottom(bottom);

        //Create the form layout
        GridPane bottom = new GridPane();
        bottom.setPadding(new Insets(10));
        bottom.setHgap(10);
        bottom.setVgap(10);

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("."));
        File mainDirectory = directoryChooser.showDialog(primaryStage);

        File train = new File(mainDirectory + "/train");
        File test = new File(mainDirectory + "/test");

        reader(train);

        testReader(test);

        WordCounter word = new WordCounter();
        word.processFile(train);
//        word.probability();
        word.processFile(test);

        System.out.println(word.calculateAccuracy());
        System.out.println(word.calculatePrecision());


//        controller.setAccuracy_field(Double.toString(word.calculateAccuracy()));
//        controller.setPrecision_field(Double.toString(word.calculatePrecision()));



        primaryStage.show();



    }

//    public boolean addItem() {
//        //Check if all fields have values
//        String _filename = filename.getText();
//        double _actualClass = Float.parseFloat(actualClass.getText().toString());
//        String _spamProbability = Float.parseFloat(spamProbability.getText().toString());
//
//
//        //Add the student
//        this.students.getItems().add(new TestFile(_filename, _actualClass, _spamProbability));
//
//        //Clear the fields
//        this.filename.setText("");
//        this.actualClass.setText("");
//        this.spamProbability.setText("");
//
//
//        return true;
//
//    }


    public static void probability() {

        Set<String> stringSet = ham.keySet();
        Iterator<String> stringIterator = stringSet.iterator();
        while (stringIterator.hasNext()) {
            String key = stringIterator.next();
            int count = ham.get(key);
            probabilityinHam.put(key, (double) (count/hamFiles));
        }

        stringSet = spam.keySet();
        stringIterator = stringSet.iterator();
        while (stringIterator.hasNext()) {
            String key = stringIterator.next();
            int count = spam.get(key);
            probabilityinSpam.put(key, (double) (count/spamFiles));
        }

        stringSet = probabilityinSpam.keySet();
        stringIterator = stringSet.iterator();
        while (stringIterator.hasNext()) {
            String key = stringIterator.next();
            if (probabilityinHam.containsKey(key)) {
                probabilitySpam.put(key, probabilityinSpam.get(key)/(probabilityinSpam.get(key) + probabilityinHam.get(key)));
            }
            else {
                probabilitySpam.put(key, 1.0);
            }
        }
    }

    // Reader for train
    public static void reader(File file) throws FileNotFoundException {
        Map<String, Integer> hamTemp = new HashMap<>();
        Map<String, Integer> spamTemp = new HashMap<>();


        if (file.isDirectory()) {
            File[] fileArray = file.listFiles();
            for (int i = 0; i < fileArray.length; i++) {
                reader(fileArray[i]);
            }
        }
        else if (file.exists()) {
            // count the words in this file
            Scanner scanner = new Scanner(file);
//            scanner.useDelimiter("\\s");//"[\s\.;:\?\!,]");//" \t\n.;,!?-/\\");
            while (scanner.hasNext()) {
                String word = scanner.next().toLowerCase();
                if (isWord(word)) {
                    if (file.getAbsolutePath().contains("/train/ham")) {
                        countWord(word, hamTemp);
                        hamFiles++;
                    }
                    else if (file.getAbsolutePath().contains("/train/spam")) {
                        countWord(word, spamTemp);
                        spamFiles++;
                    }
                }
            }

            Set<String> stringSet = hamTemp.keySet();
            Iterator<String> stringIterator = stringSet.iterator();
            while (stringIterator.hasNext()) {
                String key = stringIterator.next();
                if (ham.containsKey(key)) {
                    int oldCount = ham.get(key);
                    ham.put(key, oldCount+1);
                } else {
                    ham.put(key, 1);
                }
            }

            stringSet = spamTemp.keySet();
            stringIterator = stringSet.iterator();
            while (stringIterator.hasNext()) {
                String key = stringIterator.next();
                if (spam.containsKey(key)) {
                    int oldCount = spam.get(key);
                    spam.put(key, oldCount+1);
                } else {
                    spam.put(key, 1);
                }
            }

        }
    }

    private static void countWord(String word, Map<String, Integer> map) {
        if (!map.containsKey(word)) {
            map.put(word, 1);
        }
    }

    private static boolean isWord(String word) {
        String pattern = "^[a-zA-Z]+$";
        if (word.matches(pattern)) {
            return true;
        } else {
            return false;
        }

        // also fine:
        //return word.matches(pattern);
    }

    // Reader for test
    public static void testReader(File file) throws FileNotFoundException {
        Map<String, Integer> hamTemp = new HashMap<>();
        Map<String, Integer> spamTemp = new HashMap<>();

        double probabilitySpamWord = 0;

        if (file.isDirectory()) {
            File[] fileArray = file.listFiles();
            for (int i = 0; i < fileArray.length; i++) {
                testReader(fileArray[i]);
            }
        }
        else if (file.exists()) {
            // count the words in this file
            Scanner scanner = new Scanner(file);
//            scanner.useDelimiter("\\s");//"[\s\.;:\?\!,]");//" \t\n.;,!?-/\\");
            while (scanner.hasNext()) {
                String word = scanner.next().toLowerCase();
                if (isWord(word) && probabilitySpam.containsKey(word)) {
                    probabilitySpamWord += probabilitySpam.get(word);
                }
            }
            double fileIsSpam = 1/(1+(Math.pow(Math.E,probabilitySpamWord)));

            if (file.getAbsolutePath().contains("/train/ham")) {
                testHam.put(file.getName(), (int) fileIsSpam);
                EmailSource.setEmail(file.getName(), fileIsSpam, "Ham");
            }
            else if (file.getAbsolutePath().contains("/train/spam")) {
                testSpam.put(file.getName(), (int) fileIsSpam);
                EmailSource.setEmail(file.getName(), fileIsSpam, "Spam");
            }
        }
    }
}
