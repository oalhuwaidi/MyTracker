package com.example.mytracker;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WorkoutsAdapter extends RecyclerView.Adapter<WorkoutsAdapter.MyViewHolder> {
    Context context;
    ArrayList<WorkoutsModel> workoutsModels;
    private OnItemClickListener listener;

    public WorkoutsAdapter(Context context, ArrayList<WorkoutsModel> workoutsModels, OnItemClickListener listener){
        this.context = context;
        this.workoutsModels = workoutsModels;
        this.listener = listener;
    }
    @NonNull
    @Override
    public WorkoutsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.workout_card, parent, false);
        return new WorkoutsAdapter.MyViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutsAdapter.MyViewHolder holder, int position) {
        Log.d("WorkoutsAdapter", "Binding view at position: " + position);
        WorkoutsModel model = workoutsModels.get(position);
        holder.workoutName.setText(model.getWorkoutName());

        String exercisesText = TextUtils.join("\n", model.getExercises());
        holder.exerciseName.setText(exercisesText);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return workoutsModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView workoutName;
        TextView exerciseName;
        public MyViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            workoutName = itemView.findViewById(R.id.workout_name);
            exerciseName = itemView.findViewById(R.id.exercise_name);

            itemView.setOnLongClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemLongClick(position);
                    return true;
                }
                return false;
            });
        }
    }
    public interface OnItemClickListener {
        void onItemClick(WorkoutsModel workoutsModel);
        void onItemLongClick(int position);
    }
}
