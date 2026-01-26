package com.example.pong_gmae;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseManager {

    private DatabaseReference database;

    public FirebaseManager() {
        // Points to the "scoreboard" node in your database
        database = FirebaseDatabase.getInstance().getReference("scoreboard");
    }

    public void saveScore(String name, int scoreValue) {
        Score newScore = new Score(name, scoreValue);
        // .push() creates a unique ID so scores don't overwrite each other
        database.push().setValue(newScore);
    }

}
