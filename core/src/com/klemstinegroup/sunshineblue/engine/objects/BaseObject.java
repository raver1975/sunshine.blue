package com.klemstinegroup.sunshineblue.engine.objects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.klemstinegroup.sunshineblue.engine.commands.Command;
import com.klemstinegroup.sunshineblue.engine.overlays.SerialInterface;
import com.klemstinegroup.sunshineblue.engine.util.UUID;

import java.util.Objects;


public class BaseObject implements SerialInterface {
  public boolean regen=true;
  protected String cid;
  public String uuid= UUID.randomUUID().toString();

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof BaseObject)) return false;
    BaseObject that = (BaseObject) o;
    return uuid.equals(that.uuid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid);
  }

  @Override
  public JsonValue serialize() {
    return null;
  }

  public void regenerate(AssetManager assetManager){

  }
}
