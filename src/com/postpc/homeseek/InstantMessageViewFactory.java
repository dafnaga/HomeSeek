package com.postpc.homeseek;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.postpc.homeseek.core.hsobjects.HSInstantMessage;
import com.postpc.homeseek.core.hsobjects.HSUser;

public class InstantMessageViewFactory {
	
	public static View createView(Context context, HSUser currentUser, HSInstantMessage im, ViewGroup parent){
		
		View view;
		
		switch (im.getMessageType()){
		case HSInstantMessage.MESSAGE_TYPE.TYPE_TEXT:
			view = TextInstantMessageViewCreator.create(context, im, currentUser , parent);
			break;
		case HSInstantMessage.MESSAGE_TYPE.TYPE_PHOTO:
			view = PhotoInstantMessageViewCreator.create(context, im, currentUser, parent);
			break;
		case HSInstantMessage.MESSAGE_TYPE.TYPE_APARTMENT:
			view = ApartemntInstantMessageViewCreator.create(context, im, currentUser, parent);
			break;
		default:
			view = null;
			break;
		}
		
		if (currentUser.getId().equals(im.getFromUserId())) {
			((LinearLayout)view).setGravity(Gravity.RIGHT);
		} else {
			((LinearLayout)view).setGravity(Gravity.LEFT);
		}
		
		return view;
	}
	
}
