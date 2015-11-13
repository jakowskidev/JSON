package jakowski.json.tsh;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

class CountryAdapter extends ArrayAdapter<Country>  {
	
	private int iResource;
	private List<Country> lCountries = new ArrayList<Country>();
	
	private LayoutInflater layoutInflater;
	
	private ViewHolder viewHolder;
	
	// -----------------------
	
	public CountryAdapter(Context oC, int iResource, List<Country> lCountries) {
		super(oC, iResource, lCountries);
		
		this.iResource = iResource;
		this.lCountries = lCountries;
		
		layoutInflater = (LayoutInflater)oC.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		
		if(v == null) {
			v = layoutInflater.inflate(iResource, null);
			viewHolder = new ViewHolder();
			
			viewHolder.ivLogo = (ImageView)v.findViewById(R.id.ivLogo);
			viewHolder.textCountry = (TextView)v.findViewById(R.id.textCountry);
			viewHolder.textDate = (TextView)v.findViewById(R.id.textDate);
			
			v.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)v.getTag();
		}
		
		if(!lCountries.get(position).getIsLoading()) {
			lCountries.get(position).setIsLoading(true);
			new DownloadImageTask(position, viewHolder.ivLogo).execute(lCountries.get(position).getIMGURL());
			Log.i("JSON", "Loading-position: " + position);
		} else if(lCountries.get(position).getBitmap() != null) {
			viewHolder.ivLogo.setImageBitmap(lCountries.get(position).getBitmap());
		}
		
		
		viewHolder.textCountry.setText(lCountries.get(position).getName());
		Calendar cDate = Calendar.getInstance();
		cDate.setTimeInMillis(lCountries.get(position).getDate()*1000);
		
		viewHolder.textDate.setText(cDate.get(Calendar.YEAR) + "-" + ((cDate.get(Calendar.MONTH) + 1) < 10 ? "0" + (cDate.get(Calendar.MONTH) + 1) : (cDate.get(Calendar.MONTH) + 1)));
		return v;
	}
	
	// -----------------------
	
	static class ViewHolder {
		public ImageView ivLogo;
		public TextView textCountry;
		public TextView textDate;
	}
	
	// -----------------------
	
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		int iPos;
		ImageView iv;
		
		public DownloadImageTask(int iPos, ImageView iv) {
			this.iPos = iPos;
			this.iv = iv;
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap bIcon = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				bIcon = BitmapFactory.decodeStream(in);
				
			} catch (Exception e) {
				Log.e("JSON", e.getMessage());
				e.printStackTrace();
				
				lCountries.get(iPos).setIsLoading(false);
			} catch (OutOfMemoryError e) {
				lCountries.get(iPos).setIsLoading(false);
			}
			
			return bIcon;
		}

		protected void onPostExecute(Bitmap result) {
			lCountries.get(iPos).setBitmap(result);
			iv.setImageBitmap(lCountries.get(iPos).getBitmap());
			
			Log.i("JSON", "onPostExecute: " + iPos);
		}
	}
}