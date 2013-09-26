package com.postpc.homeseek;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.postpc.homeseek.core.hsobjects.HSUser;

public class ChatContactsAdapter extends ArrayAdapter<HSUser>{

	private final HSUser[] contacts;
	private final Context context;

	public ChatContactsAdapter(Context context, HSUser[] objects) {
		super(context, 0, objects);
		this.context = context;
		// TODO Auto-generated constructor stub
		this.contacts = objects;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row = convertView;
		if (row == null){
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(R.layout.chat_contacts_list_item, parent, false);			
		}		

		HSUser contact = contacts[position];		
		TextView nameTxt = (TextView)row.findViewById(R.id.contact_name_txt);
		nameTxt.setText(contact.getFullName());				
		
		Button chatBtn = (Button)row.findViewById(R.id.chat_btn);
		chatBtn.setTag(contact);
		chatBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {					
				HSUser contact = (HSUser)v.getTag();
				Intent intent = new Intent(context, ChatActivity.class);

				intent.putExtra(ChatActivity.ARG_WITH_USER_ID, contact.getId());
				context.startActivity(intent);
			}
		});

		return row;			
	}

}