package com.klemstinegroup.sunshinelab.engine.overlays;

import com.badlogic.gdx.input.GestureDetector;
import com.klemstinegroup.sunshinelab.engine.Statics;

public interface Overlay {
    static void setOverlay(Overlay overlay) {
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
            Statics.overlays.push(Statics.overlay);
        }
        if (overlay != null) {
            overlay.setInput();
            if (overlay instanceof Touchable) {
                Statics.im.addProcessor((Touchable) overlay);
            }
            if (overlay instanceof Gestureable) {
                GestureDetector gd = new GestureDetector(((Gestureable) overlay));
                Statics.im.addProcessor(gd);
                Statics.gestureDetectors.put((Gestureable) overlay, gd);
            }
            Statics.overlay = overlay;
        }
    }

    static void backOverlay() {
        setOverlay(Statics.overlays.pop());
        Statics.overlays.pop();
    }

    void setInput();
    void removeInput();
    void act();
}
