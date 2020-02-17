package main.java;

import java.util.ArrayList;

public class MinimalConditionNode {
    private ArrayList<int[]> maxCondition;
    private  Node left;
    private  Node right;

    public MinimalConditionNode(ArrayList<int[]> maxCondition, Node left, Node right){
        this.maxCondition = maxCondition;
        this.left = left;
        this.right = right;
    }
}
