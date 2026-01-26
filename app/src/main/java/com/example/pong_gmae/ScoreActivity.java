package com.example.pong_gmae;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;

public class ScoreActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<String> scoreList;
    private ArrayAdapter<String> adapter;
    private DatabaseReference database;

    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        // Inside ScoreActivity.java, in the onCreate method

        backButton = findViewById(R.id.backToMenuButton);

        backButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                // This closes ScoreActivity and returns to the Main Menu
                finish();
            }
        });

        listView = findViewById(R.id.scoreListView);
        scoreList = new ArrayList<>();

        // Custom adapter using our white-text-on-black layout
        adapter = new ArrayAdapter<>(this, R.layout.list_item_score, scoreList);
        listView.setAdapter(adapter);

        // Connect to Firebase
        database = FirebaseDatabase.getInstance().getReference("scoreboard");

        // Fetch data
        database.orderByChild("score").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                scoreList.clear();
                ArrayList<Score> tempScores = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Score s = snapshot.getValue(Score.class);
                    if (s != null) {
                        tempScores.add(s);
                    }
                }

                // Sort descending (Highest score first)
                Collections.sort(tempScores, (a, b) -> b.score - a.score);

                // Format the string for the list
                for (Score s : tempScores) {
                    scoreList.add(s.name + " : " + s.score);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }
}