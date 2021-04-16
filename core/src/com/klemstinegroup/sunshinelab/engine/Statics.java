package com.klemstinegroup.sunshinelab.engine;

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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.klemstinegroup.sunshinelab.engine.objects.*;
import com.klemstinegroup.sunshinelab.engine.overlays.*;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.Stack;

public class Statics {
    public static Preferences prefs = Gdx.app.getPreferences("scenes");
    public static final String IPFSGateway = "https://ipfs.io/ipfs/";
    public static final String CORSGateway="https://api.codetabs.com/v1/proxy?quest=";
    public static final boolean debug = false;
    public static Matrix4 mx4Batch = new Matrix4();
    public static int transformButton;
    public static ScreenViewport viewport;
    public static Stack<Overlay> overlays = new Stack<>();
    public static boolean gif = true;
    static TextureRegion whitePixel;
    static {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGB888);
        pixmap.setColor(Color.WHITE);
        pixmap.drawPixel(0, 0);
        whitePixel = new TextureRegion(new Texture(pixmap));
    }

    public static final Batch batch = new PolygonSpriteBatch();
    public static final ShapeDrawer shapedrawer = new ShapeDrawer(batch, whitePixel);

    public static final Array<BaseObject> userObjects = new Array<BaseObject>();
    public static final Array<BaseObject> selectedObjects = new Array<BaseObject>();
    public static InputMultiplexer im = new InputMultiplexer();
    public static StretchViewport overlayViewport = new StretchViewport((550f * Gdx.graphics.getWidth() / Gdx.graphics.getHeight()), 550);
    public static final FontOverlay FONT_OVERLAY = new FontOverlay();
    public static final BasicUIOverlay BASIC_UI_OVERLAY = new BasicUIOverlay();
    public static final TransformOverlay TRANSFORM_OVERLAY = new TransformOverlay();
    public static final DrawOverlay DRAW_OVERLAY = new DrawOverlay();
    public static final ImageOverlay IMAGE_OVERLAY = new ImageOverlay();
    public static Overlay overlay;
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
