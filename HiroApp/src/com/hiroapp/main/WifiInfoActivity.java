/**
 * Created by Jaycon Systems on 13/01/15.
 * Copyright @ 2014 Jaycon Systems. All rights reserved.
 */
package com.hiroapp.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;

public class WifiInfoActivity extends Activity implements OnClickListener {

	private ImageView img_back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.wifisafezoneinfoscreen);
		initscreen();
		setListener();
	}

	private void setListener() {
		// TODO Auto-generated method stub
		img_back.setOnClickListener(this);
	}

	private void initscreen() {
		// TODO Auto-generated method stub
		img_back = (ImageView) findViewById(R.id.imgback);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.imgback:
			finish();
			break;

		default:
			break;
		}
	}

}
