package com.klemstinegroup.sunshineblue.engine.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.klemstinegroup.sunshineblue.SunshineBlue;

public class ScreenObject extends BaseObject {
    public ScreenData sd = new ScreenData();
    public VfxManager vfxManager = new VfxManager(Pixmap.Format.RGBA8888);
    public VfxFrameBuffer vfxFrameBuffer = new VfxFrameBuffer(Pixmap.Format.RGBA8888);
    public TextureRegion tr;

    public void recenter(Vector2 touchdragcpy) {
        touchdragcpy.sub(sd.position.x, sd.position.y);
        sd.position.add(touchdragcpy);
        touchdragcpy.scl(1f / sd.scale);
        touchdragcpy.rotateDeg(-sd.rotation);
        sd.center.add(touchdragcpy);
    }

    public void setupTexture(int width,int height) {
        if (!vfxFrameBuffer.isInitialized()||vfxFrameBuffer.getTexture().getHeight()!=height||vfxFrameBuffer.getTexture().getWidth()!=width) {
            vfxFrameBuffer.initialize(width,height);
            tr = new TextureRegion(vfxFrameBuffer.getTexture());
            tr.flip(false, true);
        }
    }

    public void startBatch(Batch batch) {
        vfxManager.cleanUpBuffers();
        vfxManager.beginInputCapture();
        batch.setTransformMatrix(SunshineBlue.instance.mx4Batch);
        batch.begin();
        batch.setColor(Color.WHITE);
    }

    public void endBatch(Batch batch) {
        batch.end();
        vfxManager.endInputCapture();
        vfxManager.applyEffects();
        vfxManager.renderToFbo(vfxFrameBuffer);
    }
}
