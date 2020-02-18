package main.java;


import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class LearnTree {

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

    public static DecisionTree learnTree(int condition_set, int percentage, int level, ArrayList<DataExample> data) {
        int data_size = data.size();
        int train_set_size = (int) (data_size * (1 - (double) percentage / 100));
        //int validation_set_size = data_size - train_set_size;
        ArrayList<DataExample> train_set = new ArrayList<>(data.subList(0, train_set_size));
        ArrayList<DataExample> test_set = new ArrayList<>(data.subList(train_set_size, data_size));
        int best_size = -1;
        double fail_rate_min = 1.01;
        Condition conditions = new Condition(condition_set);
        DecisionTree dt = new DecisionTree(train_set, 0, conditions);
        for (int i = 0; i <= level; i++) {
            int treeSize = (int) Math.pow(2, i);
            dt.setTree_size(treeSize);
            dt.buildTree();
            int failRateValidation = dt.apply(test_set);
            if (dt.getFailRate() < fail_rate_min) {
                fail_rate_min = dt.getFailRate();
                best_size = dt.getTree_size();
            }
        }
        DecisionTree bestDT = new DecisionTree(data, best_size, conditions);
        bestDT.buildTree();
        System.out.println(String.format("num: %d \nerror: %d \nsize: %d",data_size,(int)(bestDT.getFailRate()*100), best_size));
        return bestDT;
    }

    public static void writeToFile(String s, String outputFile ) throws IOException {
        File f = new File(outputFile);
        f.createNewFile();
        FileWriter fileWriter = new FileWriter(f);
        fileWriter.write(s);
        fileWriter.close();
    }

    public static void main(String[] args) throws IOException {
        int condition_set = Integer.parseInt(args[0]);
        int percentage = Integer.parseInt(args[1]);
        int tree_size = Integer.parseInt(args[2]);
        String input_file = args[3];
        String output_file = args[4];

        ArrayList<DataExample> data = Parse(input_file);
        DecisionTree dt =learnTree(condition_set, percentage, tree_size, data);
        long endTime   = System.nanoTime();
        Gson gson =new Gson();
        String s = gson.toJson(dt,DecisionTree.class);
        writeToFile(s,output_file);
    }

}
