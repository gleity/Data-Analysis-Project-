package main.java;


import java.util.ArrayList;

public class MinimalDecisonTree {
    private  Node root;

    public MinimalDecisonTree(Node node){
        this.root = node;
    }

    public int apply_singly(ArrayList<DataExample> examples){
        for (DataExample example: examples) {
            ArrayList<DataExample> wrapExample = new ArrayList<>();
            wrapExample.add(example);
            this.root.applyExamples(wrapExample);
            System.out.println(example.getPredictedLabel());
        }
        double failRate = this.root.getFailRate() / examples.size();
        return (int)(failRate * 100);
    }
}
