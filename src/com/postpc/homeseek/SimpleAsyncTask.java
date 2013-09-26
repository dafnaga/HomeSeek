package com.postpc.homeseek;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

public abstract class SimpleAsyncTask extends AsyncTask<Void, String, Void> {

	protected Activity activity;
	private ProgressDialog progressDialog;

	public SimpleAsyncTask(Activity activity){
		super();
		this.activity = activity;
		
	}
	
	protected abstract void preExecute();
	
	@Override
	protected void onPreExecute(){
		progressDialog = new ProgressDialog(activity);
		
		progressDialog.setMessage("Please wait..");
		progressDialog.setTitle("HomeSeek");
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(true);
		
		progressDialog.show();
		
		preExecute();
	}
	
	@Override
	protected abstract Void doInBackground(Void... arg0);
	
	@Override
	protected void onProgressUpdate(String... progress){
		progressDialog.setMessage(progress[0]);		
	}
	
	@Override 
	protected void onPostExecute(Void result){
		try {
			progressDialog.dismiss();
			postExecute();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	protected abstract void postExecute();
}
