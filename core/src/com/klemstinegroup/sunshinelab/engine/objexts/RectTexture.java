package com.klemstinegroup.sunshinelab.engine.objexts;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.klemstinegroup.sunshinelab.engine.Statics;

public class RectTexture extends BaseObject implements Drawable {
    private com.badlogic.gdx.graphics.Texture texture;

    public Rotation rotation;
    public Bounds bounds;
    public Position position;
    public Scale scale;

    public RectTexture(com.badlogic.gdx.graphics.Texture texture) {
        this.texture = texture;
        rotation = new Rotation();
        bounds = new Bounds();
        scale = new Scale();
        position = new Position();
        bounds.setBounds(new Vector3(texture.getWidth(), texture.getHeight(), 0));
        rotation.setCenter(new Vector3(texture.getWidth() / 2f, texture.getHeight() / 2f, 0));
    }

    @Override
    public void draw(Batch batch) {
        batch.setTransformMatrix(new Matrix4().idt()
                        .translate(rotation.getCenter().x + position.getPosition().x, rotation.getCenter().y + position.getPosition().y, 0)
                        .rotate(0, 0, 1, rotation.getRotation())
                        .scale(scale.getScale(), scale.getScale(), 1)
//                .translate(-position.x, -position.y, 0)
                        .translate(-rotation.getCenter().x / scale.getScale(), -rotation.getCenter().y / scale.getScale(), 0)
        );
        batch.draw(texture, 0, 0);
        if (Statics.debug) {
//            batch.setColor(Color.RED);
            Statics.shapedrawer.setColor(Color.CYAN);
            Statics.shapedrawer.rectangle(new Rectangle(0, 0, bounds.getBounds().x, bounds.getBounds().y));
//            batch.setColor(Color.WHITE);
        }
    }
}
