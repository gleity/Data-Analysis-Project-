package main.java;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ConditionNode implements Node {
    private final String className;
    private   ArrayList<int[]> maxCondition;
    private  Node left;
    private  Node right;
    private transient Node parent;
    private  Condition condition;

    public ConditionNode(Condition condition, ArrayList<int[]> maxCondition) {
        this.className = getClass().getName();
        this.left = null;
        this.right = null;
        this.condition = condition;
        //this.parent = parent;
        this.maxCondition = maxCondition;
    }

    @Override
    public String getClassName() {
        return className;
    }

    public void applyExamples(ArrayList<DataExample> examples) {
        ArrayList<DataExample> passedExamples = (examples.stream()
                .filter(e -> this.condition.CommitLambda(maxCondition, e))).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<DataExample> failedExamples = (examples.stream()
                .filter(e -> !this.condition.CommitLambda(maxCondition, e))).collect(Collectors.toCollection(ArrayList::new));
        if (this.left == null) {
            this.left = new LabelNode();
            ((LabelNode) this.left).setParent(this);
        }
        if (this.right == null) {
            this.right = new LabelNode();
            ((LabelNode) this.right).setParent(this);
        }
        this.left.applyExamples(passedExamples);
        this.right.applyExamples(failedExamples);
    }

    public double getFailRate(){
        return (this.left).getFailRate() + (this.right).getFailRate();
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }


    public void setRight(Node node) {
        this.right = node;
    }

    public void setLeft(Node node) {
        this.left = node;
    }

    public Node getLeft() {
        return this.left;
    }

    public Node getRight() {
        return this.right;
    }

    //public Node getParent() {
       // return this.parent;
  //  }

    @Override
    public void clear(boolean deep) {
        this.left.clear(deep);
        this.right.clear(deep);
        if (deep){
            if (this.left instanceof LabelNode){
                ((LabelNode) this.left).setLeaf_label(null);
                ((LabelNode) this.left).setExamples(null);
            }
            if (this.right instanceof LabelNode){
                ((LabelNode) this.right).setLeaf_label(null);
                ((LabelNode) this.right).setExamples(null);
            }
        }
    }
}
