package com.FuelSaver;

import java.util.ArrayList;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.PathOverlay;


import com.FuelSaver.UserSetting.UserDataInfo;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

enum MapMode  { SIMPLE_MAP, SAVE_CAR_POS, WALK_TO_CAR,  MAP_WITH_POI} 
public class MapviewActivity extends Activity implements LocationListener{

	private MapController mapController;
	private MapView gasStationMapView = null;
	private ItemizedOverlay<OverlayItem> myLocationOverlay;
	private ArrayList<OverlayItem> mOverlays ;
	private ResourceProxy mResourceProxy;
	ItemizedOverlay<OverlayItem> mCarLocationOverlay = null;
	LocationManager mMapLocManger = null;

	private int minLat = Integer.MAX_VALUE;
	private int maxLat = Integer.MIN_VALUE;
	private int minLon = Integer.MAX_VALUE;
	private int maxLon = Integer.MIN_VALUE;
	private int zoomSpanLat = 0;
	private int zoomSpanLon = 0;
	

	private double mCurrLat = 0;
	private double mCurrLng = 0;

	public static final String STARTADDR = "StartAddr"; 
	public static final String ENDADDR = "EndAddr"; 
	private String mCarPosLat = null;
	private String mCarPosLng = null;
	GeoPoint mOverLayCurrentpoint = null;
	PathOverlay mPathOverlay  = null;
	OverlayItem  mCurrentLocItem = null;
	Drawable mCurrentLocIcon = null;
	
	
	FuelSaverApp mApp = null;
	
