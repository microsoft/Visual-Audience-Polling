package com.abhinav.qcards;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class GetCards extends Activity implements OnItemSelectedListener {
	ArrayList<String> gMail = new ArrayList<String>();
	String selectedEmail = null;
	String listMail = null;
	Spinner spinner;
	Button listButton;
	Button entryButton;
	EditText emailid;
	int selected = 0;
	int numAccounts = 0;
	public static boolean fromGetCards = false;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.get_qcards);
		spinner = (Spinner) findViewById(R.id.mail_spinner);
		// listButton = (Button) findViewById(R.id.list_button);
		entryButton = (Button) findViewById(R.id.entry_button);
		// entryButton.setClickable(false);
		emailid = (EditText) findViewById(R.id.email_text);
		

		disableButton();

		emailid.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

				enableButton();
			}

		});

		Pattern gMailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
		Account[] accounts = AccountManager.get(this).getAccounts();
		for (Account account : accounts) {
			if (gMailPattern.matcher(account.name).matches()) {
				gMail.add(account.name);
			}
		}
		if (gMail.size() == 0) {
			gMail.add(0, "No account");
			numAccounts = 0;

		} else {
			// numAccounts = gMail.size();
			numAccounts = gMail.size();
		}

		setSpinner(gMail);
		spinner.setOnItemSelectedListener(this);
		setFromGetCards(true);

		// Toast.makeText(this, gMail, Toast.LENGTH_LONG).show();
	}
	
	private void setFromGetCards(Boolean bool){
		fromGetCards = bool;
	}
	
	public static Boolean isFromGetCards(){
		return fromGetCards;
	}

	public void disableButton() {
		entryButton.setEnabled(false);
	}

	public void enableButton() {
		entryButton.setEnabled(true);
	}

	public void setSpinner(ArrayList<String> spinList) {
		// String[] spinArray = (String[]) spinList.toArray();
		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, spinList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// Specify the layout to use when the list of choices appears
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		// entryButton.setEnabled(false);

	}
	
	public void onBackPressed(){
		super.onBackPressed();
		Intent i = new Intent(this,QCardsActivity.class);
		fromGetCards = true;
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		if (numAccounts > 0) {
			selectedEmail = (String) parent.getItemAtPosition(position);
			if (position == 0) {
				// disableButton()
				listMail = selectedEmail;
				Integer pos = position;
				Toast toast = Toast.makeText(this, "Default ID:"
						+ selectedEmail + " selected", Toast.LENGTH_LONG);
				toast.show();
				enableButton();
			} else if (position > 0) {
				Toast toast = Toast.makeText(this, "ID selected : "
						+ selectedEmail, Toast.LENGTH_LONG);
				toast.show();
				enableButton();
			}

		} else {
			Toast toast = Toast
					.makeText(
							this,
							"No email account found on your device. Please enter an email id in the text field.",
							Toast.LENGTH_LONG);
			toast.show();
			disableButton();
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		// entryButton.setEnabled(false);
	}

	public boolean isConnected() {
		ConnectivityManager cm = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (activeNetwork == null) {
			// There are no active networks.
			Toast.makeText(
					this,
					"We need an active internet connection for this operation. No active Internet connection detected.",
					Toast.LENGTH_LONG);
			return false;
		} else {
			return activeNetwork.isConnected();
		}
	}

	public void listButton(View view) {
		if (!selectedEmail.isEmpty()) {
			// post to server
			if (isConnected()) {
				new sendData().execute(selectedEmail);

			} else {
				Toast toast = Toast
						.makeText(
								this,
								"We need an active internet connection for this operation. No active Internet connection detected.",
								Toast.LENGTH_LONG);
				toast.show();
			}

		} else {
			// warning dialog
			Dialog warn = new Dialog(view.getContext());
			warn.requestWindowFeature(Window.FEATURE_NO_TITLE);
			warn.setContentView(R.layout.sd_warning);
			TextView wt = (TextView) warn.findViewById(R.id.warning);
			wt.setText("No email id specified");
			warn.show();
		}
	}

	public void entryButtonClick(View view) {
		// entryButton.setEnabled(false);
		String mailId = emailid.getText().toString();
		if (!mailId.matches("")) {
			selectedEmail = mailId;
			enableButton();
		}

		if (!selectedEmail.isEmpty()) {
			// post to server
			if (isConnected()) {
				new sendData().execute(selectedEmail);

			} else {
				Toast toast = Toast
						.makeText(
								this,
								"We need an active internet connection for this operation. No active Internet connection detected.",
								Toast.LENGTH_LONG);
				toast.show();
			}
			
		} else {
			// warning dialog
			Dialog warn = new Dialog(view.getContext());
			warn.setContentView(R.layout.sd_warning);
			TextView wt = (TextView) warn.findViewById(R.id.warning);
			wt.setText("No email id specified");
			warn.show();
		}
	}

	private class sendData extends AsyncTask<String, Integer, Double> {

		@Override
		protected Double doInBackground(String... params) {
			// TODO Auto-generated method stub
			postData(params[0]);
			return null;
		}

		protected void onPostExecute(Double result) {

			Toast.makeText(getApplicationContext(), "Email Sent to "+selectedEmail,
					Toast.LENGTH_LONG).show();
			selectedEmail = listMail;
			emailid.setText("");
		}

		protected void onProgressUpdate(Integer... progress) {

		}

		public void postData(String valueIWantToSend) {
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					"http://www.abhinavtripathi.com/sendCards.php");

			try {
				// Add your data
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("email",
						valueIWantToSend));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		}

	}

}
