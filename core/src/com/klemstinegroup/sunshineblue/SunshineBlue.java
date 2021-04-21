package com.klemstinegroup.sunshineblue;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.github.tommyettinger.anim8.IncrementalAnimatedPNG;
import com.igormaznitsa.jjjvm.impl.JJJVMClassImpl;
import com.igormaznitsa.jjjvm.impl.jse.JSEProviderImpl;
import com.igormaznitsa.jjjvm.model.JJJVMMethod;
import com.igormaznitsa.jjjvm.model.JJJVMObject;
import com.igormaznitsa.jjjvm.model.JJJVMProvider;
import com.klemstinegroup.sunshineblue.engine.Statics;
import com.klemstinegroup.sunshineblue.engine.objects.*;
import com.klemstinegroup.sunshineblue.engine.overlays.*;
import com.klemstinegroup.sunshineblue.engine.util.*;
import space.earlygrey.shapedrawer.ShapeDrawer;
import sun.security.provider.Sun;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

import static com.badlogic.gdx.Application.LOG_INFO;

public class SunshineBlue extends ApplicationAdapter implements InputProcessor {

    public static NativeInterface nativeNet;
    public boolean isRecording;
    public TransformOverlay TRANSFORM_OVERLAY;
    public FontOverlay FONT_OVERLAY;
    public ImageOverlay IMAGE_OVERLAY;
    public DrawOverlay DRAW_OVERLAY;
    public BasicUIOverlay BASIC_UI_OVERLAY;
    public BlankOverlay BLANK_OVERLAY;
    public Batch batch;
    public BitmapFont font;
    public Overlay overlay=null;
    public StretchViewport overlayViewport;
    public ScreenViewport viewport;
    public final Array<BaseObject> userObjects = new Array<BaseObject>();
    public final Array<BaseObject> selectedObjects = new Array<BaseObject>();
    public InputMultiplexer im = new InputMultiplexer();
    public ArrayMap<Gestureable, GestureDetector> gestureDetectors = new ArrayMap<>();

    public Matrix4 mx4Batch = new Matrix4();
    public ShapeDrawer shapedrawer;
    public JSEProviderImpl JJVMprovider = new JSEProviderImpl();
    public static SunshineBlue instance;
    private int recCounter;
    private float recHalfSec=0;
    private static final float fps=10;

    //    Camera camera;
    public SunshineBlue() {
        super();
        this.nativeNet = new NativeJava();
    }

    public SunshineBlue(NativeInterface nativeIPFS) {
        super();
        this.nativeNet = nativeIPFS;
    }


//    public static Matrix4 mx4Batch = new Matrix4();

    private IncrementalAnimatedPNG apng;
    private MemoryFileHandle mfh;

    public AssetManager assetManager = new AssetManager();

