package com.klemstinegroup.sunshineblue.engine.objects;

import ar.com.hjg.pngj.ImageLineByte;
import ar.com.hjg.pngj.PngReaderApng;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import com.github.tommyettinger.anim8.GifDecoder;
import com.klemstinegroup.sunshineblue.SunshineBlue;
import com.klemstinegroup.sunshineblue.engine.Statics;
import com.klemstinegroup.sunshineblue.engine.overlays.Drawable;
import com.klemstinegroup.sunshineblue.engine.overlays.Touchable;
import com.klemstinegroup.sunshineblue.engine.util.*;
import space.earlygrey.shapedrawer.JoinType;
import sun.security.provider.Sun;

import java.io.IOException;
import java.util.Map;

public class ImageObject extends ScreenObject implements Drawable, Touchable {
    public Texture texture;
    public Animation<CustomTextureAtlas.AtlasRegion> textures;
    private Polygon polygon;
    private String cid;
    private Array<String> cids=new Array<>();
    private float stateTime;
    Vector2 angleCalc = new Vector2();
    float angleRotateAnimAngle = 0;

    public ImageObject(byte[] data, Pixmap pixmapIn, String cid) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                if (data == null || data.length == 0) {
                    return;
                }
                if (cid == null || cid.isEmpty()) {
                    SunshineBlue.nativeNet.uploadIPFS(data, new IPFSCIDListener() {
                        @Override
                        public void cid(String cid) {
                            ImageObject.this.cid = cid;
                        }


                        @Override
                        public void uploadFailed(Throwable t) {
                            Statics.exceptionLog("uload", t);
                        }
                    });
                } else {
                    ImageObject.this.cid = cid;
                }
                if (data[0] == -54 && data[1] == -2 && data[2] == -70 && data[3] == -66) {
                    SunshineBlue.removeUserObj(ImageObject.this);
                    SunshineBlue.addUserObj(new ScriptObject(data));
                    return;
                }

