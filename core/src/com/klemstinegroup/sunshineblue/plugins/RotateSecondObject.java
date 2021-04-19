package com.klemstinegroup.sunshineblue.plugins;

import com.badlogic.gdx.Gdx;
import com.klemstinegroup.sunshineblue.SunshineBlue;
import com.klemstinegroup.sunshineblue.engine.Statics;
import com.klemstinegroup.sunshineblue.engine.objects.ScreenObject;

public class RotateSecondObject  {

    public RotateSecondObject(){
        Gdx.app.log("script","loaded");
    }

    public void loop(SunshineBlue sun) {
        if (Statics.userObjects.size > 1) {
            try {
                ((ScreenObject) Statics.userObjects.get(1)).sd.rotation += .1f;
            } catch (Exception e) {
                Statics.exceptionLog("script error", e);
            }
        }
    }
}
