package com.klemstinegroup.sunshineblue;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.*;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.github.tommyettinger.anim8.IncrementalAnimatedPNG;
import com.igormaznitsa.jjjvm.impl.jse.JSEProviderImpl;
import com.klemstinegroup.sunshineblue.engine.commands.Command;
import com.klemstinegroup.sunshineblue.engine.Statics;
import com.klemstinegroup.sunshineblue.engine.objects.*;
import com.klemstinegroup.sunshineblue.engine.overlays.*;
import com.klemstinegroup.sunshineblue.engine.util.*;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.*;

import static com.badlogic.gdx.Application.LOG_INFO;

public class SunshineBlue extends ApplicationAdapter implements InputProcessor {

    public static NativeInterface nativeNet;
    public String loadCid;
    public boolean isRecording;
    public TransformOverlay TRANSFORM_OVERLAY;
    public FontOverlay FONT_OVERLAY;
    public ImageOverlay IMAGE_OVERLAY;
    public DrawOverlay DRAW_OVERLAY;
    public BasicUIOverlay BASIC_UI_OVERLAY;
    public BlankOverlay BLANK_OVERLAY;
    public ParticleOverlay PARTICLE_OVERLAY;
    public BackgroundOverlay BACKGROUND_OVERLAY;
    public LoopOverlay LOOP_OVERLAY;
    public Batch batch;
    public BitmapFont font;
    public Overlay overlay = null;
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
    public HashMap<String, String> otherCIDS = new HashMap<>();
    public Array<String> autoloaded = new Array<>();
    public boolean autoload;
    public long autoloadtime;
    //    public Rectangle recordRect;
    private int recCounter;
    public static final float fps = 10;
    public float colorFlash = 0;
    private float delta = 1;
    public Color bgColor = Color.CLEAR;
    public HashMap<Integer, Array<Command>> commands = new HashMap<>();
    public int frameCount;
    public int loopStart = 0;
    public int loopEnd = Statics.RECMAXFRAMES;
    public int lastframeCount;
    public long startTime;
    private GlyphLayout glyphLayout;
    public boolean pauseLoop = false;
    public VfxManager vfxManager;

//    public Stack<Command> commandStack = new Stack<>();

//    private int dstFunc;
//    private int srcFunc;

    //    Camera camera;
    public SunshineBlue() {
        super();
        this.nativeNet = new NativeJava();
        loadCid = "current";
    }

    public SunshineBlue(NativeInterface nativeIPFS) {
        super();
        this.nativeNet = nativeIPFS;
        loadCid = "current";
    }

    public SunshineBlue(NativeInterface nativeIPFS, String cid) {
        super();
        this.nativeNet = nativeIPFS;
        loadCid = cid;
    }


//    public static Matrix4 mx4Batch = new Matrix4();

    private IncrementalAnimatedPNG apng;
    private MemoryFileHandle mfh;

    public AssetManager assetManager = new AssetManager();

    public SunshineBlue(String cid) {
        super();
        loadCid = cid;
        this.nativeNet = new NativeJava();
    }

    @Override
    public void create() {
        Gdx.app.log("create", "started");
        this.instance = this;
        vfxManager = new VfxManager(Pixmap.Format.RGBA8888);

        font = new BitmapFont();
        glyphLayout = new GlyphLayout();

        overlayViewport = new StretchViewport((550f * Gdx.graphics.getWidth() / Gdx.graphics.getHeight()), 550);
        // set the loaders for the generator and the fonts themselves
        batch = new PolygonSpriteBatch();
        shapedrawer = new ShapeDrawer(batch, new TextureRegion(new Texture(getWhitePixel())));
        shapedrawer.setDefaultSnap(true);

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
        PARTICLE_OVERLAY = new ParticleOverlay();
        BACKGROUND_OVERLAY = new BackgroundOverlay();
        LOOP_OVERLAY = new LoopOverlay();
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
        viewport.getCamera().update();

//        System.out.println("recordrect"+"\t"+recordRect);
//        Statics.gifOptions = new ImageOptions();

//        Statics.gifEncoderA = new AnimatedGifEncoder();
//        Statics.gifEncoderA.setDelay(10);
//        Statics.gifEncoderA.start(gifEncoderFile);
//        Statics.apng=new AnimatedPNG();
        if (loadCid != null) {
            if (loadCid.equals("current")) {
                Preferences prefs = Gdx.app.getPreferences("scenes");
                loadCid = prefs.getString("current");
                if (loadCid == null || loadCid.isEmpty()) {
                    loadCid = Statics.splashCID;
                    autoload = true;
                    BASIC_UI_OVERLAY.autoload.setChecked(true);
                }

            }
            System.out.println("loading cid:" + loadCid);
            SerializeUtil.load(loadCid, false);
        }
        // remember SpriteBatch's current functions

        batch.enableBlending();
        SunshineBlue.instance.shapedrawer.setDefaultLineWidth(2);
         Preferences prefs = Gdx.app.getPreferences("scenes");
        for(Map.Entry<String,?> pref:prefs.get().entrySet()){
            if (!pref.getKey().equals("current")) {
                SunshineBlue.instance.otherCIDS.put(pref.getKey(), (String) pref.getValue());
            }
        }

        ParticleUtil.getParticleFiles();
        startTime = TimeUtils.millis();
    }


