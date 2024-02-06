package com.example.demo;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Agent {

    private HashMap<String, HashMap<Direction, Double>> qValues = new HashMap<>();
    private double alpha;
    private double epsilon;
    private double discount;

    public Agent(double a, double e, double d){
        alpha = a;
        epsilon = e;
        discount = d;
    }

    public double getQValue(String state, Direction a){
        if (qValues.get(state) == null || qValues.get(state).get(a) == null){
            return 0;
        }
        return  qValues.get(state).get(a);
    }
    public void setQValue(String state, Direction direction, double newValue){
        HashMap<Direction, Double> innerMap = qValues.get(state);

        if (innerMap != null) {
            innerMap.put(direction, newValue);
        } else {
            innerMap = new HashMap<>();
            innerMap.put(direction, newValue);
            qValues.put(state, innerMap);
        }
    }

    private ArrayList<Direction> getLegalActions(String state){
        char encodedDirection = state.charAt(0);
        ArrayList<Direction> directions = new ArrayList<>();

        if (encodedDirection == '0'){
            directions.add(Direction.UP);
            directions.add(Direction.RIGHT);
            directions.add(Direction.DOWN);
        }else if (encodedDirection == '1'){
            directions.add(Direction.DOWN);
            directions.add(Direction.LEFT);
            directions.add(Direction.RIGHT);
        }else if (encodedDirection == '2'){
            directions.add(Direction.DOWN);
            directions.add(Direction.LEFT);
            directions.add(Direction.UP);
        }else if (encodedDirection == '3'){
            directions.add(Direction.UP);
            directions.add(Direction.LEFT);
            directions.add(Direction.RIGHT);
        }

        return directions;
    }


    public Direction update(String state, Direction action, double reward, String nextState){
        double gamma = discount;
        double learningRate = alpha;

        double newVal = (1-learningRate)*getQValue(state, action);

        Direction nextAction = getAction(state);

        newVal += learningRate*(reward + gamma*getQValue(nextState, nextAction));

        setQValue(state, action, newVal);

        return nextAction;
    }

    public Direction getAction(String state){
        ArrayList<Direction> possibleActions = getLegalActions(state);
        if (possibleActions.isEmpty()){
            return null;
        }

        double randomValue = Math.random();
        java.util.Random random = new java.util.Random();

        if (randomValue < epsilon){
            return possibleActions.get(random.nextInt(possibleActions.size()));
        }

        return getBestAction(state);
    }


    public Direction getBestAction(String state){
        ArrayList<Direction> possibleActions = getLegalActions(state);

        if (possibleActions.isEmpty()){
            return null;
        }

        ArrayList<Direction> bestActions = new ArrayList<>();
        double bestVal = Double.NEGATIVE_INFINITY;

        for (Direction action : possibleActions){
            double val = getQValue(state, action);

            if (val > bestVal){
                bestActions = new ArrayList<>(Arrays.asList(action));
                bestVal = val;
            }else if (val == bestVal){
                bestActions.add(action);
            }
        }

        java.util.Random random = new java.util.Random();

        return bestActions.get(random.nextInt(bestActions.size()));
    }


    public void saveToFile(String fileName) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fileName))) {
            outputStream.writeObject(qValues);
            System.out.println("Object saved to file: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFromFile(String fileName) {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(fileName))) {
            qValues = (HashMap<String, HashMap<Direction, Double>>) inputStream.readObject();
            System.out.println("Object loaded from file: " + fileName);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void turnOffLearning(){
        epsilon = 0;
        alpha = 0;
    }

    public void displayHashMap() {
        for (Map.Entry<String, HashMap<Direction, Double>> entry : qValues.entrySet()) {
            String key = entry.getKey();
            HashMap<Direction, Double> innerMap = entry.getValue();

            System.out.println("State: " + key);

            for (Map.Entry<Direction, Double> innerEntry : innerMap.entrySet()) {
                Direction innerKey = innerEntry.getKey();
                Double value = innerEntry.getValue();

                System.out.println("  Action: " + innerKey + ", Value: " + value);
            }
        }
    }
}
