// Agilan Ampigaipathar (100553054)

package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class EmailSource {
    public static ObservableList<TestFile> emailContent = FXCollections.observableArrayList();

        public static void setEmail(String filename, double spamProbability, String actualClass) {
            emailContent.add(new TestFile(filename, spamProbability, actualClass));
        }

        public static ObservableList getEmails(){return emailContent;}
}

