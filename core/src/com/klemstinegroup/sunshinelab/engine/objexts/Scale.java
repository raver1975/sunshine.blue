package com.klemstinegroup.sunshinelab.engine.objexts;

public abstract interface Scale {
    float[] scale = new float[]{1f};

    default float getScale() {
        return scale[0];
    }

    default void setScale(float sca) {
        scale[0] = sca;
    }
}
