package jakowski.json.tsh;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private final String sWWW = "https://serene-mountain-2455.herokuapp.com";

	private ListView listView;
	private List<Country> lCountries;
	private CountryAdapter oCA;
	
	private int iLayID;
	
	// ------------------------------------
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		lCountries = new ArrayList<Country>();
		nContentView(R.layout.activity_main);
		
		new nAsyncTask(0).execute(sWWW + "/countries");
	}
	
	// ------------------------------------
	
	private void nContentView(int resID) {
		setContentView(resID);
		iLayID = resID;
		setTitle("");
		
		switch(resID) {
			case R.layout.activity_main: {
				listView = (ListView)findViewById(R.id.listView1);
				
				oCA = new CountryAdapter(getApplicationContext(), R.layout.row, lCountries);
				listView.setAdapter(oCA);
				
				listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					   @Override
					   public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					      Country listItem = (Country)listView.getItemAtPosition(position);
					      
					      nContentView(R.layout.country);
					      
					      new nAsyncTask(1).execute(sWWW + "/countries/" + listItem.getID());
					   } 
					});
				
				setTitle("Countries");
				break;
			}
			case R.layout.country: {
				
				break;
			}
		}
	}
	
	@Override
	public void onBackPressed() {
		
		if(iLayID == R.layout.activity_main) {
		   Intent setIntent = new Intent(Intent.ACTION_MAIN);
		   setIntent.addCategory(Intent.CATEGORY_HOME);
		   setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		   startActivity(setIntent);
		} else {
			switch(iLayID) {
				case R.layout.country: {
					nContentView(R.layout.activity_main);
					oCA.notifyDataSetChanged();
					break;
				}
			}
		}
	}
	
	// ------------------------------------
	
	class nAsyncTask extends AsyncTask<String, Void, String> {
		private boolean bResult = false;
		private int iTaskID;
		
		public nAsyncTask(int iTaskID) {
			this.iTaskID = iTaskID;
		}
		
		@Override
		protected String doInBackground(String... params) {
			try {
				HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet(params[0]);
				HttpResponse response = client.execute(request);
				
				int statusCode = response.getStatusLine().getStatusCode();
				
				if(statusCode == 200) {
					HttpEntity entity = response.getEntity();
					
					bResult = true;
					return EntityUtils.toString(entity);
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			bResult = false;
			return "";
		}
		
		@Override
		protected void onPostExecute(String result) {
			//Log.i("JSON", "result: " + result);
			
			if(bResult) {
				switch(iTaskID) {
					case 0: { // R.layout.activity_main
						try {
							JSONArray jArray = new JSONArray(result);
							
							for(int i = 0; i < jArray.length(); i++) {
								JSONObject jReal = jArray.getJSONObject(i);
								
								lCountries.add(new Country(jReal.getString("id"), jReal.getString("name"), jReal.getString("picture_url"), jReal.getString("date")));
							}
							
							for(int i = 0, iSize = lCountries.size(); i < iSize - 1; i++) {
								for(int j = i + 1; j < iSize; j++) {
									if(lCountries.get(i).getDate() > lCountries.get(j).getDate()) {
										Country temp = lCountries.get(i);
										lCountries.set(i, lCountries.get(j));
										lCountries.set(j, temp);
									}
								}
							}
							
							oCA.notifyDataSetChanged();
						} catch (JSONException e) {
							
						}
						break;
					}
					// ---------------------------------------------------
					case 1: { // R.layout.country

						try {
							JSONObject jReal = new JSONObject(result);
							
							setTitle(jReal.getString("name"));
							
							TextView tvDESC = (TextView)findViewById(R.id.textDESC);
							TextView textDATE = (TextView)findViewById(R.id.textDATE);
							
							Calendar cDate = Calendar.getInstance();
							cDate.setTimeInMillis(Long.parseLong(jReal.getString("date"))*1000);
							
							tvDESC.setText(jReal.getString("description"));
							textDATE.setText(cDate.get(Calendar.YEAR) + "-" + ((cDate.get(Calendar.MONTH) + 1) < 10 ? "0" + (cDate.get(Calendar.MONTH) + 1) : (cDate.get(Calendar.MONTH) + 1)));
							new DownloadImageTask().execute(jReal.getString("picture_url"));
							
							final String see_more_url = jReal.getString("see_more_url");
							
							findViewById(R.id.btnSEEMORE).setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View arg0) {
									startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("" + see_more_url)));
								}
							});
						} catch (JSONException e) {
							e.printStackTrace();
						}
						break;
					}
				}
			} else {
				
			}
		}
	}
	
	// -----------------------
	
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		
		public DownloadImageTask() {}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap bIcon = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				bIcon = BitmapFactory.decodeStream(in);
				
			} catch (Exception e) {
				Log.e("JSON", e.getMessage());
				e.printStackTrace();
			} catch (OutOfMemoryError e) {
				
			}
			
			return bIcon;
		}

		protected void onPostExecute(Bitmap result) {
			try {
				ImageView oIV = (ImageView)findViewById(R.id.ivBG);
				
				oIV.setImageBitmap(result);
			} catch(NullPointerException e) {
				
			}
		}
	}
}