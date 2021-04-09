package com.klemstinegroup.sunshinelab.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.klemstinegroup.sunshinelab.engine.objects.*;
import com.klemstinegroup.sunshinelab.engine.util.MemoryFileHandle;
import com.madgag.gif.fmsware.AnimatedGifEncoder;
//import com.squareup.gifencoder.ImageOptions;
import com.sun.prism.image.ViewPort;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.Stack;

public class Statics {
    public static final String IPFSGateway = "https://ipfs.io/ipfs/";
    public static final String IPFSMediaViewer = "QmWWoB9DUFXz8v1ZVGXT8KjjZ7r7kbUQJPzPDxfpz36ei6";
    public static final boolean debug = false;
    public static Matrix4 mx4Batch = new Matrix4();
    //    public static GifEncoder gifEncoder;
    public static MemoryFileHandle gifEncoderFile;
//        public static ImageOptions gifOptions;
    public static AnimatedGifEncoder gifEncoderA;
    public static int transformButton;
    public static ScreenViewport viewport;
    public static Stack<Overlay> overlays=new Stack<>();
    ;
//    public static final boolean debug = true;

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
    public static FitViewport overlayViewport = new FitViewport((600f *Gdx.graphics.getWidth() / Gdx.graphics.getHeight() ), 600);
    public static final BasicUIOverlay BASIC_UI_OVERLAY = new BasicUIOverlay();
    public static final TransformOverlay TRANSFORM_OVERLAY = new TransformOverlay();
    public static final DrawOverlay DRAW_OVERLAY = new DrawOverlay();
    public static final FontOverlay FONT_OVERLAY = new FontOverlay();
    public static ImageOverlay IMAGE_OVERLAY =new ImageOverlay();
    public static  Overlay overlay;

    public static void setOverlay(Overlay overlay) {
        Overlay topOverlay = Statics.overlay;
        if (topOverlay != null) {
            if (topOverlay instanceof Touchable) {
                im.removeProcessor((Touchable) topOverlay);
            }
            topOverlay.removeInput();
            Statics.overlays.push(Statics.overlay);
        }
        if (overlay!=null) {
            if (overlay instanceof Touchable) {
                im.addProcessor((Touchable) overlay);
            }
            overlay.setInput();
            Statics.overlay=overlay;
        }
    }

    public static void backOverlay() {
        setOverlay(overlays.pop());
        overlays.pop();
    }
}
