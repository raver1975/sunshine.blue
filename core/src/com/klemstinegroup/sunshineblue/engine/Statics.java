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
import com.igormaznitsa.jjjvm.impl.jse.JSEProviderImpl;
import com.igormaznitsa.jjjvm.model.JJJVMProvider;
import com.klemstinegroup.sunshineblue.engine.objects.*;
import com.klemstinegroup.sunshineblue.engine.overlays.*;
import com.klemstinegroup.sunshineblue.engine.util.SerializeUtil;
import com.klemstinegroup.sunshineblue.engine.util.UUID;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Statics {


    public static StretchViewport overlayViewport = new StretchViewport((550f * Gdx.graphics.getWidth() / Gdx.graphics.getHeight()), 550);
    public static Overlay overlay=null;
    public static String IPFSGateway = "https://ipfs.io/ipfs/";
    public static String IpfsGateway2 = "http://ipfs.infura.io/ipfs";
    public static final String CORSGateway="https://api.codetabs.com/v1/proxy?quest=";
    public static String IPFSMediaViewer="QmUQBqWRN1UQSedX8YFnDTVAB9RgVoib13MxS8EwxgMtF7";
    public static final boolean debug = false;
    public static Matrix4 mx4Batch = new Matrix4();
    public static int transformButton;
    public static ScreenViewport viewport;
    public static boolean gif = true;
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

    public static void addUserObj(BaseObject b){
        Gdx.app.log("userobject added",(b.getClass().toString()));
        userObjects.add(b);
    }
}
