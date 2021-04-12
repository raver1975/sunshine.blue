package com.klemstinegroup.sunshinelab.engine.objects;

import com.badlogic.gdx.utils.JsonValue;
import com.klemstinegroup.sunshinelab.engine.util.UUID;

public class BaseObject implements SerialInterface{

  String UUID= com.klemstinegroup.sunshinelab.engine.util.UUID.randomUUID().toString();

  @Override
  public JsonValue serialize() {
    return null;
  }
}
