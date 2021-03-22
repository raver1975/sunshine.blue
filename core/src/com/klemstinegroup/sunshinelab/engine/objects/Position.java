package com.klemstinegroup.sunshinelab.engine.objects;

import com.badlogic.gdx.math.Vector3;

public class Position{
    private Vector3 position = new Vector3();
    private float rotation=1f;
    private Vector3 center =new Vector3();
    float scale = 1f;

    private Vector3 bounds=new Vector3();

    public Vector3 getBounds(){
        return bounds;
    }

    public void setBounds(Vector3 rect){
        bounds.set(rect);
    }


    public float getScale() {
        return scale;
    }

    public void setScale(float sca) {
        scale = sca;
    }

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

    public Vector3 getPosition() {
        return position.cpy();
    }

    public void setPosition(Vector3 vec) {
        position.set(vec);
    }

    public void translate(Vector3 vector3){
        position.add(vector3);
    }

    public void setBounds(float width, float height) {
        setBounds(new Vector3(width,height,0));
    }
}
