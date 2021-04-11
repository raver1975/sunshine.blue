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

public class ImageObject extends ScreenObject implements Drawable, Touchable {
    public com.badlogic.gdx.graphics.Texture texture;
    private Polygon polygon;

    public ImageObject(String url) {
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
//                IPFSUtils.uploadPng(pixmap,bounds);
            }

            @Override
            public void downloadFailed(Throwable t) {
                String url1 = "https://api.codetabs.com/v1/proxy?quest=" + url;
                Pixmap.downloadFromUrl(url1, new Pixmap.DownloadPixmapResponseListener() {
                    @Override
                    public void downloadComplete(Pixmap pixmap) {
                        texture = new Texture(pixmap);
                        setBound();
//                        IPFSUtils.uploadPng(pixmap, bounds);
                    }

                    @Override
                    public void downloadFailed(Throwable t) {
                        Statics.userObjects.removeValue(ImageObject.this, true);
                    }
                });

            }
        });
    }


//    public RectTextureObject(Texture texture) {
//        this.texture = texture;
//        setBound();
//    }

    public ImageObject(Pixmap pixmap) {
        this.texture=new Texture(pixmap);
        setBound();
//        IPFSUtils.uploadPng(pixmap, bounds);
    }

    private void setBound() {
        bounds.set(new Vector2(texture.getWidth(), texture.getHeight()));
//        center.set(new Vector2(texture.getWidth() / 2f, texture.getHeight() / 2f));
        position.add(-center.x, -center.y);
    }


    @Override
    public void draw(Batch batch) {
        batch.setTransformMatrix(new Matrix4().idt()
                        .translate(position.x,  position.y, 0)
                        .rotate(0, 0, 1, rotation)
                        .scale(scale, scale, 1)
//                .translate(-x, -y, 0)
//                        .translate(-center.x, -center.y, 0)
        );
        if (texture != null) {
            batch.draw(texture, -center.x,-center.y);

        }

        if (Statics.debug || Statics.selectedObjects.contains(this, true)) {
//            batch.setColor(Color.RED);

            Statics.shapedrawer.setColor(Color.RED);
            Statics.shapedrawer.filledCircle(0, 0, 15);

/*batch.end();
            batch.setTransformMatrix(new Matrix4().idt()
                            .translate(position.x+touchSpot.x,  position.y+touchSpot.y, 0)
                            .rotate(0, 0, 1, rotation)
                            .scale(scale, scale, 1)
//                .translate(-x, -y, 0)
//                        .translate(-center.x, -center.y, 0)
            );
batch.begin();*/


//            Statics.shapedrawer.setColor(Color.GREEN);
//            Statics.shapedrawer.filledCircle(position.x+center.x,position.y+center.y, 15);

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
    public boolean isSelected(Vector2 touch) {
        polygon = new Polygon(new float[]{0, 0, bounds.x, 0, bounds.x, bounds.y, 0, bounds.y, 0, 0});
        polygon.setOrigin(center.x,center.y);
        polygon.setScale(scale, scale);
        polygon.rotate(rotation);
        polygon.translate(position.x-center.x, position.y-center.y);
//        polygon.translate(-center.x*scale,-center.y*scale);
        return polygon.contains(touch);
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
