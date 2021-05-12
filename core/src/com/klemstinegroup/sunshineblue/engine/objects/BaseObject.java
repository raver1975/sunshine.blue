package com.klemstinegroup.sunshineblue.engine.objects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.JsonValue;
import com.klemstinegroup.sunshineblue.engine.overlays.SerialInterface;
import com.klemstinegroup.sunshineblue.engine.util.UUID;


public class BaseObject implements SerialInterface {
  public boolean regen=true;
  protected String cid;
  public String uuid= UUID.randomUUID().toString();

  @Override
  public JsonValue serialize() {
    return null;
  }

  public void regenerate(AssetManager assetManager){

  }
}
