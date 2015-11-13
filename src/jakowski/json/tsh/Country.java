package jakowski.json.tsh;

import android.graphics.Bitmap;

public class Country {
	
	private int iID;
	private String sName;
	private String sIMGURL;
	private long lDate;
	
	private Bitmap oBitmap = null;
	private boolean isLoading = false;
	
	// -----------------------
	
	protected Country(String sID, String sName, String sIMGURL, String sDate) {
		try {
			this.setID(Integer.parseInt(sID));
			this.setDate(Long.parseLong(sDate));
		} catch(NumberFormatException e) {
			
		}
		this.setName(sName);
		this.setIMGURL(sIMGURL);
	}
	
	// -----------------------

	public int getID() {
		return iID;
	}

	public void setID(int iID) {
		this.iID = iID;
	}

	public String getName() {
		return sName;
	}

	public void setName(String sName) {
		this.sName = sName;
	}

	public String getIMGURL() {
		return sIMGURL;
	}

	public void setIMGURL(String sIMGURL) {
		this.sIMGURL = sIMGURL;
	}
	
	protected Bitmap getBitmap() {
		return oBitmap;
	}
	
	protected void setBitmap(Bitmap oBitmap) {
		this.oBitmap = oBitmap;
	}
	
	protected boolean getIsLoading() {
		return isLoading;
	}
	
	protected void setIsLoading(boolean isLoading) {
		this.isLoading = isLoading;
	}

	public long getDate() {
		return lDate;
	}

	public void setDate(long lDate) {
		this.lDate = lDate;
	}
	
	// -----------------------
}