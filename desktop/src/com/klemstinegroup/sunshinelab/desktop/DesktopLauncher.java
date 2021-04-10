package com.klemstinegroup.sunshinelab.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.klemstinegroup.sunshinelab.SunshineLab;
import com.klemstinegroup.sunshinelab.engine.util.NativeIPFS;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new SunshineLab(), config);
	}
}
