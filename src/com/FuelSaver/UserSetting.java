package com.FuelSaver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;



enum UserSettingType { SPEED_IMPACT,  UNITS, ALERT, ALERT_FREQUENCY, CAR_POS_LAT , CAR_POS_LNG, INTRO_TEXT}

public class UserSetting extends Activity{
	
	/**
     * The name of a properties file that stores the position and
     * selection when the activity is not loaded.
     */
    public static final String PREFERENCES_FILE = "UserPrefs";
    
    
    /**
     *  The initial position of the spinner when it is first installed.
     */
    public static final int DEFAULT_POSITION = 2;

    
    
	protected int mPos;
    protected String mSelection;
    
    private CheckBox mAlertCheckBox = null;
	
	protected ArrayAdapter<CharSequence> unitsAdapter;
	protected ArrayAdapter<CharSequence> sppedImpactAdapter;
	protected ArrayAdapter<Integer> mAlertFrequencyAdapter;
	
	 
      protected int mUnitsPos;
      protected String mUnitsSelection;
      
      protected int mSpeedImpactPos;
      protected String mSpeedImpactSelection;
      
      protected int mAlertState;
      protected boolean mAlertValue;
      
      protected int mAlertFrequencyPos;
      protected String mAlertFrequencySelection;
      
      
      protected String mCarPosLat;
      protected String mCarPosLng;
      
      protected boolean mIntroText = true;
      
     private Spinner mUnitsSpinner = null;
     private Spinner mSpeedImpactSpinner = null;
     private Spinner mAlertSpinner = null;
           
      
          
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      setContentView(R.layout.user_setting);
      
      
      // Alert frequency 
      mAlertFrequencyAdapter = new ArrayAdapter<Integer>(this,android.R.layout.simple_dropdown_item_1line);
      for(int i= 0; i <10; i++)
    	  mAlertFrequencyAdapter.add(5+i*5);
      
               
      // add the items to the comb box
      
      mSpeedImpactSpinner = (Spinner) findViewById(R.id.price_mileage_spinner);
	  this.sppedImpactAdapter = ArrayAdapter.createFromResource(this, R.array.SpeedImpact,
             android.R.layout.simple_dropdown_item_1line);
	  mSpeedImpactSpinner.setAdapter(this.sppedImpactAdapter);
	    
	  mSpeedImpactSpinner.getBackground().setColorFilter(Color.WHITE,PorterDuff.Mode.DARKEN);
	  
	   
	  mUnitsSpinner = (Spinner) findViewById(R.id.UnitSpinner);
	  this.unitsAdapter = ArrayAdapter.createFromResource(this, R.array.Unit,
             android.R.layout.simple_dropdown_item_1line);
	  mUnitsSpinner.setAdapter(this.unitsAdapter);
	    
	  mUnitsSpinner.getBackground().setColorFilter(Color.WHITE,PorterDuff.Mode.DARKEN);
	    
	    
	  mAlertSpinner = (Spinner) findViewById(R.id.AlertSpinner);
	  mAlertSpinner.setAdapter(this.mAlertFrequencyAdapter);
	  mAlertSpinner.getBackground().setColorFilter(Color.WHITE,PorterDuff.Mode.DARKEN);
	    
	   
	    
	    mAlertCheckBox = (CheckBox)findViewById(R.id.Alert);
	    
