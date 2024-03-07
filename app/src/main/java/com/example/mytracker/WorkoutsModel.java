package com.example.mytracker;

import java.util.List;

public class WorkoutsModel {
    String workoutName;
    List<String> exercises;

    public WorkoutsModel(String workoutName, List<String> exercises) {
        this.workoutName = workoutName;
        this.exercises = exercises;
    }

    public String getWorkoutName() {
        return workoutName;
    }

    public List<String> getExercises() {
        return exercises;
    }
}
