package com.im.sdk.ui.view.link.alpha;

import android.content.Context;
import android.util.AttributeSet;

import com.im.sdk.ui.view.link.textview.QMUISpanTouchFixTextView;

/**
 */
public class QMUIAlphaTextView extends QMUISpanTouchFixTextView implements QMUIAlphaViewInf {

    private QMUIAlphaViewHelper mAlphaViewHelper;

    public QMUIAlphaTextView(Context context) {
        super(context);
    }

    public QMUIAlphaTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public QMUIAlphaTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private QMUIAlphaViewHelper getAlphaViewHelper() {
        if (mAlphaViewHelper == null) {
            mAlphaViewHelper = new QMUIAlphaViewHelper(this);
        }
        return mAlphaViewHelper;
    }

    @Override
    protected void onSetPressed(boolean pressed) {
        super.onSetPressed(pressed);
        getAlphaViewHelper().onPressedChanged(this, pressed);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        getAlphaViewHelper().onEnabledChanged(this, enabled);
    }

    /**
     *
     * @param changeAlphaWhenPress
     */
    @Override
    public void setChangeAlphaWhenPress(boolean changeAlphaWhenPress) {
        getAlphaViewHelper().setChangeAlphaWhenPress(changeAlphaWhenPress);
    }

    /**
     *
     * @param changeAlphaWhenDisable
     */
    @Override
    public void setChangeAlphaWhenDisable(boolean changeAlphaWhenDisable) {
        getAlphaViewHelper().setChangeAlphaWhenDisable(changeAlphaWhenDisable);
    }
}
