package com.klemstinegroup.sunshinelab.engine.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.klemstinegroup.sunshinelab.engine.Statics;
import com.klemstinegroup.sunshinelab.engine.util.MemoryFileHandle;
import com.klemstinegroup.sunshinelab.engine.util.UUID;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class RectTextureObject extends ScreenObject implements Drawable {
    private com.badlogic.gdx.graphics.Texture texture;

    public RectTextureObject(String url) {
//
        Pixmap.downloadFromUrl(url, new Pixmap.DownloadPixmapResponseListener() {
            @Override
            public void downloadComplete(Pixmap pixmap) {
                texture=new Texture(pixmap);
            }

            @Override
            public void downloadFailed(Throwable t) {

            }
        });
    }


    public RectTextureObject(com.badlogic.gdx.graphics.Texture texture) {
        this.texture = texture;
        setBound();
    }

    private void setBound() {
        setBounds(new Vector3(texture.getWidth(), texture.getHeight(), 0));
        setCenter(new Vector3(texture.getWidth() / 2f, texture.getHeight() / 2f, 0));
    }

    @Override
    public void draw(Batch batch) {
        batch.setTransformMatrix(new Matrix4().idt()
                        .translate(getCenter().x + getPosition().x, getCenter().y + getPosition().y, 0)
                        .rotate(0, 0, 1, getRotation())
                        .scale(getScale(), getScale(), 1)
//                .translate(-x, -y, 0)
                        .translate(-getCenter().x, -getCenter().y, 0)
        );
        if (texture != null) {
            batch.draw(texture, 0, 0);
        }
        if (Statics.debug) {
//            batch.setColor(Color.RED);
            Statics.shapedrawer.setColor(Color.RED);
            Statics.shapedrawer.rectangle(new Rectangle(0, 0, getBounds().x, getBounds().y));
            Statics.shapedrawer.setColor(Color.RED);
            Statics.shapedrawer.filledCircle(getCenter().x, getCenter().y, 5);
//            batch.setColor(Color.WHITE);
        }
    }


}
