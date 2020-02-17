package main.java;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class LabelNode implements Node,Serializable {
    private final String className;
    private  Node left;
    private  Node right;
    private transient Node parent;
    private  Integer leaf_label;
    private transient Integer[] label_count;
    private transient double leaf_entropy;
    private transient ArrayList<DataExample> examples;

    public LabelNode() {
        this.className = getClass().getName();
        this.parent = null;
        this.left = null;
        this.right = null;
        this.label_count = new Integer[10];
        for (int i = 0; i < 10; i++) {
            label_count[i] = 0;
        }
        this.leaf_entropy = 0;
        this.examples = null;
        this.leaf_label = null;

    }

    @Override
    public String getClassName() {
        return className;
    }

    public void applyExamples(ArrayList<DataExample> examples) {
        for (DataExample example : examples) {
            label_count[example.getLabel()]++;
        }
        if (this.examples == null) {
            this.examples = examples;
        }
        if (this.leaf_label == null) {
            update_label();
            this.leaf_entropy = compute_leaf_entropy(this.examples.size());
        }
    }

    public ArrayList<DataExample> getExamples() {
        return examples;
    }

    public void update_label() {
        int maxed_value = Collections.max(Arrays.asList(this.label_count));
        for (int i = 0; i < label_count.length; i++) {
            if (maxed_value == label_count[i])
                this.leaf_label = i ;
        }
    }

    public int getLeaf_label() {
        return leaf_label;
    }

    public void setLeaf_label(int leaf_label) {
        this.leaf_label = leaf_label;
    }

    public Integer[] getLabel_count() {
        return label_count;
    }


    public double getLeaf_entropy() {
        return leaf_entropy;
    }

    public void setLeaf_entropy(int leaf_entropy) {
        this.leaf_entropy = leaf_entropy;
    }

    public double compute_leaf_entropy(int examplesLen) {
        double leaf_entropy = 0;
        for (int label : label_count) {
            if (label > 0) {
                leaf_entropy += ((double)label / (double)examplesLen) * (Math.log10((double) examplesLen) - Math.log10((double) label));
            }
        }
        return leaf_entropy;
    }

    public double getFailRate() {
        double failRate = 0;
        for (int i = 0; i < this.label_count.length; i++) {
            if (i != this.leaf_label) {
                failRate += label_count[i];
            }
        }
        return failRate;
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

    public Node getParent() {
        return this.parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public void setLeaf_label(Integer leaf_label) {
        this.leaf_label = leaf_label;
    }

    public void setExamples(ArrayList<DataExample> examples) {
        this.examples = examples;
    }

    @Override
    public void clear(boolean deep) {
        Arrays.fill(this.label_count,0);
    }
}