                if ((data[0] & 0xff) == 71 && (data[1] & 0xff) == 73 && (data[2] & 0xff) == 70) {
                    Gdx.app.log("type", "gif!");
                    GifDecoder gifDecoder = new GifDecoder();
                    gifDecoder.read(new MemoryFileHandle(data).read());
                    PixmapPacker pixmapPacker = gifDecoder.getAnimation(Animation.PlayMode.LOOP);
                    CustomTextureAtlas animationAtlas = pixmapPacker.generateCustomTextureAtlas(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear, false);
//
//        for (PixmapPacker.Page page:pixmapPacker.getPages()){
//            SunshineBlue.addUserObj(new ImageObject(page.getPixmap()));
//            for(OrderedMap.Entry<String, PixmapPacker.PixmapPackerRectangle> rec:page.getRects()){
//                Gdx.app.log("test",rec.value);
//            }
//
//        }
                    /*MemoryFileHandle mfh = SerializeUtil.serializePixmapPacker(pixmapPacker);
                    SerializeUtil.deserializePixmapPacker(mfh, new AtlasDownloadListener() {
                        @Override
                        public void atlas(Array<CustomTextureAtlas.AtlasRegion> regions) {
                            System.out.println("as:" + animationAtlas.getRegions().size);
                            if (animationAtlas.getRegions().size > 0) {
                                try {
                                    Gdx.app.log("test", "" + 1);
                                    textures = new Animation<CustomTextureAtlas.AtlasRegion>((float) gifDecoder.getDelay(0) / 1000f, regions, Animation.PlayMode.LOOP);
                                    Gdx.app.log("test", "" + 2);
                                    setBounds();
                                    return;
                                } catch (Exception e) {
                                    Statics.exceptionLog("error2", e);
                                }
                            }
                        }

                        @Override
                        public void failed(Throwable t) {

                        }
                    });*/
//                    TextureAtlas.TextureAtlasData tad = new TextureAtlas.TextureAtlasData();
//                    tad.load(mfh, mfh, true);
//                    TextureAtlas temp = new TextureAtlas(tad);

                    textures = new Animation<CustomTextureAtlas.AtlasRegion>((float) gifDecoder.getDelay(0) / 1000f, animationAtlas.getRegions(), Animation.PlayMode.LOOP);
                }
                if ((data[0] & 0xff) == 137 && (data[1] & 0xff) == 80 && (data[2] & 0xff) == 78 && (data[3] & 0xff) == 71) {
                    Gdx.app.log("type", "png!");
                    try {
                        PngReaderApng apng = new PngReaderApng(new MemoryFileHandle(data));
//                        Array<TextureRegion> arrayTexture = new Array<>();
                        PixmapPacker pixmapPacker = new PixmapPacker(2048, 2048, Pixmap.Format.RGBA8888, 3, true);
                        pixmapPacker.setPackToTexture(false);

                        int w = apng.getImgInfo().cols;
                        int h = apng.getImgInfo().rows;
                        Pixmap first = null;
                        Pixmap last=null;
                        for (int i = 0; i < apng.getApngNumFrames(); i++) {
                            apng.advanceToFrame(i);
                            int channels = apng.getCurImgInfo().channels;
                            int bitdepth = apng.getCurImgInfo().bitDepth;
                            int bitsperpixel = apng.getCurImgInfo().bitspPixel;
                            int bytesperpixel = apng.getCurImgInfo().bytesPixel;


                            int offx = apng.getFctl().getxOff();
                            int offy = apng.getFctl().getyOff();

                            Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
                            if (apng.getFctl().getBlendOp() == 1 && first != null) {
                                pixmap.drawPixmap(first, 0, 0);
                            }
                            if (apng.getFctl().getBlendOp() == 2 && last!=null) {
                                pixmap.drawPixmap(last, 0, 0);
                            }
                            Gdx.app.log("pixmap:", pixmap.getWidth() + "x" + pixmap.getHeight());
//                            int trans=apng.getMetadata().getTRNS().getRGB888();
                            int trans255 = -1;
                            try {
                                trans255 = apng.getMetadata().getTRNS().getPalletteAlpha().length;
                            } catch (Exception e) {
                            }
                            int trans888 = -1;
                            try {
                                trans888 = apng.getMetadata().getTRNS().getRGB888();
                            } catch (Exception e) {
                            }
                            for (int y = 0; y < apng.getCurImgInfo().rows; y++) {
                                ImageLineByte imageLine = apng.readRowByte();
                                byte[] linedata = imageLine.getScanline();
                                Gdx.app.log("bits:", linedata.length + "\t" + bitdepth + "\t" + bitsperpixel + "\t" + bytesperpixel + "\t" + channels);
                                for (int j = 0; j < imageLine.getImageInfo().cols; j++) {
                                    if (channels == 1) {

//                                        pixmap.setColor(((linedata[j] & 0xff) << 24) | ((linedata[j] & 0xff) << 16) | ((linedata[j] & 0xff) << 8) | 0xff);
//                                        System.out.println("***+"+apng.getMetadata().getPLTE().getNentries()+"\t"+apng.getMetadata().getTRNS().getPalletteAlpha().length);
                                        int g = apng.getMetadata().getPLTE().getEntry(linedata[j] & 0xff);
                                        int a = 255;
                                        if ((linedata[j] & 0xff) < trans255) {
                                            a = apng.getMetadata().getTRNS().getPalletteAlpha()[linedata[j] & 0xff];
                                        }
                                        pixmap.setColor(g << 8 | (a & 0xff));

                                    }
                                    if (channels == 3) {
                                        int r = (linedata[3 * j] & 0xff);
                                        int g = (linedata[3 * j + 1] & 0xff);
                                        int b = (linedata[3 * j + 2] & 0xff);
                                        int c = (r << 16) | (g << 8) | (b << 0);
                                        pixmap.setColor((c << 8) | ((c == trans888) ? 0x00 : 0xff));
                                    } else if (channels == 4) {
                                        pixmap.setColor(((linedata[4 * j] & 0xff) << 24) | ((linedata[4 * j + 1] & 0xff) << 16) | ((linedata[4 * j + 2] & 0xff) << 8) | linedata[4 * j + 3] & 0xff);
                                    }
                                    pixmap.drawPixel(j + offx, y + offy);
                                }
                            }
//                            TextureRegion region = null;
                            if (first == null) {
                                first = pixmap;
                            }
                            last=pixmap;

                            pixmapPacker.pack("f" + i, pixmap);
                        }
                        float num = 1;
                        float den = 1;
                        System.out.println("blend op=" + apng.getFctl().getBlendOp());
                        if (apng.getFctl() != null) {
                            num = apng.getFctl().getDelayNum();
                            den = apng.getFctl().getDelayDen();
                        }
                        if (den == 0) {
                            den = 100;
                        }
                        CustomTextureAtlas animationAtlas = pixmapPacker.generateCustomTextureAtlas(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear, false);

                        System.out.println("xx" + animationAtlas.getRegions().size);
//                        if (animationAtlas.getRegions().size > 0) {

                        CustomTextureAtlas cta = null;
                        float finalNum = num;
                        float finalDen = den;
                        SerializeUtil.serializePixmapPacker(pixmapPacker, new AtlasUploadListener() {
                            @Override
                            public void atlas(Array<String> strings) {
                                ImageObject.this.cids = strings;
                            }

                            @Override
                            public void failed(Throwable t) {

                            }
                        });
                  /*      SerializeUtil.deserializePixmapPacker(SerializeUtil.serializePixmapPacker(pixmapPacker,null), new AtlasDownloadListener() {
                            @Override
                            public void atlas(Array<CustomTextureAtlas.AtlasRegion> regions) {
                                System.out.println("here 80");
                                System.out.println("regions:" + regions.size);
                                textures = new Animation<CustomTextureAtlas.AtlasRegion>(finalNum / finalDen, regions, Animation.PlayMode.LOOP);
                                System.out.println("textures set:" + textures.getKeyFrame(0));
                                setBounds();
                            }

                            @Override
                            public void failed(Throwable t) {

                            }

                        });*/
                        textures = new Animation<>(num / den, animationAtlas.getRegions());


                    } catch (Exception e) {
                        Statics.exceptionLog("apng", e);
                    }

                }

