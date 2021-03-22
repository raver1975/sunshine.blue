package com.klemstinegroup.sunshinelab.engine.objexts;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public abstract interface Rotation {
    float[] rotation=new float[1];
    Vector3 center =new Vector3();

    default float getRotation() {
        return rotation[0];
    }

    default void setRotation(float rot) {
        rotation[0]=rot;
    }

    default void setCenter(Vector3 vec){
        center.set(vec);
    }

    default void rotate(float degrees){
        rotation[0]+=degrees;
    }
}
