package main.java;

public class DataExample {
    private int label;
    private int[][] data;
    private int predictedLabel;

    public DataExample(int label, int[][] data){
        this.label = label;
        this.data = data;
    }

    public int getLabel() {
        return label;
    }

    public int[][] getData() {
        return data;
    }
    public int getValue(int x, int y){
        return data[x][y];
    }

    public int getPredictedLabel() {
        return predictedLabel;
    }

    public void setPredictedLabel(int predictedLabel) {
        this.predictedLabel = predictedLabel;
    }
}
