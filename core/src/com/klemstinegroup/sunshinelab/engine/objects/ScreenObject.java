package com.klemstinegroup.sunshinelab.engine.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import jdk.nashorn.internal.parser.JSONParser;

public class ScreenObject extends BaseObject {
    public ScreenData sd=new ScreenData();

    public void recenter(Vector2 touchdragcpy) {
        touchdragcpy.sub(sd.position.x , sd.position.y);
        sd.position.add(touchdragcpy);
        touchdragcpy.scl(1f / sd.scale);
        touchdragcpy.rotateDeg(-sd.rotation);
        sd.center.add(touchdragcpy);
    }

}
