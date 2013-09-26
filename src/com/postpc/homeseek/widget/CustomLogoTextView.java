package com.postpc.homeseek.widget;


import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomLogoTextView extends TextView {

    public CustomLogoTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustomLogoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomLogoTextView(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/giant_head_regular_ot.otf");
        setTypeface(tf ,1);

    }

}


