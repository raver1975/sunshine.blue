package com.klemstinegroup.sunshinelab.engine.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.klemstinegroup.sunshinelab.engine.Statics;

public class RectTextureObject extends BaseObject implements Drawable {
    private com.badlogic.gdx.graphics.Texture texture;

    public Position position = new Position();

    public RectTextureObject(String url) {
        // Make a GET request
        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.GET);
        request.setTimeOut(2500);
        request.setUrl(url);
        System.out.println("loading:" + url);
        // Send the request, listen for the response
        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                final FileHandle tmpFile = FileHandle.tempFile("texture");
                tmpFile.write(httpResponse.getResultAsStream(), false);
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        texture = new Texture(tmpFile);
                    }
                });
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.error("yourtag", "failed to load", t);
            }

            @Override
            public void cancelled() {
                Gdx.app.log("yourtag", "load cancelled");
            }
        });
    }


    public RectTextureObject(com.badlogic.gdx.graphics.Texture texture) {
        this.texture = texture;
        setBound();
    }

    private void setBound() {
        position.setBounds(new Vector3(texture.getWidth(), texture.getHeight(), 0));
        position.setCenter(new Vector3(texture.getWidth() / 2f, texture.getHeight() / 2f, 0));
    }

    @Override
    public void draw(Batch batch) {
        batch.setTransformMatrix(new Matrix4().idt()
                        .translate(position.getCenter().x + position.getPosition().x, position.getCenter().y + position.getPosition().y, 0)
                        .rotate(0, 0, 1, position.getRotation())
                        .scale(position.getScale(), position.getScale(), 1)
//                .translate(-position.x, -position.y, 0)
                        .translate(-position.getCenter().x, -position.getCenter().y, 0)
        );
        if (texture != null) {
            batch.draw(texture, 0, 0);
        }
        if (Statics.debug) {
//            batch.setColor(Color.RED);
            Statics.shapedrawer.setColor(Color.RED);
            Statics.shapedrawer.rectangle(new Rectangle(0, 0, position.getBounds().x, position.getBounds().y));
            Statics.shapedrawer.setColor(Color.RED);
            Statics.shapedrawer.filledCircle(position.getCenter().x, position.getCenter().y, 5);
//            batch.setColor(Color.WHITE);
        }
    }


}
