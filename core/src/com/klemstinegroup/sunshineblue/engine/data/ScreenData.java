package com.klemstinegroup.sunshineblue.engine.data;

import com.badlogic.gdx.math.Vector2;

public class ScreenData {
    public static int layercnt=0;

    public Vector2 position = new Vector2();
    public float rotation=0f;
    public Vector2 center = new Vector2();
    public float scale = 1f;
    public Vector2 bounds= new Vector2();
    public float layer=layercnt++;
    public boolean visible=true;

}
