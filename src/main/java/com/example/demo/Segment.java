package com.example.demo;

public class Segment {
    public int x;
    public int y;

    public Segment(int xx, int yy){
        setPosition(xx, yy);
    }
    public void setPosition(int xx, int yy){
        x = xx;
        y = yy;
    }
}
