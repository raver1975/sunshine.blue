package com.klemstinegroup.sunshineblue.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.klemstinegroup.sunshineblue.SunshineBlue;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
//		new LwjglApplication(new SunshineBlue("QmW9VmL2sGoMSPjdQBDg4T5xdeiQjq4QtTY3wxYCSuXCEF"), config);
		new LwjglApplication(new SunshineBlue(), config);
	}
}
