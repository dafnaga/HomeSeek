package com.postpc.homeseek;

import com.parse.ParseException;
import com.postpc.homeseek.core.hsobjects.HSInstantMessage;
import com.postpc.homeseek.core.hsobjects.HSUser;
import com.postpc.homeseek.core.managers.HSUsersManager;
import com.postpc.homeseek.core.managers.exceptions.NoSuchUserException;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class TextInstantMessageView extends FrameLayout {

	private final HSInstantMessage im;

	public TextInstantMessageView(Context context, HSInstantMessage im) {
		super(context);		
		this.im = im;

		View chatTxtViewWrap = ((Activity)context).getLayoutInflater().inflate(R.layout.im_text_layout,null);
		TextView chatTxtView = (TextView)chatTxtViewWrap.findViewById(R.id.chat_txt);
		
		chatTxtView.setText(getImText());
				
		addView(chatTxtViewWrap);
	}

	private String getImText() {
		HSUser fromUser;
		HSUser toUser;	
		
		try {
			fromUser = HSUsersManager.getUserById(im.getFromUserId());
			toUser = HSUsersManager.getUserById(im.getToUserId());			
			return String.format("%s: %s", fromUser.getFirstName(), im.getMessageData());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "<error>";
		} catch (NoSuchUserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "<error>";
		}
	}
	
	
}
