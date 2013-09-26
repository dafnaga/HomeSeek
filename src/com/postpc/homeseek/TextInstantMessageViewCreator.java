package com.postpc.homeseek;

import com.postpc.homeseek.core.hsobjects.HSInstantMessage;
import com.postpc.homeseek.core.hsobjects.HSUser;
import com.postpc.homeseek.core.managers.HSUsersManager;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TextInstantMessageViewCreator{

	public static View create(Context context, HSInstantMessage im, HSUser currentUser, ViewGroup parent) {
		
		View chatTxtViewWrap = ((Activity)context).getLayoutInflater().inflate(R.layout.im_text_layout,null);
		
		HSUser fromUser = null;
		//HSUser toUser;	
		String txt, name;
		
		try {
			fromUser = HSUsersManager.getUserById(im.getFromUserId());
			//toUser = HSUsersManager.getUserById(im.getToUserId());
			txt = String.format("%s", im.getMessageData());
			name = fromUser.getFullName();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			txt = "<error>";
			name = "<error>";
		}
		
		TextView chatTxtView = (TextView)chatTxtViewWrap.findViewById(R.id.chat_txt);
		TextView chatEmailView = (TextView)chatTxtViewWrap.findViewById(R.id.chat_email);
		chatTxtView.setText(txt);
		chatEmailView.setText(name);
		
		return chatTxtViewWrap;
	}
	
}
