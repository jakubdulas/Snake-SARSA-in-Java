package com.example.demo;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;


public class Main extends Application {
    public static Snake snake;
    public static Apple apple;
    public static Agent agent = new Agent(Constants.ALPHA, Constants.EPSILON, Constants.DISCOUNT);

    @Override
    public void start(Stage primaryStage) throws Exception{
        Group root = new Group();
        Scene s = new Scene(root, Constants.WIDTH, Constants.HEIGHT, Color.BLACK);

        final Canvas canvas = new Canvas(Constants.WIDTH,Constants.HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        root.getChildren().add(canvas);

        primaryStage.setScene(s);
        primaryStage.setTitle("Snake");
        primaryStage.show();

        if (!Constants.TRAINING_MODE){
            s.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    KeyCode code = event.getCode();
                    if (code == KeyCode.RIGHT || code == KeyCode.D) {
                        snake.changeDirection(Direction.RIGHT);
                    } else if (code == KeyCode.LEFT || code == KeyCode.A) {
                        snake.changeDirection(Direction.LEFT);
                    } else if (code == KeyCode.UP || code == KeyCode.W) {
                        snake.changeDirection(Direction.UP);
                    } else if (code == KeyCode.DOWN || code == KeyCode.S) {
                        snake.changeDirection(Direction.DOWN);
                    }
                }
            });
        }

        restart();

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(Constants.STEPTIME), e -> gameStep(gc, null)));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private static void restart(){
        snake = new Snake(Constants.INITIAL_X, Constants.INITIAL_Y, Direction.RIGHT);
        apple = new Apple(snake);
    }

    private static void displayObjects(GraphicsContext gc){
        if (!Constants.TRAINING_MODE){
            drawBackground(gc);
            apple.draw(gc);
            snake.draw(gc);
        }
    }

    private static double gameStep(GraphicsContext gc, Direction action) {
        double reward = 0;

        if (Constants.TRAINING_MODE && action != null){
            snake.changeDirection(action);
        }else if (Constants.BOT_MODE){
            snake.changeDirection(agent.getBestAction(getState()));
        }

        snake.step();
        displayObjects(gc);


        if (snakeAteApple()){
            snake.eat();
            apple.generateNewPosition(snake);
            reward += 1;
        }

        if (snake.checkCollision()){
            if (Constants.TRAINING_MODE){
                return -10;
            }else{
                drawGameOver(gc);
                return 0;
            }
        }

        if (snake.hasMaxLength()){
            if (Constants.TRAINING_MODE){
                return 10;
            }else{
                drawVictory(gc);
                return 0;
            }
        }

        return reward;
    }

    private static String getState(){
        String state = "";

        if (snake.direction == Direction.RIGHT){
            state += 0;
        }else if (snake.direction == Direction.DOWN){
            state += 1;
        }else if (snake.direction == Direction.LEFT){
            state += 2;
        }else if (snake.direction == Direction.UP){
            state += 3;
        }

        state += apple.x < snake.x ? 1 : 0; // apple on right
        state += apple.y < snake.y ? 1 : 0; // apple above
        state += (apple.x == snake.x || apple.y == snake.y) ? 1 : 0; // apple on a line with snake

        snake.x -= 1;
        state += snake.checkCollision() ? 1 : 0; // collision on left
        snake.x += 2;
        state += snake.checkCollision() ? 1 : 0; // collision on right
        snake.x -= 1;
        snake.y -= 1;
        state += snake.checkCollision() ? 1 : 0; // collision above
        snake.y += 2;
        state += snake.checkCollision() ? 1 : 0; // collision below
        snake.y -= 1;

        return state;
    }

    private static boolean snakeAteApple(){
        return apple.x == snake.x && apple.y == snake.y;
    }

    private static void drawBackground(GraphicsContext gc){
        gc.setFill(Color.BLACK);
        gc.fillRect(0,0,Constants.WIDTH,Constants.HEIGHT);
    }

    private static void drawGameOver(GraphicsContext gc){
        drawBackground(gc);
        gc.setFill(Color.RED);
        gc.setFont(new Font("Digital-7", 35));
        gc.fillText("Game Over", Constants.WIDTH / 3.5, Constants.HEIGHT / 2);
    }

    private static void drawVictory(GraphicsContext gc){
        drawBackground(gc);
        gc.setFill(Color.GREEN);
        gc.setFont(new Font("Digital-7", 35));
        gc.fillText("You won!", Constants.WIDTH / 3.5, Constants.HEIGHT / 2);
    }

    public static void train(){
        restart();
        String state = getState();
        String nextState;
        Direction action = Direction.RIGHT;
        int episodes = 1;

        while (1000000 > episodes){
            boolean done = false;
            int stepsWithoutReward = 0;

            while (!done){
                double reward = gameStep(null, action);

                if (reward == 0){
                    stepsWithoutReward += 1;
                }

                if (stepsWithoutReward >= Constants.ROWS*Constants.COLUMNS-snake.tail.size()-1){
                    reward = -5;
                    done = true;
                }

                if(reward == 10 || reward == -10){
                    done = true;
                }

                nextState = getState();
                action = agent.update(state, action, reward, nextState);
                state = nextState;
            }
            restart();
            episodes += 1;

            if (episodes % 100 == 0){
                System.out.println("Episode: " + episodes + " Saving qvalues...");
                agent.saveToFile("qvalues");
            }
        }
    }

    public static void main(String[] args) {
        agent.loadFromFile("qvalues");
        agent.displayHashMap();
        if (Constants.TRAINING_MODE){
            train();
        }else{
            launch(args);
        }
    }

    //    delta = 0
    //
