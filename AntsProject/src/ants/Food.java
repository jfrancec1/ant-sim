package ants;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

public class Food {
    private List<Double> position;
    private Circle food;

    public Food(double x, double y) {
        position = new ArrayList<>();
        position.add(x);
        position.add(y);
        food = new Circle(x, y, 2);
        food.setFill(Color.YELLOW);
    }

    public List<Double> getPosition(){
        return new ArrayList<>(position);
    }

    public Circle getFood() {
        return food;
    }
}
