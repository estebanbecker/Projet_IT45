package Benchmark;

import java.io.File;
import java.io.FileWriter;

public class Benchmark {
    public static void main() throws Exception {
        // get parameters from App.java
        String folder = Problem.App.folder;
        int[] parameter2Variations = Problem.App.parameter2Variations;
        float parameter3Variations = Problem.App.parameter3Variations;
        float[] parameter4Variations = Problem.App.parameter4Variations;
        float parameter5 = Problem.App.parameter5;
        float elapsedTimeSec = Problem.App.elapsedTimeSec;

        // write those to a csv file if it doesn't exist
        String csvFile = folder + "benchmark.csv";
        File file = new File(csvFile);
        if (!file.exists()) {
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write(folder + "," + parameter2Variations[0] + "," + parameter3Variations + "," + parameter4Variations[0]
                + "," + parameter5 + "," + elapsedTimeSec + "," + "\n");
            writer.close();
        }
        // if it does exist, append to it
        FileWriter writer = new FileWriter(file, true);
        // write the parameters
        writer.write(folder + "," + parameter2Variations[0] + "," + parameter3Variations + "," + parameter4Variations[0]
                + "," + parameter5 + "," + elapsedTimeSec + "," + "\n");
        writer.close();
    }
}
