package com.cvv.birdsofonefeather.travelperfect.view;

import android.animation.Animator;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.ViewAnimationUtils;

/**
 * Created by Carla
 * Date: 31/12/2016
 * Project: Capstone-Project
 */

public class AnimationUtils {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void fabReveal(FloatingActionButton fab, final View view, AnimationAdapter animationAdapter) {

        float finalRadius = (float) Math.hypot(view.getWidth(), view.getHeight());

        view.setVisibility(View.VISIBLE);
        Animator anim = ViewAnimationUtils.createCircularReveal(view, (int) fab.getX(), (int) fab.getY(), 0, finalRadius);
        anim.addListener(animationAdapter);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.setVisibility(View.INVISIBLE);
                    }
                }, 200);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }

    public static class AnimationAdapter implements Animator.AnimatorListener {
        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }
}