                if (pixmapIn == null) {
                    Pixmap staticPixmap = null;
                    try {
                        staticPixmap = new Pixmap(new MemoryFileHandle(data));
                    } catch (Exception e1) {
                        Gdx.app.log("error", "data is not a png or jpg");
                        Gdx.app.log("data", new String(data));
                        Statics.exceptionLog("not", e1);
                    }
                    if (staticPixmap != null) {
                        texture = new Texture(staticPixmap);
                        setBounds();
                    }
                } else {
                    if (textures == null) {
                        Gdx.app.log("image", "non-animated");
                        texture = new Texture(pixmapIn);
                        setBounds();
                    }
//                    textures = null;
                }
            }
        });

    }

    public ImageObject(Pixmap pixmap) {
        texture = new Texture(pixmap);
        setBounds();
    }

    public ImageObject(Array<CustomTextureAtlas.AtlasRegion> regions, String[] jsoncids,float speed) {
        System.out.println("regions size="+regions.size);
        textures = new Animation<CustomTextureAtlas.AtlasRegion>(speed, regions, Animation.PlayMode.LOOP);

        this.cids.addAll(jsoncids);
        Gdx.app.log("success","Image animation loaded!");
    }

    public static void load(String url) {
//        Gdx.app.log("url", url);
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                if (url == null || url.isEmpty()) {
                    return;
                }
                if (url.startsWith("data:")) {
                    try {
                        final byte[] b = Base64Coder.decode(url.split(",")[1]);
                        SunshineBlue.nativeNet.uploadIPFS(b, new IPFSCIDListener() {
                            @Override
                            public void cid(String cid) {
                                SunshineBlue.nativeNet.downloadPixmap(Statics.IPFSGateway + cid, new Pixmap.DownloadPixmapResponseListener() {
                                    @Override
                                    public void downloadComplete(Pixmap pixmap) {
                                        SunshineBlue.addUserObj(new ImageObject(b, pixmap, cid));
                                    }

                                    @Override
                                    public void downloadFailed(Throwable t) {
                                        SunshineBlue.addUserObj(new ImageObject(b, null, cid));
                                    }
                                });
                            }

                            @Override
                            public void uploadFailed(Throwable t) {
                                Statics.exceptionLog("data url1", t);
                            }
                        });

                    } catch (Exception e) {
                        Statics.exceptionLog("data url", e);
                    }
                    ;


                } else if (url.startsWith("Q")) {
                    SunshineBlue.nativeNet.downloadIPFS(url, new IPFSFileListener() {
                        @Override
                        public void downloaded(byte[] file) {
                            if (file[0] == -54 && file[1] == -2 && file[2] == -70 && file[3] == -66) {
                                SunshineBlue.addUserObj(new ScriptObject(file));
                                return;
                            } else {
                                SunshineBlue.nativeNet.downloadPixmap(Statics.IPFSGateway + url, new Pixmap.DownloadPixmapResponseListener() {
                                    @Override
                                    public void downloadComplete(Pixmap pixmap) {
                                        Gdx.app.log("downl", "complete");
                                        SunshineBlue.addUserObj(new ImageObject(file, pixmap, url));
                                    }

                                    @Override
                                    public void downloadFailed(Throwable t) {
                                        Gdx.app.log("downl", "failed");
                                        SunshineBlue.addUserObj(new ImageObject(file, null, url));
                                    }
                                });
                            }
                            ;
                        }

                        @Override
                        public void downloadFailed(Throwable t) {
                            Statics.exceptionLog("downloadfailed", t);

                        }
                    });
                } else {
                    SunshineBlue.nativeNet.downloadFile(url, true, new IPFSFileListener() {
                        @Override
                        public void downloaded(byte[] file) {
                            SunshineBlue.nativeNet.downloadPixmap(url, new Pixmap.DownloadPixmapResponseListener() {
                                @Override
                                public void downloadComplete(Pixmap pixmap) {
                                    SunshineBlue.addUserObj(new ImageObject(file, pixmap, null));
                                }

                                @Override
                                public void downloadFailed(Throwable t) {
                                    SunshineBlue.addUserObj(new ImageObject(file, null, null));
                                }
                            });
                        }

                        @Override
                        public void downloadFailed(Throwable t) {

                        }
                    });

                }
            }
        });

    }

   /* private static void continueDownload(Pixmap pixmap, String url) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                Gdx.app.log("continue", url);
                SunshineBlue.nativeNet.downloadFile(url, new IPFSFileListener() {
                    @Override
                    public void downloaded(byte[] file) {
                        Gdx.app.log("continue", "download success");
                        Statics.adduserObj(new ImageObject(file, pixmap, null));

                    }

                    @Override
                    public void downloadFailed(Throwable t) {
                        Gdx.app.log("continue", "download failed");
                        Statics.exceptionLog("continued", (Exception) t);
                        Statics.adduserObj(new ImageObject(pixmap, null));
                    }
                });
            }
        });
    }*/

   /* public ImageObject() {

    }

    public ImageObject(String url) {
//
//        url = "https://api.codetabs.com/v1/proxy?quest=" + url;
        Gdx.app.log("url", url);
        if (url != null) {
            if (url.startsWith("Q")) {
                this.cid = url;
                url = Statics.IPFSGateway + url;
            }
            if (url.startsWith("data")) {
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
    }*/


    @Override
    public void setBounds() {
        polygon = new Polygon(new float[]{0, 0, sd.bounds.x, 0, sd.bounds.x, sd.bounds.y, 0, sd.bounds.y, 0, 0});
        polygon.setOrigin(sd.center.x, sd.center.y);
        polygon.setScale(sd.scale, sd.scale);
        polygon.rotate(sd.rotation);
        polygon.translate(sd.position.x - sd.center.x, sd.position.y - sd.center.y);
//        sd.position.add(-sd.center.x, -sd.center.y);
        if (texture != null) {
            sd.bounds.set(new Vector2(texture.getWidth(), texture.getHeight()));
        }
        try {
            if (textures != null) {
                TextureRegion frame = textures.getKeyFrame(0);
                sd.bounds.set(new Vector2(frame.getRegionWidth(), frame.getRegionHeight()));
                texture = null;
            } else {
                textures = null;
            }
        } catch (Exception e) {
//            textures = null;
            Statics.exceptionLog("tex", e);
        }

    }


    @Override
    public void draw(Batch batch, float delta) {
        batch.setTransformMatrix(new Matrix4().idt()
                        .translate(sd.position.x, sd.position.y, 0)
                        .rotate(0, 0, 1, sd.rotation)
                        .scale(sd.scale, sd.scale, 1)
//                        .translate(-center.x, -center.y, 0)
        );
        if (sd.visible) {
            batch.setColor(Color.WHITE);

            if (textures != null) {
                stateTime += delta;
                try {
                    batch.draw(textures.getKeyFrame(stateTime, true), -sd.center.x, -sd.center.y);
//                    System.out.println("draw:"+textures.getKeyFrame(stateTime, true));
                } catch (Exception e) {
                    Statics.exceptionLog("err", e);
                }
                ;
            } else {
                if (texture != null) {
                    batch.draw(texture, -sd.center.x, -sd.center.y);

                }
            }

            setBounds();
            if (SunshineBlue.instance.selectedObjects.contains(this, true)) {
                SunshineBlue.instance.shapedrawer.setColor(ColorHelper.numberToColorPercentage((float) SunshineBlue.instance.userObjects.indexOf(this, true) / (float) (SunshineBlue.instance.userObjects.size - 1)).cpy().lerp(Color.WHITE, SunshineBlue.instance.colorFlash));
                float radius = 10 + 10 * SunshineBlue.instance.colorFlash;
                SunshineBlue.instance.shapedrawer.circle(0, 0, radius, 2);
                angleCalc.set(0, radius);
                angleCalc.rotateDeg(angleRotateAnimAngle += 1);
                SunshineBlue.instance.shapedrawer.line(new Vector2(), angleCalc, 2);
                angleCalc.rotateDeg(90);
                SunshineBlue.instance.shapedrawer.line(new Vector2(), angleCalc, 2);
                angleCalc.rotateDeg(90);
                SunshineBlue.instance.shapedrawer.line(new Vector2(), angleCalc, 2);
                angleCalc.rotateDeg(90);
                SunshineBlue.instance.shapedrawer.line(new Vector2(), angleCalc, 2);

                if (polygon != null) {
                    batch.end();
                    batch.setTransformMatrix(SunshineBlue.instance.mx4Batch);
                    batch.begin();
//                SunshineBlue.instance.shapedrawer.setColor(Color.WHITE);

                    SunshineBlue.instance.shapedrawer.setColor(ColorHelper.numberToColorPercentage((float) SunshineBlue.instance.userObjects.indexOf(this, true) / ((float) SunshineBlue.instance.userObjects.size - 1)).cpy().lerp(Color.WHITE, SunshineBlue.instance.colorFlash));
                    SunshineBlue.instance.shapedrawer.polygon(polygon, 5, JoinType.SMOOTH);
                }

            }
        }
    }


    @Override
    public boolean isSelected(Vector2 touch) {
        setBounds();
//        polygon.translate(-center.x*scale,-center.y*scale);
        return polygon.contains(touch);
    }

    @Override
    public boolean isSelected(Polygon gon) {
        setBounds();
        return Intersector.overlapConvexPolygons(gon, polygon);
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
        val.addChild("CID", new JsonValue(cid));
        if (cids != null) {
            JsonValue cidarray = new JsonValue(JsonValue.ValueType.array);
            for (String s : cids) {
                cidarray.addChild(new JsonValue(s));
            }
            val.addChild("CIDS", cidarray);
            val.addChild("speed",new JsonValue(textures.getFrameDuration()));

        }
        val.addChild("class", new JsonValue(ImageObject.class.getName()));
        return val;
    }

    public static void deserialize(JsonValue json) {
        Gdx.app.log("deserialize", json.toJson(JsonWriter.OutputType.minimal));
        ScreenData sd1 = SerializeUtil.deserialize(json.get("screenData"), ScreenData.class);
        String cid = json.getString("CID");
        if (json.get("CIDS") != null) {
            float speed=json.getFloat("speed");
            String[] jsoncids = json.get("CIDS").asStringArray();
            SerializeUtil.deserializePixmapPacker(jsoncids, new AtlasDownloadListener() {
                @Override
                public void atlas(Array<CustomTextureAtlas.AtlasRegion> regions) {
                    ImageObject io = new ImageObject(regions,jsoncids,speed);
                    io.sd = sd1;
                    SunshineBlue.addUserObj(io);
                }

                @Override
                public void failed(Throwable t) {

                }
            });

        } else if (cid != null) {
            Gdx.app.log("cidd:", cid);
            SunshineBlue.nativeNet.downloadIPFS(cid, new IPFSFileListener() {
                @Override
                public void downloaded(byte[] file) {
                    SunshineBlue.nativeNet.downloadPixmap(Statics.IPFSGateway + cid, new Pixmap.DownloadPixmapResponseListener() {
                        @Override
                        public void downloadComplete(Pixmap pixmap) {
                            ImageObject io = new ImageObject(file, pixmap, cid);
                            io.sd = sd1;
                            SunshineBlue.addUserObj(io);
                        }

                        @Override
                        public void downloadFailed(Throwable t) {
                            ImageObject io = new ImageObject(file, null, cid);
                            io.sd = sd1;
                            SunshineBlue.addUserObj(io);
                        }
                    });

                }

                @Override
                public void downloadFailed(Throwable t) {
                    Statics.exceptionLog("deserialize", t);
                }
            });
        }
    }
}
