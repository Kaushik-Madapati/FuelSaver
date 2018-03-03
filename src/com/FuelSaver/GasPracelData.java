package com.FuelSaver;


import android.os.Parcel;
import android.os.Parcelable;



public class GasPracelData implements Parcelable{
	
	private double currentLat;
	private double currentLng;
	private double gasStationLat;
	private double gasStationLng;
	private String gasStationaddr;
	private String type;
	
	public String getGasStationaddr() {
		return gasStationaddr;
	}


	public void setGasStationaddr(String gasStationaddr) {
		this.gasStationaddr = gasStationaddr;
	}


	public void setGasStationLat(double gasStationLat) {
		this.gasStationLat = gasStationLat;
	}


	public void setGasStationLng(double gasStationLng) {
		this.gasStationLng = gasStationLng;
	}


	public void setCurrentLat(double currentLat) {
		this.currentLat = currentLat;
	}


	public void setCurrentLng(double currentLng) {
		this.currentLng = currentLng;
	}


	public void setType(String type) {
		this.type = type;
	}

	
	
	public double getGasStationLat() {
		return gasStationLat;
	}

	
	public double getGasStationLng() {
		return gasStationLng;
	}

	
	public double getCurrentLat() {
		return currentLat;
	}

	
	public double getCurrentLng() {
		return currentLng;
	}

	

	public String getParceladdr() {
		return gasStationaddr;
	}

	public String getType() {
		return type;
	}


	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		// TODO Auto-generated method stub
		dest.writeDouble(currentLat);
		dest.writeDouble(currentLng);
		dest.writeDouble(gasStationLat);
		dest.writeDouble(gasStationLng);
		dest.writeString(gasStationaddr);
		dest.writeString(type);
		
	}
	
	
	public GasPracelData(double curentLat, double currentLng, double gasLat, double gasLng, String addr, String type)
	{
		this.currentLat = curentLat;
		this.currentLng = currentLng;
		
		this.gasStationLat = gasLat;
		this.gasStationLng = gasLng;
		this.gasStationaddr = addr;
		
		this.type= type;
	}
	
	
	
	public GasPracelData() {
		// TODO Auto-generated constructor stub
	}
	
		
	
	public  static final Parcelable.Creator<GasPracelData> CREATOR = 
		new Parcelable.Creator<GasPracelData> (){

			@Override
			public GasPracelData createFromParcel(Parcel source) {
				GasPracelData data = new GasPracelData();
				data.setCurrentLat(source.readDouble());
				data.setCurrentLng(source.readDouble());
				data.setGasStationLat(source.readDouble());
				data.setGasStationLng(source.readDouble());
				data.setGasStationaddr(source.readString());
				data.setType(source.readString());
				return data;
				
			}

			@Override
			public GasPracelData[] newArray(int size) {
				// TODO Auto-generated method stub
				return new GasPracelData[size];
			}
		
	};
	

}
