package com.ji.demo;

import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AnimateFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_animate, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((ImageView) view.findViewById(R.id.halo)).setImageDrawable(getResources().getDrawable(R.drawable.halo));
        ((ImageView) view.findViewById(R.id.home)).setImageDrawable(getResources().getDrawable(R.drawable.ic_sysbar_home));

        ((ImageView) view.findViewById(R.id.halo_opa)).setImageDrawable(getResources().getDrawable(R.drawable.halo));
        ((ImageView) view.findViewById(R.id.home_opa)).setImageDrawable(getResources().getDrawable(R.drawable.ic_sysbar_opa_home));

        Drawable drawable = getResources().getDrawable(R.drawable.ic_face_scanning);
        ((ImageView) view.findViewById(R.id.animate)).setImageDrawable(drawable);

        final AnimatedVectorDrawable animation = (AnimatedVectorDrawable) drawable;
        view.findViewById(R.id.animate_start).setOnClickListener(v -> {
            animation.start();
            animation.registerAnimationCallback(new Animatable2.AnimationCallback() {
                @Override
                public void onAnimationStart(Drawable drawable) {

                }

                @Override
                public void onAnimationEnd(Drawable drawable) {
                    animation.start();
                }
            });
        });
        view.findViewById(R.id.animate_stop).setOnClickListener(v -> {
            animation.stop();
            animation.clearAnimationCallbacks();
        });
    }
}
