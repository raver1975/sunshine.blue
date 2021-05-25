package com.klemstinegroup.sunshineblue.engine.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.input.GestureDetector;
import com.klemstinegroup.sunshineblue.SunshineBlue;
import com.klemstinegroup.sunshineblue.engine.Statics;
import com.klemstinegroup.sunshineblue.engine.objects.BaseObject;

import java.util.Stack;

public interface Overlay {
    Stack<Overlay> overlays = new Stack<>();

    static void setOverlay(Overlay overlay1) {
        Overlay topOverlay = SunshineBlue.instance.overlay;
        if (topOverlay != null) {
            if (topOverlay instanceof Touchable) {
                SunshineBlue.instance.im.removeProcessor((Touchable) topOverlay);
            }
            if (topOverlay instanceof Gestureable) {
                if (SunshineBlue.instance.gestureDetectors.containsKey((Gestureable) topOverlay)) {
                    SunshineBlue.instance.im.removeProcessor(SunshineBlue.instance.gestureDetectors.get((Gestureable) topOverlay));
                    SunshineBlue.instance.gestureDetectors.removeKey((Gestureable) topOverlay);
                }
            }
            topOverlay.removeInput();
            overlays.push(topOverlay);
        }
        if (overlay1 != null) {
            overlay1.setInput();
            if (overlay1 instanceof Touchable) {
                SunshineBlue.instance.im.addProcessor((Touchable) overlay1);
            }
            if (overlay1 instanceof Gestureable) {
                GestureDetector gd = new GestureDetector(((Gestureable) overlay1));
                SunshineBlue.instance.im.addProcessor(gd);
                SunshineBlue.instance.gestureDetectors.put((Gestureable) overlay1, gd);
            }
            SunshineBlue.instance.overlay = overlay1;
            System.out.println("overlay size="+overlays.size());
        }
    }

    static void backOverlay() {
        setOverlay(overlays.pop());
        overlays.pop();
    }

    void setInput();

    void removeInput();

    void setObject(BaseObject bo);

    void dispose();
}
