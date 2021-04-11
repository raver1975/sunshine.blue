package com.klemstinegroup.sunshinelab.engine.objects;

import com.badlogic.gdx.utils.JsonValue;

public interface SerialInterface {
    public JsonValue serialize();
    public SerialInterface deserialize(JsonValue json);
}
