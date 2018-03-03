package com.FuelSaver;

import java.util.ArrayList;

import com.FuelSaver.GasInfoService.StationInfo;


import android.app.Activity;

import android.content.Intent;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;



enum  GasType { NEAREST_GAS, CHEAPEST_GAS };
public class GasInfoActivity extends Activity{
	
	StationInfo  mNeatestGasStationData;
	StationInfo  mCheapestGasStationData;
	Button mGasStationDataType1 = null;
	Button mGasStationInfo1 = null;
	Button mGasStationInfo2 = null;
	Button mGasStationDataType2 = null;
	
	static final int DIALOG_CONECTION_ERROR = 100;
	static final int DIALOG_ADDRESS_ERROR = 101;
	
	
	public static final String STATION_NAME = "Station Name";
	public static final String ADDR_NAME = "Address";
	public static final String CITY_NAME = "City";
	public static final String PRICE_VALUE = "Price Value";
	
	public static final String CHEAP_GAS_DATA = "Cheap Gas";
	public static final String NEAREST_GAS_DATA = "Nearest Gas";
	
	
	public static final int MSG_CONECTION_ERROR = 12;
	public static final int MSG_NEAREST_STATION = 13;
	public static final int MSG_CHEAPEST_STATION = 14;
	public static final int MSG_GENERAL_ERROR = 17;
	
	private GasInfoService mCheapGasSrv = null;
	
	private Drawable mErrorIcon = null;
	private  Boolean isNearestGas = false;
	private  Boolean isCheapestGas = false;
	
	private Double mLatitude = 0.0;
	private Double mLongitude = 0.0;
	
	LinearLayout  mFindGasLayout = null;

	

	
	ProgressBar pb = null;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nereast_gas_info);
		mGasStationDataType1 = (Button)findViewById(R.id.data_type_1);
		mGasStationInfo1 = (Button)findViewById(R.id.station_info_1);
		mGasStationDataType2 = (Button)findViewById(R.id.data_type_2);
		mGasStationInfo2 = (Button)findViewById(R.id.station_info_2);
	//	setContentView(R.layout.progress_dialog);
	
		
		Resources res = getResources();
		
		mErrorIcon = res.getDrawable(R.drawable.stat_notify_error);
		
		pb = (ProgressBar)findViewById(R.id.gas_progress);
		mGasStationDataType2.setVisibility(4 );
		Intent intent = getIntent();
		
		
		mLatitude = intent.getDoubleExtra(UiFramework.CURRENT_LAT, 0.0);
		mLongitude = intent.getDoubleExtra(UiFramework.CURRENT_LONG, 0.0);
		
		mCheapGasSrv = new GasInfoService(mLatitude,mLongitude, mCheapGasHandler );
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	    isNearestGas = false;
		isCheapestGas = false;
		
		if(mCheapGasSrv == null)
			return; 
		
		mNeatestGasStationData = mCheapGasSrv.getNearestGas();
		mCheapestGasStationData = mCheapGasSrv.getCheapestGas();
		
	
			
	}
	
	// 
	//Input void
	//desc: get vies hadles to updat data on buttons
	//output : void
	//
		
	

	private final Handler mCheapGasHandler = new Handler(){
		public void handleMessage(Message msg) {
			
			switch(msg.what) {
			
				
															
			case MSG_GENERAL_ERROR:
			{
				pb.setVisibility(ProgressBar.GONE);
					
			    mGasStationDataType2.setVisibility(0 );
				mGasStationDataType2.setBackgroundDrawable(mErrorIcon);
				mGasStationInfo2.setText("Sorry error in retrieving data");
							
				break;
				
			}			
			case MSG_NEAREST_STATION:
			{
				mGasStationDataType2.setVisibility(4 );
				pb.setVisibility(ProgressBar.GONE);
				
				isNearestGas = true;
				
				ShowMap();
				break;
			}	
			case MSG_CHEAPEST_STATION:
			{
				mGasStationDataType2.setVisibility(4 );
				pb.setVisibility(ProgressBar.GONE);
				isCheapestGas = true;
				ShowMap();
				
				
				
			    break;
			}	
		    case MSG_CONECTION_ERROR:
			{
				pb.setVisibility(ProgressBar.GONE);
				
				 mGasStationDataType2.setVisibility(0 );
				 mGasStationDataType2.setBackgroundDrawable(mErrorIcon);
				 mGasStationInfo2.setText("Sorry error in retrieving data");
				break;
		
			}
				
		   
		  }
		}
	};
	
	private Boolean ShowMap()
	{
		Boolean result = false;
		if((isCheapestGas) &&
		   (isNearestGas))
		{
			pb.setVisibility(ProgressBar.GONE);
			mGasStationDataType2.setVisibility(4 );
			
			Intent i = new Intent(this, MapviewActivity.class);
			
			
			ArrayList<GasPracelData> dataList = new ArrayList<GasPracelData>();
			
    		mNeatestGasStationData = mCheapGasSrv.GetNearestGasInfo();
			mCheapestGasStationData = mCheapGasSrv.GetCheapestGasInfo();
			
			GasPracelData data1 = new GasPracelData(mLatitude, mLongitude, mNeatestGasStationData.lat,
					mNeatestGasStationData.lng,
					mNeatestGasStationData.completeInfo, "Nearest");
			GasPracelData data2 = new GasPracelData(mLatitude, mLongitude, mCheapestGasStationData.lat,
					mCheapestGasStationData.lng,
					mCheapestGasStationData.completeInfo, "Cheapest");
			dataList.add(data1);
			dataList.add(data2);
		
			
			i.putParcelableArrayListExtra("GasSationInfo", dataList);
			startActivity(i);
			result = true;
			finish();
		}
		
		return result;
		
	}
    void showStationInfo(GasType type, StationInfo stationInfo )
    {
    	
    	String name = "Name "+ stationInfo.GetStationName();
		String addr = "Address" +stationInfo.GetAddress()+ "\n"+ "City"+  stationInfo.GetCity();
		String price = "Price" + stationInfo.GetPrice();
		
		String stationInfoStr = name + "\n" + addr + "\n" + price;
    	
    	if(type == GasType.NEAREST_GAS )
    	{
    		
    		mGasStationDataType1.setText("Cheapest Gas Station");
    		mGasStationInfo1.setText(stationInfoStr);
    		
    	}
    	if(type == GasType.CHEAPEST_GAS )
    	{
    		
    		mGasStationDataType2.setText("Nearest Gas Station");
    		mGasStationInfo2.setText(stationInfoStr);
    		
    	}
    	
    }
}
