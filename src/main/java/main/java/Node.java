package main.java;

import java.util.ArrayList;

public interface Node extends Convertable {


    public Node getLeft();

    public void setLeft(Node left);

    public Node getRight();

    public void setRight(Node right);

  //  public Node getParent();

    public double getFailRate();

    public void clear(boolean deep);

    public void applyExamples(ArrayList<DataExample> examples);
}
