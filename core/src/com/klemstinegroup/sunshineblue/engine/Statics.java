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


    public static String IPFSGateway = "https://ipfs.io/ipfs/";
    //    public static String IpfsGateway2 = "http://ipfs.infura.io/ipfs/";
    public static final String CORSGateway="https://api.codetabs.com/v1/proxy?quest=";
    public static String IPFSMediaViewer="QmZyHEYrFL7hjE58bGHnzPSsxXXyskSBcyXcXZjqfTAP7g";
    public static final boolean debug = true;

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
