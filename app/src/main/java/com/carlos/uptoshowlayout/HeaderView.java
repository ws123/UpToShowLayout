package com.carlos.uptoshowlayout;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.carlos.uptoshow.mylibrary.IHeaderView;

/**
 * Created by carlos on 2016/6/3.
 * 刷新头View
 */
public class HeaderView implements IHeaderView {
    private View view;


    public HeaderView(Context context, ViewGroup parent) {
        this.view = LayoutInflater.from(context).inflate(R.layout.refresh_header, parent, false);
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public void outThreshold(final View view) {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 180);
        animator.setTarget(view.findViewById(R.id.imageView));
        animator.setDuration(100).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.findViewById(R.id.imageView).setRotation((Float) animation.getAnimatedValue());
            }
        });
        ((TextView) view.findViewById(R.id.textView)).setText("松手刷新");
    }

    @Override
    public void inThreshold(final View view) {
        ValueAnimator animator = ValueAnimator.ofFloat(180, 0);
        animator.setTarget(view.findViewById(R.id.imageView));
        animator.setDuration(100).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.findViewById(R.id.imageView).setRotation((Float) animation.getAnimatedValue());
            }
        });
        ((TextView) view.findViewById(R.id.textView)).setText("下拉刷新");
    }

    @Override
    public void startRefreshing(View view) {
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        RotateAnimation rotateAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setRepeatMode(Animation.RESTART);
        imageView.setPivotX(imageView.getWidth() / 2);
        imageView.setPivotY(imageView.getHeight() / 2);
        rotateAnimation.setRepeatCount(999);
        imageView.startAnimation(rotateAnimation);
        ((TextView) view.findViewById(R.id.textView)).setText("正在刷新");
    }

    @Override
    public void stopRefreshing(View view) {
        View imageView = view.findViewById(R.id.imageView);
        imageView.setRotation(0);
        imageView.clearAnimation();
        ((TextView) view.findViewById(R.id.textView)).setText("下拉刷新");
    }
}
