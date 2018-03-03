package com.FuelSaver;

import android.app.Application;
import android.os.Handler;

public class FuelSaverApp extends Application {
	
	private Handler appHandler;
	private int mAverageSpeed = 0;
	private int mAverageMileageChange = 0;
	private float mAveragePriceChange = 0;
	MapMode mMapMode = MapMode.SIMPLE_MAP;
	
	public MapMode getmMapMode() {
		return mMapMode;
	}

	public void setmMapMode(MapMode mMapMode) {
		this.mMapMode = mMapMode;
	}

	public int getmAverageSpeed() {
		return mAverageSpeed;
	}

	public void setmAverageSpeed(int mAverageSpeed) {
		this.mAverageSpeed = mAverageSpeed;
	}

	public int getmAverageMileageChange() {
		return mAverageMileageChange;
	}

	public void setmAverageMileageChange(int mAverageMileageChange) {
		this.mAverageMileageChange = mAverageMileageChange;
	}

	public float getmAveragePriceChange() {
		return mAveragePriceChange;
	}

	public void setmAveragePriceChange(float mAveragePriceChange) {
		this.mAveragePriceChange = mAveragePriceChange;
	}

	
	

	public Handler getAppHandler() {
		return appHandler;
	}

	public void setAppHandler(Handler appHandler) {
		this.appHandler = appHandler;
	}

}
