package com.postpc.homeseek.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class CustomButton extends Button {

	public CustomButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public CustomButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public void init() {
		Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/DroidSans-Bold.ttf");
		setTypeface(tf ,1);

	}
}