	    mAlertCheckBox.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			// TODO Auto-generated method stub
				onAlertSelection( v);			
			}
		});
	    
	    
       Button tips = (Button)findViewById(R.id.setting_tips);
       
       if(tips != null)
       {
	    
    	   tips.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
				// TODO Auto-generated method stub
					onTips();			
				}
			});
       }
	    	    	    	    
 	    /*
       * Create a listener that is triggered when Android detects the
       * user has selected an item in the Spinner.
       */

        
         OnItemSelectedListener spinnerUnitsListener = new myOnItemSelectedListener(this,UserSettingType.UNITS);
         OnItemSelectedListener spinnerSpeedImpactListener = new myOnItemSelectedListener(this,UserSettingType.SPEED_IMPACT);
         OnItemSelectedListener spinneAlertistener = new myOnItemSelectedListener(this,UserSettingType.ALERT_FREQUENCY);

      /*
       * Attach the listener to the Spinner.
       */
	    
        mUnitsSpinner.setOnItemSelectedListener(spinnerUnitsListener);
        mAlertSpinner.setOnItemSelectedListener(spinneAlertistener);
        mSpeedImpactSpinner.setOnItemSelectedListener(spinnerSpeedImpactListener);
         
	    
	    
	    // Save the user setting to preference.
	
    }
	
	
	private void onAlertSelection(View view)
	{
		
		
		UserSetting.this.mAlertValue = mAlertCheckBox.isChecked();
		   	
        /*
         * Set the value of the text field in the UI
         */
        	
    	writeInstanceState(UserSetting.this, UserSettingType.ALERT);
		
	}
	
	// Show user setting screensetContentView(R.layout.);
	private void onTips()
	{
		
	    Intent i = new Intent(this, DetailActivity.class);
		 startActivity(i);
	}
	
	
	// spinner callback 
	/**
     *  A callback listener that implements the
     *  {@link android.widget.AdapterView.OnItemSelectedListener} interface
     *  For views based on adapters, this interface defines the methods available
     *  when the user selects an item from the View.
     *
     */
    public class myOnItemSelectedListener implements OnItemSelectedListener {

        /*
         * provide local instances of the mLocalAdapter and the mLocalContext
         */

        Activity mLocalContext;
        UserSettingType mSettingType;

        /**
         *  Constructor
         *  @param c - The activity that displays the Spinner.
         *  @param ad - The Adapter view that
         *    controls the Spinner.
         *  Instantiate a new listener object.
         */
        public myOnItemSelectedListener(Activity c, UserSettingType type) {

        	this.mLocalContext = c;
            this.mSettingType = type;
          
        }
        
       /**
         * When the user selects an item in the spinner, this method is invoked by the callback
         * chain. Android calls the item selected listener for the spinner, which invokes the
         * onItemSelected method.
         *
         * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(
         *  android.widget.AdapterView, android.view.View, int, long)
         * @param parent - the AdapterView for this listener
         * @param v - the View for this listener
         * @param pos - the 0-based position of the selection in the mLocalAdapter
         * @param row - the 0-based row number of the selection in the View
         */
        public void onItemSelected(AdapterView<?> parent, View v, int pos, long row) {
        	
        	
        	switch(this.mSettingType)
        	{
        		
        		   		
        		case UNITS :
        		{
        			UserSetting.this.mUnitsPos= pos;
                	UserSetting.this.mUnitsSelection = parent.getItemAtPosition(pos).toString();
                    /*
                     * Set the value of the text field in the UI
                     */
                  //  Intent i = new  Intent(UserSetting.this, UserSettingSavedPreference.class);
            	  //  startActivity(i);
            	  //  finish();
                	
                	writeInstanceState(UserSetting.this, UserSettingType.UNITS);
                	break;
        		}
        		case ALERT_FREQUENCY :
        		{
        			UserSetting.this.mAlertFrequencyPos= pos;
                	UserSetting.this.mAlertFrequencySelection = parent.getItemAtPosition(pos).toString();
                    /*
                     * Set the value of the text field in the UI
                     */
                  //  Intent i = new  Intent(UserSetting.this, UserSettingSavedPreference.class);
            	  //  startActivity(i);
            	  //  finish();
                	
                	writeInstanceState(UserSetting.this, UserSettingType.ALERT_FREQUENCY);
                	break;
        		}
        		case SPEED_IMPACT :
        		{
        			UserSetting.this.mSpeedImpactPos= pos;
                	UserSetting.this.mSpeedImpactSelection = parent.getItemAtPosition(pos).toString();
                    /*
                     * Set the value of the text field in the UI
                     */
                  //  Intent i = new  Intent(UserSetting.this, UserSettingSavedPreference.class);
            	  //  startActivity(i);
            	  //  finish();
                	
                	writeInstanceState(UserSetting.this, UserSettingType.SPEED_IMPACT);
                	break;
        		}
        	}

       }

        /**
         * The definition of OnItemSelectedListener requires an override
         * of onNothingSelected(), even though this implementation does not use it.
         * @param parent - The View for this Listener
         */
        public void onNothingSelected(AdapterView<?> parent) {

            // do nothing

        }
    }
       
    /**
     * Store the current state of the spinner (which item is selected, and the value of that item).
     * Since onPause() is always called when an Activity is about to be hidden, even if it is about
     * to be destroyed, it is the best place to save state.
     *
     * Attempt to write the state to the preferences file. If this fails, notify the user.
     *
     * @see android.app.Activity#onPause()
     */
    @Override
    public void onPause() {

        /*
         * an overridUserDataInfoe to onPause() must call the super constructor first.
         */

        super.onPause();
        
        writeFinalSetting();
       
       
     }
    
    private boolean writeFinalSetting()
    {
    	 boolean result  = true;
    	 
    	 result = writeInstanceState(this, UserSettingType.SPEED_IMPACT);
         
         if(result)
         	result = writeInstanceState(this, UserSettingType.UNITS);
         if(result)
         	result = writeInstanceState(this, UserSettingType.ALERT_FREQUENCY);
         
         return result;
    	
    }
    
    
    /**
     * Restores the current state of the spinner (which item is selected, and the value
     * of that item).
     * Since onResume() is always called when an Activity is starting, even if it is re-displaying
     * after being hidden, it is the best place to restore state.
     *
     * Attempts to read the state from a preferences file. If this read fails,
     * assume it was just installed, so do an initialization. Regardless, change the
     * state of the spinner to be the previous position.
     *
     * @see android.app.Activity#onResume()
     */
    @Override
    public void onResume() {
        boolean result = true; 
        /*
         * an override to onResume() must call the super constructor first.
         */

        super.onResume();

        /*
         * Try to read the preferences file. If not found, set the state to the desired initial
         * values.
         */

        result=  readInstanceState(this, UserSettingType.SPEED_IMPACT);
        if(result)
    	   result=  readInstanceState(this, UserSettingType.UNITS);
       if(result)
    	   result=  readInstanceState(this, UserSettingType.ALERT_FREQUENCY);
       
      

        /*
         * Set the spinner to the current state.
         */

        mUnitsSpinner.setSelection(getSpinnerPosition(UserSettingType.UNITS));
        mAlertCheckBox = (CheckBox)findViewById(R.id.Alert);
        mAlertCheckBox.setChecked(getAlertState(UserSettingType.UNITS));
        mAlertSpinner.setSelection(getSpinnerPosition(UserSettingType.ALERT_FREQUENCY));
        this.mSpeedImpactSpinner.setSelection(getSpinnerPosition(UserSettingType.SPEED_IMPACT));
        
        
        writeFinalSetting();

        

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
    
    
    /**
     * Read the previous state of the spinner from the preferences file
     * @param c - The Activity's Context
     */
    public boolean readInstanceState(Context c, UserSettingType type) {
    	
    	boolean result = true;

        /*
         * The preferences are stored in a SharedPreferences file. The abstract implementation of
         * SharedPreferences is a "file" containing a hashmap. All instances of an application
         * share the same instance of this file, which means that all instances of an application
         * share the same preference settings.
         */

        /*
         * Get the SharedPreferences object for this application
         */

        SharedPreferences p = c.getSharedPreferences(PREFERENCES_FILE, MODE_WORLD_READABLE);
        /*
         * Get the position and value of the spinner from the file, or a default value if the
         * key-value pair does not exist.
         *  
         */
        
        switch(type)
        {
          
          case UNITS:
          {
	        
	        this.mUnitsPos = p.getInt(UserDataInfo.UNITS_POSITION_KEY, UserSetting.DEFAULT_POSITION -1);
	        this.mUnitsSelection = p.getString(UserDataInfo.UNITS_SELECTION_KEY, "");
	        result = p.contains(UserDataInfo.UNITS_POSITION_KEY); 
          }
          case ALERT:
          {
        	 this.mAlertValue = p.getBoolean(UserDataInfo.ALERT_VALUE, true);
   	         result = p.contains(UserDataInfo.ALERT_VALUE); 
        	  
          }
          case ALERT_FREQUENCY:
          {
	        
	        this.mAlertFrequencyPos = p.getInt(UserDataInfo.ALERT_FREQUENCY_POSITION_KEY, UserSetting.DEFAULT_POSITION -1);
	        this.mAlertFrequencySelection = p.getString(UserDataInfo.ALERT_FREQUENCY_SELECTION_KEY, "");
	        result = p.contains(UserDataInfo.ALERT_FREQUENCY_POSITION_KEY); 
          }
          case CAR_POS_LAT:
          {
        	this.mCarPosLat = p.getString(UserDataInfo.CAR_POS_LAT_KEY, null);
  	        result = p.contains(UserDataInfo.CAR_POS_LAT_KEY); 
          }
          case CAR_POS_LNG:
          {
        	this.mCarPosLng = p.getString(UserDataInfo.CAR_POS_LNG_KEY, null);
  	        result = p.contains(UserDataInfo.CAR_POS_LNG_KEY); 
          }
          case INTRO_TEXT:
          {
        	this.mIntroText = p.getBoolean(UserDataInfo.INTRO_TEXT_KEY, false);
  	        result = p.contains(UserDataInfo.INTRO_TEXT_KEY); 
          }
          case SPEED_IMPACT:
          {
	        
	        this.mSpeedImpactPos = p.getInt(UserDataInfo.SPEED_IMPACT_POSITION_KEY, UserSetting.DEFAULT_POSITION );
	        this.mSpeedImpactSelection = p.getString(UserDataInfo.SPEED_IMPACT_SELECTION_KEY, "");
	        result = p.contains(UserDataInfo.SPEED_IMPACT_POSITION_KEY); 
          }
      }
        
       return result;
        
    }


    
    /**
     * Write the application's current state to a properties repository.
     * @param c - The Activity's Context
     *
     */
    public boolean writeInstanceState(Context c, UserSettingType type) {

        /*
         * Get the SharedPreferences object for this application
         */

        SharedPreferences p =
                c.getSharedPreferences(UserSetting.PREFERENCES_FILE, MODE_WORLD_READABLE);

        /*
         * Get the editor for this object. The editor interface abstracts the implementation of
         * updating the SharedPreferences object.
         */

        SharedPreferences.Editor e = p.edit();

        /*
         * Write the keys and values to the Editor
         */
           
        switch(type)
    	{
    		
    		case UNITS :
    		{
    			 e.putInt(UserDataInfo.UNITS_POSITION_KEY, this.mUnitsPos);
    		     e.putString(UserDataInfo.UNITS_SELECTION_KEY, this.mUnitsSelection);;
    		     break;
    		} 
    		case ALERT :
    		{
    			 e.putBoolean(UserDataInfo.ALERT_VALUE, this.mAlertValue);;
    		     break;
    		} 
    		
    		case ALERT_FREQUENCY :
    		{
    			 e.putInt(UserDataInfo.ALERT_FREQUENCY_POSITION_KEY, this.mAlertFrequencyPos);
    		     e.putString(UserDataInfo.ALERT_FREQUENCY_SELECTION_KEY, this.mAlertFrequencySelection);;
    		     break;
    		} 
    		
    		 case CAR_POS_LAT:
             {
               	 e.putString(UserDataInfo.CAR_POS_LAT_KEY, this.mCarPosLat);
    		     break;
          
             }
             case CAR_POS_LNG:
             {
            	 e.putString(UserDataInfo.CAR_POS_LNG_KEY, this.mCarPosLng);
    		     break;
             }
             case INTRO_TEXT:
             {
            	 e.putBoolean(UserDataInfo.INTRO_TEXT_KEY, this.mIntroText);
    		     break;
             }
             case SPEED_IMPACT :
     		{
     			 e.putInt(UserDataInfo.SPEED_IMPACT_POSITION_KEY, this.mSpeedImpactPos);
     		     e.putString(UserDataInfo.SPEED_IMPACT_SELECTION_KEY, this.mSpeedImpactSelection);;
     		     break;
     		} 
             
             
    	}

        /*
         * Commit the changes. Return the result of the commit. The commit fails if Android
         * failed to commit the changes to persistent storage.
         */

        return (e.commit());

    }
    
    public int getSpinnerPosition(UserSettingType type) {
    	
    	int localPos = 1;
    	switch(type)
    	{
    		
    	    case UNITS :
    			localPos =this.mUnitsPos;
    		break;
    		case ALERT_FREQUENCY :
    			localPos =this.mAlertFrequencyPos;
    		break;
    		case SPEED_IMPACT :
    			localPos =this.mSpeedImpactPos;
    		break;
    	}
    	return localPos;
    }

    public void setSpinnerPosition(int pos,UserSettingType type ) {
    	
    	switch(type)
    	{
    		case UNITS :
    			this.mUnitsPos = pos;;
    		case ALERT_FREQUENCY :
    			this.mAlertFrequencyPos = pos;
    		case SPEED_IMPACT :
    			this.mSpeedImpactPos = pos;
    		break;
    	}
    }
    
    
    public String getCarPosLat()
    {
    	return this.mCarPosLat;
    }
    
    public String getCarPosLng()
    {
    	return this.mCarPosLng;
    }
    
    public void setCarPosLat(String lat)
    {
    	this.mCarPosLat = lat;
    }
    
    public void setCarPosLng(String lng)
    {
       this.mCarPosLng = lng;
    }
    
    public boolean getAlertState (UserSettingType type)
    {
    	return this.mAlertValue; 
    	
    }
    public boolean getIntroTextState ()
    {
    	return this.mAlertValue; 
    	
    }
    
    public void setIntroTextState (boolean value)
    {
    	this.mIntroText = value; 
    	
    }
   
    public String getSpinnerSelection(UserSettingType type) {
    	
    	String localSelection =null;
    	switch(type)
    	{
    		case UNITS :
    			localSelection = this.mUnitsSelection;
    		break;
    		case ALERT_FREQUENCY :
    			localSelection = this.mAlertFrequencySelection;
    		break;
    		case SPEED_IMPACT :
    			localSelection = this.mSpeedImpactSelection;
    		break;
    	}
    	return localSelection;
     }

    public void setSpinnerSelection(String selection, UserSettingType type) {
    	
    	switch(type)
    	{
    		case UNITS :
    			this.mUnitsSelection = selection;;
    		case ALERT_FREQUENCY :
    			this.mAlertFrequencySelection = selection;
    		break;
    		case SPEED_IMPACT :
    			this.mSpeedImpactSelection = selection;
    		break;
    	}
    }
    
    
    class UserDataInfo
    {   	
    	
    	/**
         * The key or label for "position" in the preferences file
         */
        public static final String SPEED_IMPACT_POSITION_KEY = "SpeedImpactPosition";
        
        /**
         * The key or label for "selection" in the preferences file
         */
        public static final String SPEED_IMPACT_SELECTION_KEY = "SpeedImpactSelection";
               
        /**
         * The key or label for "position" in the preferences file
         */
        public static final String UNITS_POSITION_KEY = "UnitsPosition";
        
        /**
         * The key or label for "selection" in the preferences file
         */
        public static final String UNITS_SELECTION_KEY = "UnitsSelection";
        
        /**
         * The key or label for "position" in the preferences file
         */
        public static final String ALERT_STATE = "AlertPosition";
        
        /**
         * The key or label for "selection" in the preferences file
         */
        public static final String ALERT_VALUE = "AlertSelection";
        
        /**
         * The key or label for "position" in the preferences file
         */
        public static final String ALERT_FREQUENCY_POSITION_KEY = "AlertFrequencyPosition";
        
        /**
         * The key or label for "selection" in the preferences file
         */
        public static final String ALERT_FREQUENCY_SELECTION_KEY = "AlertFrequencySelection";
        
        /**
         * The key or label for "car pos (lat) " in the preferences file
         */
        public static final String CAR_POS_LAT_KEY = "Car position laitude";
        
        /**
         * The key or label for "car pos (lng) " in the preferences file
         */
        public static final String CAR_POS_LNG_KEY = "Car position longitude";
        
        /**
         * The key or label for "intro text " in the preferences file
         */
        public static final String INTRO_TEXT_KEY = "Intro text";
        
    
    }

	
}
