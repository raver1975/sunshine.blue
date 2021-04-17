package com.klemstinegroup.sunshineblue.engine.objects;

import com.badlogic.gdx.utils.JsonValue;
import com.klemstinegroup.sunshineblue.engine.overlays.SerialInterface;

public class BaseObject implements SerialInterface {

  String UUID= com.klemstinegroup.sunshineblue.engine.util.UUID.randomUUID().toString();

  @Override
  public JsonValue serialize() {
    return null;
  }
}
