package com.example.pong_gmae;

public class Score {
    public String name;
    public int score;

    public Score() {
        // Default constructor required for calls to DataSnapshot.getValue(Score.class)
    }

    public Score(String name, int score) {
        this.name = name;
        this.score = score;
    }
}