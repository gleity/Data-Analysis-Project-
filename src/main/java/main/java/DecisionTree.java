
package main.java;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class DecisionTree implements Serializable {
    private  Node root;
    private transient int tree_size;
    private transient int cur_tree_size;
    private transient double failRate;
    private transient int set_number;
    private  transient ArrayList<LabelNode> leaves;
    private  transient Condition condition;
    private transient ArrayList<DataExample> examples;

    public DecisionTree(ArrayList<DataExample> examples, int tree_size, Condition condition) {
        this.examples = examples;
        this.root = new LabelNode();
        this.tree_size = tree_size;
        this.failRate = 0;
        this.cur_tree_size = 1;
        this.leaves = new ArrayList<>();
        leaves.add((LabelNode) root);
        this.condition = condition;
        if (this.examples != null && this.root instanceof LabelNode) {
            this.root.applyExamples(this.examples);
        }
    }

    public void buildTree() {
        while (this.cur_tree_size < this.tree_size) {
            double max_H = 0;
            ArrayList<int[]> max_condition = new ArrayList<>();
            LabelNode replacedLeaf = null;
            for (LabelNode l : leaves) {
                ArrayList<DataExample> lExamples = l.getExamples();
                int lExamplesSize = l.getExamples().size();
                ArrayList<ArrayList<int[]>> conditions = condition.getConditions();
                for (ArrayList<int[]> cond: conditions) {
                        double IG_x = l.getLeaf_entropy() - new_leaf_entropy(condition, lExamples, cond);
                        double totalIG = lExamplesSize * IG_x;
                        if (totalIG > max_H) {
                            max_H = totalIG;
                            max_condition = cond;
                            replacedLeaf = l;
                        }
                }
            }
            if(replacedLeaf != null){
                ConditionNode newConditionNode = appliedCondition(max_condition, condition, replacedLeaf.getExamples());
                insertCondition(replacedLeaf, newConditionNode);
                cur_tree_size++;
            }
            if (replacedLeaf == null){
                System.out.println("breaked at "+ tree_size);
                break;
            }
        }
        this.failRate = this.root.getFailRate() / (double)this.examples.size();
    }

    public ConditionNode appliedCondition (ArrayList<int[]> maxCondition,Condition condition, ArrayList<DataExample> examples){
        ConditionNode conditionNode = new ConditionNode(condition, maxCondition);
        conditionNode.applyExamples(examples);
        return conditionNode;
    }

    public int getTree_size() {
        return tree_size;
    }

    public double getFailRate() {

        return failRate;
    }

    public void insertCondition(LabelNode replacedLeaf, ConditionNode maxCondition){
        if(this.root == replacedLeaf){
            root = maxCondition;
        }
        else{
            Node c_parent = replacedLeaf.getParent();
            if(c_parent.getRight() == replacedLeaf){
                c_parent.setRight(maxCondition);
            }
            else{
                c_parent.setLeft(maxCondition);
            }

        }
        this.leaves.remove(replacedLeaf);
        this.leaves.add((LabelNode)maxCondition.getLeft());
        this.leaves.add((LabelNode)maxCondition.getRight());
    }

    public double new_leaf_entropy(Condition condition, ArrayList<DataExample> examples,ArrayList<int[]> cond) {
        int[] label_count_fail = new int[10];
        int[] label_count_pass = new int[10];
        int numOfExamples = examples.size();
        for (DataExample example : examples) {
            if (condition.get_lambda(cond, example)) {
                label_count_pass[example.getLabel()]++;
            } else {
                label_count_fail[example.getLabel()]++;
            }
        }

        int passLabelNum = Arrays.stream(label_count_pass).sum();
        int failLabelNum =  Arrays.stream(label_count_fail).sum();

        double H_l = ((double)passLabelNum /(double)numOfExamples) * computeLeavesEntropy(label_count_pass, numOfExamples);
        double H_r =  ((double)failLabelNum /(double)numOfExamples) * computeLeavesEntropy(label_count_fail, numOfExamples);
        double H_x = H_l + H_r;
        return H_x;
    }

    public Node getRoot() {
        return root;
    }

    public double computeLeavesEntropy (int[] labelCount, int exampleNum){
        double leafEntropy = 0;
        for (int label: labelCount) {
            if (label > 0){
                leafEntropy += ((double)label / (double)exampleNum) * (Math.log10((double)exampleNum) - Math.log10((double)label));
            }
        }
        return leafEntropy;
    }

    public void setTree_size(int tree_size) {
        this.tree_size = tree_size;
    }

    public int apply(ArrayList<DataExample> validationSet){
        clear();
        this.root.applyExamples(validationSet);
        double failRate = this.root.getFailRate()/validationSet.size();
        return (int)(failRate*100);
    }
    public void clear(){
        this.root.clear(false);
    }
}

