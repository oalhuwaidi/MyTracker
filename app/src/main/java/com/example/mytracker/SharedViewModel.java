package com.example.mytracker;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private MutableLiveData<MealData> mealData = new MutableLiveData<>();

    public void setMealData(int Kcal, int mealType) {
        mealData.setValue(new MealData(Kcal, mealType));
    }

    public LiveData<MealData> getMealData() {
        return mealData;
    }
}
