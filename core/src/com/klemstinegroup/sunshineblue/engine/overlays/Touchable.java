package com.klemstinegroup.sunshineblue.engine.overlays;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public interface Touchable extends InputProcessor {


    boolean isSelected(Vector2 point);
    boolean isSelected(Polygon gon);
    void setBounds();

}
