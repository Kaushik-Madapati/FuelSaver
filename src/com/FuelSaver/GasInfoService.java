package com.FuelSaver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;




import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


enum GasStationType { NEAREST, CHEAP};

public class GasInfoService {

	//public static final String  API_URL = "http://devapi.mygasfeed.com/stations/radius/" ;
	public static final String  API_URL = "http://api.mygasfeed.com/stations/"  ;
	//public static final String AUTH_URL = "10/reg/distance/rfej9napna.json?callback=?";
	public static final String LOW_PRICE_URL = "5/reg/price/";
	public static final String NEAREST_URL = "5/reg/distance/";
	public static final String API_KEY = "i39f3fxgvl.json?callback=?";
	public static final String STATION_INFO_URL = "10/reg/price/i39f3fxgvl.json?callback=?";
	
	Double mCurrLatitude = 0.0;
	Double mCurrLongitude = 0.0;
	
	private static String TAG = "Chaep GAS" ;

	private Handler  mHandle = null;

	String mUrlOutput = null;
	List<String> mStationAddress = new ArrayList<String>();

	StationInfo mNearestStationInfo = new StationInfo();
	StationInfo mCheapestStationInfo = new StationInfo();

	GasInfoService(Double lat, Double longitude, Handler handle)
	{
		mCurrLatitude = lat;
		mCurrLongitude = longitude;
		mHandle = handle;

	}


	public StationInfo getCheapestGas()
	{
		String urlString = API_URL + "radius/"+ String.valueOf(mCurrLatitude) + "/" + String.valueOf(mCurrLongitude) + "/" + LOW_PRICE_URL + API_KEY;
		new GetCheapestGas().execute(urlString);

	//	if(RetiveDataFromURL(mUrlOutput, mCheapestStationInfo))
	//		return mCheapestStationInfo;
	//	else
			return null;

	}
	StationInfo getNearestGas()
	{

		String urlString = API_URL + "radius/"+ String.valueOf(mCurrLatitude) + "/" + String.valueOf(mCurrLongitude) + "/" + NEAREST_URL + API_KEY;
		new GetNeatestGas().execute(urlString);

	//	if(RetiveDataFromURL(mUrlOutput, mNearestStationInfo))
	//		return mNearestStationInfo;
	//	else
			return null;


	}
	
	public StationInfo GetNearestGasInfo() {
		return mNearestStationInfo;
	}
	
	
	public StationInfo GetCheapestGasInfo() {
		return mCheapestStationInfo;
	}
	class GetCheapestGas extends AsyncTask<String, Void, String>	
	{

