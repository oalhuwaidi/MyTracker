package com.example.mytracker;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class WorkoutStart extends DialogFragment {
    private LinearLayout exercisesContainer;
    private Button btnFinish;
    private String workoutName;


    // Static method to create new instance with arguments
    public static WorkoutStart newInstance(String workoutName, ArrayList<String> exercises) {
        WorkoutStart frag = new WorkoutStart();
        Bundle args = new Bundle();
        args.putString("workoutName", workoutName);
        args.putStringArrayList("exercises", exercises);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workout_start, container, false);
        exercisesContainer = view.findViewById(R.id.llExercisesContainer);
        btnFinish = view.findViewById(R.id.btnFinish);
        TextView tvWorkoutName = view.findViewById(R.id.tvWorkoutName);
        // Ensure getArguments() is not null before attempting to use it
        if (getArguments() != null) {
            tvWorkoutName.setText(getArguments().getString("workoutName"));
            ArrayList<String> exercises = getArguments().getStringArrayList("exercises");

            if (exercises != null) {
                for (String exercise : exercises) {
                    View exerciseView = inflater.inflate(R.layout.item_exercise, exercisesContainer, false);
                    TextView exerciseName = exerciseView.findViewById(R.id.tvExerciseName);
                    exerciseName.setText(exercise);
                    LinearLayout setsContainer = exerciseView.findViewById(R.id.llSetsContainer);

                    addSets(inflater, setsContainer, 3); // Add default 3 sets

                    Button addSetButton = exerciseView.findViewById(R.id.btnAddSet);
                    addSetButton.setOnClickListener(v -> addSet(inflater, setsContainer, setsContainer.getChildCount() + 1));

                    exercisesContainer.addView(exerciseView);
                }
            }
        }

        if (getArguments() != null) {
            workoutName = getArguments().getString("workoutName");
            ArrayList<String> exercises = getArguments().getStringArrayList("exercises");
            // Assume method to dynamically add exercises to exercisesContainer exists
            // addExercisesToContainer(exercises);
        }

        btnFinish.setOnClickListener(v -> saveWorkoutData());


        return view;
    }

    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT); // Use full width and height
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Optional: make the window background transparent
            }
        }
    }
    private void addSets(LayoutInflater inflater, LinearLayout setsContainer, int numberOfSets) {
        for (int i = 1; i <= numberOfSets; i++) {
            addSet(inflater, setsContainer, i);
        }
    }

    private void addSet(LayoutInflater inflater, LinearLayout setsContainer, int setNumber) {
        View setView = inflater.inflate(R.layout.item_set, setsContainer, false);
        TextView tvSetNumber = setView.findViewById(R.id.tvSetNumber);
        tvSetNumber.setText(String.format(Locale.getDefault(), "Set %d", setNumber));
        setsContainer.addView(setView);
    }

    private void saveWorkoutData() {
        if (exercisesContainer == null) {
            Toast.makeText(getContext(), "error, exercisesContainer == null", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, Object> workoutData = new HashMap<>();
        workoutData.put("workoutName", workoutName);

        List<Map<String, Object>> exercisesData = new ArrayList<>();

        for (int i = 0; i < exercisesContainer.getChildCount(); i++) {
            View exerciseView = exercisesContainer.getChildAt(i);
            LinearLayout setsContainer = exerciseView.findViewById(R.id.llSetsContainer);

            Map<String, Object> exerciseData = new HashMap<>();
            exerciseData.put("exerciseName", ((TextView) exerciseView.findViewById(R.id.tvExerciseName)).getText().toString());

            List<Map<String, Integer>> setsData = new ArrayList<>();
            for (int j = 0; j < setsContainer.getChildCount(); j++) {
                View setView = setsContainer.getChildAt(j);
                EditText etReps = setView.findViewById(R.id.etReps);
                EditText etWeight = setView.findViewById(R.id.etWeight);

                // Validate and parse the reps and weight inputs
                int reps = !etReps.getText().toString().isEmpty() ? Integer.parseInt(etReps.getText().toString()) : 0;
                int weight = !etWeight.getText().toString().isEmpty() ? Integer.parseInt(etWeight.getText().toString()) : 0;

                Map<String, Integer> setData = new HashMap<>();
                setData.put("reps", reps);
                setData.put("weight", weight);
                setsData.add(setData);
            }
            exerciseData.put("sets", setsData);
            exercisesData.add(exerciseData);
        }
        workoutData.put("exercises", exercisesData);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        db.collection("users").document(userId).collection("finishedWorkouts").document(currentDate)
                .set(workoutData)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), "Workout Saved!", Toast.LENGTH_SHORT).show();
                    dismiss();
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }
}
