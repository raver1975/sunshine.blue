package com.klemstinegroup.sunshinelab.engine.objexts;

import com.badlogic.gdx.math.Vector3;

public class Position{
    private Vector3 position = new Vector3();

    public Vector3 getPosition() {
        return position.cpy();
    }

    public void setPosition(Vector3 vec) {
        position.set(vec);
    }

    public void translate(Vector3 vector3){
        position.add(vector3);
    }
}
