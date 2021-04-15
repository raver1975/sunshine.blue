package com.klemstinegroup.sunshinelab.engine.objects;

import ar.com.hjg.pngj.ImageLineByte;
import ar.com.hjg.pngj.PngReaderApng;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.JsonValue;
import com.github.tommyettinger.anim8.GifDecoder;
import com.klemstinegroup.sunshinelab.SunshineLab;
import com.klemstinegroup.sunshinelab.engine.Statics;
import com.klemstinegroup.sunshinelab.engine.overlays.Drawable;
import com.klemstinegroup.sunshinelab.engine.overlays.Touchable;
import com.klemstinegroup.sunshinelab.engine.util.*;

public class ImageObject extends ScreenObject implements Drawable, Touchable {
    public Texture texture;
    public Animation<TextureRegion> textures;
    private Polygon polygon;
    private String cid;
    private float stateTime;


    public ImageObject(byte[] data, Pixmap pixmapIn) {
        if (data==null|data.length==0){return;}
        if ((data[0] & 0xff) == 71 && (data[1] & 0xff) == 73 && (data[2] & 0xff) == 70) {
            Gdx.app.log("type", "gif!");
            GifDecoder gifDecoder = new GifDecoder();
            gifDecoder.read(new MemoryFileHandle(data).read());
            textures = gifDecoder.getAnimation(Animation.PlayMode.LOOP);
            try {
                if (textures != null && textures.getKeyFrames().length > 0) {
                    setBound();
                    return;
                }
            }
            catch (Exception e){
                Gdx.app.log("error",e.toString());
            }
        }
        if ((data[0] & 0xff) == 137 && (data[1] & 0xff) == 80 && (data[2] & 0xff) == 78 && (data[3] & 0xff) == 71) {
            Gdx.app.log("type", "png!");
            try {
                PngReaderApng apng = new PngReaderApng(new MemoryFileHandle(data));
                Array<TextureRegion> arrayTexture = new Array<>();
                for (int i = 0; i < apng.getApngNumFrames(); i++) {
                    apng.advanceToFrame(i);
                    Pixmap pixmap = new Pixmap(apng.getImgInfo().cols, apng.getImgInfo().rows, Pixmap.Format.RGBA8888);
                    for (int y = 0; y < pixmap.getHeight(); y++) {
                        ImageLineByte imageLine = apng.readRowByte();
                        byte[] linedata = imageLine.getScanline();
                        for (int j = 0; j < pixmap.getWidth(); j++) {
                            pixmap.setColor(((linedata[4 * j] & 0xff) << 24) | ((linedata[4 * j + 1] & 0xff) << 16) | ((linedata[4 * j + 2] & 0xff) << 8) | linedata[4 * j + 3] & 0xff);
                            pixmap.drawPixel(j, y);
                        }
                    }
                    arrayTexture.add(new TextureRegion(new Texture(pixmap)));
                }
                float num=1;
                float den=1;
                if (apng.getFctl()!=null) {
                    num = apng.getFctl().getDelayNum();
                    den = apng.getFctl().getDelayDen();
                }
                if (den == 0) {
                    den = 100;
                }
                textures = new Animation<>(num / den, arrayTexture);
                setBound();
                return;
            } catch (Exception e) {
                Gdx.app.log("load error", e.getMessage());
            }
        }
        if (pixmapIn == null) {
            Pixmap staticPixmap = null;
            try {
                staticPixmap = new Pixmap(new MemoryFileHandle(data));
            } catch (Exception e1) {
                Gdx.app.log("error", "data is not a png or jpg");
                Gdx.app.log("error", e1.getMessage());
            }
            if (staticPixmap != null) {
                texture = new Texture(staticPixmap);
                setBound();
            }
        } else {
            texture = new Texture(pixmapIn);
            setBound();
        }

    }

    public ImageObject(String url) {
//
//        url = "https://api.codetabs.com/v1/proxy?quest=" + url;
        Gdx.app.log("url", url);
        if (url != null && url.startsWith("Q")) {
            url = Statics.IPFSGateway + url;
        }
        if (url == null) {
        } else if (url.startsWith("data")) {
            final byte[] b = Base64Coder.decode(url.split(",")[1]);
            Pixmap pixmap = new Pixmap(new MemoryFileHandle(b));
            uploadPNG(pixmap);
            this.texture = new Texture(pixmap);
            setBound();
        } else {
            String finalUrl = url;
            Pixmap.downloadFromUrl(url, new Pixmap.DownloadPixmapResponseListener() {
                @Override
                public void downloadComplete(Pixmap pixmap) {
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

    public ImageObject(byte[] data) {
        this(data, null);
    }

    private void uploadPNG(Pixmap pixmap) {
        IPFSUtils.uploadPngtoIPFS(pixmap, new IPFSCIDListener() {
            @Override
            public void cid(String cid1) {
                Gdx.app.log("Setting cid", cid1);
                cid = cid1;
            }

            @Override
            public void uploadFailed(Throwable t) {

            }
        });
    }

    public ImageObject(Pixmap pixmap) {
        uploadPNG(pixmap);
        this.texture = new Texture(pixmap);
        setBound();
    }

    private void setBound() {

        if (textures != null) {
            TextureRegion frame = textures.getKeyFrame(0);
            sd.bounds.set(new Vector2(frame.getRegionWidth(), frame.getRegionHeight()));
        }
        if (texture != null) {
            sd.bounds.set(new Vector2(texture.getWidth(), texture.getHeight()));
        }
        sd.position.add(-sd.center.x, -sd.center.y);
    }


    @Override
    public void draw(Batch batch) {
        batch.setTransformMatrix(new Matrix4().idt()
                        .translate(sd.position.x, sd.position.y, 0)
                        .rotate(0, 0, 1, sd.rotation)
                        .scale(sd.scale, sd.scale, 1)
//                .translate(-x, -y, 0)
//                        .translate(-center.x, -center.y, 0)
        );
        if (textures != null) {
            stateTime += Gdx.graphics.getDeltaTime();
            batch.draw(textures.getKeyFrame(stateTime, true), -sd.center.x, -sd.center.y);
        } else {
            if (texture != null) {
                batch.draw(texture, -sd.center.x, -sd.center.y);

            }
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
        polygon.setOrigin(sd.center.x, sd.center.y);
        polygon.setScale(sd.scale, sd.scale);
        polygon.rotate(sd.rotation);
        polygon.translate(sd.position.x - sd.center.x, sd.position.y - sd.center.y);
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
        JsonValue val = new JsonValue(JsonValue.ValueType.object);
        val.addChild("screenData", SerializeUtil.serialize(sd));
//        MemoryFileHandle mfh=new MemoryFileHandle();
//        IPFSUtils.writePng(pixmap, mfh, null);
//        String data="data:image/png;base64,"+new String(Base64Coder.encode(mfh.ba.toArray()));
        val.addChild("pngCID", new JsonValue(cid));
        val.addChild("class", new JsonValue(ImageObject.class.getName()));
        return val;
    }

    public static ImageObject deserialize(JsonValue json) {
//        Gdx.app.log("deserialize",json.toJson(JsonWriter.OutputType.minimal));
        ScreenData sd1 = SerializeUtil.deserialize(json.get("screenData"), ScreenData.class);
        String cid = json.getString("pngCID");
        ImageObject io = new ImageObject(cid);
        io.sd = sd1;
        return io;
    }
}