//        for state in mdp.get_all_states():
        //    prev_state = V[state]
        //
        //    best_action = None
        //    best_action_val = float('-inf')
        //
        //            for action in mdp.get_possible_actions(s):
        //              x = 0
        //
        //              for next_state, p in mdp.get_next_states(state, action).items():
        //                  reward = mdp.get_reward(state, action, next_state)
        //                  x += p*(reward+gamma*V[next_state])
        //
        //              if x > best_action_val:
        //                  best_action_val = x
        //                  best_action = action
        //
        //
        //    V[state] = best_action_val
        //    policy[state] = best_action
    //
//            delta = max(delta, abs(prev_state-V[state]))

//    public static HashMap<String, Double> valueIteration(double gamma, double theta){
//        HashMap<String, Double> V = new HashMap<>();
//        HashMap<String, Direction> policy = new HashMap<>();
//
//        ArrayList<String> allStates = getAllStates();
//        for (String currentState : allStates){
//            V.put(currentState, 0.);
//            policy.put(currentState, 0.);
//        }
//        double delta = Double.NEGATIVE_INFINITY;
//
//        while (delta > theta){
//            delta = 0;
//            for (String state : allStates){
//                double prevState = V.get(state);
//                Direction bestAction;
//                double bestActionVal = Double.NEGATIVE_INFINITY;
//
//                for (Direction action : getLegalActions(state)){
//                    double x = 0;
//
//                    for (String nextState : )
//                }
//            }
//        }
//    }
//
//    private static ArrayList<String> getNextStates(String state, Direction action){
//
//    }

    private static ArrayList<Direction> getLegalActions(String state){
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


    private static ArrayList<String> getAllStates(){
        ArrayList<String> states = new ArrayList<>();
        generateCombinationsHelper("", 0, states);
        return states;
    }

    private static void generateCombinationsHelper(String currentCombination, int currentIndex, ArrayList<String> combinations) {
        // Base case: if the combination has reached the desired length
        if (currentIndex == 7) {
            combinations.add(currentCombination);
            return;
        }

        // Append allowed characters based on the current index
        if (currentIndex == 0) {
            // First character can be 0, 1, 2, or 3
            for (char c : new char[]{'0', '1', '2', '3'}) {
                generateCombinationsHelper(currentCombination + c, currentIndex + 1, combinations);
            }
        } else {
            // Rest of the characters can be 0 or 1
            for (char c : new char[]{'0', '1'}) {
                generateCombinationsHelper(currentCombination + c, currentIndex + 1, combinations);
            }
        }
    }

}
