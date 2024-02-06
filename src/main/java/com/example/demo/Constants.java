package com.example.demo;

public class Constants {
    public static final int WIDTH = 300;
    public static final int HEIGHT = WIDTH;
    public static final int ROWS = 5;
    public static final int COLUMNS = ROWS;
    public static final int SQUARE_SIZE = WIDTH/ROWS;

    public static final int INITIAL_X = 0;
    public static final int INITIAL_Y = 0;
    public static final boolean TRAINING_MODE = false;
    public static final boolean BOT_MODE = true;
    public static final int STEPTIME = TRAINING_MODE ? 50 : 400;
    public static final double ALPHA = 0.5;
    public static final double EPSILON = 0.3;
    public static final double DISCOUNT = 0.99;


}
