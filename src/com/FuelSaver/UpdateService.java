package com.FuelSaver;

import java.util.Random;



import android.content.Context;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

import android.os.Message;
import android.util.Log;


enum SpeedDataMsgType { TRIP_COMPLETE, SPEED_UPDATE,  MILEAGE_PRICE_UPDATE }

public class UpdateService {
	
	
	public static final int CURRENTDATA = 1;
	private static final int MAX_STOP_CONTER = 60;
	private Handler mHandler = null;
	LocationManager mLocationManger = null;
	Location mCurrentlocation = null;;
	String provider=  LocationManager.GPS_PROVIDER;
	


		// Debugging
	private static final String TAG = "UpdateService";
	public boolean isRunning = false;
	private String mUnits = null;
	
	/// Zero speed counter
	private int zeroSpeed = 0;
	private int nonZeroSpeed = 0;
	private String mAlertFrequency = null;
	
	public static  String GPSDATA = "Gps Data";
	GPS mGPS = null;
	private Context mContext = null;
	private final int mMaxUpdateCounter = 1;
	private int mAccumltedSpeed = 0;
	private int mMileagePriceTimer = 0;
	
	Random ramdomGenarator = null;
	

	 public UpdateService(Context context, Handler handler) {
		
		mContext  = context; 
		mHandler = handler;
		// TODO Auto-generated method stub
					
		
	}
	public synchronized void start() {
		Log.d(TAG, "start");
		///  Gtet GPS update from location provider
		mGPS = new GPS(mContext);
			
	}

	
	private class GPS implements LocationListener {
		
		
		public GPS(Context context)
		{
			mLocationManger = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
	        	Criteria criteria = new Criteria();
	        	provider = mLocationManger.getBestProvider(criteria, true);
			
			if(mLocationManger != null)
			//	mCurrentlocation = mLocationManger.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				mCurrentlocation = mLocationManger.getLastKnownLocation(provider);
			
			startLocationUpdate();
			
			ramdomGenarator = new Random();
		}
		
		public void startLocationUpdate()
		{
			mLocationManger.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5001, 0, this);
		}

		
		public void onLocationChanged(Location location) {

			Float  Speed = location.getSpeed();
			Double  Lat = location.getLatitude();
			Double  Long = location.getLongitude();
			
			
	//		Speed = (float) ramdomGenarator.nextInt(60);
			
								
			if(Speed == 0)
				zeroSpeed++;
			else
			{
			   nonZeroSpeed++;
			   mMileagePriceTimer++;
			}
			
			Integer intAlertFrequency = Integer.getInteger(mAlertFrequency);
			
			///Convert into miliseconds
			
			int msgTimer = 0;
			
			if(intAlertFrequency != null)
			{
				intAlertFrequency *= (60*1000);
			///location update comes every 5 milicecs
				msgTimer = intAlertFrequency/5000;
			}
						
			if(zeroSpeed > MAX_STOP_CONTER)
			{
			//	Message msg = mHandler.obtainMessage(UiFramework.MSG_STOP_TRIP);
			//	mHandler.sendMessage(msg); 
				mLocationManger.removeUpdates(this);
			}
			else
			{
				if(!mLocationManger.isProviderEnabled(LocationManager.GPS_PROVIDER))
					mLocationManger.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5001, 0, this);
				
				Message msg = mHandler.obtainMessage(UiFramework.MSG_UPDATEGPSDATA);
				Bundle bundle = new Bundle();
				Integer iSpeed = GetConvertedSpeed(Speed);
				
				
								
				bundle.putInt(UiFramework.UPDATEGPSDATA, iSpeed);
				bundle.putDouble(UiFramework.UPDATE_LAT, Lat);
				bundle.putDouble(UiFramework.UPDATE_LONG, Long);
				
				
				msg.setData(bundle);
				mHandler.sendMessage(msg); 
				
				if(nonZeroSpeed == msgTimer )
				{
					Message alertMsg = mHandler.obtainMessage(UiFramework.MSG_AUDIOALERT);
					alertMsg.setData(bundle);
					
					mHandler.sendMessage(alertMsg); 
				}
				if(mMileagePriceTimer > mMaxUpdateCounter)
				{
					mMileagePriceTimer = 0;
					Message updateMileageMsg = mHandler.obtainMessage(UiFramework.MSG_MILEAGE_PRICE_UPDATE);
					Bundle milageBundle = new Bundle();
					
					int avgSpeed = mAccumltedSpeed/mMaxUpdateCounter ;
					
					milageBundle.putInt(UiFramework.UPDATE_MILEAGE_DATA, avgSpeed);
					updateMileageMsg.setData(milageBundle);
					
					mHandler.sendMessage(updateMileageMsg); 
					mAccumltedSpeed = 0;
					
				}
				else if(mMileagePriceTimer <= mMaxUpdateCounter)
				{
					mAccumltedSpeed += iSpeed;
				}
			}

			// TODO Auto-generated method stub

		}

		
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

			Log.d(TAG, "onProviderDisabled" );
			
			mLocationManger.removeUpdates(this);
		}
		
		private int GetConvertedSpeed(float speed)
		{
			if(mUnits.equals("Miles/hrs"))
				return  (int)(speed * 2.23);
			else
 				return  (int)(speed/0.277);
		}

		
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

			Log.d(TAG, "onProviderEnabled" );
			
			mLocationManger.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5001, 0, this);

		}

		
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			Log.d(TAG, "onStatusChanged" );

		}
	
	}
	
	public void  onGPSDisable()
	{
		if(mGPS != null)
		   mGPS.onProviderDisabled("GPS");
	}
	
	public void  onGPSEnable()
	{
		if(mGPS != null)
		{
			
			if(!mLocationManger.isProviderEnabled(provider))
				mGPS.onProviderEnabled(provider);
		}
	}
	
	public void onSpeedUnits(String units)
	{
		mUnits = units;
	}
	
	public void SetAlertFrequency(String frequency)
	{
		mAlertFrequency = frequency ;
	}


}
