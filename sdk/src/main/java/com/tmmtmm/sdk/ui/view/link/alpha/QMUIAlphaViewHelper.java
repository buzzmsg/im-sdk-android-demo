package com.tmmtmm.sdk.ui.view.link.alpha;

import android.view.View;

import androidx.annotation.NonNull;

import com.tmmtmm.sdk.R;
import com.tmmtmm.sdk.ui.view.link.util.QMUIResHelper;

import java.lang.ref.WeakReference;

public class QMUIAlphaViewHelper {

    private WeakReference<View> mTarget;


    private boolean mChangeAlphaWhenPress = true;

    private boolean mChangeAlphaWhenDisable = true;

    private float mNormalAlpha = 1f;
    private float mPressedAlpha = .5f;
    private float mDisabledAlpha = .5f;

    public QMUIAlphaViewHelper(@NonNull View target) {
        mTarget = new WeakReference<>(target);
        mPressedAlpha = QMUIResHelper.getAttrFloatValue(target.getContext(), R.attr.qmui_alpha_pressed);
        mDisabledAlpha = QMUIResHelper.getAttrFloatValue(target.getContext(), R.attr.qmui_alpha_disabled);
    }

    public QMUIAlphaViewHelper(@NonNull View target, float pressedAlpha, float disabledAlpha) {
        mTarget = new WeakReference<>(target);
        mPressedAlpha = pressedAlpha;
        mDisabledAlpha = disabledAlpha;
    }


    public void onPressedChanged(View current, boolean pressed) {
        View target = mTarget.get();
        if (target == null) {
            return;
        }
        if (current.isEnabled()) {
            target.setAlpha(mChangeAlphaWhenPress && pressed && current.isClickable() ? mPressedAlpha : mNormalAlpha);
        } else {
            if (mChangeAlphaWhenDisable) {
                target.setAlpha(mDisabledAlpha);
            }
        }
    }

    public void onEnabledChanged(View current, boolean enabled) {
        View target = mTarget.get();
        if (target == null) {
            return;
        }
        float alphaForIsEnable;
        if (mChangeAlphaWhenDisable) {
            alphaForIsEnable = enabled ? mNormalAlpha : mDisabledAlpha;
        } else {
            alphaForIsEnable = mNormalAlpha;
        }
        if (current != target && target.isEnabled() != enabled) {
            target.setEnabled(enabled);
        }
        target.setAlpha(alphaForIsEnable);
    }

    public void setChangeAlphaWhenPress(boolean changeAlphaWhenPress) {
        mChangeAlphaWhenPress = changeAlphaWhenPress;
    }

    public void setChangeAlphaWhenDisable(boolean changeAlphaWhenDisable) {
        mChangeAlphaWhenDisable = changeAlphaWhenDisable;
        View target = mTarget.get();
        if (target != null) {
            onEnabledChanged(target, target.isEnabled());
        }

    }

}
