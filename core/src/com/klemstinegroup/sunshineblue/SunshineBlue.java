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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.crashinvaders.vfx.VfxManager;
import com.github.tommyettinger.anim8.IncrementalAnimatedPNG;
import com.igormaznitsa.jjjvm.impl.JJJVMClassFieldImpl;
import com.igormaznitsa.jjjvm.impl.jse.JSEProviderImpl;
import com.klemstinegroup.sunshineblue.engine.commands.Command;
import com.klemstinegroup.sunshineblue.engine.Statics;
import com.klemstinegroup.sunshineblue.engine.objects.*;
import com.klemstinegroup.sunshineblue.engine.overlays.*;
import com.klemstinegroup.sunshineblue.engine.util.*;
import space.earlygrey.shapedrawer.ShapeDrawer;
import sun.security.provider.Sun;

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
    public long autoloadtime;
    //    public Rectangle recordRect;
    private int recCounter;
    public static final float fps = 10;
    //    public float colorFlash = 0;
    private float delta = 1;
    public Color bgColor = Color.CLEAR;
    public HashMap<Integer, Array<Command>> commands = new HashMap<>();
    public int frameCount;
    public int loopStart = 0;
    public int loopEnd = Statics.recframes;
    public int lastframeCount;
    public long startTime;
    private GlyphLayout glyphLayout;
    public boolean pauseLoop = false;
    public VfxManager vfxManager;
    private IncrementalAnimatedPNG apng;
    private MemoryFileHandle mfh;
    public AssetManager assetManager = new AssetManager();
    private Vector2 touchdown = new Vector2();
    private boolean tempPauseLoop;
    private boolean loopEndDrag;
    private boolean loopStartDrag;


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
                    ParticleObject.deserialize(new JsonReader().parse(Statics.firstSceneJson));
                }

            }
            System.out.println("loading cid:" + loadCid);
            SerializeUtil.load(loadCid, false);
        }
        // remember SpriteBatch's current functions

        batch.enableBlending();
        SunshineBlue.instance.shapedrawer.setDefaultLineWidth(2);
        Preferences prefs = Gdx.app.getPreferences("scenes");
        for (Map.Entry<String, ?> pref : prefs.get().entrySet()) {
            if (!pref.getKey().equals("current")) {
                SunshineBlue.instance.otherCIDS.put(pref.getKey(), (String) pref.getValue());
            }
        }

        ParticleUtil.getParticleFiles();
        startTime = TimeUtils.millis();
    }


    @Override
    public void render() {


        int frameCount1 = frameCount;
        if (!pauseLoop) {
            frameCount1 = ((int) ((TimeUtils.millis() - startTime) / (1000f / fps)));
            if (isRecording) {
                frameCount1 = frameCount + 1;
            }
            delta = isRecording ? (1f / fps) : Gdx.graphics.getDeltaTime();
        } else {
            startTime = TimeUtils.millis() - (long) ((frameCount * 1000f) / fps);
            delta = 0;
        }
        vfxManager.update(delta);

        if (frameCount1 != lastframeCount) {
            Command.compress(frameCount1);

            if (frameCount1 > loopEnd) {
                if (BASIC_UI_OVERLAY.autoloadButton.isChecked() && TimeUtils.millis() > autoloadtime) {
                    Gdx.app.log("autoload", "");
                    autoloadtime = TimeUtils.millis() + Statics.AUTOLOADTIME;
                    if (otherCIDS.size() > 0) {
                        if (otherCIDS.size() <= autoloaded.size) {
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
                if (isRecording) {
                    stopRecording();
                }
//                Array<Command> commandstoexec = commands.get(frameCount);
//                System.out.println("executiring:" + frameCount);
//                if (commandstoexec != null) {
//                    for (Command c : commandstoexec) {
//                        c.execute();
//                    }
//                }
                frameCount1 = loopStart;

            }
            Command.setToFrame(frameCount1);

            if (isRecording) {
                System.out.println("rec frame");
                apng.write(FrameBufferUtils.drawObjectsPix(batch, viewport, userObjects, 600 * viewport.getScreenWidth() / viewport.getScreenHeight(), 600, false));
                if (recCounter-- <= 0) {
                    stopRecording();
                }
            }
        }
        lastframeCount = frameCount1;
//        colorFlash += delta / 10f;
//        if (colorFlash > .2f) {
//            colorFlash = 0;
//        }
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


        for (int i = 0; i < userObjects.size; i++) {
            BaseObject bo = SunshineBlue.instance.userObjects.get(i);

//            if (SunshineBlue.instance.overlay != SunshineBlue.instance.TRANSFORM_OVERLAY || (SunshineBlue.instance.TRANSFORM_OVERLAY.checkBoxArray.size > i && SunshineBlue.instance.TRANSFORM_OVERLAY.checkBoxArray.get(i).isChecked())) {


            if (bo.regen) {
                bo.regen = false;
                bo.regenerate(assetManager);
            }
            if (bo instanceof Drawable) {
                boolean bounds = SunshineBlue.instance.selectedObjects.contains(bo, true);
                ((Drawable) bo).draw(batch, delta, bounds);
            }
            batch.setTransformMatrix(mx4Batch);
//            }

            if (bo instanceof Actable) {
                ((Actable) bo).act();
            }


        }


        batch.end();
        vfxManager.endInputCapture();
        vfxManager.applyEffects();
        vfxManager.renderToScreen();

        Array<BaseObject> temp = SunshineBlue.instance.selectedObjects.size == 0 ? SunshineBlue.instance.userObjects : SunshineBlue.instance.selectedObjects;
        int[][] b = new int[temp.size][Statics.recframes];
        int max = 0;
        for (int i = 0; i < Statics.recframes; i++) {
            Array<Command> subarray = SunshineBlue.instance.commands.get(i);
            if (subarray != null) {
                for (int j = 0; j < temp.size; j++) {
                    BaseObject bo = temp.get(j);
                    for (Command c : subarray) {
                        if (c.actionOnUUID.equals(bo.uuid)) {
                            b[j][i]++;
                            if (b[j][i] > max) {
                                max = b[j][i];
                            }
                        }
                    }

                }
            }
        }

        if (overlay != null) {
            batch.begin();
            ((Drawable) overlay).draw(batch, delta, false);
            if (overlay != SunshineBlue.instance.BLANK_OVERLAY) {

                batch.setColor(Color.WHITE);
                shapedrawer.update();
                shapedrawer.setColor(Color.GRAY);
                shapedrawer.setDefaultLineWidth(12);
                shapedrawer.line(10 + (SunshineBlue.instance.overlayViewport.getWorldWidth() - 20) * ((float) (loopStart) / (float) (Statics.recframes)), SunshineBlue.instance.overlayViewport.getWorldHeight() - 13, 10 + (SunshineBlue.instance.overlayViewport.getWorldWidth() - 20) * ((float) (loopEnd) / (float) (Statics.recframes)), SunshineBlue.instance.overlayViewport.getWorldHeight() - 13);
//                shapedrawer.setDefaultLineWidth(3);

                for (int i = 0; i < temp.size; i++) {
                    shapedrawer.setColor(ColorUtil.numberToColorPercentage((float) SunshineBlue.instance.userObjects.indexOf(temp.get(i), true) / (float) (SunshineBlue.instance.userObjects.size - 1)));
                    for (int j = 0; j < Statics.recframes - 1; j++) {
                        if (b[i][j] > 0) {
                            shapedrawer.line(10 + (SunshineBlue.instance.overlayViewport.getWorldWidth() - 20) * ((float) (j) / (float) (Statics.recframes)), SunshineBlue.instance.overlayViewport.getWorldHeight() - 13, 10 + (SunshineBlue.instance.overlayViewport.getWorldWidth() - 20) * ((float) (j + 1) / (float) (Statics.recframes)), SunshineBlue.instance.overlayViewport.getWorldHeight() - 13);
                        }
                    }

                }
                shapedrawer.setColor(Color.WHITE);
                shapedrawer.line(10 + (SunshineBlue.instance.overlayViewport.getWorldWidth() - 20) * ((float) (loopStart) / (float) (Statics.recframes)), SunshineBlue.instance.overlayViewport.getWorldHeight() - 5, 10 + (SunshineBlue.instance.overlayViewport.getWorldWidth() - 20) * ((float) (loopStart) / (float) (Statics.recframes)), SunshineBlue.instance.overlayViewport.getWorldHeight() - 21);
                shapedrawer.line(10 + (SunshineBlue.instance.overlayViewport.getWorldWidth() - 20) * ((float) (loopEnd) / (float) (Statics.recframes)), SunshineBlue.instance.overlayViewport.getWorldHeight() - 5, 10 + (SunshineBlue.instance.overlayViewport.getWorldWidth() - 20) * ((float) (loopEnd) / (float) (Statics.recframes)), SunshineBlue.instance.overlayViewport.getWorldHeight() - 21);
                shapedrawer.setColor(Color.RED);
                shapedrawer.line(10 + (SunshineBlue.instance.overlayViewport.getWorldWidth() - 20) * ((float) (frameCount) / (float) (Statics.recframes)), SunshineBlue.instance.overlayViewport.getWorldHeight() - 5, 10 + (SunshineBlue.instance.overlayViewport.getWorldWidth() - 20) * ((float) (frameCount) / (float) (Statics.recframes)), SunshineBlue.instance.overlayViewport.getWorldHeight() - 21);
                shapedrawer.setDefaultLineWidth(3);
                String text = loopStart + " / " + frameCount + " / " + loopEnd;
                glyphLayout.setText(font, text);
                font.draw(batch, text, SunshineBlue.instance.overlayViewport.getWorldWidth() - glyphLayout.width - 10, SunshineBlue.instance.overlayViewport.getWorldHeight() - 23);

                /*int ycnt=100;
                for (BaseObject bo:SunshineBlue.instance.userObjects){
                    font.draw(batch,bo.uuid+" "+bo.getClass().toString(),10,ycnt);
                    ycnt+=20;
                }*/
            }
            batch.setTransformMatrix(mx4Batch);
            if (isRecording) {
                font.draw(batch, "" + (recCounter / 10f), 0, 0);
            }


            batch.end();
        }
        //------------------------------------------------------------

        //------------------------------------------------------------
    }


    @Override
    public void dispose() {
        batch.dispose();
        vfxManager.dispose();
        assetManager.dispose();
        TRANSFORM_OVERLAY.dispose();
        FONT_OVERLAY.dispose();
        IMAGE_OVERLAY.dispose();
        DRAW_OVERLAY.dispose();
        BASIC_UI_OVERLAY.dispose();
        BLANK_OVERLAY.dispose();
        PARTICLE_OVERLAY.dispose();
        BACKGROUND_OVERLAY.dispose();
        LOOP_OVERLAY.dispose();
        font.dispose();
        if (apng != null) apng.dispose();
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log("resize", width + "\t" + height);
        viewport.update(width, height);
        overlayViewport.update(width, height);
        vfxManager.resize(width, height);
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
        tempPauseLoop = pauseLoop;
        if (touched(screenX, screenY)) {
            pauseLoop = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        pauseLoop = tempPauseLoop;
        if (touched(screenX, screenY)) {
            pauseLoop = false;
            loopEndDrag = false;
            loopStartDrag = false;
            return true;
        }
        return false;
    }

    private boolean touched(int screenX, int screenY) {
        SunshineBlue.instance.overlayViewport.unproject(touchdown.set(screenX, screenY));
        if (overlay != BLANK_OVERLAY) {
            if (touchdown.y >= overlayViewport.getWorldHeight() - 30) {
                touchdown.sub(10, 0);
                int frameCount1 = Math.max(0, (int) (Statics.recframes * touchdown.x / (overlayViewport.getWorldWidth() - 20)));
                frameCount1 = Math.min(frameCount1, Statics.recframes);
                if (loopEndDrag || (frameCount1 >= SunshineBlue.instance.loopEnd && frameCount1 >= SunshineBlue.instance.loopStart)) {
                    SunshineBlue.instance.loopEnd = frameCount1;
                    loopEndDrag = true;
                } else if (loopStartDrag || (frameCount1 <= SunshineBlue.instance.loopStart && frameCount1 <= SunshineBlue.instance.loopEnd)) {
                    SunshineBlue.instance.loopStart = frameCount1;
                    loopStartDrag = true;
                }
                Command.setToFrame(frameCount1);
                //lastframeCount = (int) (Statics.recframes*touchdown.x/(overlayViewport.getWorldWidth()-20));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return touched(screenX, screenY);
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

        recCounter = 1 + loopEnd - loopStart;
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
//        SunshineBlue.nativeNet.uploadIPFS(mfh.readBytes(), null);
        SunshineBlue.nativeNet.uploadIPFS(mfh.readBytes(), new IPFSCIDListener() {
            @Override
            public void cid(String cid) {
                IPFSUtils.openIPFSViewer(cid,true);
            }

            @Override
            public void uploadFailed(Throwable t) {

            }
        });

//        Gdx.graphics.setContinuousRendering(true);
    }

    public static Array<String> prohibited = new Array<>();

    public static void addUserObj(BaseObject b) {
        Gdx.app.log("userobject added", (b.getClass().toString()));
        if (b instanceof CompositeObject) {
            for (BaseObject ba : ((CompositeObject) b).objects) {
                prohibited.add(ba.uuid);
            }

        }
        Array<BaseObject> removed=new Array<>();
        for (BaseObject ba : SunshineBlue.instance.userObjects) {
            if (prohibited.contains(ba.uuid, false)) {
//                    SunshineBlue.instance.userObjects.removeValue(ba,false);
                System.out.println("found:"+ba.uuid);
                removed.add(ba);
            }
        }
        for (BaseObject ba:removed){
            removeUserObj(ba);
        }
        if (!prohibited.contains(b.uuid, false)) {
            SunshineBlue.instance.userObjects.add(b);
        }
    }

    public static void removeUserObj(BaseObject b) {
        Gdx.app.log("userobject removed", (b.getClass().toString()));
        if (b instanceof CompositeObject) {
            for (BaseObject ba : ((CompositeObject) b).objects) {
                prohibited.removeValue(ba.uuid,false);
                addUserObj(ba);
            }
        }
        SunshineBlue.instance.userObjects.removeValue(b, false);
    }
}
