package com.klemstinegroup.sunshinelab.engine.objexts;

import com.badlogic.gdx.math.Vector3;

public class Rotation {
    private float rotation=1f;
    private Vector3 center =new Vector3();

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rot) {
        rotation=rot;
    }

    public void setCenter(Vector3 vec){
        center.set(vec);
    }

    public void rotate(float degrees){
        rotation+=degrees;
    }

    public Vector3 getCenter() {
        return center.cpy();
    }
}
