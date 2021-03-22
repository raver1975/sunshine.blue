package com.klemstinegroup.sunshinelab.engine.objexts;

import com.badlogic.gdx.math.Vector3;

public class Bounds {
    private Vector3 bounds=new Vector3();

    public Vector3 getBounds(){
        return bounds;
    }

    public void setBounds(Vector3 rect){
        bounds.set(rect);
    }

}
