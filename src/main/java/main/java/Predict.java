package main.java;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Predict {

    public static void predict(String treeFile, String testFile) throws IOException {
        MinimalDecisonTree decisionTree = parseTreeFromFile(treeFile);
        ArrayList<DataExample> dataExamples = Parse(testFile);
        double failRate = decisionTree.apply_singly(dataExamples);
        System.out.println(failRate);
    }


    public static ArrayList<DataExample> Parse(String input_file) throws IOException {
        ArrayList<DataExample> dataStruct = new ArrayList<>();
        String dir = System.getProperty("user.dir");
        dir = dir + "\\src\\main\\" + input_file;
        File csvFile = new File(dir);
        if (csvFile.isFile()) {
            String row;
            BufferedReader csvReader = new BufferedReader(new FileReader(dir));
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
        }
        return dataStruct;
    }

    public static MinimalDecisonTree parseTreeFromFile(String treeFile) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        File f = new File(treeFile);
        f.setReadable(true);
        BufferedReader reader = new BufferedReader(new FileReader(treeFile));
        String currentLine;
        while((currentLine = reader.readLine()) != null){
            contentBuilder.append(currentLine);
        }
        String jsonInput = contentBuilder.toString();
        RuntimeTypeAdapterFactory<Node> typeAdapterFactory =RuntimeTypeAdapterFactory.of(Node.class,"className")
                                                                                 .registerSubtype(ConditionNode.class, "main.java.ConditionNode")
                                                                                .registerSubtype(LabelNode.class, "main.java.LabelNode");
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(typeAdapterFactory).create();
        return gson.fromJson(jsonInput,MinimalDecisonTree.class);
    }
    public static void main(String[] args) throws IOException {
        String treeFile = args[0];
        String testFile = args[1];
        String dir = System.getProperty("user.dir");
        dir = dir + "\\"  + treeFile;
        predict(dir, testFile);
    }
}
