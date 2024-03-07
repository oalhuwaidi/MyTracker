package com.example.mytracker;

import android.content.ClipData;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateInputActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String userId;
    private DocumentReference docRef;

    private ArrayList<TemplateModel> templateModel = new ArrayList<>();
    private List<String> selectedWorkouts = new ArrayList<>();
    private FloatingActionButton fabClickedExercise;
    private androidx.appcompat.widget.SearchView searchView;
    TemplateAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        userId = currentUser.getUid();
        docRef = db.collection("users").document(userId);
        //

        setContentView(R.layout.activity_template_input);
        fabClickedExercise = findViewById(R.id.fab_addClickedExercises);
        searchView = findViewById(R.id.searchView);
        RecyclerView rvTemplate = findViewById(R.id.rv_template);
        setupTemplateModel();
        adapter = new TemplateAdapter(this, templateModel);
        rvTemplate.setAdapter(adapter);
        rvTemplate.setLayoutManager(new LinearLayoutManager(this));

        Map<String, String> clickedExercises = new HashMap<>();

        adapter.setOnCheckboxClickListener(item -> {
            String exerciseName = item.getExerciseName();
            String targetedMuscle = item.getTargetedMuscle();
            if (clickedExercises.containsKey(exerciseName)){
                clickedExercises.remove(exerciseName, targetedMuscle);
            }else{
                clickedExercises.put(exerciseName, targetedMuscle);
            }
        });

        fabClickedExercise.setOnClickListener(v -> {
            EditText edtWorkoutTitle = findViewById(R.id.edt_workout_title);
            String workoutTitle = String.valueOf(edtWorkoutTitle.getText()).trim();
            if (TextUtils.isEmpty(workoutTitle)){
                Toast.makeText(TemplateInputActivity.this, "Enter Workout Title", Toast.LENGTH_SHORT).show();
            }
            else{
                docRef.collection("workouts").document(workoutTitle).set(clickedExercises).addOnSuccessListener(unused -> {
                    Toast.makeText(TemplateInputActivity.this, "Workout Added!", Toast.LENGTH_SHORT).show();
                    finish();
                }).addOnFailureListener(e -> Toast.makeText(TemplateInputActivity.this, "Failed to add workout", Toast.LENGTH_SHORT).show());
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return false;
            }
        });


    }

    private void filterList(String query) {
        ArrayList<TemplateModel> filteredList = new ArrayList<>();
        for (TemplateModel item : templateModel){
            if(item.getExerciseName().toLowerCase().contains(query.toLowerCase())){
                filteredList.add(item);
            }
        }

        adapter.filterList(filteredList);

    }

    private void setupTemplateModel() {

        Map<String, String> exerciseData = new HashMap<>();

// Chest Exercises
        exerciseData.put("Incline Bench Press", "Upper Chest");
        exerciseData.put("Decline Bench Press", "Lower Chest");
        exerciseData.put("Benchpress", "Chest");
        exerciseData.put("Chest Flyes", "Chest");
        exerciseData.put("Dumbbell Flyes", "Chest");
        exerciseData.put("Machine Chest Press", "Chest");
        exerciseData.put("Push-ups", "Chest");
        exerciseData.put("Cable Crossover", "Chest");
        exerciseData.put("Pec Deck Machine", "Chest");
        exerciseData.put("Floor Press", "Chest");

// Back and Core Exercises
        exerciseData.put("Pull-ups", "Back");
        exerciseData.put("Bent-over Rows", "Back");
        exerciseData.put("Barbell Rows", "Back");
        exerciseData.put("Seated Cable Rows", "Back");
        exerciseData.put("Lat Pulldowns", "Back");
        exerciseData.put("Renegade Rows", "Back and Core");
        exerciseData.put("Plank", "Core");
        exerciseData.put("Russian Twists", "Core");
        exerciseData.put("Crunches", "Core");
        exerciseData.put("Side Plank", "Core");
        exerciseData.put("Hanging Leg Raises", "Core");
        exerciseData.put("Deadlifts", "Back");
        exerciseData.put("T-Bar Row", "Back");
        exerciseData.put("Hyperextensions", "Lower Back");
        exerciseData.put("Cable Crunches", "Core");
        exerciseData.put("Ab Wheel Rollout", "Core");


// Shoulder Exercises
        exerciseData.put("Shoulder Press", "Shoulders");
        exerciseData.put("Overhead Press", "Shoulders");
        exerciseData.put("Arnold Press", "Shoulders");
        exerciseData.put("Lateral Raises", "Shoulders");
        exerciseData.put("Lat Raises", "Shoulders");
        exerciseData.put("Reverse Flyes", "Shoulders");
        exerciseData.put("Face Pulls", "Shoulders");
        exerciseData.put("Shrug", "Upper Traps");

// Arms (Biceps & Triceps) Exercises
        exerciseData.put("Bicep Curls", "Biceps");
        exerciseData.put("Hammer Curls", "Biceps");
        exerciseData.put("Barbell Curls", "Biceps");
        exerciseData.put("Tricep Dips", "Triceps");
        exerciseData.put("Skull Crushers", "Triceps");
        exerciseData.put("Tricep Extensions", "Triceps");
        exerciseData.put("Concentration Curls", "Biceps");
        exerciseData.put("Preacher Curls", "Biceps");
        exerciseData.put("Cable Tricep Pushdown", "Triceps");
        exerciseData.put("Overhead Cable Tricep Extension", "Triceps");

// Leg Exercises
        exerciseData.put("Squats", "Legs");
        exerciseData.put("Lunges", "Legs");
        exerciseData.put("Dumbbell Lunges", "Legs");
        exerciseData.put("Front Squats", "Legs");
        exerciseData.put("Walking Lunges", "Legs");
        exerciseData.put("Leg Press", "Legs");
        exerciseData.put("Leg Extensions", "Quadriceps");
        exerciseData.put("Hamstring Curls", "Hamstrings");
        exerciseData.put("Romanian Deadlifts", "Hamstrings");
        exerciseData.put("Glute Bridges", "Glutes");
        exerciseData.put("Hip Thrusts", "Glutes");
        exerciseData.put("Stiff-Legged Deadlifts", "Hamstrings");
        exerciseData.put("Calf Press on Leg Machine", "Calves");
        exerciseData.put("Bulgarian Split Squat", "Legs");

// Calves Exercises
        exerciseData.put("Calf Raises", "Calves");
        exerciseData.put("Seated Calf Raises", "Calves");

// Additional Full Body / Compound Exercises
        exerciseData.put("Clean and Press", "Full Body");
        exerciseData.put("Thrusters", "Full Body");
        exerciseData.put("Farmer's Walk", "Full Body");

        // Additional Glute Exercises
        exerciseData.put("Cable Kickbacks", "Glutes");
        exerciseData.put("Single-Leg Deadlift", "Glutes");

// Additional Core Exercises
        exerciseData.put("Butterfly Sit-ups", "Core");
        exerciseData.put("V-ups", "Core");

        for (Map.Entry<String, String> entry : exerciseData.entrySet()){
            String exerciseName = entry.getKey();
            String targetedMuscle = entry.getValue();
            templateModel.add(new TemplateModel(exerciseName, targetedMuscle));
        }
    }
}