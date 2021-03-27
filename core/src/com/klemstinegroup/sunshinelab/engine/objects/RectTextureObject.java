package com.klemstinegroup.sunshinelab.engine.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.klemstinegroup.sunshinelab.engine.Statics;

public class RectTextureObject extends ScreenObject implements Drawable {
    private com.badlogic.gdx.graphics.Texture texture;


    public RectTextureObject(String url) {
//
//        url = "https://api.codetabs.com/v1/proxy?quest=" + url;
        Pixmap.downloadFromUrl(url, new Pixmap.DownloadPixmapResponseListener() {
            @Override
            public void downloadComplete(Pixmap pixmap) {
                texture = new Texture(pixmap);
                setBound();
            }

            @Override
            public void downloadFailed(Throwable t) {
                String url1 = "https://api.codetabs.com/v1/proxy?quest=" + url;
                Pixmap.downloadFromUrl(url1, new Pixmap.DownloadPixmapResponseListener() {
                    @Override
                    public void downloadComplete(Pixmap pixmap) {
                        texture = new Texture(pixmap);
                        setBound();
                    }

                    @Override
                    public void downloadFailed(Throwable t) {

                    }
                });

            }
        });
    }


    public RectTextureObject(com.badlogic.gdx.graphics.Texture texture) {
        this.texture = texture;
        setBound();
    }

    private void setBound() {
        bounds.set(new Vector3(texture.getWidth(), texture.getHeight(), 0));
        center.set(new Vector3(texture.getWidth() / 2f, texture.getHeight() / 2f, 0));
        position.add(-center.x,-center.y,0);
    }

    @Override
    public void draw(Batch batch) {
        batch.setTransformMatrix(new Matrix4().idt()
                        .translate(center.x+ position.x,center.y+ position.y , 0)
                        .rotate(0, 0, 1, rotation)
                        .scale(scale, scale, 1)
//                .translate(-x, -y, 0)
                        .translate(-center.x,-center.y, 0)
        );
        if (texture != null) {
            System.out.println(position);
            batch.draw(texture,0,0);

        }
        if (Statics.debug) {
//            batch.setColor(Color.RED);
            Statics.shapedrawer.setColor(Color.RED);
            Statics.shapedrawer.rectangle(new Rectangle(0, 0, bounds.x, bounds.y));
            Statics.shapedrawer.setColor(Color.RED);
            Statics.shapedrawer.filledCircle(center.x, center.y, 5);
//            batch.setColor(Color.WHITE);
        }
    }


}
