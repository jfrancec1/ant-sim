package ants;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

public class FoodPheremone {
    private double decay;
    private Circle pheremone;
    private List<Double> position;

    public FoodPheremone(double x, double y) {
        position = new ArrayList<>(List.of(x, y));
        decay = 1.0;
        pheremone = new Circle(x, y, 2);
        pheremone.setFill(Color.RED);
    }

    public Circle getPheremone() {
        return pheremone;
    }

    public List<Double> getPosition() {
        return new ArrayList<>(position);
    }

    public double getDecay() {
        return decay;
    }

    public void decay() {
        decay -= 0.0025;
        if(decay > 0) {
            pheremone.setFill(new Color(1, 0, 0, decay));
        }
        else{
            pheremone.setOpacity(0);
        }
    }

    public boolean isDecayed() {
        return decay < 0.0;
    }
}
