package com.klemstinegroup.sunshinelab.engine.objexts;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class RectTexture extends BaseObject implements Position, Rotation, Drawable, Bounds, Scale {
    private com.badlogic.gdx.graphics.Texture texture;

    public RectTexture(com.badlogic.gdx.graphics.Texture texture) {
        this.texture = texture;
        this.setBounds(new Vector3(texture.getWidth(), texture.getHeight(), 0));
        this.setCenter(new Vector3(texture.getWidth() / 2f, texture.getHeight() / 2f, 0));
    }

    @Override
    public void draw(Batch batch) {
        batch.setTransformMatrix(new Matrix4().idt()
                .translate(center.x+position.x, center.y+position.y, 0)
                .rotate(0, 0, 1, getRotation())
                .scale(getScale(), getScale(), 1)
//                .translate(-position.x, -position.y, 0)
                .translate(-center.x / getScale(), -center.y / getScale(), 0)
        );
        batch.draw(texture, 0, 0);
    }
}
