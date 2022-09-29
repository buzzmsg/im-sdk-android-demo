package com.tmmtmm.sdk.ui.view.link.util;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class QMUIViewHelper {

    // copy from View.generateViewId for API <= 16
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);


    private static final int[] APPCOMPAT_CHECK_ATTRS = {
            androidx.appcompat.R.attr.colorPrimary
    };

    public static void checkAppCompatTheme(Context context) {
        TypedArray a = context.obtainStyledAttributes(APPCOMPAT_CHECK_ATTRS);
        final boolean failed = !a.hasValue(0);
        a.recycle();
        if (failed) {
            throw new IllegalArgumentException("You need to use a Theme.AppCompat theme "
                    + "(or descendant) with the design library.");
        }
    }


    public static View getActivityRoot(Activity activity) {
        return ((ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT)).getChildAt(0);
    }


    @SuppressWarnings("deprecation")
    public static void requestApplyInsets(Window window) {
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            window.getDecorView().requestFitSystemWindows();
        } else if (Build.VERSION.SDK_INT >= 21) {
            window.getDecorView().requestApplyInsets();
        }
    }


    public static void expendTouchArea(final View view, final int expendSize) {
        if (view != null) {
            final View parentView = (View) view.getParent();

            parentView.post(new Runnable() {
                @Override
                public void run() {
                    Rect rect = new Rect();
                    view.getHitRect(rect);
                    rect.left -= expendSize;
                    rect.top -= expendSize;
                    rect.right += expendSize;
                    rect.bottom += expendSize;
                    parentView.setTouchDelegate(new TouchDelegate(rect, view));
                }
            });
        }
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void setBackground(View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    public static void setBackgroundKeepingPadding(View view, Drawable drawable) {
        int[] padding = new int[]{view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom()};
        view.setBackground(drawable);
        view.setPadding(padding[0], padding[1], padding[2], padding[3]);
    }

    @SuppressWarnings("deprecation")
    public static void setBackgroundKeepingPadding(View view, int backgroundResId) {
        setBackgroundKeepingPadding(view, ContextCompat.getDrawable(view.getContext(), backgroundResId));
    }

    public static void setBackgroundColorKeepPadding(View view, @ColorInt int color) {
        int[] padding = new int[]{view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom()};
        view.setBackgroundColor(color);
        view.setPadding(padding[0], padding[1], padding[2], padding[3]);
    }


    public static void playBackgroundBlinkAnimation(final View v, @ColorInt int bgColor) {
        if (v == null) {
            return;
        }
        int[] alphaArray = new int[]{0, 255, 0};
        playViewBackgroundAnimation(v, bgColor, alphaArray, 300);
    }


    public static Animator playViewBackgroundAnimation(final View v, @ColorInt int bgColor, int[] alphaArray, int stepDuration, final Runnable endAction) {
        int animationCount = alphaArray.length - 1;

        Drawable bgDrawable = new ColorDrawable(bgColor);
        final Drawable oldBgDrawable = v.getBackground();
        setBackgroundKeepingPadding(v, bgDrawable);

        List<Animator> animatorList = new ArrayList<>();
        for (int i = 0; i < animationCount; i++) {
            ObjectAnimator animator = ObjectAnimator.ofInt(v.getBackground(), "alpha", alphaArray[i], alphaArray[i + 1]);
            animatorList.add(animator);
        }

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(stepDuration);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setBackgroundKeepingPadding(v, oldBgDrawable);
                if (endAction != null) {
                    endAction.run();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animatorSet.playSequentially(animatorList);
        animatorSet.start();
        return animatorSet;
    }

    public static void playViewBackgroundAnimation(final View v, @ColorInt int bgColor, int[] alphaArray, int stepDuration) {
        playViewBackgroundAnimation(v, bgColor, alphaArray, stepDuration, null);
    }


    public static void playViewBackgroundAnimation(final View v, @ColorInt int startColor, @ColorInt int endColor, long duration, int repeatCount, int setAnimTagId, final Runnable endAction) {
        final Drawable oldBgDrawable = v.getBackground(); // 存储旧的背景
        QMUIViewHelper.setBackgroundColorKeepPadding(v, startColor);
        final ValueAnimator anim = new ValueAnimator();
        anim.setIntValues(startColor, endColor);
        anim.setDuration(duration / (repeatCount + 1));
        anim.setRepeatCount(repeatCount);
        anim.setRepeatMode(ValueAnimator.REVERSE);
        anim.setEvaluator(new ArgbEvaluator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                QMUIViewHelper.setBackgroundColorKeepPadding(v, (Integer) animation.getAnimatedValue());
            }
        });
        if (setAnimTagId != 0) {
            v.setTag(setAnimTagId, anim);
        }
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setBackgroundKeepingPadding(v, oldBgDrawable);
                if (endAction != null) {
                    endAction.run();
                }
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

    public static void playViewBackgroundAnimation(final View v, int startColor, int endColor, long duration) {
        playViewBackgroundAnimation(v, startColor, endColor, duration, 0, 0, null);
    }

    public static int generateViewId() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return View.generateViewId();
        } else {
            for (; ; ) {
                final int result = sNextGeneratedId.get();
                // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                int newValue = result + 1;
                if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
                if (sNextGeneratedId.compareAndSet(result, newValue)) {
                    return result;
                }
            }
        }
    }

    public static AlphaAnimation fadeIn(View view, int duration, Animation.AnimationListener listener, boolean isNeedAnimation) {
        if (view == null) {
            return null;
        }
        if (isNeedAnimation) {
            view.setVisibility(View.VISIBLE);
            AlphaAnimation alpha = new AlphaAnimation(0, 1);
            alpha.setInterpolator(new DecelerateInterpolator());
            alpha.setDuration(duration);
            alpha.setFillAfter(true);
            if (listener != null) {
                alpha.setAnimationListener(listener);
            }
            view.startAnimation(alpha);
            return alpha;
        } else {
            view.setAlpha(1);
            view.setVisibility(View.VISIBLE);
            return null;
        }
    }

    public static AlphaAnimation fadeOut(final View view, int duration, final Animation.AnimationListener listener, boolean isNeedAnimation) {
        if (view == null) {
            return null;
        }
        if (isNeedAnimation) {
            AlphaAnimation alpha = new AlphaAnimation(1, 0);
            alpha.setInterpolator(new DecelerateInterpolator());
            alpha.setDuration(duration);
            alpha.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    if (listener != null) {
                        listener.onAnimationStart(animation);
                    }
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    view.setVisibility(View.GONE);
                    if (listener != null) {
                        listener.onAnimationEnd(animation);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    if (listener != null) {
                        listener.onAnimationRepeat(animation);
                    }
                }
            });
            view.startAnimation(alpha);
            return alpha;
        } else {
            view.setVisibility(View.GONE);
            return null;
        }
    }

    public static void clearValueAnimator(Animator animator) {
        if (animator != null) {
            animator.removeAllListeners();
            if (animator instanceof ValueAnimator) {
                ((ValueAnimator) animator).removeAllUpdateListeners();
            }

            if (Build.VERSION.SDK_INT >= 19) {
                animator.pause();
            }
            animator.cancel();
        }
    }

    public static Rect calcViewScreenLocation(View view) {
        int[] location = new int[2];
        // 获取控件在屏幕中的位置，返回的数组分别为控件左顶点的 x、y 的值
        view.getLocationOnScreen(location);
        return new Rect(location[0], location[1], location[0] + view.getWidth(),
                location[1] + view.getHeight());
    }

    public static
    @Nullable
    TranslateAnimation slideIn(final View view, int duration, final Animation.AnimationListener listener, boolean isNeedAnimation, QMUIDirection direction) {
        if (view == null) {
            return null;
        }
        if (isNeedAnimation) {
            TranslateAnimation translate = null;
            switch (direction) {
                case LEFT_TO_RIGHT:
                    translate = new TranslateAnimation(
                            Animation.RELATIVE_TO_SELF, -1f, Animation.RELATIVE_TO_SELF, 0f,
                            Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f
                    );
                    break;
                case TOP_TO_BOTTOM:
                    translate = new TranslateAnimation(
                            Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                            Animation.RELATIVE_TO_SELF, -1f, Animation.RELATIVE_TO_SELF, 0f
                    );
                    break;
                case RIGHT_TO_LEFT:
                    translate = new TranslateAnimation(
                            Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f,
                            Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f
                    );
                    break;
                case BOTTOM_TO_TOP:
                    translate = new TranslateAnimation(
                            Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                            Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f
                    );
                    break;
            }
            translate.setInterpolator(new DecelerateInterpolator());
            translate.setDuration(duration);
            translate.setFillAfter(true);
            if (listener != null) {
                translate.setAnimationListener(listener);
            }
            view.setVisibility(View.VISIBLE);
            view.startAnimation(translate);
            return translate;
        } else {
            view.clearAnimation();
            view.setVisibility(View.VISIBLE);

            return null;
        }
    }


    public static
    @Nullable
    TranslateAnimation slideOut(final View view, int duration, final Animation.AnimationListener listener, boolean isNeedAnimation, QMUIDirection direction) {
        if (view == null) {
            return null;
        }
        if (isNeedAnimation) {
            TranslateAnimation translate = null;
            switch (direction) {
                case LEFT_TO_RIGHT:
                    translate = new TranslateAnimation(
                            Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f,
                            Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f
                    );
                    break;
                case TOP_TO_BOTTOM:
                    translate = new TranslateAnimation(
                            Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                            Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f
                    );
                    break;
                case RIGHT_TO_LEFT:
                    translate = new TranslateAnimation(
                            Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1f,
                            Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f
                    );
                    break;
                case BOTTOM_TO_TOP:
                    translate = new TranslateAnimation(
                            Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                            Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1f
                    );
                    break;
            }
            translate.setInterpolator(new DecelerateInterpolator());
            translate.setDuration(duration);
            translate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    if (listener != null) {
                        listener.onAnimationStart(animation);
                    }
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    view.setVisibility(View.GONE);
                    if (listener != null) {
                        listener.onAnimationEnd(animation);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    if (listener != null) {
                        listener.onAnimationRepeat(animation);
                    }
                }
            });
            view.startAnimation(translate);
            return translate;
        } else {
            view.clearAnimation();
            view.setVisibility(View.GONE);
            return null;
        }

    }


    public static void setPaddingLeft(View view, int value) {
        if (value != view.getPaddingLeft()) {
            view.setPadding(value, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
        }
    }


    public static void setPaddingTop(View view, int value) {
        if (value != view.getPaddingTop()) {
            view.setPadding(view.getPaddingLeft(), value, view.getPaddingRight(), view.getPaddingBottom());
        }
    }


    public static void setPaddingRight(View view, int value) {
        if (value != view.getPaddingRight()) {
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), value, view.getPaddingBottom());
        }
    }


    public static void setPaddingBottom(View view, int value) {
        if (value != view.getPaddingBottom()) {
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), value);
        }
    }



    public static void safeSetImageViewSelected(ImageView imageView, boolean selected) {
        Drawable drawable = imageView.getDrawable();
        if (drawable == null) {
            return;
        }
        int drawableWidth = drawable.getIntrinsicWidth();
        int drawableHeight = drawable.getIntrinsicHeight();
        imageView.setSelected(selected);
        if (drawable.getIntrinsicWidth() != drawableWidth || drawable.getIntrinsicHeight() != drawableHeight) {
            imageView.requestLayout();
        }
    }


    /**
     * please use ImageViewCompat.setImageTintList() replace this.
     */
    @Deprecated
    public static ColorFilter setImageViewTintColor(ImageView imageView, @ColorInt int tintColor) {
        LightingColorFilter colorFilter = new LightingColorFilter(Color.argb(255, 0, 0, 0), tintColor);
        imageView.setColorFilter(colorFilter);
        return colorFilter;
    }


    public static boolean isListViewAlreadyAtBottom(ListView listView) {
        if (listView.getAdapter() == null || listView.getHeight() == 0) {
            return false;
        }

        if (listView.getLastVisiblePosition() == listView.getAdapter().getCount() - 1) {
            View lastItemView = listView.getChildAt(listView.getChildCount() - 1);
            if (lastItemView != null && lastItemView.getBottom() == listView.getHeight()) {
                return true;
            }
        }
        return false;
    }


    /**
     * Retrieve the transformed bounding rect of an arbitrary descendant view.
     * This does not need to be a direct child.
     *
     * @param descendant descendant view to reference
     * @param out        rect to set to the bounds of the descendant view
     */
    public static void getDescendantRect(ViewGroup parent, View descendant, Rect out) {
        out.set(0, 0, descendant.getWidth(), descendant.getHeight());
        ViewGroupHelper.offsetDescendantRect(parent, descendant, out);
    }

    public static boolean getDescendantVisibleRect(ViewGroup target, View descendant, Rect out){
        out.set(0, 0, descendant.getWidth(), descendant.getHeight());
        ViewParent parent = descendant.getParent();
        View next = descendant;
        while (parent instanceof ViewGroup && parent != target){
            final ViewGroup vp = (ViewGroup) parent;
            ViewGroupHelper.offsetDescendantRect(vp, next, out);
            if(out.left >= vp.getWidth() || out.right <= 0 || out.top >= vp.getHeight() || out.bottom <= 0){
                return false;
            }
            if(out.left < 0){
                out.left = 0;
            }
            if(out.right > vp.getWidth()){
                out.right = vp.getWidth();
            }
            if(out.top < 0){
                out.top = 0;
            }
            if(out.bottom > vp.getHeight()){
                out.bottom = vp.getHeight();
            }
            next = vp;
            parent = parent.getParent();
        }
        return out.left < target.getWidth() && out.right > 0 && out.top < target.getHeight() && out.bottom > 0;
    }

    private static class ViewGroupHelper {
        private static final ThreadLocal<Matrix> sMatrix = new ThreadLocal<>();
        private static final ThreadLocal<RectF> sRectF = new ThreadLocal<>();

        public static void offsetDescendantRect(ViewGroup group, View child, Rect rect) {
            Matrix m = sMatrix.get();
            if (m == null) {
                m = new Matrix();
                sMatrix.set(m);
            } else {
                m.reset();
            }

            m.preTranslate(-group.getScrollX(), -group.getScrollY());
            offsetDescendantMatrix(group, child, m);

            RectF rectF = sRectF.get();
            if (rectF == null) {
                rectF = new RectF();
                sRectF.set(rectF);
            }
            rectF.set(rect);
            m.mapRect(rectF);
            rect.set((int) (rectF.left + 0.5f), (int) (rectF.top + 0.5f),
                    (int) (rectF.right + 0.5f), (int) (rectF.bottom + 0.5f));
        }

        static void offsetDescendantMatrix(ViewParent target, View view, Matrix m) {
            final ViewParent parent = view.getParent();
            if (parent instanceof View && parent != target) {
                final View vp = (View) parent;
                offsetDescendantMatrix(target, vp, m);
                m.preTranslate(-vp.getScrollX(), -vp.getScrollY());
            }

            m.preTranslate(view.getLeft(), view.getTop());

            if (!view.getMatrix().isIdentity()) {
                m.preConcat(view.getMatrix());
            }
        }
    }
}
