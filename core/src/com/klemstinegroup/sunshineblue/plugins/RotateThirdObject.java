package com.klemstinegroup.sunshineblue.plugins;

import com.klemstinegroup.sunshineblue.SunshineBlue;
import com.klemstinegroup.sunshineblue.engine.Statics;
import com.klemstinegroup.sunshineblue.engine.objects.ScreenObject;

public class RotateThirdObject implements Plugin {
    @Override
    public void render(SunshineBlue sun) {
        if (Statics.userObjects.size > 2) {
            try {
                ((ScreenObject) Statics.userObjects.get(2)).sd.rotation += .1f;
            } catch (Exception e) {
                Statics.exceptionLog("script error", e);
            }
        }
    }
}
