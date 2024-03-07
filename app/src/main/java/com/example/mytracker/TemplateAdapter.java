package com.example.mytracker;


import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TemplateAdapter extends RecyclerView.Adapter<TemplateAdapter.MyViewHolder> {
    Context context;
    ArrayList<TemplateModel> templateModels;
    private SparseBooleanArray checkedStates = new SparseBooleanArray();


    public TemplateAdapter(Context context, ArrayList<TemplateModel> templateModels){
        this.context = context;
        this.templateModels = templateModels;
    }

    @NonNull
    @Override
    public TemplateAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.template_card, parent, false);
        return new TemplateAdapter.MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull TemplateAdapter.MyViewHolder holder, int position) {
        holder.tvExerciseName.setText(templateModels.get(position).getExerciseName());
        holder.tvMuscleTargeted.setText(templateModels.get(position).getTargetedMuscle());

        holder.cb_1.setOnCheckedChangeListener(null);
        holder.cb_1.setChecked(checkedStates.get(position, false));
        holder.cb_1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            TemplateModel currentItem = templateModels.get(holder.getBindingAdapterPosition()); // Use getAdapterPosition() for the current position
            checkedStates.put(position, isChecked);

            if (listener != null) {
                listener.onCheckboxClicked(currentItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return templateModels.size();
    }

    public void filterList(ArrayList<TemplateModel> filteredList) {
        // Conceptual: You would need to adjust checked states here based on actual items remaining
        // This snippet won't directly solve the issue but highlights where logic needs adjustment
        SparseBooleanArray newCheckedStates = new SparseBooleanArray();
        for (int i = 0; i < filteredList.size(); i++) {
            TemplateModel item = filteredList.get(i);
            int originalIndex = templateModels.indexOf(item);
            newCheckedStates.put(i, checkedStates.get(originalIndex, false));
        }

        // Assuming a direct translation of state from original to filtered list indices
        checkedStates = newCheckedStates;

        templateModels = filteredList;
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvExerciseName, tvMuscleTargeted;
        CheckBox cb_1;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cb_1 = itemView.findViewById(R.id.cb_1);
            tvExerciseName = itemView.findViewById(R.id.tv_exercise_name);
            tvMuscleTargeted = itemView.findViewById(R.id.tv_muscleTargeted);
        }
    }
    // Interface for CheckBox click listener
    public interface OnCheckboxClickListener {
        void onCheckboxClicked(TemplateModel item);
    }

    private OnCheckboxClickListener listener;

    public void setOnCheckboxClickListener(OnCheckboxClickListener listener) {
        this.listener = listener;
    }
}
