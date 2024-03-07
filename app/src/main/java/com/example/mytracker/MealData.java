package com.example.mytracker;


public class MealData {
    private int Kcal, mealType;

    public MealData(int Kcal, int mealType) {
        this.Kcal = Kcal;
        this.mealType = mealType;
    }

    public int getKcal() {
        return Kcal;
    }

    public int getMeal() {
        return mealType;
    }

    public void resetMeal() {
        Kcal = 0;
        mealType = 0;
    }
}
