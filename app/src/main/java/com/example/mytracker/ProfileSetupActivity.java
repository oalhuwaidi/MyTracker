package com.example.mytracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileSetupActivity extends AppCompatActivity {
    private TextView tv1, tvActivity;
    private NumberPicker numpicker;
    private String goal, gender;
    private int age, weight, height, goal_weight;
    private Button btnEnter;
    private RadioGroup radioGroupGender, radioGroupActivity, radioGroupGoal;
    private ConstraintLayout genderInput;
    private LinearLayout activityInput;
    private boolean isGender, isAge, isHeight, isWeight, isActivity, isGoal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);
        tv1 = findViewById(R.id.tv1);
        tvActivity = findViewById(R.id.tv_light);
        numpicker = findViewById(R.id.numpicker);
        btnEnter = findViewById(R.id.btn_enter);
        radioGroupGender = findViewById(R.id.radio_group_gender);
        radioGroupActivity = findViewById(R.id.radio_group_activity);
        radioGroupGoal = findViewById(R.id.radio_group_goal);
        // to put gone/visable/animate
        genderInput = findViewById(R.id.gender_input);
        activityInput = findViewById(R.id.activity_input);


        isGender = isAge = isHeight = isWeight = isActivity = isGoal = true;

        Log.d("ß", "onCreate: "+isGender);

        numpicker.setMinValue(0);
        numpicker.setMaxValue(100);
        numpicker.setValue(18);

        ObjectAnimator fadeOutAnimator = ObjectAnimator.ofFloat(tv1, "alpha", 1f, 0f);
        fadeOutAnimator.setDuration(500);
        ObjectAnimator fadeOutAnimator2 = ObjectAnimator.ofFloat(genderInput, "alpha", 1f, 0f);
        fadeOutAnimator2.setDuration(500);
        ObjectAnimator fadeOutActivity = ObjectAnimator.ofFloat(tvActivity, "alpha", 1f, 0f);
        fadeOutActivity.setDuration(500);
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        if (isGender){
                            int selectedRadioButtonId = radioGroupGender.getCheckedRadioButtonId();
                            if (selectedRadioButtonId != -1) {
                                if (selectedRadioButtonId == R.id.radio_male) {
                                    gender = "male";
                                } else if (selectedRadioButtonId == R.id.radio_female) {
                                    gender = "female";
                                }
                                fadeOutView(genderInput);
                                fadeInView(numpicker);
                                isGender = false;
                            } else {
                                Toast.makeText(getApplicationContext(), "Please select an option", Toast.LENGTH_SHORT).show();
                            }

                        } else if (isAge) {
                            age = numpicker.getValue();
                            fadeOutView(numpicker);
                            fadeInView(numpicker);
                            isAge = false;
                            return;
                        } else if (isWeight) {
                            weight = numpicker.getValue();
                            fadeOutView(numpicker);
                            fadeInView(numpicker);
                            isWeight = false;
                            return;
                        } else if (isHeight) {
                            height = numpicker.getValue();

                            fadeOutView(numpicker);
                            fadeInView(activityInput);
                            isHeight = false;
                            return;
                        } else if (isActivity) {


                            isActivity = false;
                            return;
                        } else if (isGoal) {
                            int selectedRadioButtonId = radioGroupGoal.getCheckedRadioButtonId();
                            if (selectedRadioButtonId != -1) {
                                if (selectedRadioButtonId == R.id.radio_male) {
                                    gender = "male";
                                } else if (selectedRadioButtonId == R.id.radio_female) {
                                    gender = "female";
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Please select an option", Toast.LENGTH_SHORT).show();}
                        }


            }
        });
        radioGroupActivity.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_light) {
                    tvActivity.setText(R.string.light_activity_desc);
                    tvActivity.setVisibility(View.VISIBLE);
                    fadeInView(tvActivity);
                } else if (checkedId == R.id.radio_moderate) {
                    tvActivity.setText(R.string.moderate_activity_desc);
                    tvActivity.setVisibility(View.VISIBLE);
                    fadeInView(tvActivity);
                } else if (checkedId == R.id.radio_active) {
                    tvActivity.setText(R.string.active_activity_desc);
                    tvActivity.setVisibility(View.VISIBLE);
                    fadeInView(tvActivity);
                }
            }
        });
    }
    private void fadeInView(View view) {
        view.setVisibility(View.VISIBLE);
        ObjectAnimator fadeInAnimator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        fadeInAnimator.setDuration(500);
        fadeInAnimator.start();
    }
    private void fadeOutView(View view) {
        view.setVisibility(View.INVISIBLE);
        ObjectAnimator fadeOutActivity = ObjectAnimator.ofFloat(tvActivity, "alpha", 1f, 0f);
        fadeOutActivity.setDuration(500);
        fadeOutActivity.start();
    }
}