package com.FuelSaver;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class IntroActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intro);
		
		Button okBtn = (Button)(Button)findViewById(R.id.ok_intro);
		
		okBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				
			}
		});
		
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

	
}
