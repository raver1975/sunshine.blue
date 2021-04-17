package com.klemstinegroup.sunshineblue.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.klemstinegroup.sunshineblue.engine.objects.*;
import com.klemstinegroup.sunshineblue.engine.overlays.*;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Statics {


    public static StretchViewport overlayViewport = new StretchViewport((550f * Gdx.graphics.getWidth() / Gdx.graphics.getHeight()), 550);
//    public static  FontOverlay FONT_OVERLAY = new FontOverlay(assetManager);
//    public static  BasicUIOverlay BASIC_UI_OVERLAY = new BasicUIOverlay(assetManager);
//    public static  TransformOverlay TRANSFORM_OVERLAY = new TransformOverlay(assetManager);
//    public static  DrawOverlay DRAW_OVERLAY = new DrawOverlay(assetManager);
//    public static  ImageOverlay IMAGE_OVERLAY = new ImageOverlay(assetManager);
    public static Overlay overlay=null;
    public static Preferences prefs = Gdx.app.getPreferences("scenes");
    public static final String IPFSGateway = "https://ipfs.io/ipfs/";
    public static final String CORSGateway="https://api.codetabs.com/v1/proxy?quest=";
    public static final boolean debug = false;
    public static Matrix4 mx4Batch = new Matrix4();
    public static int transformButton;
    public static ScreenViewport viewport;
    public static boolean gif = true;
    static TextureRegion whitePixel;
    static {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGB888);
        pixmap.setColor(Color.WHITE);
        pixmap.drawPixel(0, 0);
        whitePixel = new TextureRegion(new Texture(pixmap));
    }

    public static Batch batch = new PolygonSpriteBatch();
    public static final ShapeDrawer shapedrawer = new ShapeDrawer(batch, whitePixel);

    public static final Array<BaseObject> userObjects = new Array<BaseObject>();
    public static final Array<BaseObject> selectedObjects = new Array<BaseObject>();
    public static InputMultiplexer im = new InputMultiplexer();

    public static ArrayMap<Gestureable, GestureDetector> gestureDetectors = new ArrayMap<>();


    public Statics(){}

    public static void exceptionLog(String tag,Exception e) {
        Gdx.app.log(tag,e.toString());
        StackTraceElement[] st=e.getStackTrace();
        for (StackTraceElement s:st){
            Gdx.app.log(tag,s.toString());
        }
    }

    public static void exceptionLog(String tag, Throwable t) {
        exceptionLog(tag,(Exception)t);
    }
}
