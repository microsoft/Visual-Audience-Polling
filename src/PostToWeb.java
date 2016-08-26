package com.abhinav.qcards;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.util.Log;

public class PostToWeb extends AsyncTask<String, Integer, Long> {

	@Override
	protected Long doInBackground(String... params) {
		// TODO Auto-generated method stub
		try {
			// URL url = new URL(params[0]);
			HttpClient client = new DefaultHttpClient();
			// String postURL = "http://abhinavtripathi.com/sendCards.php";
			String postURL = params[0];
			HttpPost post = new HttpPost(postURL);
			List<NameValuePair> params1 = new ArrayList<NameValuePair>();
			params1.add(new BasicNameValuePair("email",
					"abhinavtripathi01@hotmail.com"));
			UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params1,
					HTTP.UTF_8);
			post.setEntity(ent);
			HttpResponse responsePOST = client.execute(post);
			HttpEntity resEntity = responsePOST.getEntity();
			if (resEntity != null) {
				Log.i("RESPONSE", EntityUtils.toString(resEntity));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
