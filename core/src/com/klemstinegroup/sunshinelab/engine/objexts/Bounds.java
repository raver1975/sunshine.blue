package com.klemstinegroup.sunshinelab.engine.objexts;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public interface Bounds {
    Vector3 bounds=new Vector3();

    default Vector3 getBounds(){
        return bounds;
    }

    default void setBounds(Vector3 rect){
        bounds.set(rect);
    }

}
