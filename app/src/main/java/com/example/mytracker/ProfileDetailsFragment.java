package com.example.mytracker;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Objects;


public class ProfileDetailsFragment extends Fragment {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String userId, username, gender, activityLevel, goal;
    private Long age, height, weight;
    private DocumentReference docRef;
    private TextInputLayout textLayoutUsername, dialogLayout;
    private NumberPicker genderPicker, activityPicker;
    private LinearLayout layoutAge, layoutHeight, layoutWeight, layoutGender, layoutActivity, layoutGoal;
    private EditText dialogEdit, editUsername, editPassword, editEmail;
    private TextView tvAge, tvHeight, tvWeight, tvGender, tvActivity, tvGoal;
    private Button btnUpdateProfile;
    private ListenerRegistration listenerRegistration;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_details, container, false);
        textLayoutUsername = view.findViewById(R.id.text_layout_username);
        btnUpdateProfile = view.findViewById(R.id.btn_update_profile);
        layoutAge = view.findViewById(R.id.layout_age);
        layoutHeight = view.findViewById(R.id.layout_height);
        layoutWeight = view.findViewById(R.id.layout_weight);
        layoutGender = view.findViewById(R.id.layout_gender);
        layoutActivity = view.findViewById(R.id.layout_activity);
        layoutGoal = view.findViewById(R.id.layout_goal);
        //tv
        tvAge = view.findViewById(R.id.tv_age);
        tvHeight = view.findViewById(R.id.tv_height);
        tvWeight = view.findViewById(R.id.tv_weight);
        tvGender = view.findViewById(R.id.tv_gender);
        tvActivity = view.findViewById(R.id.tv_activity);
        tvGoal = view.findViewById(R.id.tv_goal);
        // input
        editUsername = view.findViewById(R.id.edit_username);
        editPassword = view.findViewById(R.id.edit_password);
        editEmail = view.findViewById(R.id.edit_email);
        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userId = mAuth.getCurrentUser().getUid();
        docRef = db.collection("users").document(userId);
        listenerRegistration  = docRef.addSnapshotListener((value, error) -> {
            if (value != null && value.exists()) {
                username = value.getString("username");
                gender = value.getString("gender");
                activityLevel = value.getString("activityLevel");
                age = value.getLong("age");
                height = value.getLong("height");
                weight = value.getLong("weight");
                goal = value.getString("goal");
                updateUI();
                calculateMacros(age, height, weight, gender, activityLevel, goal);
            }
        });
       layoutAge.setOnClickListener(v -> {
           View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_layout_edittext, null);
           dialogLayout = view1.findViewById(R.id.dialog_layout);
           dialogEdit = view1.findViewById(R.id.dialog_edit);
           dialogLayout.setHint("Enter age");
           dialogLayout.setPlaceholderText(age.toString());
           AlertDialog alertDialog = new MaterialAlertDialogBuilder(getActivity()).setTitle("Age").setView(view1)
                   .setPositiveButton("OK", (dialog, which) -> {
                        int age = Integer.parseInt(dialogEdit.getText().toString());
                        docRef.update("age", age);
                   }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).create();
           alertDialog.show();
       });
       layoutHeight.setOnClickListener(v -> {
           View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_layout_edittext, null);
           dialogLayout = view1.findViewById(R.id.dialog_layout);
           dialogEdit = view1.findViewById(R.id.dialog_edit);
           dialogLayout.setHint("Enter height");
           dialogLayout.setPlaceholderText(height.toString());
           AlertDialog alertDialog = new MaterialAlertDialogBuilder(getActivity()).setTitle("Height").setView(view1)
                   .setPositiveButton("OK", (dialog, which) -> {
                       int height = Integer.parseInt(dialogEdit.getText().toString());
                       docRef.update("height", height);
                   }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).create();
           alertDialog.show();
       });
       layoutWeight.setOnClickListener(v -> {
           View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_layout_edittext, null);
           dialogLayout = view1.findViewById(R.id.dialog_layout);
           dialogEdit = view1.findViewById(R.id.dialog_edit);
           dialogLayout.setHint("Enter current weight");
           dialogLayout.setPlaceholderText(weight.toString());
           AlertDialog alertDialog = new MaterialAlertDialogBuilder(getActivity()).setTitle("Weight").setView(view1)
                   .setPositiveButton("OK", (dialog, which) -> {
                       int weight = Integer.parseInt(dialogEdit.getText().toString());
                       docRef.update("weight", weight);
                   }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).create();
           alertDialog.show();
       });
       layoutGender.setOnClickListener(v -> {
           View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_layout_radio, null);
           RadioButton r1 = view1.findViewById(R.id.radio1);
           RadioButton r2 = view1.findViewById(R.id.radio2);
           RadioButton r3 = view1.findViewById(R.id.radio3);
           r1.setText("Male");
           r2.setText("Female");
           r3.setVisibility(View.GONE);
           AlertDialog alertDialog = new MaterialAlertDialogBuilder(getActivity()).setTitle("Activity level").setView(view1)
                   .setPositiveButton("OK", (dialog, which) -> {
                       RadioGroup rg = view1.findViewById(R.id.radio_group);
                       int selectedRadio = rg.getCheckedRadioButtonId();
                       if (selectedRadio == R.id.radio1) gender = "male";
                       else if (selectedRadio == R.id.radio2) gender = "female";
                       docRef.update("gender", gender);
                   }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).create();
           alertDialog.show();
       });
       layoutActivity.setOnClickListener(v -> {
           View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_layout_radio, null);
           RadioButton r1 = view1.findViewById(R.id.radio1);
           RadioButton r2 = view1.findViewById(R.id.radio2);
           RadioButton r3 = view1.findViewById(R.id.radio3);
           r1.setText("Light");
           r2.setText("Moderate");
           r3.setText("Active");
           TextView tvDescription = view1.findViewById(R.id.tv_description);
           r1.setOnClickListener(v1 -> {fadeOutInViewTv(tvDescription, R.string.light_activity_desc);});
           r2.setOnClickListener(v1 -> {fadeOutInViewTv(tvDescription, R.string.moderate_activity_desc);});
           r3.setOnClickListener(v1 -> {fadeOutInViewTv(tvDescription, R.string.active_activity_desc);});
           AlertDialog alertDialog = new MaterialAlertDialogBuilder(getActivity()).setTitle("Activity level").setView(view1)
                   .setPositiveButton("OK", (dialog, which) -> {
                       RadioGroup rg = view1.findViewById(R.id.radio_group);
                       int selectedRadio = rg.getCheckedRadioButtonId();
                       if (selectedRadio == R.id.radio1) activityLevel = "light";
                       else if (selectedRadio == R.id.radio2) activityLevel = "moderate";
                       else if (selectedRadio == R.id.radio3) activityLevel = "active";
                       docRef.update("activityLevel", activityLevel);
                   }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).create();
           alertDialog.show();
       });
       layoutGoal.setOnClickListener(v -> {
           View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_layout_radio, null);

           AlertDialog alertDialog = new MaterialAlertDialogBuilder(getActivity()).setTitle("Goal").setView(view1)
                   .setPositiveButton("OK", (dialog, which) -> {
                       RadioGroup rg = view1.findViewById(R.id.radio_group);
                       int selectedRadio = rg.getCheckedRadioButtonId();
                        if (selectedRadio == R.id.radio1) goal = "bulk";
                        else if (selectedRadio == R.id.radio2) goal = "cut";
                        else if (selectedRadio == R.id.radio3) goal = "maintain";
                        docRef.update("goal", goal);
                   }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).create();
           alertDialog.show();
       });

       btnUpdateProfile.setOnClickListener(v -> {
           String tempUsername = editUsername.getText().toString();
           String tempEmail = editEmail.getText().toString();
           String tempPassword = editPassword.getText().toString();
           if (!tempUsername.isEmpty()) docRef.update("username", tempUsername);
           if (!tempEmail.isEmpty()) {
               user.verifyBeforeUpdateEmail(tempEmail).addOnCompleteListener(task -> {
                   Toast.makeText(getActivity(), "Email updated", Toast.LENGTH_SHORT).show();
                   docRef.update("email", tempEmail);
               });
           }
           if (!tempPassword.isEmpty()) user.updatePassword(tempPassword).addOnCompleteListener(task -> {
               Toast.makeText(getActivity(), "Password updated", Toast.LENGTH_SHORT).show();
           });
       });

        return view;
    }

    private void updateUI() {
        textLayoutUsername.setPlaceholderText(username);
        tvAge.setText(age.toString());
        tvHeight.setText(height.toString());
        tvWeight.setText(weight.toString());
        tvGender.setText(gender);
        tvActivity.setText(activityLevel);
        tvGoal.setText(goal);
    }
    private void calculateMacros(Long age, Long height, Long weight, String gender, String activityLevel, String goal) {
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

        docRef.update("totalKcal", totalCalories);
        docRef.update("protein", protein);
        docRef.update("fat", fat);
        docRef.update("carbs", carbs);
    }
    private void fadeOutInViewTv(TextView view, int x) {
        view.animate().alpha(0f).setDuration(100).withEndAction(() -> {
            view.setText(x);
            view.setVisibility(View.VISIBLE);
            view.animate().alpha(1f).setDuration(500).start();
        }).start();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
}