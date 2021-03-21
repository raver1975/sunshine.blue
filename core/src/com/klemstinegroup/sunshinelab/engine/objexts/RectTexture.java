package com.klemstinegroup.sunshinelab.engine.objexts;

import com.badlogic.gdx.graphics.g2d.Batch;

public class RectTexture extends BaseObject implements Position, Rotation, Drawable {
    private com.badlogic.gdx.graphics.Texture texture;

    public RectTexture(com.badlogic.gdx.graphics.Texture texture) {
        this.texture = texture;
    }

    @Override
    public void draw(Batch batch) {
        batch.setTransformMatrix(getRotation());
        batch.draw(texture,getPosition().x,getPosition().y);
    }
}
