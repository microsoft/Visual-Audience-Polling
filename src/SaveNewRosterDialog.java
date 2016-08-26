package com.abhinav.qcards;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class SaveNewRosterDialog extends Dialog implements android.view.View.OnClickListener {

	private static final String TAG = null;
	public Activity c;
	public Dialog d;
	public Button saveBut, discardBut;
	TextView message;

	public SaveNewRosterDialog(Activity a) {
		super(a);
		// TODO Auto-generated constructor stub
		this.c = a;
	}

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.custom_dialog);
		saveBut = (Button) findViewById(R.id.pos_but);
		discardBut = (Button) findViewById(R.id.neg_but);
		message = (TextView) findViewById(R.id.dialog);
		message.setTextSize(20);
		message.setText("Are you sure you do not want to add more entries to this roster?");		
		saveBut.setOnClickListener(this);
		discardBut.setOnClickListener(this);

	}

	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pos_but:
			try {
				RosterEntry.saveData(RosterEntry.rosterEntries);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			c.finish();
			break;
		case R.id.neg_but:
			dismiss();
			break;
		default:
			break;
		}
		dismiss();
	}
}