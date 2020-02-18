package main.java;

import java.util.ArrayList;
import java.util.Random;

public class Condition {
    private int set;
    private ArrayList<ArrayList<int[]>> conditions;
    private final int pixelRange = 28;

    public Condition(int set) {
        this.set = set;
        if (set == 1) {
            this.conditions = buildSet1Conds();

        }
        if (set == 2) {
            this.conditions = buildSet2Conds();
        }
    }

    private ArrayList<ArrayList<int[]>> buildSet1Conds() {
        ArrayList<ArrayList<int[]>> conds = new ArrayList<>();
        for (int x = 0; x < 28; x++) {
            for (int y = 0; y < 28; y++) {
                ArrayList<int[]> cond = new ArrayList<>();
                cond.add(new int[]{x, y});
                conds.add(cond);
            }
        }
        return conds;
    }

    private ArrayList<ArrayList<int[]>> buildSet2Conds() {
        int numOfPixels = pixelRange * pixelRange;
        int length = (numOfPixels / 4);
        ArrayList<ArrayList<int[]>> coordinatesList = new ArrayList<>();
        coordinatesList.addAll(getRandomCoordinatesLists(length, true));
        coordinatesList.addAll(getRandomCoordinatesLists(length, false));
        coordinatesList.addAll(getRandomEqCoordinatesLists(length, true));
        coordinatesList.addAll(getRandomEqCoordinatesLists(length, false));
        return coordinatesList;
    }

    public ArrayList<ArrayList<int[]>> getConditions() {
        return conditions;
    }

    public boolean CommitLambda(ArrayList<int[]> cond, DataExample example) {
        if (set == 1) {
            int[] temp = cond.get(0);
            return example.getValue(temp[0], temp[1]) > 128;
        } else {
            return avgMatrixVal(example, cond) > 100 ;
        }
    }

    public double avgMatrixVal(DataExample example, ArrayList<int[]> coordList) {
        int values_sum = 0;
        for (int[] coord : coordList) {
            values_sum += example.getValue(coord[0], coord[1]);
        }
        return (double) values_sum / (double) coordList.size();
    }

    public ArrayList<ArrayList<int[]>> getRandomEqCoordinatesLists(int length, boolean linear) {
        ArrayList<ArrayList<int[]>> coordinates = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            coordinates.add(getEqCoorList(linear));
        }
        return coordinates;
    }

    public int getSet() {
        return set;
    }

    public ArrayList<int[]> getEqCoorList(boolean linear) {
        ArrayList<int[]> s = new ArrayList<>();
        int rangeLength = getRandRange();
        int startIndex = new Random().nextInt(pixelRange - rangeLength);
        double[] y;
        if (linear) {
            y = getRandomLinearEq();
        } else {
            y = getRandomParaboleEq();
        }
        apply_x(linear, s, rangeLength, startIndex, y);
        while (s.size() == 0) {
            if (linear) {
                y = getRandomLinearEq();
            } else {
                y = getRandomParaboleEq();
            }
            apply_x(linear, s, rangeLength, startIndex, y);
        }
        return s;
    }

    private void apply_x(boolean linear, ArrayList<int[]> s, int rangeLength, int startIndex, double[] y) {
        double result;
        for (int x = startIndex; x < startIndex + rangeLength; x++) {
            if (linear) {
                result = linearEq(y[0], y[1], x);
            } else {
                result = parabolaEq(y[0], y[1], y[2], x);
            }
            if ((int) result < 28 && (int) result >= 0) {
                s.add(new int[]{x,(int)result});
            }
        }
    }


    public double[] getRandomLinearEq() {
        int[] pointA = getRandCoordinate(0, pixelRange);
        int[] pointB = getRandCoordinate(pixelRange - 1, pixelRange);
        double a = pointB[1] - pointA[1];
        double b = pointA[0] - pointB[0];
        while (a == b || a == 0 || b == 0) {
            pointA = getRandCoordinate(0, pixelRange);
            pointB = getRandCoordinate(pixelRange - 1, pixelRange);
            a = pointB[1] - pointA[1];
            b = pointA[0] - pointB[0];
        }
        double m = -a / b;
        double c = a * (pointA[0]) + b * (pointA[1]) / b;
        double[] linearEq = {m, c};
        return linearEq;
    }

    public double[] getRandomParaboleEq() {
        int[][] coefs = {{1, 1}, {1, -1}, {-1, 1}};
        Random rnd = new Random();
        int[] coefficient = coefs[rnd.nextInt(coefs.length)];
        double a = coefficient[0] * (double) (rnd.nextInt(35) + 1) / 10;
        double b = rnd.nextInt(pixelRange);
        double c = coefficient[1] * rnd.nextInt(pixelRange);
        return new double[]{a, b, c};
    }

    public int[] getRandCoordinate(int from, int to) {
        int[] a = new int[2];
        Random rnd = new Random();
        if (rnd.nextBoolean()) {
            a[0] = from;
            a[1] = rnd.nextInt(to);
        } else {
            a[0] = rnd.nextInt(to);
            a[1] = from;
        }
        return a;
    }

    public double linearEq(double m, double b, int x) {
        return m * x + b;
    }

    public double parabolaEq(double a, double b, double c, int x) {
        return a * Math.pow((x - b), 2) + c;
    }

    public ArrayList<ArrayList<int[]>> getRandomCoordinatesLists(int length, boolean isVertical) {
        ArrayList<ArrayList<int[]>> coordinates = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            int rangeLength = getRandRange();
            int startIndex = new Random().nextInt(pixelRange - rangeLength);
            int axis = new Random().nextInt(pixelRange);
            ArrayList<int[]> record = new ArrayList<>();
            for (int j = startIndex; j < startIndex + rangeLength; j++) {
                int[] coordinate = new int[2];
                if (isVertical) {
                    coordinate[0] = axis;
                    coordinate[1] = j;
                } else {
                    coordinate[0] = j;
                    coordinate[1] = axis;
                }
                record.add(coordinate);
            }
            coordinates.add(record);
        }
        return coordinates;
    }

    public int getRandRange() {
        Random gen = new Random();
        int[] ranges = {2, 3, 4, 5, 6};
        float p = gen.nextFloat();
        if (p <= 0.5) {
            return ranges[0];
        }
        if (p <= 0.8) {
            return ranges[1];
        }
        if (p <= 0.9) {
            return ranges[2];
        }
        if (p <= 0.975) {
            return ranges[3];
        } else {
            return ranges[4];
        }

    }
}
