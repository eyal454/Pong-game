package com.example.pong_gmae;

public class Score {
    public String name;
    public int score;

    public Score() {} // Required for Firebase

    public Score(String name, int score) {
        this.name = name;
        this.score = score;
    }
}
