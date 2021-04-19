package com.klemstinegroup.sunshineblue.engine.objects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.JsonValue;
import com.klemstinegroup.sunshineblue.engine.overlays.SerialInterface;

public class BaseObject implements SerialInterface {
  public boolean regen=true;
  protected String cid;

  @Override
  public JsonValue serialize() {
    return null;
  }

  public void regenerate(AssetManager assetManager){

  }
}
