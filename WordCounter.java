// Agilan Ampigaipathar (100553054)

package sample;

import java.io.*;
import java.util.*;

import static sample.Main.testHam;
import static sample.Main.testSpam;


public class WordCounter {
    private Map<String,Integer> wordCounts;

    public WordCounter() {
        wordCounts = new TreeMap<>();
    }

    // Calculate accuracy, not fully functional
    public double calculateAccuracy(){

        // Traverse maps containing test ham and calculate the amount of correct guesses
        int correct = 0;
        Set<String> keys = testHam.keySet();
        Iterator<String> keyIterator = keys.iterator();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next();
            double count = testHam.get(key);
            if(count < 0.5){
                correct++;
            }
        }

        // Traverse maps containing test spam and calculate the amount of correct guesses
        keys = testSpam.keySet();
        keyIterator = keys.iterator();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next();
            double count = testSpam.get(key);
            if(count >= 0.5){
                correct++;
            }
        }

        double accuracy = (double)correct/(testHam.size()+testSpam.size());
        System.out.println(accuracy);
        return accuracy;
    }

    // Calculate precision, not fully functional
    public double calculatePrecision() {
        double precision;
        int trueposition = 0;
        int falseposition = 0;

        Set<String> keys = testHam.keySet();
        Iterator<String> keyIterator = keys.iterator();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next();
            double count = testHam.get(key);
            if (count >= 0.5) {
                falseposition++;
            }
        }

        keys = testSpam.keySet();
        keyIterator = keys.iterator();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next();
            double count = testSpam.get(key);
            if (count >= 0.5) {
                trueposition++;
            }
        }
        precision = (double) trueposition / (falseposition + trueposition);
        System.out.println(precision);
        return precision;
    }

    private void countWord(String word) {
        if (wordCounts.containsKey(word)) {
            int oldCount = wordCounts.get(word);
            wordCounts.put(word, oldCount+1);
        } else {
            wordCounts.put(word, 1);
        }
    }

    public void processFile(File file) throws IOException {
        System.out.println("Processing " + file.getAbsolutePath() + "...");
        // Check if file is a directory
        if (file.isDirectory()) {
            // process all the files in that directory
            File[] contents = file.listFiles();
            for (File current: contents) {
                processFile(current);
            }
            // Check if the file exists
        } else if (file.exists()) {
            // Count the words in the file
            Scanner scanner = new Scanner(file);
            scanner.useDelimiter("\\s");//"[\s\.;:\?\!,]");//" \t\n.;,!?-/\\");
            while (scanner.hasNext()) {
                String word = scanner.next();
                if (isWord(word)) {
                    countWord(word);
                }
            }
        }
    }

    private static boolean isWord(String word) {
        // Regex to make sure the string is a word
        String pattern = "^[a-zA-Z]+$";
        if (word.matches(pattern)) {
            return true;
        } else {
            return false;
        }

        // also fine:
        //return word.matches(pattern);
    }




    public void outputWordCounts(int minCount, File outFile)
            throws IOException {
        System.out.println("Saving word counts to " + outFile.getAbsolutePath());
        System.out.println("# of words: " + wordCounts.keySet().size());
        if (!outFile.exists()) {
            outFile.createNewFile();
            if (outFile.canWrite()) {
                PrintWriter fileOut = new PrintWriter(outFile);

                Set<String> keys = wordCounts.keySet();
                Iterator<String> keyIterator = keys.iterator();

                while (keyIterator.hasNext()) {
                    String key = keyIterator.next();
                    int count = wordCounts.get(key);

                    if (count >= minCount) {
                        fileOut.println(key + ": " + count);
                    }
                }

                fileOut.close();
            } else {
                System.err.println("Error:  Cannot write to file: " + outFile.getAbsolutePath());
            }
        } else {
            System.err.println("Error:  File already exists: " + outFile.getAbsolutePath());
            System.out.println("outFile.exists(): " + outFile.exists());
            System.out.println("outFile.canWrite(): " + outFile.canWrite());
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java WordCounter <dir> <outfile>");
            System.exit(0);
        }

        WordCounter wordCounter = new WordCounter();
        File dataDir = new File(args[0]);
        File outFile = new File(args[1]);

        try {
            wordCounter.processFile(dataDir);
            wordCounter.outputWordCounts(2, outFile);
        } catch (FileNotFoundException e) {
            System.err.println("Invalid input dir: " + dataDir.getAbsolutePath());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}