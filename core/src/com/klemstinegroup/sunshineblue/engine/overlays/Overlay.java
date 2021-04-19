package com.klemstinegroup.sunshineblue.engine.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.input.GestureDetector;
import com.klemstinegroup.sunshineblue.engine.Statics;

import java.util.Stack;

public interface Overlay {
    Stack<Overlay> overlays = new Stack<>();

    static void setOverlay(Overlay overlay1) {
        Overlay topOverlay = Statics.overlay;
        if (topOverlay != null) {
            if (topOverlay instanceof Touchable) {
                Statics.im.removeProcessor((Touchable) topOverlay);
            }
            if (topOverlay instanceof Gestureable) {
                if (Statics.gestureDetectors.containsKey((Gestureable) topOverlay)) {
                    Statics.im.removeProcessor(Statics.gestureDetectors.get((Gestureable) topOverlay));
                    Statics.gestureDetectors.removeKey((Gestureable) topOverlay);
                }
            }
            topOverlay.removeInput();
            overlays.push(topOverlay);
        }
        if (overlay1 != null) {
            overlay1.setInput();
            if (overlay1 instanceof Touchable) {
                Statics.im.addProcessor((Touchable) overlay1);
            }
            if (overlay1 instanceof Gestureable) {
                GestureDetector gd = new GestureDetector(((Gestureable) overlay1));
                Statics.im.addProcessor(gd);
                Statics.gestureDetectors.put((Gestureable) overlay1, gd);
            }
            Statics.overlay = overlay1;
            System.out.println("overlay size="+overlays.size());
        }
    }

    static void backOverlay() {
        setOverlay(overlays.pop());
        overlays.pop();
    }

    void setInput();

    void removeInput();

    void act();
}
