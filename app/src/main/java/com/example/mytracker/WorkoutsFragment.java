package com.example.mytracker;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkoutsFragment extends Fragment {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String userId;
    private DocumentReference docRef;
    private CollectionReference workoutRef;
    FloatingActionButton fab_template;
    ArrayList<WorkoutsModel> workoutsModels = new ArrayList<>();
    RecyclerView rvWorkout;
    WorkoutsAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        userId = currentUser.getUid();
        docRef = db.collection("users").document(userId);
        workoutRef = db.collection("users").document(userId).collection("workouts");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workouts, container, false);
        fab_template = view.findViewById(R.id.fab_template);
        rvWorkout = view.findViewById(R.id.rv_wokrouts);

        adapter = new WorkoutsAdapter(getContext(), workoutsModels, new WorkoutsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(WorkoutsModel workoutsModel) {
                String workoutName = workoutsModel.getWorkoutName();
                ArrayList<String> exercisesList = new ArrayList<>(workoutsModel.getExercises());
                WorkoutStart dialog = WorkoutStart.newInstance(workoutName, exercisesList);
                dialog.show(getChildFragmentManager(), "workout_start");

            }
            @Override
            public void onItemLongClick(int position) {
                showYourDialogMethod(position);
            }
        });
        rvWorkout.setAdapter(adapter);
        rvWorkout.setLayoutManager(new LinearLayoutManager(getContext()));

        fetchWorkoutAndSetupModels();

        fab_template.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), TemplateInputActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void showYourDialogMethod(int position) {
        if (position >= 0 && position < workoutsModels.size()) {
            WorkoutsModel selectedWorkout = workoutsModels.get(position);
            String workoutTitle = selectedWorkout.getWorkoutName();

            new AlertDialog.Builder(getContext())
                    .setTitle("Remove Template")
                    .setMessage("Are you sure you want to remove this Workout Template?  " + workoutTitle)
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        workoutRef.document(workoutTitle).delete().addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Workout removed!", Toast.LENGTH_SHORT).show();
                            workoutsModels.remove(selectedWorkout);
                            adapter.notifyItemRemoved(position);
                            adapter.notifyItemRangeChanged(position, workoutsModels.size() - position);
                            fetchWorkoutAndSetupModels();
                        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to remove workout", Toast.LENGTH_SHORT).show());
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        }
    }


    private void fetchWorkoutAndSetupModels(){
        workoutRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.w("FirestoreError", "Listen failed.", error);
                return;
            }

            if (value != null && !value.isEmpty()) {
                workoutsModels.clear(); // Clear existing data
                for (QueryDocumentSnapshot documentSnapshot : value) {
                    String workoutName = documentSnapshot.getId();
                    Map<String, Object> exercisesMap = documentSnapshot.getData();
                    List<String> exercisesList = new ArrayList<>();
                    for (Map.Entry<String, Object> entry : exercisesMap.entrySet()){
                        String exerciseName = entry.getKey();
                        String targetedMuscle = (String) entry.getValue();
                        exercisesList.add(exerciseName + " - " + targetedMuscle);
                    }
                    workoutsModels.add(new WorkoutsModel(workoutName, exercisesList));
                }
                if (!workoutsModels.isEmpty()) {
                    if (rvWorkout != null && rvWorkout.getAdapter() != null) {
                        rvWorkout.getAdapter().notifyDataSetChanged();
                    }
                }
            }
        });
    }
}