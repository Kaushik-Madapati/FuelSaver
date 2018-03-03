package com.FuelSaver;



import java.util.ArrayList;
import java.util.List;

import com.FuelSaver.UserSetting.UserDataInfo;

import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import android.view.View.OnClickListener;


import android.widget.Button;

import android.widget.LinearLayout;
import android.widget.TextView;



import android.app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;

import android.graphics.drawable.Drawable;
import android.location.Location;

import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;



public class UiFramework extends Activity{

	
	public static final String UPDATEGPSDATA = "GPSUpdate";
	public static final String UPDATE_LAT = "Latitude";
	public static final String UPDATE_LONG = "Longitude";
	public static final String UPDATE_MILEAGE_DATA = "MilageUpdate";
	public static final String CURRENT_LAT = "Latitude";
	public static final String CURRENT_LONG = "Longitude";
			
	public static final int  MSG_UPDATEGPSDATA = 1;
	public static final int MSG_STOP_TRIP = 2;
	public static final int MSG_AUDIOALERT = 3;
	public static final int MSG_MILEAGE_PRICE_UPDATE = 4;

	private Button  realSpeed = null;
	private Button mMileagePriceChange = null;
	private Button mNearestCheapGas = null;
	private Button mMileagePriceChangeTxt = null;
	TextView   txtView = null;
	private Button mSpeedTxtBtn = null;
	
	
	
	
	LocationManager mLocationManger = null;
	Location mCurrentlocation = null;;
	String provider=  LocationManager.GPS_PROVIDER;
    
    private String mrReferenceSpeed = null;
    private UpdateService mUpdateService = null;
    private Integer  mEfficientMilage = 60;
    private float mPriceChange = (float)0.0; 
    private float mPriceImpact = (float)0.21;
    
    
    
    Integer mAvgEfficientChange = 0;
    Integer mAvgSpeed = 0;
    Float mAvgPriceChange = (float) 0.0;
    
    Integer mAvgEfficientChangeCounter = 0;
    Integer mAvgSpeedCounter = 0;
    Integer mAvgPriceChangeCounter = 0;
    Double  mLatalitude = 33.98554118;
    Double mLongitude = -117.75123995;
    private boolean mDisplayMileageChange = true;
    private Drawable mFindGasIcon = null;
    float mSpeedTextSize = (float) 0.0;
    int mSpeedRange = 5;
    String mSpeedImpactPref = null;
    
    List<Integer> mEfficientList = new ArrayList<Integer>();
   
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
			
		realSpeed = (Button)findViewById(R.id.entry); 
		
		mMileagePriceChange = (Button)findViewById(R.id.milage_percentage);
		mMileagePriceChangeTxt = (Button)findViewById(R.id.milage_percentage_txt);
		
		 //set List 
		EfficeentList();
	
	
		mSpeedTxtBtn = (Button)findViewById(R.id.speed_text);
		mNearestCheapGas = (Button)findViewById(R.id.find_gas);
		Resources res = getResources();

		mFindGasIcon = res.getDrawable(R.drawable.gas_ring);
		mNearestCheapGas.setBackgroundDrawable(mFindGasIcon);
		mNearestCheapGas.setEnabled(false);
		
		
	
		Button mapBtn  = (Button)findViewById(R.id.where_am_i);
		
	
		Drawable map_icon = res.getDrawable(R.drawable.map_ring);
		
		mapBtn.setBackgroundDrawable(map_icon);
		
