package com.example.mytracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class ProfileFragment extends Fragment {
    private FirebaseAuth mAuth;
    private LinearLayout personalDetails;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private String userId, username, goal, activityLevel;
    private Long age, weight;
    private DocumentReference docRef;
    private ListenerRegistration listenerRegistration;
    private TextView tvWeight, tvGoal, tvUsername;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userId = mAuth.getCurrentUser().getUid();
        docRef = db.collection("users").document(userId);
        listenerRegistration  = docRef.addSnapshotListener((value, error) -> {
            if (value != null && value.exists()) {
                username = value.getString("username");
                activityLevel = value.getString("activityLevel");
                age = value.getLong("age");
                weight = value.getLong("weight");
                goal = value.getString("goal");
                updateUI();
            }
        });

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        tvUsername = view.findViewById(R.id.tv_username);
        tvWeight = view.findViewById(R.id.tv_weight);
        tvGoal = view.findViewById(R.id.tv_goal);
        personalDetails = view.findViewById(R.id.card_personal_details);
        Button btnLogout = view.findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });
        personalDetails.setOnClickListener(v -> {
            ProfileDetailsFragment profileDetailsFragment = new ProfileDetailsFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            androidx.navigation.ui.R.animator.nav_default_pop_enter_anim,
                            androidx.navigation.ui.R.animator.nav_default_pop_exit_anim
                    )
                    .replace(R.id.profile_container, profileDetailsFragment)
                    .addToBackStack(null)
                    .commit();

        });
        return view;
    }

    private void updateUI() {
        tvUsername.setText(username);
        if (goal == "bulk") tvGoal.setText("Gain weight");
        else if (goal == "cut") tvGoal.setText("Lose weight");
        else if (goal == "maintain") tvGoal.setText("Maintain weight");
        tvWeight.setText(weight.toString());
    }
}