package ants;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Ant {
    private Circle ant;
    private List<Double> direction;
    private List<Double> position;
    private double motionScale = 1;
    private double thetaWeight = 0.5;
    private int timer;
    private int timeSinceLastFood;
    private final int START_TIMER = 5;
    private List<TrackingPheremones> phemList;
    private boolean hasFood;
    private final double foodDistance = 5.0;
    private final double viewDistance = 50;
    private double screenWidth, screenHeight;
    private final double homeWidth = 20;
    private final double weightModWeight = 12000.0;
    private boolean showPheremones;

    public Ant(int screenWidth, int screenHeight, boolean showPheremones) {
        this(screenWidth, screenHeight, ThreadLocalRandom.current().nextDouble(screenWidth),
                ThreadLocalRandom.current().nextDouble(screenHeight), showPheremones);
    }

    public Ant(int screenWidth, int screenHeight, double posX, double posY, boolean showPheremones) {
        this.showPheremones = showPheremones;
        timeSinceLastFood = 0;

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        phemList = new ArrayList<>();

        hasFood = false;
        timer = START_TIMER;
        direction = new ArrayList<>();
        double randDouble = ThreadLocalRandom.current().nextDouble(2.0)-1.0;
        direction.add(randDouble);
        randDouble = ThreadLocalRandom.current().nextDouble(2.0)-1.0;
        direction.add(randDouble);
        normalize();

        position = new ArrayList<>();
        position.add(posX);
        position.add(posY);

        ant = new Circle(position.get(0), position.get(1), 2);
    }

    private void normalize() {
        double mag = Math.sqrt(direction.get(0)*direction.get(0) + direction.get(1)*direction.get(1));
        direction.set(0, direction.get(0) / mag);
        direction.set(1, direction.get(1) / mag);
    }

    private void normalizeVect(List<Double> vector) {
        double mag = Math.sqrt(vector.get(0)*vector.get(0) + vector.get(1)*vector.get(1));
        vector.set(0, vector.get(0) / mag);
        vector.set(1, vector.get(1) / mag);
    }

    // Doesn't assume anything about the walls or anything
    public void move(Pane root, VectorField vectorField, List<FoodPheremone> foodPhemList, List<Food> foodList) {
        double weightMod = Math.max((weightModWeight-timeSinceLastFood)/weightModWeight, 0);

        double randDouble = weightMod*(ThreadLocalRandom.current().nextDouble(thetaWeight)-(thetaWeight/2));
        if(!hasFood) {
            // Move the ant in a preferred direction towards food
            double fCount = findFood(foodList, 0);
            double lCount = findFood(foodList, -60);
            double rCount = findFood(foodList, 60);

            fCount += findFoodPheremones(foodPhemList, 0)/6;
            lCount += findFoodPheremones(foodPhemList, -60)/6;
            rCount += findFoodPheremones(foodPhemList, 60)/6;

            if(lCount > fCount) randDouble -= (thetaWeight/4);
            if(rCount > fCount) randDouble += (thetaWeight/4);
            //if(fCount > 0) randDouble /= 2;
        }
        // hasFood is true
        else {
            double fCount = findTrackingPheremones(phemList, 0);
            double lCount = findTrackingPheremones(phemList, -45);
            double rCount = findTrackingPheremones(phemList, 45);
            lCount += findTrackingPheremones(phemList, -180)/2;
            //lCount += findTrackingPheremones(phemList, -120)/2;
            //rCount += findTrackingPheremones(phemList, 120)/2;

            //if(lCount > fCount) randDouble /= 2;
            //if(rCount > fCount) randDouble /= 2;
            if(lCount > fCount) randDouble -= (thetaWeight/3);
            if(rCount > fCount) randDouble += (thetaWeight/3);

            if(distance(List.of(screenWidth/2, screenHeight/2), position) < homeWidth) {
                hasFood = false;
                timeSinceLastFood = 0;
                ant.setFill(Color.BLACK);
            }
        }

        double s = Math.sin(randDouble);
        double c = Math.cos(randDouble);
        double d0 = direction.get(0);
        double d1 = direction.get(1);

        List<Double> tempDir = directionToCenter(position);

        direction.set(0, d0*c + d1*s + weightMod*tempDir.get(0));
        direction.set(1, -d0*s + d1*c + weightMod*tempDir.get(1));

        List<List<Double>> hhr = vectorField.getHHReflect((int) Math.round(position.get(0)),
                (int) Math.round(position.get(1)));
        d0 = direction.get(0);
        d1 = direction.get(1);

        direction.set(0, d0*hhr.get(0).get(0) + d1*hhr.get(0).get(1));
        direction.set(1, d0*hhr.get(1).get(0) + d1*hhr.get(1).get(1));

        normalize(); // Just to check

        position.set(0, position.get(0) + direction.get(0)*motionScale);
        position.set(1, position.get(1) + direction.get(1)*motionScale);

        ant.setCenterX(position.get(0));
        ant.setCenterY(position.get(1));

        List<TrackingPheremones> remList = new ArrayList<>();
        for(TrackingPheremones tPhem : phemList) {
            tPhem.decay();
            if(tPhem.isDecayed()) {
                if (showPheremones) root.getChildren().remove(tPhem.getPheremone());
                remList.add(tPhem);
            }
        }
        phemList.removeAll(remList);

        timer--;
        if(timer <= 0) {
            runTimerInterrupt(root, foodPhemList);
        }

        // Check if food is nearby
        Food foundFood = foundFood(foodList);
        if(foundFood != null && !hasFood) {
            foodList.remove(foundFood);
            root.getChildren().remove(foundFood.getFood());
            hasFood = true;
            timeSinceLastFood = 0;
            ant.setFill(Color.YELLOWGREEN);
        }

        timeSinceLastFood++;
    }

    public List<Double> directionToCenter(List<Double> position) {
        List<Double> tempPos = new ArrayList<>();
        tempPos.add((screenWidth/2) - position.get(0));
        tempPos.add((screenHeight/2)- position.get(1));
        if(-0.1 < tempPos.get(0) && tempPos.get(0) < 0.1 &&
                -0.1 < tempPos.get(1) && tempPos.get(1) < 0.1)
            return tempPos;
        normalizeVect(tempPos);
        tempPos.set(0, tempPos.get(0)/30);
        tempPos.set(1, tempPos.get(1)/30);

        return tempPos;
    }

    public Food foundFood(List<Food> foodList) {
        for(Food food : foodList) {
            if(distance(food.getPosition(), position) < foodDistance) {
                return food;
            }
        }

        return null;
    }

    public int findTrackingPheremones(List<TrackingPheremones> foodList, double rotation) {
        int count = 0;

        List<Double> tempPos = getRotationOffset(position, direction, rotation, viewDistance);
        for(TrackingPheremones food : foodList) {
            double dist = distance(food.getPosition(), tempPos);
            if (dist < 2.5*viewDistance/4.0) {
                //count += (2-food.getDecay());
                count += 2*food.getDecay();
                //count++;
            }
        }

        return count;
    }

    public double findFoodPheremones(List<FoodPheremone> foodList, double rotation) {
        double count = 0;

        List<Double> tempPos = getRotationOffset(position, direction, rotation, viewDistance);
        for(FoodPheremone food : foodList) {
            double dist = distance(food.getPosition(), tempPos);
            if (dist < 3.0*viewDistance/4.0) {
                //count++;
                count += food.getDecay()/2;
            }
        }

        return count;
    }

    public int findFood(List<Food> foodList, double rotation) {
        int count = 0;

        List<Double> tempPos = getRotationOffset(position, direction, rotation, viewDistance);
        for(Food food : foodList) {
            double dist = distance(food.getPosition(), tempPos);
            if (dist < 3.0*viewDistance/4.0) {
                count++;
            }
        }

        return count;
    }

    public List<Double> getRotationOffset(List<Double> pos, List<Double> direction, double theta, double dist) {
        List<Double> tempPos = new ArrayList<>(direction);
        double d0 = tempPos.get(0);
        double d1 = tempPos.get(1);
        double rad = theta * Math.PI / 180.0;
        double c = Math.cos(rad);
        double s = Math.sin(rad);
        tempPos.set(0, (d0*c + d1*s) * dist + pos.get(0));
        tempPos.set(1, (-d0*s+ d1*c) * dist + pos.get(1));
        return tempPos;
    }

    public double distance(List<Double> pos1, List<Double> pos2) {
        double d0 = pos1.get(0)-pos2.get(0);
        double d1 = pos1.get(1)-pos2.get(1);
        return Math.sqrt(d0*d0 + d1*d1);
    }

    public void runTimerInterrupt(Pane root, List<FoodPheremone> foodPhemList) {
        timer = START_TIMER;

        if(hasFood) {
            FoodPheremone tPhem = new FoodPheremone((int) Math.round(position.get(0)), (int) Math.round(position.get(1)));
            foodPhemList.add(tPhem);
            if (showPheremones) root.getChildren().add(tPhem.getPheremone());
        }
        else {
            TrackingPheremones tPhem = new TrackingPheremones((int) Math.round(position.get(0)), (int) Math.round(position.get(1)));
            phemList.add(tPhem);
            if (showPheremones) root.getChildren().add(tPhem.getPheremone());
        }

    }

    public Circle getAnt() {
        return ant;
    }
}