		@Override
		protected String doInBackground(String... arg0) {

			try {
				URL url;
				url = new URL(arg0[0]);
				HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
				urlConnection.setRequestMethod("GET");
				urlConnection.setDoInput(true);
				urlConnection.setDoOutput(true);
				Log.d(TAG, "on thread started");

				try {
					urlConnection.connect();
				}catch (IOException e)
				{
					Message msg = mHandle.obtainMessage(GasInfoActivity.MSG_CONECTION_ERROR);
					mHandle.sendMessage(msg); 
					return null ;

				}
						
				InputStream result = urlConnection.getInputStream();
				String outString = convertStreamToString(result);
				return RetiveDataFromURL(outString,mCheapestStationInfo );

			}  catch (Exception e1) {
				// TODO Auto-generated catch block
				 Message msg = mHandle.obtainMessage(GasInfoActivity.MSG_GENERAL_ERROR);
				 mHandle.sendMessage(msg); 
				return null ;
			}


		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result != null)
			{
				Message msg = mHandle.obtainMessage(GasInfoActivity.MSG_CHEAPEST_STATION);
				Bundle bundle = new Bundle();
				bundle.putString(GasInfoActivity.CHEAP_GAS_DATA, result);
				msg.setData(bundle);
				mHandle.sendMessage(msg); 	
			}

		}


	}


	class GetNeatestGas extends AsyncTask<String, Void, String>	
	{

		@Override
		protected String doInBackground(String... arg0) {

			try {
				URL url;
				url = new URL(arg0[0]);
				HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
				urlConnection.setRequestMethod("GET");
				urlConnection.setDoInput(true);
				urlConnection.setDoOutput(true);
				Log.d(TAG, "on thread started");

				try {
					urlConnection.connect();
				}catch (IOException e)
				{
					//		Message msg = mHandle.obtainMessage(CheapGasActivity.MSG_CONECTION_ERROR);
					//		mHandle.sendMessage(msg); 
					return null ;

				}
				InputStream result = urlConnection.getInputStream();
				String outString = convertStreamToString(result);
				return RetiveDataFromURL(outString,mNearestStationInfo );

			}  catch (Exception e1) {
				// TODO Auto-generated catch block
				Message msg = mHandle.obtainMessage(GasInfoActivity.MSG_GENERAL_ERROR);
			    mHandle.sendMessage(msg); 
				return null ;
			}


		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			mUrlOutput = result;
			
			if(result != null)
			{
				Message msg = mHandle.obtainMessage(GasInfoActivity.MSG_NEAREST_STATION);
				Bundle bundle = new Bundle();
				bundle.putString(GasInfoActivity.NEAREST_GAS_DATA, result);
				msg.setData(bundle);
				mHandle.sendMessage(msg);
			}
			

		}


	}



	private String RetiveDataFromURL(String input, StationInfo stationInfo )
	{

		try {

			///
			/// Get station ID 
			///

			int fisrtLatOccurance = input.indexOf("\"lat\":");

			if(fisrtLatOccurance == -1)
			{
				Message msg = mHandle.obtainMessage(GasInfoActivity.MSG_GENERAL_ERROR);
				mHandle.sendMessage(msg); 
				return null;

			}

			String stationId = input.substring(fisrtLatOccurance - 20, fisrtLatOccurance);

			int fisrtIdOccurance = stationId.indexOf("\"id\":");

			if(fisrtIdOccurance == -1)
			{
				Message msg = mHandle.obtainMessage(GasInfoActivity.MSG_GENERAL_ERROR);
				mHandle.sendMessage(msg); 
				return null;
			}

			String stationExcatId = stationId.substring(fisrtIdOccurance);

			int staionID = GetIntegerValue(stationExcatId);

			if(staionID == -1)
			{
				Message msg = mHandle.obtainMessage(GasInfoActivity.MSG_GENERAL_ERROR);
				mHandle.sendMessage(msg); 
				return null;
			}


			return GetSationInfo(staionID, stationInfo);

			//	ParseStationInfo(parseGeocoding[1]);


		} catch (Exception e1) {
			// TODO Auto-generated catch block
			Message msg = mHandle.obtainMessage(GasInfoActivity.MSG_GENERAL_ERROR);
			mHandle.sendMessage(msg); 
			return null;
		}

	}

	///
	/// Input StationID  string 
	/// output int : station ID in int
	/// Des : this function will parse string and return   string ID in int
	///

	private int GetIntegerValue(String input)
	{


		String local = null;

		int index = 0;

		for(int i = 0; i < input.length(); i++)
		{
			if(Character.isDigit(input.charAt(i)))
			{
				//chStationID[index] = stationExcatId.charAt(i);
				if(local == null)
					local = String.valueOf(input.charAt(i));
				else
					local += input.charAt(i);
				index++;
			}
		}

		return Integer.parseInt(local);


	}
	
	
	private float GetFloatValue(String input)
	{


		String local = null;

		int index = 0;

		for(int i = 0; i < input.length(); i++)
		{
			if((Character.isDigit(input.charAt(i))) || 
			   (input.charAt(i) == '.') ||
			   (input.charAt(i) == '-'))
			{
				//chStationID[index] = stationExcatId.charAt(i);
				if(local == null)
					local = String.valueOf(input.charAt(i));
				else
					local += input.charAt(i);
				index++;
			}
		}

		return Float.parseFloat(local);


	}

	///
	/// Input StationID  int 
	/// output boolean : success  or failure in  getting station  info
	/// Des : this function will call mygasfeed.com and get more info about station and 
	///  extract required data.
	///
	private String  GetSationInfo(Integer stationId, StationInfo stationInfo)
	{
		try {
			URL stationInfoUrl = new URL(API_URL + "details/"+ stationId + "/" + API_KEY);
			HttpURLConnection urlConnection = (HttpURLConnection)stationInfoUrl.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);

			try {
				urlConnection.connect();
			}catch (IOException e)
			{
				Message msg = mHandle.obtainMessage(GasInfoActivity.MSG_CONECTION_ERROR);
				mHandle.sendMessage(msg); 
				e.printStackTrace();
			}

			InputStream result = urlConnection.getInputStream();

			String responce = convertStreamToString(result);


			int startAddress = responce.indexOf("\"address\":");
			int endAddress = responce.indexOf("\"diesel\":");

			if((startAddress == -1) || (endAddress == -1))
			{
				stationInfo.address = "N/A";

			}
			else {

				String address = responce.substring(startAddress, endAddress);
				System.out.print(address)	;

				stationInfo.address = SplitTokenAndValue(address, "address");
			}

			int startCity = responce.indexOf("\"city\":");
			int endCity = responce.indexOf("\"region\":");

			if((startAddress == -1) || (endAddress == -1))
			{
				stationInfo.city = "N/A";
			}
			else {

				String city = responce.substring(startCity, endCity);

				stationInfo.city = SplitTokenAndValue(city, "city");
				System.out.print(city)	;
			}


			int startStationName = responce.indexOf("\"station_name\":");
			int endStationName = responce.indexOf("\"reg_price\":");
			if((startAddress == -1) || (endAddress == -1))
			{
				stationInfo.stationName = "NA";

			}
			else  {
				String stationName = responce.substring(startStationName, endStationName);
				stationInfo.stationName = SplitTokenAndValue(stationName, "station_name");
				System.out.print(stationName)	;
			}

			int startRegPrice = responce.indexOf("\"reg_price\":");
			int endRegPrice = responce.indexOf("\"reg_date\":");
			if((startAddress == -1) || (endAddress == -1))
			{
				stationInfo.price = "N/A";
			}
			else  {
				String regPrice = responce.substring(startRegPrice, endRegPrice);
				stationInfo.price = SplitTokenAndValue(regPrice, "reg_price");
				System.out.print(regPrice)	;
			}
			
			/// get LAt value 
			
			int startLat = responce.indexOf("\"lat\":");
			int endLat = responce.indexOf("\"lng\":");
			if((startLat == -1) || (endLat == -1))
			{
				stationInfo.lat = 0;
			}
			else  {
				String strLat = responce.substring(startLat, endLat);
				stationInfo.lat = GetFloatValue(strLat);
				System.out.print(stationInfo.lat)	;
			}
			
			
			// get Longitude 
			int startLng = responce.indexOf("\"lng\":");
			int endLng = responce.indexOf("\"logo\":");
			if((startLng == -1) || (endLng == -1))
			{
				stationInfo.lng = 0;
			}
			else  {
				String strLng = responce.substring(startLng, endLng);
				stationInfo.lng = GetFloatValue(strLng);
				System.out.print(stationInfo.lng)	;
			}


			String name = "Name "+ stationInfo.GetStationName();
			String addr = "Address" +stationInfo.GetAddress()+ "\n"+ "City"+  stationInfo.GetCity();
			String price = "Price" + stationInfo.GetPrice();
			
			String stationInfoStr = name + "\n" + addr + "\n" + price;
			stationInfo.completeInfo = stationInfoStr;

			return stationInfoStr;


		} catch (Exception e1) {
			// TODO Auto-generated catch block
			Message msg = mHandle.obtainMessage(GasInfoActivity.MSG_GENERAL_ERROR);
			mHandle.sendMessage(msg); 
			return null;
		}
	}

	String SplitTokenAndValue(String input, String token)
	{
		String [] local = new String[2];

		local = input.split(token);

		local[1].replace('"', ' ');

		return local[1].replace('"', ' ');

	}

	public String convertStreamToString(InputStream in) throws IOException {

		if(in != null)
		{
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];

			try{
				Reader reader = new BufferedReader( new InputStreamReader(in, "UTF-8"));
				int n;
				while((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);

				}
			} finally{
				in.close();
			}

			return writer.toString();
		}
		return null;

	}

	class StationInfo
	{
		String  address;
		String  city;
		String  stationName;
		String  price = "NA";
		String  completeInfo ;
		float  lat;
		float lng;
		

		String GetAddress() {  return address; }
		String GetCity() {  return city; }
		String GetStationName() {  return stationName; }
		String GetPrice() {  return price; }
	}
	
	


}
