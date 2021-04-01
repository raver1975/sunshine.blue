package com.klemstinegroup.sunshinelab.engine;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.klemstinegroup.sunshinelab.engine.objects.BaseObject;
import com.klemstinegroup.sunshinelab.engine.util.MemoryFileHandle;
import com.squareup.gifencoder.GifEncoder;
import com.squareup.gifencoder.ImageOptions;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Statics {
    public static final boolean debug = false;
    public static Matrix4 mx4Batch = new Matrix4();
    public static String test="test1234";
    public static GifEncoder gifEncoder;
    public static MemoryFileHandle gifEncoderFile;
    public static ImageOptions gifOptions;
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

    public static final Array<BaseObject> objects = new Array<BaseObject>();
    public static final Array<BaseObject> selectedobjects = new Array<BaseObject>();

}
