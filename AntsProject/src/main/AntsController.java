package main;

import ants.Ant;
import ants.Food;
import ants.FoodPheremone;
import ants.VectorField;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AntsController extends Application {
    private final int WIDTH = 800;
    private final int HEIGHT = 800;
    private List<Ant> antList;
    private VectorField vectorField;
    private List<FoodPheremone> foodPhemList;
    private List<Food> foodList;
    private final int foodClusterCount = 30;
    private final int foodCountPerCluster = 40;
    private final int antCount = 50;
    private final int callsPerFrame = 1;
    private final boolean showPheremones = true;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Ant Thing");

        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        Pane root = new Pane(canvas);

        makeVectorField();
        makeBorder(root);
        makeAntList(root);
        makeFoodList(root);
        makeColonyHome(root);

        foodPhemList = new ArrayList<>();

        Scene scene1 = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setScene(scene1);
        primaryStage.show();

        AnimationTimer at = new AnimationTimer() {
            @Override
            public void handle(long now) {
                for(int i = 0; i < callsPerFrame; i++) {
                    for(Ant ant : antList) {
                        ant.move(root, vectorField, foodPhemList, foodList);
                    }
                    List<FoodPheremone> foodPhemRemList = new ArrayList<>();
                    for(FoodPheremone foodPhem : foodPhemList){
                        foodPhem.decay();
                        if(foodPhem.isDecayed()) {
                            foodPhemRemList.add(foodPhem);
                            if (showPheremones) root.getChildren().remove(foodPhem.getPheremone());
                        }
                    }
                    foodPhemList.removeAll(foodPhemRemList);
                }
            }
        };

        at.start();
    }

    public void makeColonyHome(Pane root) {
        Circle c = new Circle(WIDTH/2, HEIGHT/2, 20);
        c.setFill(Color.BEIGE);
        root.getChildren().add(c);
    }

    public void makeFoodList(Pane root) {
        foodList = new ArrayList<>();
        int thickness = 5;

        double rand1, rand2, rand3, rand4;
        for(int i = 0; i < foodClusterCount; i++) {
            rand1 = ThreadLocalRandom.current().nextDouble(WIDTH-2*thickness)+thickness;
            rand2 = ThreadLocalRandom.current().nextDouble(HEIGHT-2*thickness)+thickness;
            for(int j = 0; j < foodCountPerCluster; j++) {
                rand3 = ThreadLocalRandom.current().nextDouble(10)-5;
                rand4 = ThreadLocalRandom.current().nextDouble(10)-5;
                Food food = new Food(rand1+rand3, rand2+rand4);
                foodList.add(food);
                root.getChildren().add(food.getFood());
            }
        }
    }

    public void makeVectorField() {
        vectorField = new VectorField(WIDTH, HEIGHT);
    }

    public void makeBorder(Pane root) {
        int thickness = 5;

        Rectangle rect = new Rectangle(WIDTH, HEIGHT, Color.BLACK);
        root.getChildren().add(rect);
        rect = new Rectangle(WIDTH-2*thickness, HEIGHT-2*thickness, Color.WHITE);
        rect.setTranslateX(thickness);
        rect.setTranslateY(thickness);
        root.getChildren().add(rect);

        for(int i = thickness; i < WIDTH-thickness; i++) {
            for(int j = 0; j < thickness; j++) {
                vectorField.setXComp(i, j, 0);
                vectorField.setYComp(i, j, -1);
            }
        }
        for(int j = thickness; j < HEIGHT-thickness; j++) {
            for(int i = 0; i < thickness; i++) {
                vectorField.setXComp(i, j, -1);
                vectorField.setYComp(i, j, 0);
            }
        }
        for(int i = thickness; i < WIDTH-thickness; i++) {
            for(int j = HEIGHT-1; j > HEIGHT-1 - thickness; j--) {
                vectorField.setXComp(i, j, 0);
                vectorField.setYComp(i, j, 1);
            }
        }
        for(int j = thickness; j < HEIGHT-thickness; j++) {
            for(int i = WIDTH-1; i > WIDTH-1 - thickness; i--) {
                vectorField.setXComp(i, j, 1);
                vectorField.setYComp(i, j, 0);
            }
        }

        for(int i = 0; i < thickness; i++) {
            for(int j = 0; j < thickness; j++) {
                vectorField.setXComp(i, j, -1);
                vectorField.setYComp(i, j, -1);
                vectorField.normalizeXY(i, j);
            }
        }
        for(int i = 0; i < thickness; i++) {
            for(int j = HEIGHT-1; j > HEIGHT-1 - thickness; j--) {
                vectorField.setXComp(i, j, -1);
                vectorField.setYComp(i, j, 1);
                vectorField.normalizeXY(i, j);
            }
        }
        for(int i = WIDTH-1; i > WIDTH-1 - thickness; i--) {
            for(int j = 0; j < thickness; j++) {
                vectorField.setXComp(i, j, 1);
                vectorField.setYComp(i, j, -1);
                vectorField.normalizeXY(i, j);
            }
        }
        for(int i = WIDTH-1; i > WIDTH-1 - thickness; i--) {
            for(int j = HEIGHT-1; j > HEIGHT-1 - thickness; j--) {
                vectorField.setXComp(i, j, 1);
                vectorField.setYComp(i, j, 1);
                vectorField.normalizeXY(i, j);
            }
        }
    }

    public void makeAntList(Pane root) {
        antList = new ArrayList<>();

        for(int i = 0; i < antCount; i++) {
            Ant ant = new Ant(WIDTH, HEIGHT, WIDTH/2, HEIGHT/2, showPheremones);
            antList.add(ant);
            root.getChildren().add(ant.getAnt());
        }
    }
}
