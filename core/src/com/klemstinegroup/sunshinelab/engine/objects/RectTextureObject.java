package com.klemstinegroup.sunshinelab.engine.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Base64Coder;
import com.klemstinegroup.sunshinelab.SunshineLab;
import com.klemstinegroup.sunshinelab.engine.Statics;
import com.klemstinegroup.sunshinelab.engine.util.IPFSUtils;
import com.klemstinegroup.sunshinelab.engine.util.MemoryFileHandle;

import java.util.Arrays;

public class RectTextureObject extends ScreenObject implements Drawable, Touchable {
    private com.badlogic.gdx.graphics.Texture texture;
    private Polygon polygon;

    public RectTextureObject(String url) {
//
//        url = "https://api.codetabs.com/v1/proxy?quest=" + url;
        Gdx.app.log("url", url);
        if (url.startsWith("data")) {
            final byte[] b = Base64Coder.decode(url.split(",")[1]);
            this.texture=new Texture(new Pixmap(new MemoryFileHandle(b)));

//            Pixmap.downloadFromUrl(url,new Pixmap.DownloadPixmapResponseListener(){
//                @Override
//                public void downloadComplete(Pixmap pixmap) {
//                    texture = new Texture(pixmap);
//                    setBound();
//                }
//
//                @Override
//                public void downloadFailed(Throwable t) {
//Statics.objects.removeValue(RectTextureObject.this,true);
//                }
//            });
//           Pixmap pixmap=new Pixmap(new FileHandle(){
//               @Override
//               public byte[] readBytes() {
//                   return b;
//               }
//           });
//           if (pixmap!=null){
//               texture=new Texture(pixmap);
//               setBound();
//           }
//            Pixmap.DownloadPixmapResponseListener responseListener = new Pixmap.DownloadPixmapResponseListener() {
//                @Override
//                public void downloadComplete(Pixmap pixmap) {
//                    texture = new Texture(pixmap);
//                    setBound();
//                }
//
//                @Override
//                public void downloadFailed(Throwable t) {
//
//                }
//            };
//            Gdx.app.postRunnable(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Pixmap pixmap = new Pixmap(b, 0,b.length);
//                        responseListener.downloadComplete(pixmap);
//                    } catch (Throwable t) {
//                        Statics.objects.removeValue(RectTextureObject.this, true);
//                    }
//                }
//            });
        } else Pixmap.downloadFromUrl(url, new Pixmap.DownloadPixmapResponseListener() {
            @Override
            public void downloadComplete(Pixmap pixmap) {
                texture = new Texture(pixmap);
                setBound();
                IPFSUtils.uploadPng(pixmap,bounds);
            }

            @Override
            public void downloadFailed(Throwable t) {
                String url1 = "https://api.codetabs.com/v1/proxy?quest=" + url;
                Pixmap.downloadFromUrl(url1, new Pixmap.DownloadPixmapResponseListener() {
                    @Override
                    public void downloadComplete(Pixmap pixmap) {
                        texture = new Texture(pixmap);
                        setBound();
                        IPFSUtils.uploadPng(pixmap, bounds);
                    }

                    @Override
                    public void downloadFailed(Throwable t) {
                        Statics.userObjects.removeValue(RectTextureObject.this, true);
                    }
                });

            }
        });
    }


//    public RectTextureObject(Texture texture) {
//        this.texture = texture;
//        setBound();
//    }

    public RectTextureObject(Pixmap pixmap) {
        this.texture=new Texture(pixmap);
        setBound();
        IPFSUtils.uploadPng(pixmap, bounds);
    }

    private void setBound() {
        bounds.set(new Vector3(texture.getWidth(), texture.getHeight(), 0));
        center.set(new Vector3(texture.getWidth() / 2f, texture.getHeight() / 2f, 0));
        position.add(-center.x, -center.y, 0);
    }


    @Override
    public void draw(Batch batch) {
        batch.setTransformMatrix(new Matrix4().idt()
                        .translate(center.x + position.x, center.y + position.y, 0)
                        .rotate(0, 0, 1, rotation)
                        .scale(scale, scale, 1)
//                .translate(-x, -y, 0)
                        .translate(-center.x, -center.y, 0)
        );
        if (texture != null) {
            batch.draw(texture, 0, 0);

        }

        if (Statics.debug || Statics.selectedObjects.contains(this, true)) {
//            batch.setColor(Color.RED);
            Statics.shapedrawer.setColor(Color.RED);
            Statics.shapedrawer.rectangle(new Rectangle(0, 0, bounds.x, bounds.y));
            Statics.shapedrawer.setColor(Color.RED);
            Statics.shapedrawer.filledCircle(center.x, center.y, 5);

        }
        batch.end();
        batch.setTransformMatrix(SunshineLab.mx4Batch);
        batch.begin();
        if (polygon != null) {
            Statics.shapedrawer.setColor(Color.WHITE);
            Statics.shapedrawer.polygon(polygon);
        }

    }


    @Override
    public boolean isSelected(Vector3 touch) {
        polygon = new Polygon(new float[]{0, 0, bounds.x, 0, bounds.x, bounds.y, 0, bounds.y, 0, 0});
//        polygon.translate(center.x,center.y);
        polygon.setOrigin(center.x, center.y);
        polygon.setScale(scale, scale);
        polygon.rotate(rotation);
        polygon.translate(position.x, position.y);
//        polygon.translate(s);

        System.out.println(Arrays.toString(polygon.getTransformedVertices()));
        return polygon.contains(touch.x,touch.y);
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
