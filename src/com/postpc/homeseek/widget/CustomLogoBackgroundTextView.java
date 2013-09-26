package com.postpc.homeseek.widget;


import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomLogoBackgroundTextView extends TextView {

    public CustomLogoBackgroundTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustomLogoBackgroundTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomLogoBackgroundTextView(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/giant_head_two_tt.ttf");
        setTypeface(tf ,1);

    }

}


