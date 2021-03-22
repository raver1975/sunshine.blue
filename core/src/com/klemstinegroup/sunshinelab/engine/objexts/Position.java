package com.klemstinegroup.sunshinelab.engine.objexts;

import com.badlogic.gdx.math.Vector3;

public abstract interface Position{
    Vector3 position = new Vector3();

    default Vector3 getPosition() {
        return position.cpy();
    }

    default void setPosition(Vector3 vec) {
        position.set(vec);
    }

    default void translate(Vector3 vector3){
        position.add(vector3);
    }
}
