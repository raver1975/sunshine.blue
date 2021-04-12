package com.klemstinegroup.sunshinelab.engine.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.ByteArray;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.klemstinegroup.sunshinelab.SunshineLab;
import com.klemstinegroup.sunshinelab.engine.Statics;
import com.klemstinegroup.sunshinelab.engine.util.*;

public class ImageObject extends ScreenObject implements Drawable, Touchable {
    public Texture texture;
    public Pixmap pixmap;
    private Polygon polygon;
    private String cid;


    public ImageObject(String url) {
//
//        url = "https://api.codetabs.com/v1/proxy?quest=" + url;
        Gdx.app.log("url", url);
        if (url!=null&&url.startsWith("Q")){url=Statics.IPFSGateway+url;}
        if (url==null){}
         else if (url.startsWith("data")) {
            final byte[] b = Base64Coder.decode(url.split(",")[1]);
            pixmap=new Pixmap(new MemoryFileHandle(b));
           uploadPNG(pixmap);
            this.texture=new Texture(pixmap);
        } else {
            String finalUrl = url;
            Pixmap.downloadFromUrl(url, new Pixmap.DownloadPixmapResponseListener() {
                @Override
                public void downloadComplete(Pixmap pixmap) {
                    ImageObject.this.pixmap=pixmap;
                    uploadPNG(pixmap);
                    texture = new Texture(pixmap);
                    setBound();
    //                IPFSUtils.uploadPng(pixmap,bounds);

                }

                @Override
                public void downloadFailed(Throwable t) {
                    String url1 = "https://api.codetabs.com/v1/proxy?quest=" + finalUrl;
                    Pixmap.downloadFromUrl(url1, new Pixmap.DownloadPixmapResponseListener() {
                        @Override
                        public void downloadComplete(Pixmap pixmap) {
                            ImageObject.this.pixmap=pixmap;
                            uploadPNG(pixmap);
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
    }

    private void uploadPNG(Pixmap pixmap){
        IPFSUtils.uploadPngtoIPFS(pixmap, new IPFSCIDListener() {
            @Override
            public void cid(String cid1) {
                Gdx.app.log("Setting cid",cid1);
                cid=cid1;
            }

            @Override
            public void uploadFailed(Throwable t) {

            }
        });
    }

    public ImageObject(Pixmap pixmap) {
        this.pixmap=pixmap;
        uploadPNG(pixmap);
        this.texture=new Texture(pixmap);
        setBound();
    }

    private void setBound() {
        sd.bounds.set(new Vector2(texture.getWidth(), texture.getHeight()));
        sd.position.add(-sd.center.x, -sd.center.y);
    }


    @Override
    public void draw(Batch batch) {
        batch.setTransformMatrix(new Matrix4().idt()
                        .translate(sd.position.x,  sd.position.y, 0)
                        .rotate(0, 0, 1, sd.rotation)
                        .scale(sd.scale, sd.scale, 1)
//                .translate(-x, -y, 0)
//                        .translate(-center.x, -center.y, 0)
        );
        if (texture != null) {
            batch.draw(texture, -sd.center.x,-sd.center.y);

        }

        if (Statics.debug || Statics.selectedObjects.contains(this, true)) {
//            batch.setColor(Color.RED);

            Statics.shapedrawer.setColor(Color.RED);
            Statics.shapedrawer.filledCircle(0, 0, 15);
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
        polygon = new Polygon(new float[]{0, 0, sd.bounds.x, 0, sd.bounds.x, sd.bounds.y, 0, sd.bounds.y, 0, 0});
        polygon.setOrigin(sd.center.x,sd.center.y);
        polygon.setScale(sd.scale, sd.scale);
        polygon.rotate(sd.rotation);
        polygon.translate(sd.position.x-sd.center.x, sd.position.y-sd.center.y);
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

    @Override
    public JsonValue serialize() {
        JsonValue val=new JsonValue(JsonValue.ValueType.object);
        val.addChild("screenData", SerializeUtil.serialize(sd));
//        MemoryFileHandle mfh=new MemoryFileHandle();
//        IPFSUtils.writePng(pixmap, mfh, null);
//        String data="data:image/png;base64,"+new String(Base64Coder.encode(mfh.ba.toArray()));
        val.addChild("pngCID",new JsonValue(cid));
        val.addChild("class",new JsonValue(ImageObject.class.getName()));
        return val;
    }

    public static ImageObject deserialize(JsonValue json) {
//        Gdx.app.log("deserialize",json.toJson(JsonWriter.OutputType.minimal));
        ScreenData sd1=SerializeUtil.deserialize(json.get("screenData"),ScreenData.class);
        String cid=json.getString("pngCID");
        ImageObject io=new ImageObject(cid);
        io.sd=sd1;
        return io;
    }
}
