package com.klemstinegroup.sunshinelab.engine.objexts;

import com.badlogic.gdx.math.Matrix4;

public abstract interface Rotation {
    Matrix4 rotation = new Matrix4();

    default Matrix4 getRotation() {
        return rotation.cpy();
    }

    default void setRotation(Matrix4 mat) {
        rotation.set(mat);
    }

}
