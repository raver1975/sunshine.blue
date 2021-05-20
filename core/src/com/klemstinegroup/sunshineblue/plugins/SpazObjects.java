package com.klemstinegroup.sunshineblue.plugins;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.klemstinegroup.sunshineblue.SunshineBlue;
import com.klemstinegroup.sunshineblue.engine.Statics;
import com.klemstinegroup.sunshineblue.engine.data.ScreenData;
import com.klemstinegroup.sunshineblue.engine.objects.ScreenObject;

public class SpazObjects {

    public SpazObjects() {
        Gdx.app.log("script", "loaded");
//        layer=SunshineBlue.instance.userObjects.size-2;
//        if (layer<0){layer=0;}    }
    }

    public void loop(SunshineBlue sun) {
        int layer = MathUtils.random(SunshineBlue.instance.userObjects.size);
        try {
            ScreenData sd = ((ScreenObject) SunshineBlue.instance.userObjects.get(layer)).sd;
            if (MathUtils.random() > .99f) {
                sd.rotation = 90;
            }
            if (MathUtils.random() < .01f) {
                sd.rotation = -90;
            }
            if (sd.rotation != 0) {
                sd.rotation -= sd.rotation / 10;
            }

        } catch (Exception e) {
            Statics.exceptionLog("script error", e);
        }
    }

}
