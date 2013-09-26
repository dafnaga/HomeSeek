package com.postpc.homeseek.widget;


import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomTitleTextView extends TextView {

    public CustomTitleTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustomTitleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomTitleTextView(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/DroidSans.ttf");
        setTypeface(tf ,1);
        setTextSize(30);

    }

}


