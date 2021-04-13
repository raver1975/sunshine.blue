package com.klemstinegroup.sunshinelab.engine.overlays;

import com.badlogic.gdx.utils.JsonValue;

public interface SerialInterface {
    public JsonValue serialize();
//    public SerialInterface deserialize(JsonValue json);
    //don't forget to make deserializer static
}
