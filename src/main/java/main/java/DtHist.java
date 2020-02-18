package main.java;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DtHist {

    public static ArrayList<DataExample> Parse(String input_file) throws IOException {
        ArrayList<DataExample> dataStruct = new ArrayList<>();
        String row;
        BufferedReader csvReader = new BufferedReader(new FileReader(input_file));
        while ((row = csvReader.readLine()) != null) {
            int[] data = Arrays.stream(row.split(",")).mapToInt(Integer::parseInt).toArray();
            int label = data[0];
            int[][] dataReshape = new int[28][28];
            data = Arrays.copyOfRange(data, 1, data.length);
            for (int i = 0; i < 28; i++) {
                System.arraycopy(data, i * 28, dataReshape[i], 0, 28);
            }
            dataStruct.add(new DataExample(label, dataReshape));
        }
        return dataStruct;
    }

    public static double mean(double[] m) {
        double sum = 0;
        for (int i = 0; i < m.length; i++) {
            sum += m[i];
        }
        return sum / m.length;
    }

    public static ArrayList<DataExample> getPercentageOfSample(ArrayList<DataExample> sample, int percentage) {
        int validationSampleSize = (int) ((sample.size() * percentage) / 100);
        Collections.shuffle(sample);
        ArrayList<DataExample> percentageOfSample = new ArrayList<>(sample.subList(0, validationSampleSize));
        return percentageOfSample;
    }

    public static int mostCommon(int[] a) {

        if (a == null || a.length == 0)
            return 0;

        Arrays.sort(a);

        int previous = a[0];
        int popular = a[0];
        int count = 1;
        int maxCount = 1;

        for (int i = 1; i < a.length; i++) {
            if (a[i] == previous)
                count++;
            else {
                if (count > maxCount) {
                    popular = a[i - 1];
                    maxCount = count;
                }
                previous = a[i];
                count = 1;
            }
        }

        return count > maxCount ? a[a.length - 1] : popular;

    }

    public static void toCSV(ArrayList<String[]> data){
        File csvOutputFile = new File("csvOutput");
        try (PrintWriter pw =new PrintWriter(csvOutputFile)){
            data.stream()
                    .map(DtHist::convertToCSV)
                    .forEach(pw::println);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String convertToCSV(String[] dataRow){
        return String.join(",", dataRow);
    }

    public static void TrainSetSizeTestErrorHist(String set1, String set2, String test1, String test2) throws IOException {
        ArrayList<DataExample> parsedSet1 = Parse(set1);
        ArrayList<DataExample> parsedSet2 = Parse(set2);
        ArrayList<DataExample> parsedTest1 = Parse(test1);
        ArrayList<DataExample> parsedTest2 = Parse(test2);
        ArrayList[] sets = new ArrayList[]{parsedSet1, parsedSet2};
        ArrayList[] tests = new ArrayList[]{parsedTest1, parsedTest2};
        String[] names = {"mnist", "kannada"};
        int[] percentages = {1, 10, 25, 50, 75, 100};
        int checks = 3;
        ArrayList<String[]> data = new ArrayList<>();
        String[] headers = {"percentage","set","low","max","avg","tree size"};
        data.add(headers);
        for (int percentage : percentages) {
            System.out.println(String.format("--- In percentage %d", percentage));
            for (int i = 0; i < sets.length; i++) {
                Integer[] allErrorsSet = new Integer[3];
                double[] allErrorSetDouble = new double[3];
                int[] treeSizes = new int[3];
                for (int j = 0; j < checks; j++) {
                    System.out.println(String.format("--- In run %d", j));
                    ArrayList<DataExample> pOfSet = getPercentageOfSample(sets[i], percentage);
                    DecisionTree dt = LearnTree.learnTree(1, 25, 7, pOfSet);
                    treeSizes[j] = dt.getTree_size();
                    int error = dt.apply(tests[i]);
                    System.out.println(String.format("------ Error: %d Tree size: %d", error, treeSizes[j]));
                    allErrorsSet[j] = error;
                    allErrorSetDouble[j] = (double) error;
                }
                int maxError = Collections.max(Arrays.asList(allErrorsSet));
                int minError = Collections.min(Arrays.asList(allErrorsSet));
                double avg = mean(allErrorSetDouble);
                int treeSize = mostCommon(treeSizes);
                String[] dataRow = {Integer.toString(percentage),names[i],Integer.toString(minError),Integer.toString(maxError),Double.toString(avg),Integer.toString(treeSize)};
                data.add(dataRow);
            }
        }
        toCSV(data);
    }

    public static void main(String[] args) throws IOException {
        String trainSet1 = args[0];
        String trainSet2 = args[1];
        String testSet1 = args[2];
        String testSet2 = args[3];
        TrainSetSizeTestErrorHist(trainSet1, trainSet2, testSet1, testSet2);
    }
}
