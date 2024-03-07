package com.example.mytracker;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class HomeFragment extends Fragment {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private DocumentReference docRef, todayMealRef;
    private LocalDate localDate;
    private String userId, username, currentDate, currentDay;
    private TextView tvWelcome, tvKcalToday, tvKcalGoal;
    private TextView tvBreakfastKcal, tvLunchKcal, tvDinnerKcal, tvSnackKcal;
    private TextView tvBreakfastConsumed, tvLunchConsumed, tvDinnerConsumed, tvSnacksConsumed;
    private FloatingActionButton fabBreakfast, fabLunch, fabDinner, fabSnack, fabWorkout, btnAddBreakfast, btnAddLunch, btnAddDinner, btnAddSnack;
    int breakfastMinGoal,breakfastMaxGoal, lunchMinGoal, lunchMaxGoal, dinnerMinGoal, dinnerMaxGoal, snacksMinGoal, snacksMaxGoal;
    int totalKcalBreakfast, totalKcalLunch, totalKcalDinner, totalKcalSnack, totalKcalToday, totalKcalGoal;
    private FloatingActionButton[] otherFabs;
    private boolean areFabsVisible = false;
    private ConstraintSet constraintSet;
    private ConstraintLayout parentLayout;
    private SharedViewModel viewModel;
    private FrameLayout overlay;
    private CircularProgressIndicator pbMain;
    private ScrollView scrollView;
    private String goal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // Initialize Dates
        localDate = LocalDate.now();
        currentDate = LocalDate.now().toString();
        currentDay = localDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        userId = currentUser.getUid();
        docRef = db.collection("users").document(userId);
        todayMealRef = db.collection("users").document(userId).collection("meals").document(currentDate);

        checkForMissingInformation();

        // Find views
        overlay = view.findViewById(R.id.overlay);
        tvWelcome = view.findViewById(R.id.tv_welcome);
        parentLayout = view.findViewById(R.id.parent_layout);
        constraintSet = new ConstraintSet();
        constraintSet.clone(parentLayout);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
//        tvKcalToday = view.findViewById(R.id.tv_kcalToday);
        tvKcalGoal = view.findViewById(R.id.tv_kcalGoal);
        pbMain = view.findViewById(R.id.pb_main);
        tvBreakfastKcal = view.findViewById(R.id.tv_breakfast_kcal);
        tvLunchKcal = view.findViewById(R.id.tv_lunch_kcal);
        tvDinnerKcal = view.findViewById(R.id.tv_dinner_kcal);
        tvSnackKcal = view.findViewById(R.id.tv_snacks_kcal);
        tvBreakfastConsumed = view.findViewById(R.id.tv_breakfast_consumed);
        tvLunchConsumed = view.findViewById(R.id.tv_lunch_consumed);
        tvDinnerConsumed = view.findViewById(R.id.tv_dinner_consumed);
        tvSnacksConsumed = view.findViewById(R.id.tv_snacks_consumed);
        scrollView = view.findViewById(R.id.scrollView);
        // FAB
        FloatingActionButton fabMain = view.findViewById(R.id.fab_main);
        fabBreakfast = view.findViewById(R.id.fab_breakfast);
        fabLunch = view.findViewById(R.id.fab_lunch);
        fabDinner = view.findViewById(R.id.fab_dinner);
        fabSnack  = view.findViewById(R.id.fab_snack);
        fabWorkout = view.findViewById(R.id.fab_workout);
        btnAddBreakfast = view.findViewById(R.id.btn_add_breakfast);
        btnAddLunch = view.findViewById(R.id.btn_add_lunch);
        btnAddDinner = view.findViewById(R.id.btn_add_dinner);
        btnAddSnack = view.findViewById(R.id.btn_add_snacks);

        otherFabs = new FloatingActionButton[]{
                fabBreakfast, fabLunch, fabDinner, fabSnack, fabDinner, fabWorkout
        };

        FloatingActionButton[] btnAddGroup =  new FloatingActionButton[]{
                btnAddBreakfast, btnAddLunch, btnAddDinner, btnAddSnack
        };
        for (FloatingActionButton btn : btnAddGroup){
            btn.setOnClickListener(v -> {
                if (btn.getId() == btnAddBreakfast.getId()) openMealDialog(1);
                else if (btn.getId() == btnAddLunch.getId())openMealDialog(2);
                else if (btn.getId() == btnAddDinner.getId())openMealDialog(3);
                else if (btn.getId() == btnAddSnack.getId())openMealDialog(4);
            });
        }

        fabMain.setOnClickListener(v -> {
            if (areFabsVisible) {
                hideFabs();
            } else {
                showFabs();
            }
        });
        // when any other fab clicked hides
        for (FloatingActionButton fab : otherFabs) {
            int id = fab.getId();
            fab.setOnClickListener(v ->{
                if(id == R.id.fab_breakfast){
                    openMealDialog(1);
                } else if (id == R.id.fab_lunch) {
                    openMealDialog(2);
                }else if (id == R.id.fab_dinner) {
                    openMealDialog(3);
                }else if (id == R.id.fab_snack) {
                    openMealDialog(4);
                }else if (id == R.id.fab_workout) {
                    Toast.makeText(getContext(), "workout", Toast.LENGTH_SHORT).show();
                }
                hideFabs();
            });
        }


        viewModel.getMealData().observe(getViewLifecycleOwner(), mealData -> {
            if (mealData.getKcal() > 0) {
                addMealInput(mealData.getKcal(), mealData.getMeal());
                mealData.resetMeal();
            }
        });

        scrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            final int threshold = 250;
            if (scrollY > threshold) {
                fabMain.setVisibility(View.INVISIBLE);
            } else {
                fabMain.setVisibility(View.VISIBLE);
                float alpha = 1 - (float) scrollY / threshold;
                fabMain.setAlpha(alpha);
            }
        });

        fetchUserDataAndMeals();
        updateUI();


        return view;
    }
    private void checkForMissingInformation() {
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    String goal = documentSnapshot.getString("goal");
                    if (goal == null) {
                        Intent intent = new Intent(getActivity(), ProfileSetupActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }
            }
        });
    }
    private void showFabs() {
        areFabsVisible = true;
        for (FloatingActionButton otherFab : otherFabs) {
            otherFab.setVisibility(View.INVISIBLE);
            otherFab.animate()
                    .translationYBy(parentLayout.getHeight()-otherFab.getHeight())
                    .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(5)
                    .withEndAction(() -> {
                        overlay.setAlpha(0);
                        overlay.setVisibility(View.VISIBLE);
                        overlay.animate().alpha(0.8f).setDuration(500).start();
                        otherFab.setVisibility(View.VISIBLE);
                        otherFab.animate().translationY(0)
                                .setInterpolator(new AccelerateDecelerateInterpolator())
                                .setDuration(500)
                                .start();
                    })
                    .start();
        }
    }
    private void hideFabs() {
        areFabsVisible = false;
        overlay.animate().alpha(0f).setDuration(500).withEndAction(() -> overlay.setVisibility(View.GONE)).start();
        for (FloatingActionButton otherFab : otherFabs) {
            otherFab.animate()
                    .translationYBy(parentLayout.getHeight()-otherFab.getHeight())
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setDuration(500)
                    .withEndAction(() -> otherFab.setVisibility(View.GONE))
                    .start();
        }
    }
    private void openMealDialog(int i) {
        MealDialog mealDialog = new MealDialog();
        Bundle args = new Bundle();
        args.putInt("clickedButtonId", i);
        mealDialog.setArguments(args);
        mealDialog.show(requireActivity().getSupportFragmentManager(), "Meal Dialog");
    }
    private void addMealInput(int Kcal, int mealType){
        Map<String, Object> mealData = new HashMap<>();

        switch (mealType){
            case 1:{
                mealData.put("totalKcalBreakfast", FieldValue.increment(Kcal));
                mealData.put("totalKcalToday", FieldValue.increment(Kcal));
                break;
            }
            case 2:{
                mealData.put("totalKcalLunch", FieldValue.increment(Kcal));
                mealData.put("totalKcalToday", FieldValue.increment(Kcal));
                break;
            }
            case 3:{
                mealData.put("totalKcalDinner", FieldValue.increment(Kcal));
                mealData.put("totalKcalToday", FieldValue.increment(Kcal));
                break;
            }
            case 4:{
                mealData.put("totalKcalSnack", FieldValue.increment(Kcal));
                mealData.put("totalKcalToday", FieldValue.increment(Kcal));
                break;
            }
        }
        mealData.put("day", currentDay);
        mealData.put("date", currentDate);
        mealData.put("timestamp", FieldValue.serverTimestamp());



        todayMealRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()){
                    addMealToExistingDocument(currentDate, mealData);
                    updateUI();
                }
                else {
                    createNewDocumentForDate(currentDate, mealData);
                    updateUI();
                }
            }
        });
    }
    private void createNewDocumentForDate(String currentDate, Map<String, Object> mealData) {
        db.collection("users").document(userId).collection("meals").document(currentDate)
                .set(mealData)
                .addOnSuccessListener(aVoid -> {
                    fetchMealData(todayMealRef);
                    Toast.makeText(getContext(), "Meal Added!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show());
    }
    private void addMealToExistingDocument(String currentDate, Map<String, Object> mealData) {
        db.collection("users").document(userId).collection("meals").document(currentDate)
                .update(mealData)
                .addOnSuccessListener(unused -> {
                    fetchMealData(todayMealRef);
                    Toast.makeText(getContext(), "Meal Added!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show());
    }
    private int getSafeValue(Integer value) {
        return value != null ? value : 0;
    }
    private void updateUI() {
        requireActivity().runOnUiThread(() -> {
            pbMain.setMax(getSafeValue(totalKcalGoal));
            pbMain.setProgress(getSafeValue(totalKcalToday));
            tvKcalGoal.setText(getSafeValue(totalKcalToday) + "/" + getSafeValue(totalKcalGoal));
            tvWelcome.setText(String.format("Hello %s!", username != null ? username : "USER"));
            tvBreakfastKcal.setText(String.format(Locale.US, "Recommended %d - %d kcal", getSafeValue(breakfastMinGoal), getSafeValue(breakfastMaxGoal)));
            tvLunchKcal.setText(String.format(Locale.US, "Recommended %d - %d kcal", getSafeValue(lunchMinGoal), getSafeValue(lunchMaxGoal)));
            tvDinnerKcal.setText(String.format(Locale.US, "Recommended %d - %d kcal", getSafeValue(dinnerMinGoal), getSafeValue(dinnerMaxGoal)));
            tvSnackKcal.setText(String.format(Locale.US, "Recommended %d - %d kcal", getSafeValue(snacksMinGoal), getSafeValue(snacksMaxGoal)));
            tvBreakfastConsumed.setText("consumed: " + getSafeValue(totalKcalBreakfast));
            tvLunchConsumed.setText("consumed: " + getSafeValue(totalKcalLunch));
            tvDinnerConsumed.setText("consumed: " + getSafeValue(totalKcalDinner));
            tvSnacksConsumed.setText("consumed: " + getSafeValue(totalKcalSnack));
        });
    }
    private void fetchUserDataAndMeals() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference userRef = db.collection("users").document(userId);
            DocumentReference todayMealRef = db.collection("users").document(userId).collection("meals").document(currentDate);

            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    username = documentSnapshot.getString("username");
                    Long totalKcalGoalLong = documentSnapshot.getLong("totalKcal");
                    if (totalKcalGoalLong != null) {
                        totalKcalGoal = totalKcalGoalLong.intValue();
                        breakfastMinGoal = (int) (totalKcalGoal * 0.20);
                        breakfastMaxGoal = (int) (totalKcalGoal * 0.25);
                        lunchMinGoal = (int) (totalKcalGoal * 0.25);
                        lunchMaxGoal = (int) (totalKcalGoal * 0.30);
                        dinnerMinGoal = (int) (totalKcalGoal * 0.25);
                        dinnerMaxGoal = (int) (totalKcalGoal * 0.30);
                        snacksMinGoal = (int) (totalKcalGoal * 0.20);
                        snacksMaxGoal = (int) (totalKcalGoal * 0.30);
                    } else {
                        totalKcalGoal = 440; // Default value
                    }
                }
            }).addOnFailureListener(e -> Log.e("fetchUserData", "Error fetching user data", e))
                    .addOnCompleteListener(task -> {
                        updateUI();
                        fetchMealData(todayMealRef);
                    });

        }
    }
    private void fetchMealData(DocumentReference todayMealRef) {
        todayMealRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Long totalKcalTodayLong = documentSnapshot.getLong("totalKcalToday");

                totalKcalToday = totalKcalTodayLong != null ? totalKcalTodayLong.intValue() : 0;
                totalKcalBreakfast = safeLongToInt(documentSnapshot.getLong("totalKcalBreakfast"));
                totalKcalLunch = safeLongToInt(documentSnapshot.getLong("totalKcalLunch"));
                totalKcalDinner = safeLongToInt(documentSnapshot.getLong("totalKcalDinner"));
                totalKcalSnack = safeLongToInt(documentSnapshot.getLong("totalKcalSnack"));

                updateUI();
            }
        }).addOnFailureListener(e -> Log.e("fetchMealData", "Error fetching meal data", e));
    }

    private int safeLongToInt(Long value) {
        return value != null ? value.intValue() : 0;
    }
}