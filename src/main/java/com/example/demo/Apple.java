package com.example.demo;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Apple {
    int x;
    int y;

    public Apple(Snake snake){
        generateNewPosition(snake);
    }

    public void generateNewPosition(Snake snake){
        ArrayList<Integer> possibleXs = new ArrayList<>();
        ArrayList<Integer> possibleYs = new ArrayList<>();

        for (int i = 0; i < Constants.ROWS; i++){
            for (int j = 0; j < Constants.COLUMNS; j++){
                boolean add = true;
                for (int s = 0; s < snake.tail.size(); s++){
                    Segment seg = snake.tail.get(s);

                    if ((seg.x == j && seg.y == i) || (snake.x == j && snake.y == i)){
                        add = false;
                        break;
                    }
                }
                if (add){
                    possibleYs.add(i);
                    possibleXs.add(j);
                }
            }
        }


        java.util.Random random = new java.util.Random();

        if (!possibleYs.isEmpty()){
            y = possibleYs.get(random.nextInt(possibleYs.size()));
        }else{
            y = -1;
        }

        if (!possibleXs.isEmpty()){
            x = possibleXs.get(random.nextInt(possibleXs.size()));
        }else{
            x = -1;
        }
    }

    public void draw(GraphicsContext gc){
        gc.setFill(Color.RED);
        gc.fillRect(Constants.SQUARE_SIZE*x,Constants.SQUARE_SIZE*y,Constants.SQUARE_SIZE,Constants.SQUARE_SIZE);
    }
}
