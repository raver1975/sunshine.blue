package com.klemstinegroup.sunshineblue;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.klemstinegroup.sunshineblue.engine.util.NativeJava;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.depth=8;
		Intent intent = getIntent();
		String action = intent.getAction();
		Uri data = intent.getData();
		int l=data.toString().indexOf("?");
		if (l>0) {
			String cid = data.toString().substring(data.toString().indexOf('?')+1);
			initialize(new SunshineBlue(new NativeJava(), cid), config);
		}
		else{
			initialize(new SunshineBlue(),config);
		}
	}
}
