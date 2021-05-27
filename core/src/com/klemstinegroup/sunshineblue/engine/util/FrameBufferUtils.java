package com.klemstinegroup.sunshineblue.engine.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.klemstinegroup.sunshineblue.SunshineBlue;
import com.klemstinegroup.sunshineblue.engine.Statics;
import com.klemstinegroup.sunshineblue.engine.objects.BaseObject;
import com.klemstinegroup.sunshineblue.engine.overlays.Drawable;

import java.nio.ByteBuffer;

public class FrameBufferUtils {
    /*static public Pixmap drawObjects(Batch batch, Viewport viewport, Array<BaseObject> objects) {

        SunshineBlue.instance.vfxManager.cleanUpBuffers(SunshineBlue.instance.bgColor);
        VfxFrameBuffer fb = new VfxFrameBuffer(Pixmap.Format.RGBA8888);
//        fb.addRenderer(new VfxFrameBuffer.BatchRendererAdapter(batch));
        int w = viewport.getScreenWidth();
        int h = viewport.getScreenHeight();
        fb.initialize(w, h);
        SunshineBlue.instance.vfxManager.beginInputCapture();
        Gdx.gl.glClearColor(SunshineBlue.instance.bgColor.r, SunshineBlue.instance.bgColor.g, SunshineBlue.instance.bgColor.b, SunshineBlue.instance.bgColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        draw(batch, viewport, objects, 0);
        SunshineBlue.instance.vfxManager.endInputCapture();
        SunshineBlue.instance.vfxManager.applyEffects();
        SunshineBlue.instance.vfxManager.renderToFbo(fb);
        fb.getFbo().bind();
//        Pixmap pixmap = textureToPixmap(batch, new TextureRegion(fb.getTe
//        xture()));
        Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, w, h);
        SunshineBlue.instance.vfxManager.rebind();
        fb.dispose();
        return flipPixmap(pixmap);
    }*/

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

    private static void draw(Batch batch, Viewport viewport, Array<BaseObject> objects, float delta) {
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.setTransformMatrix(SunshineBlue.instance.mx4Batch);
        for (BaseObject bo : objects) {
            if (bo instanceof Drawable) {
                ((Drawable) bo).draw(batch, delta,false);
            }
            batch.setTransformMatrix(SunshineBlue.instance.mx4Batch);

        }
    }

   /* public static Pixmap textureToPixmap(Batch batch,TextureRegion texture){
        FrameBuffer fb=new FrameBuffer(Pixmap.Format.RGBA8888,texture.getRegionWidth(),texture.getRegionHeight(),false);
        fb.begin();
        batch.setTransformMatrix(SunshineBlue.instance.mx4Batch);
        batch.setProjectionMatrix(SunshineBlue.instance.mx4Batch);
        boolean batchflag = false;
        if (!batch.isDrawing()) {
            batch.begin();
            batchflag = true;
        }
        batch.setColor(Color.WHITE);
        batch.draw(texture, 0, 0, texture.getRegionWidth(), texture.getRegionHeight());

        if (batchflag) {
            batch.end();
        }
        fb.end();
        Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, texture.getRegionWidth(), texture.getRegionHeight());
        fb.dispose();
//        SunshineBlue.instance.vfxManager.getResultBuffer().getFbo().bind();
//        SunshineBlue.instance.vfxManager.rebind();
        return pixmap;
    }*/

    /*public static Pixmap textureToPixmap(Batch batch, TextureRegion texture) {
        VfxFrameBuffer fb=new VfxFrameBuffer(Pixmap.Format.RGBA8888);
//        fb.addRenderer(new VfxFrameBuffer.BatchRendererAdapter(batch));
        fb.initialize(texture.getRegionWidth(),texture.getRegionHeight());
SunshineBlue.instance.vfxManager.cleanUpBuffers(SunshineBlue.instance.bgColor);
        fb.begin();
        batch.setTransformMatrix(SunshineBlue.instance.mx4Batch);
        batch.setProjectionMatrix(SunshineBlue.instance.mx4Batch);
        boolean batchflag = false;
        if (!batch.isDrawing()) {
            batch.begin();
            batchflag = true;
        }
        batch.setColor(Color.WHITE);
        batch.draw(texture, 0, 0, texture.getRegionWidth(), texture.getRegionHeight());

        if (batchflag) {
            batch.end();
        }
        fb.end();
        Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, texture.getRegionWidth(), texture.getRegionHeight());
        fb.dispose();
//        SunshineBlue.instance.vfxManager.getResultBuffer().getFbo().bind();
//        SunshineBlue.instance.vfxManager.rebind();
        return pixmap;
    }*/

    public static Pixmap drawObjectsPix(Batch batch, Viewport viewport, Array<BaseObject> objects, int w, int h, boolean flip) {
        SunshineBlue.instance.selectedObjects.clear();
//        Gdx.gl.glClearColor(SunshineBlue.instance.bgColor.r, SunshineBlue.instance.bgColor.g, SunshineBlue.instance.bgColor.b, SunshineBlue.instance.bgColor.a);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        SunshineBlue.instance.vfxManager.cleanUpBuffers(SunshineBlue.instance.bgColor);
        VfxFrameBuffer fb = new VfxFrameBuffer(Pixmap.Format.RGBA8888);
//        fb.addRenderer(new VfxFrameBuffer.BatchRendererAdapter(batch));
        fb.initialize(w, h);
        SunshineBlue.instance.vfxManager.beginInputCapture();
        Gdx.gl.glClearColor(SunshineBlue.instance.bgColor.r, SunshineBlue.instance.bgColor.g, SunshineBlue.instance.bgColor.b, SunshineBlue.instance.bgColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        draw(batch, viewport, objects, 0);
        batch.end();
        SunshineBlue.instance.vfxManager.endInputCapture();
        SunshineBlue.instance.vfxManager.applyEffects();
        SunshineBlue.instance.vfxManager.renderToFbo(fb);
        fb.getFbo().bind();
        Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, w, h);
        SunshineBlue.instance.vfxManager.rebind();
//        Pixmap pixmap = textureToPixmap(batch, new TextureRegion(fb.getTexture()));
        fb.dispose();
        if (flip) {
            return flipPixmap(pixmap);
        } else return pixmap;
    }
}
