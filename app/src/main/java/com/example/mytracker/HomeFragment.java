package com.example.mytracker;

import android.animation.ObjectAnimator;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;


public class HomeFragment extends Fragment {
    private FrameLayout overlay;
    private FloatingActionButton[] otherFabs;
    private TextView tvWelcome;

    private boolean areFabsVisible = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // Find views
        overlay = view.findViewById(R.id.overlay);
        tvWelcome = view.findViewById(R.id.tv_welcome);
        FloatingActionButton fabMain = view.findViewById(R.id.fab_main);
        otherFabs = new FloatingActionButton[]{
                view.findViewById(R.id.fab_breakfast),
                view.findViewById(R.id.fab_lunch),
                view.findViewById(R.id.fab_dinner),
                view.findViewById(R.id.fab_snack),
                view.findViewById(R.id.fab_workout),
        };
        // Set click listener for FAB
        fabMain.setOnClickListener(v -> {
            if (areFabsVisible) {
                hideFabs();
            } else {
                hideFabs();
                showFabs();

            }
        });
        for (FloatingActionButton fab : otherFabs) {
            fab.setOnClickListener(v -> hideFabs());
        }
        return view;
    }
    private void showFabs() {
        areFabsVisible = true;
        for (FloatingActionButton otherFab : otherFabs) {
            overlay.setVisibility(View.VISIBLE);
            overlay.animate().alpha(0.8f).setDuration(1).start();
            otherFab.setVisibility(View.VISIBLE);

            // Translate animation
            ObjectAnimator translationAnimator = ObjectAnimator.ofFloat(otherFab, "translationY", otherFab.getHeight(), 0);
            translationAnimator.setDuration(300);
            translationAnimator.start();

            // Alpha animation
            ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(otherFab, "alpha", 0f, 1f);
            alphaAnimator.setDuration(800);
            alphaAnimator.start();
        }
    }
    private void hideFabs() {
        areFabsVisible = false;
        overlay.animate().alpha(0f).setDuration(300).withEndAction(() -> overlay.setVisibility(View.GONE)).start();
        for (FloatingActionButton otherFab : otherFabs) {
            // Translate animation
            ObjectAnimator translationAnimator = ObjectAnimator.ofFloat(otherFab, "translationY", 0, otherFab.getHeight());
            translationAnimator.setDuration(300);
            translationAnimator.start();

            // Alpha animation
            ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(otherFab, "alpha", 1f, 0f);
            alphaAnimator.setDuration(800);
            alphaAnimator.start();
        }
    }
}
