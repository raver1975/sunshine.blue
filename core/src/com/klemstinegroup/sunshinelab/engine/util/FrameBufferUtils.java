package com.klemstinegroup.sunshinelab.engine.util;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.klemstinegroup.sunshinelab.engine.Statics;
import com.klemstinegroup.sunshinelab.engine.objects.BaseObject;
import com.klemstinegroup.sunshinelab.engine.objects.Drawable;
import com.klemstinegroup.sunshinelab.engine.objects.Overlay;

public class FrameBufferUtils {
    static public Pixmap drawObjects(Viewport viewport, Array<BaseObject> objects) {
        FrameBuffer fb = new FrameBuffer(Pixmap.Format.RGBA8888, viewport.getScreenWidth(), viewport.getScreenHeight(), true);
        fb.begin();
        draw(viewport);
        Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, viewport.getScreenWidth(), viewport.getScreenHeight());
        fb.end();
        fb.dispose();
        return flipPixmap(pixmap);
//        return pixmap;
    }

    public static void flipPixmap1(Pixmap p) {
        int w = p.getWidth();
        int h = p.getHeight();
        int hold;

        //change blending to 'none' so that alpha areas will not show
        //previous orientation of image
        p.setBlending(Pixmap.Blending.None);
        for (int y = 0; y < h / 2; y++) {
            for (int x = 0; x < w / 2; x++) {
                //get color of current pixel
                hold = p.getPixel(x, y);
                //draw color of pixel from opposite side of pixmap to current position
                p.drawPixel(x, y, p.getPixel(w - x - 1, y));
                //draw saved color to other side of pixmap
                p.drawPixel(w - x - 1, y, hold);
                //repeat for height/width inverted pixels
                hold = p.getPixel(x, h - y - 1);
                p.drawPixel(x, h - y - 1, p.getPixel(w - x - 1, h - y - 1));
                p.drawPixel(w - x - 1, h - y - 1, hold);
            }
        }
        //set blending back to default
        p.setBlending(Pixmap.Blending.SourceOver);
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

    private static void draw(Viewport viewport) {
        Statics.batch.setProjectionMatrix(viewport.getCamera().combined);
        Statics.batch.setTransformMatrix(Statics.mx4Batch);
        for (BaseObject bo : Statics.userObjects) {
            if (bo instanceof Drawable) {
                ((Drawable) bo).draw(Statics.batch);
            }
            Statics.batch.setTransformMatrix(Statics.mx4Batch);

        }
    }

    public static int[][] drawObjectsInt(Viewport viewport, Array<BaseObject> objects,int width,int height) {
        FrameBuffer fb = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, true);
        fb.begin();
//        ((OrthographicCamera)viewport.getCamera()).setToOrtho(false,width,height);
        draw(viewport);
        Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, width, height);
        fb.end();
        fb.dispose();
        int[][] pixels = new int[height][width];
        for (int x = 0; x <width; x++) {
            for (int y = 0; y < height; y++) {
                pixels[y][x] = pixmap.getPixel(x, height - y - 1)>>8;
            }
        }
        pixmap.dispose();
        return pixels;
    }

    public static Pixmap drawObjectsPix(Viewport viewport, Array<BaseObject> objects,int width,int height) {
        FrameBuffer fb = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, true);
        fb.begin();
//        ((OrthographicCamera)viewport.getCamera()).setToOrtho(false,width,height);
        draw(viewport);
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
