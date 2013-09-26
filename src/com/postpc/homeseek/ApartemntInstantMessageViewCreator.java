package com.postpc.homeseek;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.postpc.homeseek.core.hsobjects.HSApartment;
import com.postpc.homeseek.core.hsobjects.HSInstantMessage;
import com.postpc.homeseek.core.hsobjects.HSUser;
import com.postpc.homeseek.core.managers.HSApartmentsManager;
import com.postpc.homeseek.core.managers.HSUsersManager;

public class ApartemntInstantMessageViewCreator {

	
	public static View create(Context context, HSInstantMessage im, HSUser currentUser, ViewGroup parent) {
		View chatTxtViewWrap = ((Activity)context).getLayoutInflater().inflate(R.layout.im_apartment_layout,null);
		
		HSUser fromUser = null;
		HSApartment apartment;
		String txt, link, name, apartmentId;
		
		try {
			fromUser = HSUsersManager.getUserById(im.getFromUserId());
			apartment = HSApartmentsManager.getApartmentById(im.getMessageData());			
			
			txt = "Check out the apartment at:";
			name = fromUser.getFullName();
			link = apartment.getTitle();
			apartmentId = apartment.getId();
			
		} catch (Exception e) {
			e.printStackTrace();
			txt = "<error>";
			name = "<error>";
			link = "<error>";
			apartmentId = "<error>"; 
		}
		
		TextView chatTxtView = (TextView)chatTxtViewWrap.findViewById(R.id.chat_txt);
		TextView chatEmailView = (TextView)chatTxtViewWrap.findViewById(R.id.chat_email);
		TextView apartmentLinkView = (TextView)chatTxtViewWrap.findViewById(R.id.apartment_link);
		chatTxtView.setText(txt);
		chatEmailView.setText(name);
		apartmentLinkView.setText(link);
		apartmentLinkView.setMovementMethod(LinkMovementMethod.getInstance());
		apartmentLinkView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		apartmentLinkView.setTag(apartmentId);
		
		return chatTxtViewWrap;
	}

}
