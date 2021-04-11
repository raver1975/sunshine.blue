package com.klemstinegroup.sunshinelab.engine.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class ScreenObject extends BaseObject {
    public Vector2 position = new Vector2();
    public float rotation=1f;
    public Vector2 center = new Vector2();
    public float scale = 1f;
    public Vector2 bounds= new Vector2();

    public void recenter(Vector2 touchdragcpy) {
//        touchdragcpy.set(touchdown);
        touchdragcpy.sub(position.x , position.y);
        position.add(touchdragcpy);
        touchdragcpy.scl(1f / scale);
        touchdragcpy.rotateDeg(-rotation);
        center.add(touchdragcpy);
    }

}
