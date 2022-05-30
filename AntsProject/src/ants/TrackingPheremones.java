package ants;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

public class TrackingPheremones {
    private double decay;
    private Circle pheremone;
    private List<Double> position;

    public TrackingPheremones(double x, double y) {
        position = new ArrayList<>(List.of(x, y));
        decay = 1.0;
        pheremone = new Circle(x, y, 2);
        pheremone.setFill(new Color(0, 1, 0, 1));
    }

    public Circle getPheremone() {
        return pheremone;
    }

    public void decay() {
        decay -= 0.0005;
        if (decay-0.5 > 0) {
            pheremone.setFill(new Color(0, 1, 0, (decay-0.5)/3));
        }
        else{
            pheremone.setOpacity(0);
        }
    }

    public boolean isDecayed() {
        return decay < 0.0;
    }

    public double getDecay() {
        return decay;
    }

    public List<Double> getPosition() {
        return position;
    }
}
