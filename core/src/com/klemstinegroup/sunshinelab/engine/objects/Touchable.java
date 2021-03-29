package com.klemstinegroup.sunshinelab.engine.objects;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

public interface Touchable extends InputProcessor {


    boolean isSelected(Vector2 touch);
}