    @Override
    public void create() {
        Gdx.app.log("create", "started");
        font = new BitmapFont();
        overlayViewport = new StretchViewport((550f * Gdx.graphics.getWidth() / Gdx.graphics.getHeight()), 550);
        Net.HttpRequest req = new Net.HttpRequest("GET");
       /* req.setUrl(Statics.IpfsGateway2 + "QmWATWQ7fVPP2EFGu71UkfnqhYXDYH566qy47CnJDgvs8u");
        req.setTimeOut(30000);
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                Gdx.net.sendHttpRequest(req, new Net.HttpResponseListener() {
                    @Override
                    public void handleHttpResponse(Net.HttpResponse httpResponse) {
                        String result = httpResponse.getResultAsString();
                        if (result.equals("Hello World\n")) {
                            String t1 = Statics.IPFSGateway;
                            Statics.IPFSGateway = Statics.IpfsGateway2;
                            Statics.IpfsGateway2 = t1;
                        }
                    }

                    @Override
                    public void failed(Throwable t) {
                        Statics.exceptionLog("love", t);
                    }

                    @Override
                    public void cancelled() {

                    }
                });

            }
        });*/

        this.instance = this;
        // set the loaders for the generator and the fonts themselves
        batch = new PolygonSpriteBatch();
        shapedrawer = new ShapeDrawer(batch, new TextureRegion(new Texture(getWhitePixel())));

//        shapedrawer = new ShapeDrawer(batch, Statics.whitePixel);
        FileHandleResolver resolver = new InternalFileHandleResolver();
        assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

        assetManager.load("skins/orange/skin/uiskin.json", Skin.class);
        assetManager.load("skin-composer-ui/skin-composer-ui.json", Skin.class);
        Texture.setAssetManager(assetManager);
        TRANSFORM_OVERLAY = new TransformOverlay();
        FONT_OVERLAY = new FontOverlay();
        IMAGE_OVERLAY = new ImageOverlay();
        DRAW_OVERLAY = new DrawOverlay();
        BASIC_UI_OVERLAY = new BasicUIOverlay();
        BLANK_OVERLAY = new BlankOverlay();
        Gdx.input.setCatchKey(Input.Keys.BACK, true);
        Gdx.input.setCatchKey(Input.Keys.ESCAPE, true);
//        VisUI.load(VisUI.SkinScale.X2);
        Gdx.input.setInputProcessor(im);
        im.addProcessor(this);
        if (overlay == null) Overlay.setOverlay(BLANK_OVERLAY);
        Gdx.app.setLogLevel(LOG_INFO);
/*//        img = new Texture("badlogic.jpg");


//        Statics.adduserObj(new ImageObject("https://upload.wikimedia.org/wikipedia/commons/thumb/6/6a/PNG_Test.png/477px-PNG_Test.png"));
        Statics.adduserObj(new ImageObject("QmZkvRWdSeksERDHhWSja9W7tja6wYQbk5KEfSxTbH87Va"));
        Statics.adduserObj(new ImageObject("QmZkvRWdSeksERDHhWSja9W7tja6wYQbk5KEfSxTbH87Va"));
        ((ScreenObject) Statics.userObjects.get(1)).sd.position.set(-200, -200);
        ((ScreenObject) Statics.userObjects.get(0)).sd.center.set(100, 100);
//        ((ScreenObject) Statics.userObjects.get(0)).scale = .1f;
        ((ScreenObject) Statics.userObjects.get(0)).sd.scale = .4f;
        ((ScreenObject) Statics.userObjects.get(1)).sd.scale = .4f;
        ((ScreenObject) Statics.userObjects.get(0)).sd.rotation = 45;


        ScreenData sd = new ScreenData();
        FontData fd = new FontData();
        fd.text = "tesT";
        FontObject fo = new FontObject(fd, sd);
        Statics.adduserObj(fo);
        for (int i = 0; i < 10; i++) {
            FontObject focpo = (FontObject) SerializeUtil.copy((FontObject) Statics.userObjects.get(Statics.userObjects.size - 1));
            focpo.sd.position.add(20, 20);
            focpo.sd.rotation += 10;
            Statics.adduserObj(focpo);
        }
//        Statics.overlayViewport = new FitViewport((800f *Gdx.graphics.getWidth() / Gdx.graphics.getHeight() )/ Gdx.graphics.getDensity(), 800 / Gdx.graphics.getDensity());


//        Statics.im.addProcessor(this);*/

//        SunshineBlue.nativeIPFS.downloadFile("QmQ2r6iMNpky5f1m4cnm3Yqw8VSvjuKpTcK1X7dBR1LkJF/cat.gif", new IPFSFileListener() {
//        Statics.adduserObj(new ImageObject("https://i.redd.it/0h1nbwj4bto61.jpg"));


        viewport = new ScreenViewport();
        mx4Batch = batch.getTransformMatrix().cpy();


//--------------------------------------------------------------------------------------------------


//       Statics.addUserObj(new ScriptObject("QmRaUUxykwAdMAeS2s3Qg1KXaSBngGeTJrbqcU6VzTTz1r"));


        //--------------------------------------------------------------------------------------------------
        viewport.apply();
//        Statics.gifOptions = new ImageOptions();

//        Statics.gifEncoderA = new AnimatedGifEncoder();
//        Statics.gifEncoderA.setDelay(10);
//        Statics.gifEncoderA.start(gifEncoderFile);
//        Statics.apng=new AnimatedPNG();

    }


    int cnt = 100;

