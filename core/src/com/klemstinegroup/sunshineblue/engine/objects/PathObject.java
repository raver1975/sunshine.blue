package com.klemstinegroup.sunshineblue.engine.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class PathObject {
    Color color=Color.WHITE;
    int size=10;
    Array<Vector2> path=new Array<>();

    public PathObject(Color color, int size, Array<Vector2> path) {
        this.color=color;
        this.size=size;
        this.path=path;
    }

    public PathObject() {

    }
}
