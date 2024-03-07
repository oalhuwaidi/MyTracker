package com.example.mytracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

public class MealDialog extends AppCompatDialogFragment {
    private EditText editTextMeal;
    private SharedViewModel viewModel;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.meal_input_dialog, null);
        editTextMeal = view.findViewById(R.id.edit_meal);

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        assert getArguments() != null;
        int temp = getArguments().getInt("clickedButtonId");
        String mealName = "QUICK TRACK";
        if (temp==1) mealName = "Enter Breakfast";
        else if (temp==2) mealName = "Enter Lunch";
        else if (temp==3) mealName = "Enter Dinner";
        else if (temp==4) mealName = "Enter Snacks";

        builder.setView(view).setTitle(mealName)
                .setNegativeButton("cancel", (dialog, which) -> {

                })
                .setPositiveButton("Enter", (dialog, which) -> {
                    viewModel.setMealData(Integer.parseInt(editTextMeal.getText().toString()),
                            getArguments().getInt("clickedButtonId"));
                    dismiss();
                });


        return builder.create();
    }
}
