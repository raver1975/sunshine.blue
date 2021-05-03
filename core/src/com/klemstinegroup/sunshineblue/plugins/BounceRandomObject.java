package com.klemstinegroup.sunshineblue.plugins;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.klemstinegroup.sunshineblue.SunshineBlue;
import com.klemstinegroup.sunshineblue.engine.Statics;
import com.klemstinegroup.sunshineblue.engine.objects.BaseObject;
import com.klemstinegroup.sunshineblue.engine.objects.ScreenData;
import com.klemstinegroup.sunshineblue.engine.objects.ScreenObject;

public class BounceRandomObject {

    int dir = 1;

    public BounceRandomObject() {
        Gdx.app.log("script", "loaded");
//        layer=SunshineBlue.instance.userObjects.size-2;
//        if (layer<0){layer=0;}    }
    }

    public void loop(SunshineBlue sun) {
        try {
            int r = MathUtils.random(SunshineBlue.instance.userObjects.size - 1);
            BaseObject bo = SunshineBlue.instance.userObjects.get(r);
            if (bo instanceof ScreenObject) {
                ScreenData sd = ((ScreenObject) bo).sd;
                sd.rotation += dir;
                if (dir > 0) {
                    dir += .1f;
                } else if (dir < 0) {
                    dir -= .1f;
                }

                if (sd.rotation > 45) {
                    dir = -1;
                } else if (sd.rotation < -45) {
                    dir = +1;
                }
            }

        } catch (Exception e) {
            Statics.exceptionLog("script error", e);
        }
    }
}