    @Override
    public void render() {
        if (assetManager.update()) {
            // we are done loading, let's move to another screen!
        }
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
        viewport.apply();


        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

//        if (cnt--==2800){
//            ImageObject.load("https://upload.wikimedia.org/wikipedia/commons/thumb/6/6a/PNG_Test.png/477px-PNG_Test.png");
//            SunshineBlue.nativeNet.downloadIPFS("QmPfaw52jwB8WGPDMG8Xuo2vx94LRAHb3iB6L9RW9oruFj", new IPFSFileListener() {
//                @Override
//                public void downloaded(byte[] file) {
//                    Statics.adduserObj(new ImageObject(file));
//                }
//
//                @Override
//                public void downloadFailed(Throwable t) {
//
//                }
//            });
//            SunshineBlue.nativeNet.downloadIPFS("QmbSq4P8MaQqUBzoobcKbQ78MtQujrq9bAxi6MRDi11BWc", new IPFSFileListener() {
//                @Override
//                public void downloaded(byte[] file) {
//                    ImageObject bb = new ImageObject(file);
//                    Statics.adduserObj(bb);
//                    bb.sd.position.sub(500,data:image/jpeg;base64,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500);
//                }
//
//                @Override
//                public void downloadFailed(Throwable t) {
//
//                }
//            });
//            SunshineBlue.nativeNet.downloadFile("https://s0.2mdn.net/9340650/2719916979880523/Linux_Servers_Unmatched_Resiliency_2_300x250.png", new IPFSFileListener() {
//            SunshineBlue.nativeNet.downloadFile("https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/18cf621f-daff-4f65-8a94-a08911234091/d5msnyk-2dd09fd1-6d15-4602-b059-2ba3a5f899ba.png?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOiIsImlzcyI6InVybjphcHA6Iiwib2JqIjpbW3sicGF0aCI6IlwvZlwvMThjZjYyMWYtZGFmZi00ZjY1LThhOTQtYTA4OTExMjM0MDkxXC9kNW1zbnlrLTJkZDA5ZmQxLTZkMTUtNDYwMi1iMDU5LTJiYTNhNWY4OTliYS5wbmcifV1dLCJhdWQiOlsidXJuOnNlcnZpY2U6ZmlsZS5kb3dubG9hZCJdfQ.GXhVLfaMuEi6BlvpXbmu1BoV6yNjdxHHb9gi1s3dFJs", new IPFSFileListener() {
//            SunshineBlue.nativeNet.downloadFile("https://media.tenor.com/images/3c6f16ee7048e074dd823b9262538806/tenor.gif", new IPFSFileListener() {
//            SunshineBlue.nativeNet.downloadFile("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcReZeHB-40uL8fsio3gVfg8J6UkFO_S7YIzJy5q2C2NhvUW1J-zSylg6p6GdEBYOTFaWqc&usqp=CAU", new IPFSFileListener() {
              /*  @Override
                public void downloaded(byte[] file) {
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            Gdx.app.log("downloaded",file.length+"");
                            ImageObject bb = new ImageObject(file);
                            Statics.adduserObj(bb);
                            bb.sd.position.sub(0,0);
                        }
                    });

                }

                @Override
                public void downloadFailed(Throwable t) {

                }
            });*/

//        }
        recHalfSec+=Gdx.graphics.getDeltaTime();
        if (isRecording &&recHalfSec>(1f/fps) ) {
            recHalfSec=0;
            apng.write(FrameBufferUtils.drawObjectsPix(batch, viewport, userObjects, 600*viewport.getScreenWidth()/viewport.getScreenHeight(), 600));
            if (recCounter-- <= 0) {
                stopRecording();
            }
        }

        userObjects.sort(new Comparator<BaseObject>() {
            @Override
            public int compare(BaseObject o1, BaseObject o2) {
                if (o1 instanceof ScreenObject && o2 instanceof ScreenObject) {
                    return Integer.compare(((ScreenObject) o1).sd.layer, ((ScreenObject) o2).sd.layer);
                } else return 0;
            }
        });
        for (BaseObject bo : userObjects) {
            if (bo.regen) {
                bo.regen = false;
                bo.regenerate(assetManager);
            }
            if (bo instanceof Drawable) {
                ((Drawable) bo).draw(batch);
            }
            if (bo instanceof Actable) {
                ((Actable) bo).act();
            }
            batch.setTransformMatrix(mx4Batch);
        }
        if (overlay != null) {
            overlay.act();
            ((Drawable) overlay).draw(batch);
        }
        batch.setTransformMatrix(mx4Batch);
        if (isRecording) {
            font.draw(batch, "" + (recCounter/10), 10, 10);
        }
        batch.end();
        //------------------------------------------------------------

        //------------------------------------------------------------
    }


    @Override
    public void dispose() {
//        batch.dispose();
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log("resize", width + "\t" + height);
//        int WORLD_WIDTH=(550*width)/height;
//        int WORLD_HEIGHT=550;
        viewport.update(width, height);
        overlayViewport.update(width, height);
//        Statics.overlayViewport.getCamera().viewportWidth = WORLD_WIDTH;
//        Statics.overlayViewport.getCamera().viewportHeight = WORLD_HEIGHT;
//        Statics.overlayViewport.getCamera().position.set(WORLD_WIDTH/2,WORLD_HEIGHT/2, 0);
//        Statics.overlayViewport.getCamera().update();

        /*Statics stat=new Statics();
        Field[] fields = ClassReflection.getFields(Statics.class);
        for (Field f:fields){
            if (Overlay.class.isAssignableFrom(f.getType())){
                if (f.getType().isInterface()){continue;}
                try {
                    Object o=f.get(stat);
                    System.out.println(f.getType().getName());
                    Field stageField=ClassReflection.getField(f.getType(),"stage");
                    Stage st=(Stage) stageField.get(o);
                    st.getViewport().update(width,height);
//                    st.getCamera().viewportWidth = WORLD_WIDTH;
//                    st.getCamera().viewportHeight = WORLD_HEIGHT;
//                    st.getCamera().position.set(st.getCamera().viewportWidth / 2, st.getCamera().viewportHeight / 2, 0);
                    st.getCamera().update();
//                    st.setViewport(Statics.overlayViewport);
//                    st.getViewport().apply(true);
                } catch (ReflectionException e) {
                    e.printStackTrace();
                }
            }
        }*/
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK) {
            if (Overlay.overlays.size() > 0) {
                Overlay.backOverlay();
            }
        }
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

