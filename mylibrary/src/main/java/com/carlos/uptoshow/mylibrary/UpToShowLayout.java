package com.carlos.uptoshow.mylibrary;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by carlos on 2016/6/2.
 * 一个可以上拉显示新View的布局
 */
public class UpToShowLayout extends RelativeLayout {
    int lastMoveY = 0;
    boolean isTime = false;
    private int translateY = 0;
    //第二个view是否显示中
    private boolean isSecondShow = false;
    private boolean isTimeToRefresh = false;
    private boolean isRefreshing = false;

    private ViewChangeListener viewChangeListener;

    public UpToShowLayout(Context context) {
        super(context);
    }

    public UpToShowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UpToShowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        isTwoChild();
        getChildAt(0).layout(0, 0, getWidth(), getHeight());
        getChildAt(1).layout(0, getHeight(), getWidth(), getHeight() * 2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 检测这个layout是否含有两个view
     */
    private void isTwoChild() {
        if (getChildCount() != 2) {
            throw new CarlosException("this layout is limited to have two childView");
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isRefreshing) return super.onInterceptTouchEvent(ev);
        if (isSecondShow) {
            if (!ViewCompat.canScrollVertically(getChildAt(1), -1)) {
                switch (ev.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        translateY = 0;
                        lastMoveY = (int) ev.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (lastMoveY < ev.getY()) {
                            isTime = true;
                            return true;
                        } else {
                            return false;
                        }
                    case MotionEvent.ACTION_UP:
                        isTime = false;
                        lastMoveY = 0;
                        translateY = 0;
                        break;
                }
            }
        } else {
            if (!ViewCompat.canScrollVertically(getChildAt(0), 1)) {
                switch (ev.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        translateY = 0;
                        lastMoveY = (int) ev.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (lastMoveY > ev.getY()) {
                            isTime = true;
                            return true;
                        } else {
                            return false;
                        }
                    case MotionEvent.ACTION_UP:
                        isTime = false;
                        lastMoveY = 0;
                        translateY = 0;
                        break;
                }
            } else if (!ViewCompat.canScrollVertically(getChildAt(0), -1)) {
                switch (ev.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        translateY = 0;
                        lastMoveY = (int) ev.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (lastMoveY < ev.getY()) {
                            isTimeToRefresh = true;
                            return true;
                        } else {
                            return false;
                        }
                    case MotionEvent.ACTION_UP:
                        isTimeToRefresh = false;
                        lastMoveY = 0;
                        translateY = 0;
                        break;
                }
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isSecondShow) {
            return secondViewTouchEvent(event);
        } else {
            if (isTimeToRefresh) {
                return pullToRefresh(event);
            } else {
                return firstViewTouchEvent(event);
            }
        }
    }

    private boolean secondViewTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                translateY = (int) (event.getY() - lastMoveY) / 3;
                getChildAt(1).setTranslationY(translateY - getHeight());
                break;
            case MotionEvent.ACTION_UP:
                isTime = false;
                if (getChildAt(1).getTranslationY() < 200 - getHeight()) {
                    ValueAnimator animator = ValueAnimator.ofFloat(getChildAt(1).getTranslationY(), -getHeight());
                    animator.setTarget(getChildAt(1));
                    animator.setDuration(500).start();
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            getChildAt(1).setTranslationY((Float) animation.getAnimatedValue());
                        }
                    });
                } else {
                    if (viewChangeListener != null) viewChangeListener.hideBeforeAnim();
                    ValueAnimator animator = ValueAnimator.ofFloat(getChildAt(1).getTranslationY(), 0);
                    animator.setTarget(getChildAt(1));
                    animator.setDuration(800).start();
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            getChildAt(1).setTranslationY((Float) animation.getAnimatedValue());
                        }
                    });
                    isSecondShow = false;
                    if (viewChangeListener != null) viewChangeListener.hideAfterAnim();
                }
                translateY = 0;
                break;
        }
        return super.onTouchEvent(event);
    }

    private boolean pullToRefresh(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                translateY = (int) (event.getY() - lastMoveY) / 3;
                if (translateY < 0) {
                    translateY = 0;
                    return super.onTouchEvent(event);
                }
                getChildAt(0).setTranslationY(translateY);
                break;
            case MotionEvent.ACTION_UP:
                isTimeToRefresh = false;
                if (getChildAt(0).getTranslationY() > 200) {
                    isRefreshing = true;
                    //达到了下拉刷新的标准
                    ValueAnimator animator = ValueAnimator.ofFloat(getChildAt(0).getTranslationY(), 200);
                    animator.setTarget(getChildAt(0));
                    animator.setDuration((long) ((getChildAt(0).getTranslationY() - 200) * 2)).start();
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            getChildAt(0).setTranslationY((Float) animation.getAnimatedValue());
                        }
                    });
                } else {
                    ValueAnimator animator = ValueAnimator.ofFloat(getChildAt(0).getTranslationY(), 0);
                    animator.setTarget(getChildAt(0));
                    animator.setDuration((long) (getChildAt(0).getTranslationY() * 2)).start();
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            getChildAt(0).setTranslationY((Float) animation.getAnimatedValue());
                        }
                    });
                }
                translateY = 0;
                break;
            case MotionEvent.ACTION_CANCEL:
                translateY = 0;
                break;
        }
        return super.onTouchEvent(event);
    }

    private boolean firstViewTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                translateY = (int) (event.getY() - lastMoveY) / 3;
                getChildAt(1).setTranslationY(translateY);
                break;
            case MotionEvent.ACTION_UP:
                isTime = false;
                if (getChildAt(1).getTranslationY() > -200) {
                    ValueAnimator animator = ValueAnimator.ofFloat(getChildAt(1).getTranslationY(), 0);
                    animator.setTarget(getChildAt(1));
                    animator.setDuration(500).start();
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            getChildAt(1).setTranslationY((Float) animation.getAnimatedValue());
                        }
                    });
                } else {
                    if (viewChangeListener != null) viewChangeListener.showBeforeAnim();
                    ValueAnimator animator = ValueAnimator.ofFloat(getChildAt(1).getTranslationY(), -getHeight());
                    animator.setTarget(getChildAt(1));
                    animator.setDuration(800).start();
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            getChildAt(1).setTranslationY((Float) animation.getAnimatedValue());
                        }
                    });
                    isSecondShow = true;
                    if (viewChangeListener != null) viewChangeListener.showAfterAnim();
                }
                translateY = 0;
                break;
        }
        return super.onTouchEvent(event);
    }

    public void setViewChangeListener(ViewChangeListener viewChangeListener) {
        this.viewChangeListener = viewChangeListener;
    }

    public void stopRefreshing() {
        ValueAnimator animator = ValueAnimator.ofFloat(getChildAt(0).getTranslationY(), 0);
        animator.setTarget(getChildAt(0));
        animator.setDuration((long) (getChildAt(0).getTranslationY() * 2)).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                getChildAt(0).setTranslationY((Float) animation.getAnimatedValue());
            }
        });
        isRefreshing = false;
    }

    interface ViewChangeListener {
        /**
         * 第二个View出现
         * SecondView出现会有一个动画，这个方法会在动画结束后调用
         */
        void showAfterAnim();

        /**
         * 第二个View出现
         * SecondView出现会有一个动画，这个方法会在动画开始前调用
         */
        void showBeforeAnim();

        /**
         * 第二个View隐藏
         * SecondView隐藏会有一个动画，这个方法在动画结束后调用
         */
        void hideAfterAnim();

        /**
         * 第二个View隐藏
         * SecondView隐藏时会有一个动画，这个方法将在动画开始前调用
         */
        void hideBeforeAnim();
    }
}
