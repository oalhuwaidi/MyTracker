package com.example.mytracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileSetupActivity extends AppCompatActivity {
    private TextView tv1, tvActivity, tvGoal, tvInputDesc;
    private NumberPicker numpicker;
    private String goal, gender, activityLevel;
    private Map<String, Object> user = new HashMap<>();
    private int age, weight, height, goal_weight;
    private Button btnEnter;
    private RadioGroup radioGroupGender, radioGroupActivity, radioGroupGoal;
    private ConstraintLayout genderInput;
    private LinearLayout activityInput;
    private boolean isGender, isAge, isHeight, isWeight, isActivity, isGoal;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);
        tv1 = findViewById(R.id.tv1);
        tvInputDesc= findViewById(R.id.tv_input_desc);
        tvActivity = findViewById(R.id.tv_light);
        tvGoal = findViewById(R.id.tv_goal);
        numpicker = findViewById(R.id.numpicker);
        btnEnter = findViewById(R.id.btn_enter);
        // Radio Groups
        radioGroupGender = findViewById(R.id.radio_group_gender);
        radioGroupActivity = findViewById(R.id.radio_group_activity);
        radioGroupGoal = findViewById(R.id.radio_group_goal);
        // Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        // to put gone/visible/animate
        genderInput = findViewById(R.id.gender_input);
        activityInput = findViewById(R.id.activity_input);

        isGender = isAge = isHeight = isWeight = isActivity = isGoal = true;

        numpicker.setMinValue(0);
        numpicker.setMaxValue(100);
        numpicker.setValue(18);

        btnEnter.setOnClickListener(v -> {
                    if (isGender){
                        int selectedRadioButtonId = radioGroupGender.getCheckedRadioButtonId();
                        if (selectedRadioButtonId != -1) {
                            if (selectedRadioButtonId == R.id.radio_male) {
                                gender = "male";
                            } else if (selectedRadioButtonId == R.id.radio_female) {
                                gender = "female";
                            }
                            fadeOutInViewView(genderInput, numpicker);
                            fadeOutInViewDesc(tvInputDesc, R.string.enter_your_age);
                            isGender = false;
                        } else {
                            Toast.makeText(getApplicationContext(), "Please select an option", Toast.LENGTH_SHORT).show();
                        }

                    } else if (isAge) {
                        age = numpicker.getValue();
                        fadeOutInView(numpicker, 25, 60, 300);
                        fadeOutInViewDesc(tvInputDesc, R.string.enter_your_current_weight);

                        isAge = false;
                    } else if (isWeight) {
                        weight = numpicker.getValue();
                        fadeOutInView(numpicker, 120, 180, 240);
                        fadeOutInViewDesc(tvInputDesc, R.string.enter_your_height);
                        isWeight = false;
                    } else if (isHeight) {
                        height = numpicker.getValue();
                        fadeOutInViewView(numpicker, activityInput);
                        fadeOutInViewDesc(tvInputDesc, R.string.what_is_your_activity_level);

                        isHeight = false;
                    } else if (isActivity) {
                        int selectedRadioButtonId = radioGroupActivity.getCheckedRadioButtonId();
                        if (selectedRadioButtonId != -1) {
                            if (selectedRadioButtonId == R.id.radio_light) {
                                activityLevel = "light";
                            } else if (selectedRadioButtonId == R.id.radio_moderate) {
                                activityLevel = "moderate";
                            }else if (selectedRadioButtonId == R.id.radio_active) {
                                activityLevel = "active";
                            }
                            isActivity = false;
                            fadeOutInViewView(activityInput, radioGroupGoal);
                            fadeOutInViewDesc(tvInputDesc, R.string.what_is_your_goal);
                        } else {Toast.makeText(getApplicationContext(), "Please select an option", Toast.LENGTH_SHORT).show();}
                    }
                    else if (isGoal) {
                        int selectedRadioButtonId = radioGroupGoal.getCheckedRadioButtonId();
                        if (selectedRadioButtonId != -1) {
                            if (selectedRadioButtonId == R.id.goal_bulk) {
                                goal = "bulk";
                            } else if (selectedRadioButtonId == R.id.goal_cut) {
                                goal = "cut";
                            }else if (selectedRadioButtonId == R.id.goal_maintain) {
                                goal = "maintain";
                            }
                            calculateMacros(age, height, weight, gender, activityLevel, goal);
                            user.put("gender", gender);
                            user.put("age", age);
                            user.put("weight", weight);
                            user.put("height", height);
                            user.put("activityLevel", activityLevel);
                            user.put("goal", goal);
                            db.collection("users").document(userId).update(user).addOnSuccessListener(unused -> {
                                fadeOutView(radioGroupGoal, true);
                                fadeOutView(tvInputDesc, true);
                                Toast.makeText(getApplicationContext(), "Profile created successfully", Toast.LENGTH_SHORT).show();
                            });
                            isGoal = false;
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Please select an option", Toast.LENGTH_SHORT).show();}
                    }


        });
        radioGroupActivity.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_light) {
                fadeOutInViewTv(tvActivity, R.string.light_activity_desc);
            } else if (checkedId == R.id.radio_moderate) {
                fadeOutInViewTv(tvActivity, R.string.moderate_activity_desc);
            } else if (checkedId == R.id.radio_active) {
                fadeOutInViewTv(tvActivity, R.string.active_activity_desc);

            }
        });
        radioGroupGoal.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.goal_bulk) {
                fadeOutInViewTv(tvGoal, R.string.bulk_desc);
            } else if (checkedId == R.id.goal_cut) {
                fadeOutInViewTv(tvGoal, R.string.cut_desc);
            } else if (checkedId == R.id.goal_maintain) {
                fadeOutInViewTv(tvGoal, R.string.maintain_desc);

            }
        });
    }
    private void fadeInView(View view) {
        view.setVisibility(View.VISIBLE);
        view.animate().alpha(1f).setDuration(500).start();
    }
    private void fadeOutView(View view, boolean setGone) {
        if(setGone) view.animate().alpha(0f).setDuration(500).withEndAction(() -> view.setVisibility(View.GONE)).start();
        else view.animate().alpha(0f).setDuration(500).start();
    }
    private void fadeOutInViewView(View view1, View view2){
        view2.animate().alpha(0f).start();
        view1.animate().alpha(0f).setDuration(500).withEndAction(() -> {
            view1.setVisibility(View.GONE);
            view2.setVisibility(View.VISIBLE);
            view2.animate().alpha(1f).setDuration(500).start();
        }).start();
    }
    private void fadeOutInView(View view, int x, int y, int z) {
        view.animate().alpha(0f).setDuration(500).withEndAction(new Runnable() {
            @Override
            public void run() {
                numpicker.setMinValue(x);
                numpicker.setValue(y);
                numpicker.setMaxValue(z);
                view.animate().alpha(1f).setDuration(500).start();
            }
        }).start();
    }
    private void fadeOutInViewDesc(TextView view, int x) {
        view.animate().alpha(0f).setDuration(500).withEndAction(new Runnable() {
            @Override
            public void run() {
                view.setText(x);
                view.setVisibility(View.VISIBLE);
                view.animate().alpha(1f).setDuration(500).start();
            }
        }).start();
    }
    private void fadeOutInViewTv(TextView view, int x) {
        view.animate().alpha(0f).setDuration(100).withEndAction(new Runnable() {
            @Override
            public void run() {
                view.setText(x);
                view.setVisibility(View.VISIBLE);
                view.animate().alpha(1f).setDuration(500).start();
            }
        }).start();
    }
    private void fadeOutInView(View view) {
        view.animate().alpha(0f).setDuration(500).withEndAction(() -> view.animate().alpha(1f).setDuration(500).start()).start();
    }
    private void calculateMacros(int age, int height, int weight, String gender, String activityLevel, String goal) {
        // Calculate BMR
        double bmr;
        if (Objects.equals(gender, "male")) {
            bmr = 10 * weight + 6.25 * height - 5 * age + 5;
        } else {
            bmr = 10 * weight + 6.25 * height - 5 * age - 161;
        }

        double totalCalories = 0;


        // Adjust BMR based on activity level
        if (Objects.equals(activityLevel, "light")){
             totalCalories = bmr * 1.2;
        } else if (Objects.equals(activityLevel, "moderate")) {
             totalCalories = bmr * 1.55;
        } else if (Objects.equals(activityLevel, "active")) {
             totalCalories = bmr * 1.9;
        }

        if (Objects.equals(goal, "maintain")){
            return;
        } else if (Objects.equals(goal, "cut")) {
            totalCalories *= 0.8;
        } else if (Objects.equals(goal, "bulk")) {
            totalCalories *= 1.2;
        }

        // Calculate macronutrients (protein, carbs, fat)
        double protein = weight * 2.2 * 0.8; // 0.8 grams per pound of body weight
        double fat = totalCalories * 0.25 / 9; // 25% of total calories from fat, 1 gram of fat = 9 calories
        double carbs = (totalCalories - (protein * 4) - (fat * 9)) / 4; // remaining calories from carbs

        user.put("totalKcal", totalCalories);
        user.put("protein", protein);
        user.put("fat", fat);
        user.put("carbs", carbs);
    }
}