    /*@Override
    public boolean keyDown(int keycode) {
        for (BaseObject bo : Statics.userObjects) {
            if (bo instanceof Touchable) {
                ((Touchable) bo).keyDown(keycode);
            }
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        for (BaseObject bo : Statics.userObjects) {
            if (bo instanceof Touchable) {
                ((Touchable) bo).keyDown(keycode);
            }
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        for (BaseObject bo : Statics.userObjects) {
            if (bo instanceof Touchable) {
                ((Touchable) bo).keyTyped(character);
            }
        }
        return false;
    }

//

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        for (BaseObject bo : Statics.userObjects) {
            if (bo instanceof Touchable) {
                ((Touchable) bo).touchUp(screenX, screenY, pointer, button);
            }
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        viewport.unproject(touchdrag.set(screenX, screenY, 0));
        for (BaseObject bo : Statics.userObjects) {
            if (bo instanceof Touchable) {
                ((Touchable) bo).touchDragged(screenX, screenY, pointer);
            }
        }
        for (BaseObject bo : Statics.selectedObjects) {
            if (bo instanceof ScreenObject) {
                switch (Statics.transformButton) {
                    case 0:
                        ((ScreenObject) bo).position.add(touchdrag.cpy().sub(touchdown));
                        break;
                    case 1:
                        ((ScreenObject) bo).rotation += touchdrag.x - touchdown.x;
                        break;
                    case 2:
                        ((ScreenObject) bo).scale += (touchdrag.x - touchdown.x) / 200f;
                        break;
                }

            }
        }
        touchdown.set(touchdrag);


        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }*/

    @Override
    public void pause() {
        super.pause();
        SerializeUtil.save("autosave-" + UUID.randomUUID(), new IPFSCIDListener() {
            @Override
            public void cid(String cid) {
                Gdx.app.log("saved", "saved at " + cid);
//                SunshineBlue.super.pause();
//                Gdx.app.exit();
            }

            @Override
            public void uploadFailed(Throwable t) {
                Gdx.app.log("saved", "failed");
                Statics.exceptionLog("not saved", t);
//                Gdx.app.exit();
//                SunshineBlue.super.pause();
            }
        });

    }

    //    @Override
    public void resume() {
        super.resume();
        Pixmap pixmap = getWhitePixel();
        TextureRegion whitePixel = new TextureRegion(new Texture(pixmap));
        shapedrawer = new ShapeDrawer(batch, whitePixel);
        TRANSFORM_OVERLAY = new TransformOverlay();
        FONT_OVERLAY = new FontOverlay();
        IMAGE_OVERLAY = new ImageOverlay();
        DRAW_OVERLAY = new DrawOverlay();
        BASIC_UI_OVERLAY = new BasicUIOverlay();
//        Overlay.backOverlay();

//        Statics.prefs = Gdx.app.getPreferences("scenes");
//        Overlay.setOverlay(BASIC_UI_OVERLAY);
//        SerializeUtil.load("current");
    }

    private Pixmap getWhitePixel() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGB888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        return pixmap;
    }

    public void startRecording() {
        isRecording = true;
        recCounter = 300;
        recHalfSec=0;
        apng = new IncrementalAnimatedPNG();
        apng.setFlipY(true);
        mfh = new MemoryFileHandle();
        apng.start(mfh, (short) fps, 600*viewport.getScreenWidth()/viewport.getScreenHeight(), 600);
    }

    public void stopRecording() {
        isRecording = false;
        apng.end();
        SunshineBlue.nativeNet.uploadIPFS(mfh.readBytes(), new IPFSCIDListener() {
            @Override
            public void cid(String cid) {
                IPFSUtils.openIPFSViewer(cid);
            }

            @Override
            public void uploadFailed(Throwable t) {

            }
        });
    }
    public static void addUserObj(BaseObject b){
        Gdx.app.log("userobject added",(b.getClass().toString()));
        SunshineBlue.instance.userObjects.add(b);
    }

    public static void removeUserObj(BaseObject b){
        Gdx.app.log("userobject removed",(b.getClass().toString()));
        SunshineBlue.instance.userObjects.removeValue(b,true);
    }
}