	String mProvider=  LocationManager.GPS_PROVIDER;




	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_view);
		
		mApp = (FuelSaverApp)getApplicationContext();
		
		
		
		/// Set  mapview for POI and MAP

		gasStationMapView = (MapView)findViewById(R.id.gas_station_mapview);
		gasStationMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
		gasStationMapView.setBuiltInZoomControls(true);
		gasStationMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
		mapController = gasStationMapView.getController();
		mapController.setZoom(16);

		///
		/// Get Data POI  and current location from parcel
		///

		Intent intent = getIntent();
		ArrayList <GeoPoint> items =  new ArrayList<GeoPoint>();
		ArrayList<GasPracelData> data  = new ArrayList<GasPracelData>();
		data = intent.getParcelableArrayListExtra("GasSationInfo");
		mCurrLat = data.get(0).getCurrentLat();
		mCurrLng = data.get(0).getCurrentLng();
		
	    ///
		/// Create OVerLayItems and add to map
		///
    	mOverlays = new ArrayList<OverlayItem>();
		GeoPoint points2 = new GeoPoint(data.get(0).getCurrentLat(), data.get(0).getCurrentLng() );	
		items.add(points2);
		mapController.setCenter(points2);
		mOverLayCurrentpoint  = new GeoPoint(data.get(0).getCurrentLat(), data.get(0).getCurrentLng() );
		String des = String.valueOf(data.get(0).getCurrentLat()) + "\n" + String.valueOf(data.get(0).getCurrentLng());
		mCurrentLocItem = new OverlayItem("Current Location ", des, mOverLayCurrentpoint);
		mOverlays.add(mCurrentLocItem);
		mCurrentLocIcon = this.getResources().getDrawable(R.drawable.ic_maps_indicator_current_position_small);
		mCurrentLocItem.setMarker(mCurrentLocIcon);
		
		/// show pois on the map if not just map

		if(!data.get(0).getType().equals("Map")) {

			// Add point on the map
			GeoPoint overNearestLaypoint  = new GeoPoint(data.get(0).getGasStationLat(), data.get(0).getGasStationLng() );
			OverlayItem neatestGasItem = new OverlayItem("Nearest Gas Station", data.get(0).getGasStationaddr(), overNearestLaypoint);
			Drawable nearIcon = this.getResources().getDrawable(R.drawable.nearest_gas);

			neatestGasItem.setMarker(nearIcon);

			mOverlays.add(neatestGasItem);

			GeoPoint overCheapestLaypoint  = new GeoPoint(data.get(1).getGasStationLat(), data.get(1).getGasStationLng() );
			OverlayItem cheapestGasItem = new OverlayItem("Cheapest Gas Station", data.get(1).getParceladdr(), overCheapestLaypoint);
			mOverlays.add(cheapestGasItem);

			Drawable cheapIcon = this.getResources().getDrawable(R.drawable.cheapest_gas);
			cheapestGasItem.setMarker(cheapIcon);

			items.add(overNearestLaypoint);
			items.add(overCheapestLaypoint);

			for(GeoPoint item : items)
			{
				int lat = item.getLatitudeE6();
				int lon = item.getLongitudeE6();

				maxLat = Math.max(lat, maxLat);
				minLat = Math.min(lat, minLat);
				maxLon = Math.max(lon, maxLon);
				minLon = Math.min(lon, minLon);

			}
			double fitFactor = 1.5;
			zoomSpanLat = (int)(Math.abs(maxLat - minLat)*fitFactor);
			zoomSpanLon = (int)(Math.abs(maxLon - minLon)*fitFactor);
			
			
			mApp.setmMapMode(MapMode.MAP_WITH_POI);
			
		}
		
		/// Draw Items on the Map
		DrawOverLays(mOverlays);

		///  Se thet location provide 
		mMapLocManger = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(1);
		mProvider = mMapLocManger.getBestProvider(criteria, true);
		

	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		/// Don't show updated map when display POI because POI info will not be relavent 
		///  as you update map with GPS 
		///
		if(mApp.getmMapMode() != MapMode.MAP_WITH_POI)
			startLocationUpdate();
		
		DisplayItemsOnMap(mApp.getmMapMode(),mOverLayCurrentpoint);
		
	}

	private void DrawOverLays(ArrayList<OverlayItem> overLays )
	{
		mResourceProxy = new DefaultResourceProxyImpl(getApplicationContext());
		//	myLocationOverlay = new ItemizedIconOverlay<OverlayItem>(overlays, null, mResourceProxy);

		myLocationOverlay = new ItemizedIconOverlay<OverlayItem>(overLays, 
				new  ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {

			@Override
			public boolean onItemLongPress(int arg0, OverlayItem arg1) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean onItemSingleTapUp(int arg0, OverlayItem arg1) {


				Toast.makeText(getApplicationContext(), arg1.mTitle + "\n" + arg1.mDescription +"\n" ,Toast.LENGTH_LONG).show();

				// TODO Auto-generated method stub
				return true;
			}


		}, mResourceProxy);




		gasStationMapView.getOverlays().add(myLocationOverlay);
		gasStationMapView.invalidate();
	}
	
	
	public void startLocationUpdate()
	{
		mMapLocManger.requestLocationUpdates(mProvider, 1000, 0, this);
	}




	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		onProviderDisabled("GPS");
		mApp.setmMapMode(MapMode.SIMPLE_MAP);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub

		/// Don't show menu  when display POI because POI info will not be relavent 
		///  as you update map with GPS 
		///
		if(mApp.getmMapMode() != MapMode.MAP_WITH_POI)
			getMenuInflater().inflate(R.menu.map_menu, menu);
		return true;
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
   
	   /// Mark Car Position 
	private void MarkPos(GeoPoint Pos)
	{
			OverlayItem  carPosItem = new OverlayItem("Position ", "your  Pos", Pos);
			ArrayList<OverlayItem> posOverlayItem = new ArrayList<OverlayItem>();
			posOverlayItem.add(carPosItem);
			mCarLocationOverlay = new ItemizedIconOverlay<OverlayItem>(posOverlayItem, null, mResourceProxy);
			gasStationMapView.getOverlays().add(mCarLocationOverlay);
			
		   
	}
	

	private void MarkRoute(GeoPoint currPos)
	{
		GeoPoint carPos = new GeoPoint(Double.valueOf(this.mCarPosLat), Double.valueOf(this.mCarPosLng));
		mPathOverlay= new PathOverlay(Color.BLUE,this);
		mPathOverlay.addPoint(carPos);
		 
		mPathOverlay.addPoint(currPos);
		gasStationMapView.getOverlays().add(mPathOverlay);
		
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch(item.getItemId()) {

		case R.id.SaveCarPos: {
			mApp.setmMapMode(MapMode.SAVE_CAR_POS);
			
			onSavePos();
			DisplayItemsOnMap(mApp.getmMapMode(), mOverLayCurrentpoint);
			mApp.setmMapMode(MapMode.SIMPLE_MAP);
			
			
			return true;
		}

		case R.id.FindCarPos: {
			if(onFindCarPos())
			{
				mApp.setmMapMode(MapMode.WALK_TO_CAR);
				DisplayItemsOnMap(mApp.getmMapMode(), mOverLayCurrentpoint);

			}
			else
				Toast.makeText(getApplicationContext(), "No Car Position is saved ",Toast.LENGTH_LONG).show();
				
			return true;
		}
		case R.id.ClearCarPos : {
			onClearCarPos();
			
			mApp.setmMapMode(MapMode.SIMPLE_MAP);
			DisplayItemsOnMap(mApp.getmMapMode(), mOverLayCurrentpoint);
			return true;
		}
			
		default: {
			return false;
		}

		}
		

	}

    ///
	/// Save Car Position to Setting preference 
	public void onSavePos()
	{
		SharedPreferences sPrefrence = getSharedPreferences(UserSetting.PREFERENCES_FILE, MODE_WORLD_READABLE);
		SharedPreferences.Editor e = sPrefrence.edit();
		e.putString(UserDataInfo.CAR_POS_LAT_KEY, Double.toString(mCurrLat));
		e.putString(UserDataInfo.CAR_POS_LNG_KEY, Double.toString(mCurrLng));
		e.commit();

	}
	///
	/// Save Car Position to Setting preference 
	public void onClearCarPos()
	{
		SharedPreferences sPrefrence = getSharedPreferences(UserSetting.PREFERENCES_FILE, MODE_WORLD_READABLE);
		SharedPreferences.Editor e = sPrefrence.edit();
		e.putString(UserDataInfo.CAR_POS_LAT_KEY, null);
		e.putString(UserDataInfo.CAR_POS_LNG_KEY, null);
		e.commit();

	}

	/// 
	/// Check if Car position is saved
	///
	public boolean onFindCarPos()
	{

		SharedPreferences sPrefrence = getSharedPreferences(UserSetting.PREFERENCES_FILE, MODE_WORLD_READABLE);
		mCarPosLat =  sPrefrence.getString(UserDataInfo.CAR_POS_LAT_KEY, null);
		mCarPosLng =  sPrefrence.getString(UserDataInfo.CAR_POS_LNG_KEY, null);
		if((mCarPosLat == null) || (mCarPosLat == null))
			return false;

		return true;

	}


	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		if((zoomSpanLat == 0) || (zoomSpanLon == 0))
			return;

		mapController.zoomToSpan(  zoomSpanLat, zoomSpanLon);
		mapController.animateTo(new GeoPoint((maxLat + minLat)/2, (maxLon + minLon )/2));
		gasStationMapView.invalidate();


	}


	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

		Integer latitude = (int)(location.getLatitude()*1E6);
		Integer longitude = (int)(location.getLongitude()*1E6);
		
		
		this.mCurrLat = location.getLatitude();
		this.mCurrLng = location.getLongitude();
				
		GeoPoint mapCenter = new GeoPoint(latitude,longitude );
		
		mOverLayCurrentpoint.setCoordsE6(latitude, longitude);
		
		
		DisplayItemsOnMap(mApp.getmMapMode(),mapCenter);
		
		//Toast.makeText(getApplicationContext(), "Lat : "+  latitude.toString() +"\n" + "Lng :" + longitude.toString() , Toast.LENGTH_LONG).show();



	}
	
		
	/// Mark Current location on map 
	///
	private void UpdateCurrentLocationOnMap(GeoPoint point, boolean clearOverLay)
	{
		if(clearOverLay)
			gasStationMapView.getOverlays().clear();
		mapController.setCenter(point);
		mOverlays = new ArrayList<OverlayItem>();
		mCurrentLocItem = new OverlayItem("Current Location ", "temp", mOverLayCurrentpoint);
		mOverlays.add(mCurrentLocItem);
		mCurrentLocItem.setMarker(mCurrentLocIcon);
		DrawOverLays(mOverlays);
	}
	
    private void DisplayItemsOnMap(MapMode  mapMode, GeoPoint point)
    {
    	   	
    	switch(mapMode)
    	{
    		case SIMPLE_MAP :
    			UpdateCurrentLocationOnMap(point, true);
    		break;
    		case  SAVE_CAR_POS:
    		{
    			if(onFindCarPos())
    			{
	    			GeoPoint carPos = new GeoPoint(Double.valueOf(this.mCarPosLat), Double.valueOf(this.mCarPosLng));
	    			UpdateCurrentLocationOnMap(point, true);
	    			MarkPos(carPos);
    			}
    		}
    		break;
    		case  WALK_TO_CAR:
    		{
    			if(onFindCarPos())
    			{
	    			GeoPoint carPos = new GeoPoint(Double.valueOf(this.mCarPosLat), Double.valueOf(this.mCarPosLng));
	    			UpdateCurrentLocationOnMap(point, true);
	    			MarkPos(carPos);
	    			this.MarkRoute(point);
    			}
    		}
    		break;
    		default :
    		  break;
    	}
    	gasStationMapView.invalidate();
    }


	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		mMapLocManger.removeUpdates(this);

	}


	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}


	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}


}