package com.kiminonawa.mydiary.shared.gui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Button;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.shared.ColorTools;
import com.kiminonawa.mydiary.shared.ScreenHepler;
import com.kiminonawa.mydiary.shared.ThemeManager;

/**
 * Created by daxia on 2016/11/13.
 */

public class MyDiaryButton extends Button {


    public MyDiaryButton(Context context) {
        super(context);
    }

    public MyDiaryButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyDiaryButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyDiaryButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.setBackgroundResource(ThemeManager.getInstance().getButtonBgResource());
        this.setTextColor(ColorTools.getColorStateList(getContext(), R.color.button_default_text_color));
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            this.setStateListAnimator(null);
        }
        this.setMinimumWidth(ScreenHepler.dpToPixel(getContext().getResources(), 110));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
