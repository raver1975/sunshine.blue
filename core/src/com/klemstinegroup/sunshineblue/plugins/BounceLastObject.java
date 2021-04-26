package com.klemstinegroup.sunshineblue.plugins;

import com.badlogic.gdx.Gdx;
import com.klemstinegroup.sunshineblue.SunshineBlue;
import com.klemstinegroup.sunshineblue.engine.Statics;
import com.klemstinegroup.sunshineblue.engine.objects.ScreenData;
import com.klemstinegroup.sunshineblue.engine.objects.ScreenObject;

public class BounceLastObject {

    int dir=1;
    int layer=0;
    public BounceLastObject() {
        Gdx.app.log("script", "loaded");
//        layer=SunshineBlue.instance.userObjects.size-2;
//        if (layer<0){layer=0;}    }
    }

    public void loop(SunshineBlue sun) {
        if (layer>=0 && SunshineBlue.instance.userObjects.size > layer) {
            try {
                ScreenData sd = ((ScreenObject) SunshineBlue.instance.userObjects.get(layer)).sd;
                sd.rotation += dir;
                if (dir>0){dir+=.1f;}
                else if (dir<0){dir-=.1f;}

                if (sd.rotation>45){
                    dir=-1;
                }
                else if (sd.rotation<-45){
                    dir=+1;
                }

            } catch (Exception e) {
                Statics.exceptionLog("script error", e);
            }
        }
    }
}
