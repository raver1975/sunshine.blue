package com.klemstinegroup.sunshinelab.engine;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.klemstinegroup.sunshinelab.engine.objects.BaseObject;
import com.klemstinegroup.sunshinelab.engine.objects.Drawable;
import com.klemstinegroup.sunshinelab.engine.objects.Overlay;
import com.klemstinegroup.sunshinelab.engine.objects.ScreenObject;

public class FrameBufferUtils {
    static public Pixmap drawObjects(Viewport viewport, Array<BaseObject> objects) {
        FrameBuffer fb = new FrameBuffer(Pixmap.Format.RGBA8888, viewport.getScreenWidth(), viewport.getScreenHeight(), true);
        fb.begin();
        draw(viewport);
        Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, viewport.getScreenWidth(), viewport.getScreenHeight());
        fb.end();
return        flipPixmap(pixmap);
//        return pixmap;
    }

    public static Pixmap flipPixmap(Pixmap src) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        Pixmap flipped = new Pixmap(width, height, src.getFormat());
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                flipped.drawPixel(x, y, src.getPixel( x, height-y-1));
            }
        }
        return flipped;
    }

    private static void draw(Viewport viewport) {
        Statics.batch.setProjectionMatrix(viewport.getCamera().combined);
        Statics.batch.setTransformMatrix(Statics.mx4Batch);
        for (BaseObject bo : Statics.objects) {
            if (bo instanceof Drawable && !(bo instanceof Overlay)) {
                ((Drawable) bo).draw(Statics.batch);
            }
            Statics.batch.setTransformMatrix(Statics.mx4Batch);

        }
    }

}
