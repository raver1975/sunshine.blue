package com.klemstinegroup.sunshineblue.plugins;

import com.badlogic.gdx.Gdx;
import com.klemstinegroup.sunshineblue.SunshineBlue;
import com.klemstinegroup.sunshineblue.engine.Statics;
import com.klemstinegroup.sunshineblue.engine.objects.BaseObject;
import com.klemstinegroup.sunshineblue.engine.objects.ScreenObject;

public class RotateFirstObject implements Plugin {
    @Override
    public void render(SunshineBlue sun) {

        try {
            ((ScreenObject) Statics.userObjects.get(1)).sd.rotation += .1f;
        } catch (Exception e) {
            Statics.exceptionLog("first script",e);
        }
    }

}