		realSpeed.setTextSize(30);
		realSpeed.setText("Acquiring GPS ..");
		
			
		///
		///  listener for Detail button 
		///
		mNearestCheapGas.setOnClickListener(new OnClickListener() {
			
			@Override
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ShowGasStation();
				
			}
		});
		
		mapBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onShowMap();
				
			}
		});
		
		/// Show Into text if this first time  your r using App
		
		IntroText();
      
			
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		
		getMenuInflater().inflate(R.menu.user_menu, menu);
		return true;
	}




	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
		
		case R.id.user_setting: {
			ShowSetting();
		return true;
		}
		
		case R.id.Tips: {
			ShowDetails();
		return true;
		}
		default: {
		   return false;
		}
		
		}
	
	}
	
		
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		boolean result =  super.onKeyDown(keyCode, event);
		
		if(keyCode == KeyEvent.KEYCODE_MENU && event.isLongPress()) {
			return true;
		}
		
		return result;
	}
	private void IntroText()
	{
		SharedPreferences sPrefrence = getSharedPreferences(UserSetting.PREFERENCES_FILE, MODE_WORLD_READABLE);
		
		if(!sPrefrence.getBoolean(UserDataInfo.INTRO_TEXT_KEY, false))
		{
		
			SharedPreferences.Editor e = sPrefrence.edit();
			e.putBoolean(UserDataInfo.INTRO_TEXT_KEY, true);
			e.commit();
			Intent i = new Intent(this, IntroActivity.class);
			startActivity(i);
		}
	}



	void EfficeentList()
	{
		mEfficientList.add(0);
		mEfficientList.add(8);
		mEfficientList.add(17);
		mEfficientList.add(23);
		mEfficientList.add(28);
		mEfficientList.add(35);
		
	}
	
	private void ShowSetting()
	{
		
		Intent i = new Intent(this, UserSetting.class);
		
	    startActivity(i);
	}
	private void ShowDetails()
	{
		
	    Intent i = new Intent(this, DetailActivity.class);
		 startActivity(i);
	}
	
	/// 
	/// This function will get lat and log and sent intent to 
	/// start cheap gas activity
	///
	private void ShowGasStation()
	{
		
		Intent i = new Intent(this, GasInfoActivity.class);
		i.putExtra(CURRENT_LAT, mLatalitude);
		i.putExtra(UPDATE_LONG, mLongitude);
		 startActivity(i);
		
	
	}
	
	private void onShowMap()
	{
		ArrayList<GasPracelData> dataList = new ArrayList<GasPracelData>();
					
		GasPracelData data1 = new GasPracelData(mLatalitude, mLongitude, 0,0,null, "Map");
		dataList.add(data1);
		Intent i = new Intent(this, MapviewActivity.class);
				
		i.putParcelableArrayListExtra("GasSationInfo", dataList);
		startActivity(i);
	}
	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		if(mUpdateService != null)
			mUpdateService.onGPSDisable();
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		SharedPreferences sPrefrence = getSharedPreferences(UserSetting.PREFERENCES_FILE, MODE_WORLD_READABLE);
		
		mMileagePriceChange.setText("0");
	    	
		String units =  sPrefrence.getString(UserDataInfo.UNITS_SELECTION_KEY, "Miles/hrs");
		mSpeedImpactPref =  sPrefrence.getString(UserDataInfo.SPEED_IMPACT_SELECTION_KEY, "Both");
		
	///	lview.SetUnits(units);
		
		//String speedTxt = "Speed" + "\n" + "(" + units + ")";
		mSpeedTxtBtn.setText("Speed");
		
		String alertFrequency =  sPrefrence.getString(UserDataInfo.ALERT_FREQUENCY_SELECTION_KEY, "15");
					
		mUpdateService = new UpdateService(this.getApplicationContext(), mHandler);
		mUpdateService.onSpeedUnits(units);
		mUpdateService.SetAlertFrequency(alertFrequency);
		mUpdateService.start();
		
		
		
		// convert mileage/price changes  depend on units 
		
		if(units.equals("Miles/hrs"))
		{
			mEfficientMilage = 60;
			mSpeedRange = 5;
			
		}
		else
		{
			mEfficientMilage = (int) ((int) 60 * 1.6);;
			mSpeedRange = (int) ((int) 5 * 1.6);
		}
		
	}
	
	private final Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what){
			case MSG_UPDATEGPSDATA:
			{
				 mNearestCheapGas.setEnabled(true);
				Integer speed = msg.getData().getInt(UPDATEGPSDATA);
				mLatalitude = msg.getData().getDouble(UPDATE_LAT);
				mLongitude = msg.getData().getDouble(UPDATE_LONG);
				
				//realSpeed.setText(speedStr);
				
				
				realSpeed.setTextSize(50);
				
				realSpeed.setText(speed.toString());
				
				if(mSpeedImpactPref.equals("Both"))
				   updateMileagePriceChange(speed);
				else if (mSpeedImpactPref.equals("Mileage"))
					updateMileageChange(speed);
				else
					updatePriceChange(speed);
					
				break;
			}	
			
					
			case MSG_AUDIOALERT: {
                Integer speed = msg.getData().getInt(UPDATEGPSDATA);
                Integer percentageChange = 0;
			 	SharedPreferences sPrefrence = getSharedPreferences(UserSetting.PREFERENCES_FILE, MODE_WORLD_READABLE);
			 	Boolean userAlert = sPrefrence.getBoolean(UserDataInfo.ALERT_VALUE, true);
			 	Integer refSpeed =  Integer.getInteger(mrReferenceSpeed);
				if(refSpeed != null)
				   percentageChange = (speed/refSpeed)*100;
				Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
				Ringtone ring = RingtoneManager.getRingtone(getApplicationContext(), notification);
				
				if (percentageChange > 150)
				{
					if((userAlert == true) && (ring != null))
                            ring.play();							
				}
				break;
			
			}
			
		  }
		}
	};
	
	
	///
	/// update the Mileage change
	
	private void updateMileageChange(Integer speed)
	{
		Integer avgEffiviency = getEfficiencyPercentage(speed);
		mMileagePriceChangeTxt.setText("Mileage Change");
		if(avgEffiviency != 0)
		{
			
			String milageChange = "-" + getEfficiencyPercentage(speed).toString() + "%";
		    mMileagePriceChange.setText(milageChange);
		}
		else
			mMileagePriceChange.setText("0");
	}
	
	///
	/// update the Mileage change
	
	private void updatePriceChange(Integer speed)
	{
		mMileagePriceChangeTxt.setText("Price Change");
		Float avgPrice = GasPriceIncrease(speed);
		if(avgPrice != 0.0)
		{ 
			String priceChange = "+" + "$" + GasPriceIncrease(speed).toString();
			mMileagePriceChange.setText(priceChange);
		}
		else
		   mMileagePriceChange.setText("0.0");
		
	}
	
	//
	//Input : Speed
	// output = void 
	// this function going to update mileage and price change
	//
	
	private void updateMileagePriceChange(Integer speed)
	{
			
		if(mDisplayMileageChange)
		   
		{
			updateMileageChange(speed);
			mDisplayMileageChange = false;
		}
		else
		{
			
			updatePriceChange(speed);
			mDisplayMileageChange = true;
							
		}
	
	}
	
	private Integer getEfficiencyPercentage(Integer speed)
	{
		
		Integer local = (speed - mEfficientMilage)/mSpeedRange;
		if(local <= 0)
			return mEfficientList.get(0);
		 if (local >=  (mEfficientList.size()  ))
		   local =  mEfficientList.size() -1;
		
		return mEfficientList.get(local);
	}
		
	
	
	private Float GasPriceIncrease(Integer speed)
	{
		Integer local = speed - mEfficientMilage;
		if(local < 0)
		    mPriceChange = (float)0.0; 
		else
			mPriceChange = mPriceImpact * (local/mSpeedRange);
		
		mPriceChange = (float) (Math.round(mPriceChange*100.0)/100.0);
		
	 	
		return mPriceChange;
	}


	
	
	
	
	
	
	
	
}

