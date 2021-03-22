package com.klemstinegroup.sunshinelab.engine.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.klemstinegroup.sunshinelab.engine.Statics;

public class RectTextureObject extends BaseObject implements Drawable {
    private com.badlogic.gdx.graphics.Texture texture;

    public Position position;

    public RectTextureObject(com.badlogic.gdx.graphics.Texture texture) {
        this.texture = texture;
        position = new Position();
        position.setBounds(new Vector3(texture.getWidth(), texture.getHeight(), 0));
        position.setCenter(new Vector3(texture.getWidth() / 2f, texture.getHeight() / 2f, 0));
    }

    @Override
    public void draw(Batch batch) {
        batch.setTransformMatrix(new Matrix4().idt()
                        .translate(position.getCenter().x+ position.getPosition().x, position.getCenter().y + position.getPosition().y, 0)
                        .rotate(0, 0, 1, position.getRotation())
                        .scale(position.getScale(), position.getScale(), 1)
//                .translate(-position.x, -position.y, 0)
                        .translate(-position.getCenter().x , -position.getCenter().y , 0)
        );
        batch.draw(texture, 0, 0);
        if (Statics.debug) {
//            batch.setColor(Color.RED);
            Statics.shapedrawer.setColor(Color.CYAN);
            Statics.shapedrawer.rectangle(new Rectangle(0, 0, position.getBounds().x, position.getBounds().y));
//            batch.setColor(Color.WHITE);
        }
    }
}
