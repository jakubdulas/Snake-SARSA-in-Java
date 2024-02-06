package com.example.demo;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Snake {
    public int x;
    public int y;
    public Direction direction;
    public Direction newDirection;
    ArrayList<Segment> tail = new ArrayList<>();
    public boolean addSegment = false;


    public Snake(int xx, int yy, Direction dir){
        x = xx;
        y = yy;
        direction = dir;
        newDirection = dir;
    }

    public void eat(){
        addSegment = true;
    }

    public void changeDirection(Direction d){
        if (d == Direction.UP && direction == Direction.DOWN){
            return;
        }
        if (d == Direction.DOWN && direction == Direction.UP){
            return;
        }
        if (d == Direction.LEFT && direction == Direction.RIGHT){
            return;
        }
        if (d == Direction.RIGHT && direction == Direction.LEFT){
            return;
        }
        newDirection = d;
    }

    public void step(){
        direction = newDirection;

        int newX = x;
        int newY = y;

        moveHead();

        for (int i = 0; i < tail.size(); i++) {
            int prevX = tail.get(i).x;
            int prevY = tail.get(i).y;
            tail.get(i).setPosition(newX, newY);
            newX = prevX;
            newY = prevY;
        }

        if (addSegment){
            tail.add(new Segment(newX, newY));
            addSegment = false;
        }
    }

    private void moveHead() {
        switch (direction) {
            case RIGHT:
                x++;
                break;
            case LEFT:
                x--;
                break;
            case UP:
                y--;
                break;
            case DOWN:
                y++;
                break;
        }
    }

    public boolean checkCollision(){
        if (x < 0 || x >= Constants.COLUMNS){
            return true;
        }else if (y < 0 || y >= Constants.ROWS){
            return true;
        }
        for (int i = 0; i < tail.size(); i++){
            if (x == tail.get(i).x && y == tail.get(i).y){
                return true;
            }
        }
        return false;
    }

    public boolean hasMaxLength(){
        return tail.size()+1 == Constants.COLUMNS*Constants.ROWS;
    }

    public void draw(GraphicsContext gc){
        gc.setFill(Color.YELLOWGREEN);
        gc.fillRect(Constants.SQUARE_SIZE*x,Constants.SQUARE_SIZE*y,Constants.SQUARE_SIZE,Constants.SQUARE_SIZE);

        gc.setFill(Color.GREEN);
        for (int i = 0; i < tail.size(); i++){
            Segment s = tail.get(i);
            gc.fillRect(Constants.SQUARE_SIZE*s.x,Constants.SQUARE_SIZE*s.y,Constants.SQUARE_SIZE,Constants.SQUARE_SIZE);
        }
    }
}
