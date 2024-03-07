package com.example.mytracker;

public class TemplateModel {
    String exerciseName, targetedMuscle;

    public TemplateModel(String exerciseName, String targetedMuscle) {
        this.exerciseName = exerciseName;
        this.targetedMuscle = targetedMuscle;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public String getTargetedMuscle() {
        return targetedMuscle;
    }
}
