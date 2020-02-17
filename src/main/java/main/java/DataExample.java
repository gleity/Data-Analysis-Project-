package main.java;

public class DataExample {
    private int label;
    private int[][] data;

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
}
