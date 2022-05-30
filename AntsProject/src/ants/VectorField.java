package ants;

import java.util.ArrayList;
import java.util.List;

public class VectorField {
    private List<List<List<Double>>> vectorField;
    private int width;
    private int height;

    public VectorField(int screenWidth, int screenHeight) {
        width = screenWidth;
        height = screenHeight;
        vectorField = new ArrayList<>();
        for(int i = 0; i < screenWidth; i++) {
            vectorField.add(new ArrayList<>());
            for(int j = 0; j < screenHeight; j++) {
                vectorField.get(i).add(new ArrayList<>());
                vectorField.get(i).get(j).add(0.0);
                vectorField.get(i).get(j).add(0.0);
            }
        }
    }

    public List<Double> getVector(int i, int j) {
        if((0 > i || i >= width) || (0 > j || j >= height)) return List.of(0.0, 0.0);
        return vectorField.get(i).get(j);
    }

    public double getXComp(int i, int j) {
        if((0 > i || i >= width) || (0 > j || j >= height)) return 0.0;
        return vectorField.get(i).get(j).get(0);
    }

    public double getYComp(int i, int j) {
        if((0 > i || i >= width) || (0 > j || j >= height)) return 0.0;
        return vectorField.get(i).get(j).get(1);
    }

    public void setXComp(int i, int j, double value) {
        if((0 > i || i >= width) || (0 > j || j >= height)) return;
        vectorField.get(i).get(j).set(0, value);
    }

    public void setYComp(int i, int j, double value) {
        if((0 > i || i >= width) || (0 > j || j >= height)) return;
        vectorField.get(i).get(j).set(1, value);
    }

    public void normalizeXY(int i, int j) {
        if((0 > i || i >= width) || (0 > j || j >= height)) return;
        double mag = Math.sqrt(getXComp(i, j)*getXComp(i, j)+getYComp(i, j)*getYComp(i, j));
        setXComp(i, j, getXComp(i, j)/mag);
        setYComp(i, j, getYComp(i, j)/mag);
    }

    public List<List<Double>> getHHReflect(int i, int j) {
        if((0 > i || i >= width) || (0 > j || j >= height)) return List.of(List.of(0.0, 0.0), List.of(0.0, 0.0));
        if(!(-0.01 < getXComp(i, j) && getXComp(i, j) < 0.01 &&
                -0.01 < getYComp(i, j) && getYComp(i, j) < 0.01)) {
            normalizeXY(i, j);
        }

        List<List<Double>> retList = new ArrayList<>();
        retList.add(new ArrayList<>());
        retList.add(new ArrayList<>());
        retList.get(0).add(1-2*getXComp(i, j)*getXComp(i, j));
        retList.get(0).add(-2*getXComp(i, j)*getYComp(i, j));
        retList.get(1).add(-2*getYComp(i, j)*getXComp(i, j));
        retList.get(1).add(1-2*getYComp(i, j)*getYComp(i, j));

        return retList;
    }
}