    @Override
    public void render() {
        if (autoload && TimeUtils.millis() > autoloadtime) {
            Gdx.app.log("autoload", "");
            autoloadtime = TimeUtils.millis() + 10000;
            if (otherCIDS.size() > 0) {
                if (otherCIDS.size() == autoloaded.size) {
                    autoloaded.clear();
                }
                while (true) {
                    int otherIndex = MathUtils.random(otherCIDS.size() - 1);
                    Iterator<Map.Entry<String, String>> iter = otherCIDS.entrySet().iterator();
                    for (int i = 0; i < otherIndex; i++) {
                        iter.next();
                    }
                    Map.Entry<String, String> entry = iter.next();
                    if (!autoloaded.contains(entry.getKey(), false)) {
                        autoloaded.add(entry.getKey());
                        SerializeUtil.load(entry.getKey(), false);
                        break;
                    }
                }

            }

        }

        if (!pauseLoop) {
            frameCount = ((int) ((TimeUtils.millis() - startTime) / (1000f / fps)));
            delta = isRecording ? (1f / fps) : Gdx.graphics.getDeltaTime();
        } else {
            startTime = TimeUtils.millis() - (long) ((frameCount * 1000f) / fps);
            delta = 0;
        }
        vfxManager.update(delta);

        if (frameCount != lastframeCount) {
            if (frameCount > loopEnd) {
                if (isRecording){
                    stopRecording();
                }
                Command.setToFrame(loopStart);
                frameCount = loopStart;
                startTime = TimeUtils.millis() - (long) ((frameCount * 1000f) / fps);
            }
            Command.compress(frameCount);
            Array<Command> commandstoexec = commands.get(frameCount);
            if (commandstoexec != null) {
                for (Command c : commandstoexec) {
                    c.execute();
                }
            }
            if (isRecording) {
                System.out.println("rec frame");
                apng.write(FrameBufferUtils.drawObjectsPix(batch, viewport, userObjects, 600 * viewport.getScreenWidth() / viewport.getScreenHeight(), 600, false));
                if (recCounter-- <= 0) {
                    stopRecording();
                }
            }
        }
        lastframeCount = frameCount;
        colorFlash += delta / 3f;
        if (colorFlash > .4f) {
            colorFlash = 0;
        }
        if (assetManager.update()) {
            // we are done loading, let's move to another screen!
        }
        Gdx.gl.glClearColor(bgColor.r, bgColor.g, bgColor.b, bgColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
        viewport.apply();
        userObjects.sort(new Comparator<BaseObject>() {
            @Override
            public int compare(BaseObject o1, BaseObject o2) {
                if (o1 instanceof ScreenObject && o2 instanceof ScreenObject) {
                    return Float.compare(((ScreenObject) o1).sd.layer, ((ScreenObject) o2).sd.layer);
                } else if (o1 instanceof ScreenObject) {
                    return -1;
                } else if (o2 instanceof ScreenObject) {
                    return 1;
                }
                return 0;
            }
        });

        vfxManager.cleanUpBuffers(bgColor);
        vfxManager.beginInputCapture();

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();


        for (BaseObject bo : userObjects) {
            if (bo.regen) {
                bo.regen = false;
                bo.regenerate(assetManager);
            }
            if (bo instanceof Drawable) {
                ((Drawable) bo).draw(batch, delta);
            }
            if (bo instanceof Actable) {
                ((Actable) bo).act();
            }
            batch.setTransformMatrix(mx4Batch);
        }




        batch.end();
        vfxManager.endInputCapture();
        vfxManager.applyEffects();
        vfxManager.renderToScreen();

        batch.begin();
        if (overlay != null) {
            ((Drawable) overlay).draw(batch, delta);
        }
        String text = loopStart + " / " + frameCount + " / " + loopEnd;
        glyphLayout.setText(font, text);
        batch.setColor(Color.WHITE);
        shapedrawer.update();
        shapedrawer.setColor(Color.RED);
        shapedrawer.setDefaultLineWidth(4);
        shapedrawer.line(10 + (SunshineBlue.instance.overlayViewport.getWorldWidth() - 20) * ((float) (loopStart) / (float) (Statics.RECMAXFRAMES)), SunshineBlue.instance.overlayViewport.getWorldHeight() - 5, 10 + (SunshineBlue.instance.overlayViewport.getWorldWidth() - 20) * ((float) (loopEnd) / (float) (Statics.RECMAXFRAMES)), SunshineBlue.instance.overlayViewport.getWorldHeight() - 5);
        shapedrawer.setColor(Color.WHITE);
        shapedrawer.setDefaultLineWidth(2);
        shapedrawer.line(10 + (SunshineBlue.instance.overlayViewport.getWorldWidth() - 20) * ((float) (frameCount) / (float) (Statics.RECMAXFRAMES)), SunshineBlue.instance.overlayViewport.getWorldHeight() - 2, 10 + (SunshineBlue.instance.overlayViewport.getWorldWidth() - 20) * ((float) (frameCount) / (float) (Statics.RECMAXFRAMES)), SunshineBlue.instance.overlayViewport.getWorldHeight() - 10);
        font.draw(batch, text, SunshineBlue.instance.overlayViewport.getWorldWidth() - glyphLayout.width - 10, SunshineBlue.instance.overlayViewport.getWorldHeight() - 10);
        batch.setTransformMatrix(mx4Batch);
        if (isRecording) {
            font.draw(batch, "" + (recCounter / 10f), 0, 0);
        }
        batch.end();

        //------------------------------------------------------------

        //------------------------------------------------------------
    }


    /*@Override
    public void dispose() {
        batch.dispose();
        vfxManager.dispose();
        assetManager.dispose();
    }*/

    @Override
    public void resize(int width, int height) {
        Gdx.app.log("resize", width + "\t" + height);
        viewport.update(width, height);
        overlayViewport.update(width, height);
        vfxManager.resize(width,height);
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

    @Override
    public void pause() {
        SerializeUtil.save(new IPFSCIDListener() {
            @Override
            public void cid(String cid) {
                Gdx.app.log("saved", "saved at " + cid);
            }

            @Override
            public void uploadFailed(Throwable t) {
                Gdx.app.log("saved", "failed");
                Statics.exceptionLog("not saved", t);
            }
        });
        super.pause();
    }


    private Pixmap getWhitePixel() {
        Pixmap pixmap = new Pixmap(10, 10, Pixmap.Format.RGB888);
        pixmap.setColor(Color.BLACK);
        pixmap.fill();

        pixmap.setColor(Color.WHITE);
        for (int i = 0; i < 10; i++) {
            pixmap.drawPixel(i, i);
        }
        return pixmap;
    }

    public void startRecording() {
//        Gdx.graphics.setContinuousRendering(false);

//        loopStart = 0;
//        loopEnd = Statics.RECMAXFRAMES;
        pauseLoop = false;
        isRecording = true;

        recCounter = 1+loopEnd-loopStart;
        apng = new IncrementalAnimatedPNG();
        apng.setFlipY(true);
        mfh = new MemoryFileHandle();
        int w = 600 * viewport.getScreenWidth() / viewport.getScreenHeight();
        int h = 600;
        apng.start(mfh, (short) fps, w, h);
        Command.setToFrame(SunshineBlue.instance.loopStart);
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
//        Gdx.graphics.setContinuousRendering(true);
    }

    public static void addUserObj(BaseObject b) {
        Gdx.app.log("userobject added", (b.getClass().toString()));
        SunshineBlue.instance.userObjects.add(b);
    }

    public static void removeUserObj(BaseObject b) {
        Gdx.app.log("userobject removed", (b.getClass().toString()));
        SunshineBlue.instance.userObjects.removeValue(b, true);
    }
}
