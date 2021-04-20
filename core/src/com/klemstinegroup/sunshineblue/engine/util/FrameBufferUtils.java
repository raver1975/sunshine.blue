package com.klemstinegroup.sunshineblue.engine.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.klemstinegroup.sunshineblue.engine.Statics;
import com.klemstinegroup.sunshineblue.engine.objects.BaseObject;
import com.klemstinegroup.sunshineblue.engine.overlays.Drawable;

public class FrameBufferUtils {
    static public Pixmap drawObjects(Batch batch,Viewport viewport, Array<BaseObject> objects) {
        FrameBuffer fb = new FrameBuffer(Pixmap.Format.RGBA8888, viewport.getScreenWidth(), viewport.getScreenHeight(), true);
        fb.begin();
        draw(batch,viewport);
        Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, viewport.getScreenWidth(), viewport.getScreenHeight());
        fb.end();
        fb.dispose();
        return flipPixmap(pixmap);
//        return pixmap;
    }

    public static Pixmap flipPixmap(Pixmap src) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        Pixmap flipped = new Pixmap(width, height, src.getFormat());
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                flipped.drawPixel(x, y, src.getPixel(x, height - y - 1));
            }
        }
        src.dispose();
        return flipped;
    }

    private static void draw(Batch batch, Viewport viewport) {
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.setTransformMatrix(Statics.mx4Batch);
        for (BaseObject bo : Statics.userObjects) {
            if (bo instanceof Drawable) {
                ((Drawable) bo).draw(batch);
            }
            batch.setTransformMatrix(Statics.mx4Batch);

        }
    }

    public static Pixmap drawObjectsPix(Batch batch,Viewport viewport, Array<BaseObject> objects,int width,int height) {
        FrameBuffer fb = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, true);
        fb.begin();
//        ((OrthographicCamera)viewport.getCamera()).setToOrtho(false,width,height);
        draw(batch,viewport);
        Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, width, height);
        fb.end();
        fb.dispose();
//        int[][] pixels = new int[height][width];
//        for (int x = 0; x <width; x++) {
//            for (int y = 0; y < height; y++) {
//                pixels[y][x] = pixmap.getPixel(x, height - y - 1)>>8;
//            }
//        }
        return pixmap;
    }
}
