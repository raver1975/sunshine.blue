package com.klemstinegroup.sunshineblue.engine.objects;

import com.badlogic.gdx.math.Vector2;
import com.klemstinegroup.sunshineblue.engine.data.ScreenData;

public class ScreenObject extends BaseObject {
    public ScreenData sd=new ScreenData();

    public void recenter(Vector2 touchdragcpy) {
        touchdragcpy.sub(sd.position);
        sd.position.add(touchdragcpy);
        touchdragcpy.scl(1f / sd.scale);
        touchdragcpy.rotateDeg(-sd.rotation);
        sd.center.add(touchdragcpy);
    }

    public void transform(Vector2 posDelta, float rotDelta, float scaleDelta) {
        sd.position.add(posDelta);
        sd.rotation+=rotDelta;
        sd.scale+=scaleDelta;
    }

    public void invtransform(Vector2 posDelta, float rotDelta, float scaleDelta) {
        sd.position.sub(posDelta);
        sd.rotation-=rotDelta;
        sd.scale-=scaleDelta;
    }
}
